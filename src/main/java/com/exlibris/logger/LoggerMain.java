package com.exlibris.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.exlibris.util.FTPUtil;

public class LoggerMain {

    final static Logger logger = Logger.getLogger(LoggerMain.class);


    @DisallowConcurrentExecution
    public static class LoggerJob implements Job {

        public void execute(JobExecutionContext context) throws JobExecutionException {
            logger.info("LoggerJob executed");
            try {
                String backupFile = "logs//application.log_" + new SimpleDateFormat("yyyy-MM-dd")
                        .format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))
                        + ".log";
                boolean ok = FTPUtil.uploadSingleFile(backupFile, "/wrlc_scf/" + backupFile);
                logger.info("LoggerJob ended - " + ok);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
