package com.exlibris.restapis;

import org.apache.log4j.Logger;

public class JobApi {

    final private static Logger logger = Logger.getLogger(JobApi.class);

    public static HttpResponse runJob(String jobId, String op, String baseUrl, String apiKey, String body) {
        logger.info("Starting to handle run job id : " + jobId + ".");
        logger.info("Job Id: " + jobId + " - calling POST");

        String url = baseUrl + "/almaws/v1/conf/jobs/" + jobId + "?op=" + op + "&apikey=" + apiKey;
        HttpResponse itemResponse = AlmaRestUtil.sendHttpReq(url, "POST", body);

        return itemResponse;
    }

}
