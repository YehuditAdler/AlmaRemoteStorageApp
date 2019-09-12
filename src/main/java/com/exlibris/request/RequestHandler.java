package com.exlibris.request;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.exlibris.items.ItemData;
import com.exlibris.restapis.HttpResponse;
import com.exlibris.restapis.JobApi;
import com.exlibris.util.ConfigurationHandler;
import com.exlibris.util.SCFUtil;

public class RequestHandler {

    final private static Logger logger = Logger.getLogger(RequestHandler.class);

    public static void createItemRequest(ItemData requestData) {
        logger.info("Create Item Request. Barcode: " + requestData.getBarcode());
        JSONObject jsonItemObject = SCFUtil.getSCFItem(requestData);
        if (jsonItemObject != null) {
            SCFUtil.createSCFRequest(jsonItemObject, requestData);
        } else {
            logger.warn("Create Request Failed. Barcode: X" + requestData.getBarcode() + "Does not exist in SCF");
        }

    }

    public static void createBibRequest(ItemData itemData) {
        logger.info("Create Bib Request. Mms Id : " + itemData.getMmsId());
        logger.debug("get Institution Bib to get NZ MMS ID");
        JSONObject jsonINSBibObject = SCFUtil.getINSBib(itemData);
        String networkNumber = getNetworkNumber(jsonINSBibObject.getJSONArray("network_number"));
        itemData.setNetworkNumber(networkNumber);
        logger.debug("get SCF Bibbased on NZ MMS ID");
        JSONObject jsonBibObject = SCFUtil.getSCFBib(itemData);
        SCFUtil.createSCFBibRequest(jsonBibObject, itemData);
    }

    private static String getNetworkNumber(JSONArray networkNumbers) {
        for (int i = 0; i < networkNumbers.length(); i++) {
            String networkNumber = networkNumbers.getString(i);
            if (networkNumber != null && networkNumber.contains("EXLNZ")) {
                return networkNumber.replaceAll("^\\(EXLNZ(.*)\\)", "");
            }
        }
        return null;
    }

    public static void runJob(String institution) {
        JSONObject props = ConfigurationHandler.getInstance().getConfiguration();
        String institutionApiKey = null;
        String requestsJobId = null;
        String baseUrl = props.get("gateway").toString();
        for (int i = 0; i < props.getJSONArray("institutions").length(); i++) {
            JSONObject inst = props.getJSONArray("institutions").getJSONObject(i);
            if (inst.get("code").toString().equals(institution)) {
                institutionApiKey = inst.getString("apikey");
                requestsJobId = inst.getString("requests_job_id");
                break;
            }
        }
        HttpResponse jobResponce = JobApi.runJob(requestsJobId, "run", baseUrl, institutionApiKey, "<job/>");
        if (jobResponce.getResponseCode() == HttpsURLConnection.HTTP_BAD_REQUEST) {
            logger.warn("Can't run job institution : " + institution + ". Job Id : " + requestsJobId);
        }
    }

}
