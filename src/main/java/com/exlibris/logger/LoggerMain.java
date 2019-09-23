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
                logger.info("backup File is - " + backupFile);
                boolean ok = FTPUtil.uploadSingleFile(backupFile, "/wrlc_scf/" + backupFile);
                logger.info("LoggerJob ended - " + ok);
            } catch (Exception e) {
                logger.info(e.getMessage());
                String file = "logs//application.log";
                boolean ok = false;
                try {
                    logger.info("backup File is - " + file);
                    ok = FTPUtil.uploadSingleFile(file, "/wrlc_scf/" + file);
                } catch (Exception e1) {
                    logger.info(e1.getMessage());
                    logger.info("LoggerJob ended - " + "no backup log file copied log file- " + ok);
                }

            }
            logger.info("LoggerJob ended");
        }

    }
}
