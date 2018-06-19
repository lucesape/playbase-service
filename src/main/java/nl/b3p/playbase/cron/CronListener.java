/*
 * Copyright (C) 2017 B3Partners B.V.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.b3p.playbase.cron;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Project;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author Meine Toonen
 */
public class CronListener implements ServletContextListener {

    private static final Log log = LogFactory.getLog(CronListener.class);
    private ServletContext context;
    private static Scheduler scheduler;

    public static final String QUARTZ_GROUP_NAME = "PlaybaseJobgroup";
    public static final String QUARTZ_JOB_NAME = "PlaybaseJob";
    public static final String QUARTZ_TRIGGER_NAME = "PlaybaseTrigger";
    public static final String QUARTZ_JOB_DATA_MAP_ENTITY_KEY = "CronJobEntity";

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        init(sce);

        Properties props = new Properties();
        props.put("org.quartz.scheduler.instanceName", "MonitoringScheduler");
        props.put("org.quartz.threadPool.threadCount", "10");
        props.put("org.quartz.scheduler.interruptJobsOnShutdownWithWait", "true");
        // Job store for monitoring does not need to be persistent
        props.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        try {
            scheduler = new StdSchedulerFactory(props).getScheduler();
        } catch (SchedulerException ex) {
            log.error("Cannot create scheduler. ", ex);
        }

        ResultSetHandler<List<Project>> handler = new BeanListHandler(Project.class);
        String sql = "select id,cronexpressie,type_,username,password,name,lastrun,baseurl,exporthash from " + DB.PROJECT_TABLE;
        try {
            List<Project> jobs = DB.qr().query(sql, handler);
            for (Project jobEntity : jobs) {
                try {
                    scheduleJob(jobEntity);
                } catch (SchedulerException ex) {
                    log.error("Cannot create jobs", ex);
                }
            }

            scheduler.start();
        } catch (NamingException | SQLException | SchedulerException ex) {
            log.error("Cannot retrieve jobs", ex);
        }
    }

    public static void runNow(Project job) throws SchedulerException {
        //Create a new Job 
        JobKey jobKey = JobKey.jobKey(QUARTZ_JOB_NAME + job.getId(), QUARTZ_GROUP_NAME);

        JobDetail jobDetail = createJobDetail(job, true);
        //Register this job to the scheduler
        scheduler.addJob(jobDetail, true);

        //Immediately fire the Job 
        scheduler.triggerJob(jobKey);
    }

    public static void scheduleJob(Project jobEntity) throws SchedulerException {
        if (CronExpression.isValidExpression(jobEntity.getCronexpressie())) {

            log.info("Scheduling job for expression " + jobEntity.getCronexpressie());
            JobDetail job = createJobDetail(jobEntity, false);
            CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(jobEntity.getCronexpressie());

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(QUARTZ_TRIGGER_NAME + jobEntity.getId(), QUARTZ_GROUP_NAME)
                    .startNow()
                    .withSchedule(cronSchedule)
                    .build();

            scheduler.scheduleJob(job, trigger);
        }
    }

    public static void rescheduleJob(Project jobEntity) throws SchedulerException {
        if (CronExpression.isValidExpression(jobEntity.getCronexpressie())) {
            log.info("Rescheduling job for expression " + jobEntity.getCronexpressie());

            CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(jobEntity.getCronexpressie());

            TriggerKey oldTk = new TriggerKey(QUARTZ_TRIGGER_NAME + jobEntity.getId(), QUARTZ_GROUP_NAME);
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(QUARTZ_TRIGGER_NAME + jobEntity.getId(), QUARTZ_GROUP_NAME)
                    .startNow()
                    .withSchedule(cronSchedule)
                    .build();

            scheduler.rescheduleJob(oldTk, trigger);
        }
    }

    public static void unscheduleJob(Project job) {
        try {
            TriggerKey tk = new TriggerKey(QUARTZ_TRIGGER_NAME + job.getId(), QUARTZ_GROUP_NAME);
            scheduler.unscheduleJob(tk);
        } catch (SchedulerException ex) {
            log.error("Cannot unschedule job " + job.getId(), ex);
        }
    }

    public static Date getNextFireTime(Project job) {
        try {
            TriggerKey tk = new TriggerKey(QUARTZ_TRIGGER_NAME + job.getId(), QUARTZ_GROUP_NAME);
            Trigger t = scheduler.getTrigger(tk);
            return t != null ? t.getNextFireTime() : null;
        } catch (SchedulerException ex) {
            log.error("Cannot get next firetime for job " + job.getId(), ex);
        }
        return null;
    }

    private static JobDetail createJobDetail(Project jobEntity, boolean durable) {
        JobDetail job = JobBuilder.newJob(PlaybaseJob.class)
                .withIdentity(QUARTZ_JOB_NAME + jobEntity.getId(), QUARTZ_GROUP_NAME)
                .storeDurably(durable)
                .usingJobData(createJobDataMap(jobEntity))
                .build();
        return job;
    }
    
    private static JobDataMap createJobDataMap(Project job){
        JobDataMap jdm = new JobDataMap();
        jdm.put(QUARTZ_JOB_DATA_MAP_ENTITY_KEY, job);
        return jdm;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            try {
                scheduler.shutdown(true);
            } catch (SchedulerException ex) {
                log.error("Cannot shutdown quartz scheduler. ", ex);
            }
        }
    }

    private void init(ServletContextEvent sce) {
        this.context = sce.getServletContext();

    }
}
