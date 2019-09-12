package com.exlibris.request;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.exlibris.items.ItemData;
import com.exlibris.util.ConfigurationHandler;
import com.exlibris.util.FTPUtil;

public class RequestsMain {

    static String mainLocalFolder = "files//requests//";

    final static Logger logger = Logger.getLogger(RequestsMain.class);

    @DisallowConcurrentExecution
    public static class RequestSendToSCF implements Job {

        public void execute(JobExecutionContext context) throws JobExecutionException {
            logger.info("RequestSendToSCF executed");

            for (String institution : getInstitutionsList()) {
                sendRequestsToSCF(institution);
            }
            logger.info("RequestSendToSCF ended");
        }

        private void sendRequestsToSCF(String institution) {
            try {
                logger.info("Starting Send Requests To SCF For Institution: " + institution);

                JSONObject props = ConfigurationHandler.getInstance().getConfiguration();
                String ftpFolder = props.getJSONObject("ftp_server").getString("main_folder");

                // empty the local folder
                File xmlFolder = new File(mainLocalFolder + "xml//");
                if (xmlFolder.isDirectory()) {
                    FileUtils.cleanDirectory(xmlFolder);
                } else {
                    xmlFolder.mkdirs();
                }
                // get files from ftp
                FTPUtil.getFiles("/" + ftpFolder + "/" + institution + "/requests/", mainLocalFolder + "xml//");

                // loop over xml files and convert them to records
                File[] xmlFiles = xmlFolder.listFiles();
                int totalRecords = 0;
                for (File xmlFile : xmlFiles) {
                    String content = new String(Files.readAllBytes(xmlFile.toPath()));
                    List<ItemData> requestList = ItemData.xmlStringToRequestData(content, institution);
                    for (ItemData request : requestList) {
                        if (request.getBarcode() != null && !request.getBarcode().isEmpty()) {
                            RequestHandler.createItemRequest(request);
                        } else {
                            RequestHandler.createBibRequest(request);
                        }

                    }

                    totalRecords++;
                }
                logger.info("Total Records from FTP: " + totalRecords);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @DisallowConcurrentExecution
    public static class RequestRunJob implements Job {

        public void execute(JobExecutionContext context) throws JobExecutionException {
            logger.info("RequestRunJob executed");

            for (String institution : getInstitutionsList()) {
                runRequestsJob(institution);
            }
            logger.info("RequestRunJob ended");
        }

        private void runRequestsJob(String institution) {
            try {
                logger.info("Starting run Requests For Institution: " + institution);
                RequestHandler.runJob(institution);
            } catch (Exception e) {
            }

        }

    }

    private static List<String> getInstitutionsList() {
        List<String> institutionsList = new ArrayList<String>();
        JSONObject props = ConfigurationHandler.getInstance().getConfiguration();
        JSONArray institutions = props.getJSONArray("institutions");
        for (int i = 0; i < institutions.length(); i++) {
            if (!institutions.getJSONObject(i).getString("code").equals(props.getString("remote_storage_inst"))) {
                institutionsList.add(institutions.getJSONObject(i).getString("code"));
            }
        }
        return institutionsList;
    }
}
