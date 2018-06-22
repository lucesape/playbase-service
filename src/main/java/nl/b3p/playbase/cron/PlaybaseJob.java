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

import nl.b3p.playbase.entities.ProjectType;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import javax.naming.NamingException;
import nl.b3p.mail.Mailer;
import nl.b3p.playbase.ImageDownloader;
import nl.b3p.playbase.ImportReport;
import nl.b3p.playbase.ImportReport.ImportType;
import nl.b3p.playbase.PlaymappingImporter;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Project;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
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
    private ImageDownloader downloader;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {

        JobDataMap jdm = jec.getJobDetail().getJobDataMap();
        Project cj = (Project) jdm.get(CronListener.QUARTZ_JOB_DATA_MAP_ENTITY_KEY);
        log.info("Executing playbasejob " + cj.getId() + ". Type is " + cj.getType_().toString());
        ProjectType ct = cj.getType_();

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

    public void importPlaymapping(Project job) {

        PlaymappingImporter pi = new PlaymappingImporter(job);

        ImportReport locationReport = new ImportReport();
        ImportReport assetsReport = new ImportReport();
        String logString;
        String importedString = null;
        try {
            List<String> pm_guids = pi.importJSONLocationsFromAPI(job.getUsername(), job.getPassword(), "https://api.playmapping.com/CustomerLocation/GetAll",locationReport);//tJSONFromAPI(job.getUsername(), job.getPassword(), "https://api.playmapping.com/CustomerLocation/GetAll"); // import/update locations
            assetsReport = pi.importJSONAssetsFromAPI(job.getUsername(), job.getPassword(), "https://api.playmapping.com/CustomerAsset/GetByLocationId/",pm_guids,assetsReport);//NFromAPI(job.getUsername(), job.getPassword(), "https://api.playmapping.com/CustomerAsset/GetAll"); // import/update locations
        } catch (SQLException | NamingException ex) {
            log.error("Cannot import playmapping: ", ex);
        }
        try {
            if (locationReport != null && assetsReport != null) {
                logString = "Location: " + locationReport.toLog() + System.lineSeparator() + "Assets: " + assetsReport.toLog();
                importedString = "Location: " + locationReport.getImportedstring().get(ImportType.LOCATION) + System.lineSeparator() + "Assets: " + assetsReport.getImportedstring().get(ImportType.ASSET);
            } else {
                logString = "Kon niet importeren. Zie logfile.";
            }
            savecronjob(job, logString, importedString);
            sendMail(job, logString);
        } catch (SQLException | NamingException ex) {
            log.error("Cannot save report: ", ex);
        }

    }
    
    public void importPlayadvisor(Project job)  {
    }
    public void exportPlayadvisor(Project job)  {
    }
    
    private void getAllImagesForJob(Project job) {
        log.info("Getting images for job");
        try {
            String query = "SELECT id from " + DB.LOCATION_TABLE;
            
            ArrayListHandler rsh = new ArrayListHandler();
            query += " where project = ?";
            List<Object[]> locations = DB.qr().query(query, rsh, job.getName());
            for (Object[] location : locations) {
                Integer id = (Integer)location[0];
                retrieveImages(id);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot get locations for cronjob",ex);
        }
        log.info("All images submitted");

    }
     protected void retrieveImages(Integer id) throws NamingException, SQLException {
        ArrayListHandler rsh = new ArrayListHandler();

        List<Object[]> images = DB.qr().query("SELECT url, caption,pm_guid from " + DB.IMAGES_TABLE + " WHERE location = ? order by equipment desc, lastupdated desc", rsh, id);

        int index = 0;
        for (Object[] image : images) {
            String url = (String) valueOrEmptyString(image[0]);
            if (url.isEmpty()) {
                continue;
            }
            String imageName = url.substring(url.lastIndexOf("/") + 1);
            if (imageName.contains("GetImage.ashx")) {
                imageName = "Image" + id + "-" + index + ".jpg";
            }
            downloadImage(url, imageName);
            index++;
        }
    }
    
    private void downloadImage(String url, String filename) {
        downloader.add(url, filename);
    }
    
    private Object valueOrEmptyString(Object value) {
        return value == null ? "" : value;
    }

    private void savecronjob(Project cronjob, String logString, String importedString) throws NamingException, SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(DB.PROJECT_TABLE);
        sb.append(" set ");
        sb.append("log = ?,");
        sb.append("importedstring = ?,");
        sb.append("lastrun = ?");
        sb.append(" where id = ?");

        DB.qr().update(sb.toString(), logString, importedString, new Timestamp(new java.util.Date().getTime()), cronjob.getId());
    }

    private void sendMail(Project cronjob, String logString) {
        if (cronjob.getMailaddress() != null) {
            String subject = "Playbase cron status: " + cronjob.getType_().toString() + " voor project " + cronjob.getName();
            StringBuilder content = new StringBuilder();
            content.append("Status rapport ").append(cronjob.getId());
            content.append(System.lineSeparator());
            content.append("Log: ");
            content.append(System.lineSeparator());
            content.append(logString);
            try {
                Mailer.sendMail("Playbase", "support@b3partners.nl", cronjob.getMailaddress(), subject, content.toString());
            } catch (Exception ex) {
                log.error("Cannot send mail:", ex);
            }
        }
    }
}
