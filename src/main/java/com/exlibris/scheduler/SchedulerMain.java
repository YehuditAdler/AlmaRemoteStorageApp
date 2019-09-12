package com.exlibris.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatHourlyForever;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.exlibris.items.ItemsMain.ItemJob;
import com.exlibris.logger.LoggerMain.LoggerJob;
import com.exlibris.request.RequestsMain.RequestRunJob;
import com.exlibris.request.RequestsMain.RequestSendToSCF;

public class SchedulerMain {

    final static Logger logger = Logger.getLogger(SchedulerMain.class);

    public static void main(String[] args) throws Exception {

        logger.info("hello SchedulerMain");
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        scheduler.start();

        JobDetail jobRequestSendToSCFDetail = newJob(RequestSendToSCF.class).build();
        JobDetail jobRequestRunJobDetail = newJob(RequestRunJob.class).build();
        JobDetail jobItemJobDetail = newJob(ItemJob.class).build();
        JobDetail jobLoggerJobDetail = newJob(LoggerJob.class).build();

        // wait 1 hour
        Trigger triggerItemJo = newTrigger().startNow().withSchedule(repeatHourlyForever(1)).build();

        // At minute 30. ,more at:https://crontab.guru/#30_*_*_*_*
        Trigger triggerRequestSendToSCF = newTrigger().startNow()
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("00 30 * * * ?").withMisfireHandlingInstructionDoNothing())
                .build();
        // At minute 0. more at:https://crontab.guru/#0_*_*_*_*
        Trigger triggerRequestRunJob = newTrigger().startNow()
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("00 00 * * * ?").withMisfireHandlingInstructionDoNothing())
                .build();
        // 10 minutes after midnight
        Trigger triggerLoggerJob = newTrigger().startNow().withSchedule(
                CronScheduleBuilder.cronSchedule("00 10 * * * ?").inTimeZone(TimeZone.getTimeZone("Etc/UTC")))
                .startNow().build();


        scheduler.scheduleJob(jobRequestSendToSCFDetail, triggerRequestSendToSCF);
        scheduler.scheduleJob(jobRequestRunJobDetail, triggerRequestRunJob);
        scheduler.scheduleJob(jobItemJobDetail, triggerItemJo);
        scheduler.scheduleJob(jobLoggerJobDetail, triggerLoggerJob);
    }

}
