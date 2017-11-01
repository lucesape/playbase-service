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
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import nl.b3p.mail.Mailer;
import nl.b3p.playbase.ImportReport;
import nl.b3p.playbase.ImportReport.ImportType;
import nl.b3p.playbase.PlaymappingImporter;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.CronJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Meine Toonen
 */
public class PlaybaseJob implements Job {

    private static final Log log = LogFactory.getLog(PlaybaseJob.class);

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {

        JobDataMap jdm = jec.getJobDetail().getJobDataMap();
        CronJob cj = (CronJob) jdm.get(CronListener.QUARTZ_JOB_DATA_MAP_ENTITY_KEY);
        log.info("Executing playbasejob " + cj.getId() + ". Type is " + cj.getType_().toString());
        CronType ct = cj.getType_();

        switch (ct) {
            case IMPORT_PLAYADVISOR:
                importPlayadvisor(cj);
                break;
            case IMPORT_PLAYMAPPING:
                importPlaymapping(cj);
                break;
            default:
                break;
        }
    }

    private void importPlaymapping(CronJob job) {

        PlaymappingImporter pi = new PlaymappingImporter();

        ImportReport locationReport = null;
        ImportReport assetsReport = null;
        String logString;
        String importedString = null;
        try {
            locationReport = pi.importJSONFromAPI(job.getUsername(), job.getPassword(), "https://api.playmapping.com/CustomerLocation/GetAll"); // import/update locations
            assetsReport = pi.importJSONFromAPI(job.getUsername(), job.getPassword(), "https://api.playmapping.com/CustomerAsset/GetAll"); // import/update locations

        } catch (SQLException | NamingException ex) {
            log.error("Cannot import playmapping: ", ex);
        }
        try {
            if (locationReport != null && assetsReport != null) {
                logString = "Location: " + locationReport.toLog() + System.lineSeparator() + "Assets: " + assetsReport.toLog();
                importedString = "Location: " + locationReport.getImportedstring().get(ImportType.LOCATION) + System.lineSeparator() + "Assets: " + assetsReport.getImportedstring().get(ImportType.ASSET);
            }else{
                logString = "Kon niet importeren. Zie logfile.";
            }
            savecronjob(job, logString, importedString);
            sendMail(job, logString);
        } catch (SQLException | NamingException ex) {
            log.error("Cannot save report: ", ex);
        }

    }

    private void importPlayadvisor(CronJob job) {
        throw new UnsupportedOperationException("Automagically importing playdvisor not yet implemented");
    }

    private void savecronjob(CronJob cronjob, String logString, String importedString) throws NamingException, SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(DB.CRONJOB_TABLE);
        sb.append(" set ");
        sb.append("log = ?,");
        sb.append("importedstring = ?,");
        sb.append("lastrun = ?");
        sb.append(" where id = ?");

        DB.qr().update(sb.toString(), logString, importedString, new Timestamp(new java.util.Date().getTime()), cronjob.getId());
    }
    
    private void sendMail(CronJob cronjob, String logString) {
        if (cronjob.getMailaddress() != null) {
            String subject = "Playbase cron status: " + cronjob.getType_().toString() + " voor project " + cronjob.getProject();
            StringBuilder content = new StringBuilder();
            content.append("Status rapport ").append(cronjob.getId());
            content.append(System.lineSeparator());
            content.append("Log: ");
            content.append(System.lineSeparator());
            content.append(logString);
            try {
                Mailer.sendMail("Playbase", "support@b3partners.nl", cronjob.getMailaddress(), subject, content.toString());
            } catch (Exception ex) {
                log.error("Cannot send mail:",ex);
            }
        }
    }
}
