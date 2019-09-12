package com.exlibris.test;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TestMain {

    final static Logger logger = Logger.getLogger(TestJob.class);


    @DisallowConcurrentExecution
    public static class TestJob implements Job {

        public void execute(JobExecutionContext context) throws JobExecutionException {
            logger.info("TestJob executed");
        }

    }

}
