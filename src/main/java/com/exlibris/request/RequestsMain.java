package com.exlibris.request;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.exlibris.configuration.ConfigurationHandler;
import com.exlibris.items.ItemData;
import com.exlibris.util.FTPUtil;

public class RequestsMain {

    static String mainLocalFolder = "files//requests//";

    final static Logger logger = Logger.getLogger(RequestsMain.class);

    public static synchronized void sendRequestsToSCF(String institution) {
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
