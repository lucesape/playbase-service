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
package nl.b3p.playbase.stripes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.StrictBinding;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.EnumeratedTypeConverter;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import nl.b3p.playbase.ImportReport;
import nl.b3p.playbase.PlayadvisorImporter;
import nl.b3p.playbase.PlaymappingImporter;
import nl.b3p.playbase.cron.CronListener;
import nl.b3p.playbase.cron.CronType;
import nl.b3p.playbase.cron.PlaybaseJob;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.CronJob;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;

/**
 *
 * @author Meine Toonen
 */
@StrictBinding
@UrlBinding("/action/cron/{$event}")
public class CronActionBean implements ActionBean {

    private ResultSetHandler<CronJob> cronHandler = new BeanHandler(CronJob.class);
    private static final Log log = LogFactory.getLog(CronActionBean.class);

    private ActionBeanContext context;
    private static final String JSP = "/WEB-INF/jsp/admin/cron/cron.jsp";
    
    private static final String WIZARD_NEW_JSP = "/WEB-INF/jsp/admin/cron/createNew.jsp";
    private static final String WIZARD_AFTER_INITIAL_IMPORT = "/WEB-INF/jsp/admin/cron/afterInitialImport.jsp";
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Validate
    private Integer cronjobid;

    @ValidateNestedProperties({
        @Validate(field = "cronexpressie"),
        @Validate(field = "username"),
        @Validate(field = "password"),
        @Validate(field = "mailaddress"),
        @Validate(field = "authkey"),
        @Validate(field = "baseurl"),
        @Validate(field = "project"),
        @Validate(field = "id"),
        @Validate(field = "type_", converter = EnumeratedTypeConverter.class, required = true, on = "save")
    })
    private CronJob cronjob = new CronJob();

    @DefaultHandler
    public Resolution view() {
        if (cronjobid != null) {
            try {
                cronjob = DB.qr().query("SELECT id,cronexpressie,type_,username,password,project,log,lastrun,mailaddress,baseurl,exporthash from " + DB.CRONJOB_TABLE + " WHERE id = ?", cronHandler, cronjobid);
            } catch (NamingException | SQLException ex) {
                log.error("Cannot load cronjob", ex);
            }
        }
        return new ForwardResolution(JSP);
    }
    
    public Resolution nieuw(){
        cronjob = new CronJob();
        cronjobid = null;
        return new ForwardResolution(WIZARD_NEW_JSP);
    }
    
    public Resolution removeCron(){
        try {
            CronListener.unscheduleJob(cronjob);
            DB.qr().query("delete from " + DB.CRONJOB_TABLE + " WHERE id = ?", cronHandler, cronjob.getId());
        } catch (NamingException | SQLException ex) {
            log.error("Cannot delete cronjob", ex);
            context.getValidationErrors().add("cronjob", new SimpleError("Error deleting:", ex.getLocalizedMessage()));
        }
        return view();
    }
    
    public Resolution runNow(){
        try {
            CronListener.runNow(cronjob);
        } catch (SchedulerException ex) {
            log.error("Cannot run cronjob", ex);
            context.getValidationErrors().add("cronjob", new SimpleError("Error running:", ex.getLocalizedMessage()));
        }
        return view();
    }
    
    public Resolution downloadString(){
        ResultSetHandler h = new MapHandler();
        try {
            Map o  = (Map)DB.qr().query("SELECT importedstring from " + DB.CRONJOB_TABLE + " WHERE id = ?", h, cronjob.getId());
            
            StreamingResolution res = new StreamingResolution("application/json",(String)o.get("importedstring"));
            res.setFilename(cronjob.getType_().toString() + "-" + cronjob.getProject() + cronjob.getId() );
            res.setAttachment(true);
            return res;
        } catch (NamingException | SQLException ex) {
            log.error("Cannot retrieve string",ex);
            return view();
        }
    }
    
    public Resolution saveNew(){
        cronjob.setType_(CronType.PLAYMAPPING_PLAYADVISOR);
        save();
        String logString;
        try(Connection con = DB.getConnection()){
            ImportReport report = new ImportReport();
            PlayadvisorImporter paimporter = new PlayadvisorImporter(cronjob.getProject());
            paimporter.initialLoad(cronjob, report, con);
            logString = "Playadvisor:  "+ System.lineSeparator() + report.toLog();
        } catch (NamingException | SQLException ex) {
            log.error("Cannot do initial load for playadvisor", ex);
            context.getValidationErrors().add("cronjob", new SimpleError("Error saving:" + ex.getLocalizedMessage()));
            logString = "Error for playadvisor: "+ System.lineSeparator();
            logString += ex.getLocalizedMessage();
        }
       
        try (Connection con = DB.getConnection()) {
            PlaymappingImporter pi = new PlaymappingImporter(cronjob.getProject());
            ImportReport locationReport = new ImportReport();
            ImportReport assetsReport = new ImportReport();
            
            List<String> pm_guids = pi.importJSONLocationsFromAPI(cronjob.getUsername(), cronjob.getPassword(), "https://api.playmapping.com/CustomerLocation/GetAll", locationReport);//tJSONFromAPI(job.getUsername(), job.getPassword(), "https://api.playmapping.com/CustomerLocation/GetAll"); // import/update locations
            assetsReport = pi.importJSONAssetsFromAPI(cronjob.getUsername(), cronjob.getPassword(), "https://api.playmapping.com/CustomerAsset/GetByLocationId/", pm_guids, assetsReport);//NFromAPI(job.getUsername(), job.getPassword(), "https://api.playmapping.com/CustomerAsset/GetAll"); // import/update locations

            logString += "Playmapping: " + System.lineSeparator() + " Location: " + locationReport.toLog() + System.lineSeparator() + "Assets: " + assetsReport.toLog();

        } catch (NamingException | SQLException ex) {
            log.error("Cannot do initial load for playadvisor", ex);
            context.getValidationErrors().add("cronjob", new SimpleError("Error saving:" + ex.getLocalizedMessage()));
            logString = "Error for playmapping: "+ System.lineSeparator();
            logString += ex.getLocalizedMessage();
        }
        
        cronjob.setLog(logString);
        
        return new ForwardResolution(WIZARD_AFTER_INITIAL_IMPORT);
    }

    public Resolution save() {

        if (!CronExpression.isValidExpression(cronjob.getCronexpressie())) {
            context.getValidationErrors().add("cronjob.cronexpressie", new SimpleError("Cronexpressie niet correct"));
            return view();
        }
        try (Connection con = DB.getConnection()){
            StringBuilder sb = new StringBuilder();
            if (cronjob.getId() == null) {
                sb.append("INSERT ");
                sb.append("INTO ");
                sb.append(DB.CRONJOB_TABLE);
                sb.append("(");
                sb.append("username,");
                sb.append("password,");
                sb.append("project,");
                sb.append("baseurl,");
                sb.append("authkey,");
                sb.append("mailaddress,");
                sb.append("cronexpressie) ");
                sb.append("VALUES(  ?,?,?,?,?,?,?);");

                cronjob = DB.qr().insert(con, sb.toString(), cronHandler, cronjob.getUsername(), cronjob.getPassword(), cronjob.getProject(),
                        cronjob.getBaseurl(), cronjob.getAuthkey(), cronjob.getMailaddress(), cronjob.getCronexpressie());
                CronListener.scheduleJob(cronjob);
            } else {
                sb.append("update ");
                sb.append(DB.CRONJOB_TABLE);
                sb.append(" set ");
                sb.append("username = ?,");
                sb.append("password = ?,");
                sb.append("project = ?,");
                sb.append("mailaddress= ?,");
                sb.append("baseurl= ?,");
                sb.append("authkey= ?,");
                sb.append("cronexpressie = ?");
                sb.append(" where id = ?");

                DB.qr().update(con,sb.toString(), cronjob.getUsername(), cronjob.getPassword(), cronjob.getProject(),
                       cronjob.getMailaddress(),  cronjob.getBaseurl(), cronjob.getAuthkey(), cronjob.getCronexpressie(), cronjob.getId());
                CronListener.rescheduleJob(cronjob);
            }
        } catch (NamingException | SQLException | SchedulerException ex) {
            log.error("Cannot save cronjob", ex);
            context.getValidationErrors().add("cronjob", new SimpleError("Error saving:" + ex.getLocalizedMessage()));
        }
        return view();
    }

    public Resolution tabledata() {
        JSONObject result = new JSONObject();

        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        Gson gson = builder.create();

        try {

            ResultSetHandler<List<CronJob>> handler = new BeanListHandler(CronJob.class);
            String sql = "select id,cronexpressie,type_,username,password,project,log,lastrun from " + DB.CRONJOB_TABLE;
            List<CronJob> jobs = DB.qr().query(sql, handler);
            JSONArray ar = new JSONArray();
            for (CronJob job : jobs) {
                JSONObject obj = new JSONObject(gson.toJson(job, CronJob.class));
                Date d = CronListener.getNextFireTime(job);
                String formattedDate = d !=  null ? sdf.format(d) : "";
                obj.put("next_fire_time",formattedDate);
                obj.put("lastrun", obj.optString("lastrun", " - "));
                ar.put(obj);
            }
            result.put("data", ar);

        } catch (NamingException | SQLException ex) {
            log.error("Cannot get geometryConverter: ", ex);
            result.put("message", "Cannot get geometryConverter: " + ex.getLocalizedMessage());
        }

        StreamingResolution res = new StreamingResolution("application/json", result.toString(4));
        res.setFilename("");
        res.setAttachment(true);
        return res;
    }

    // <editor-fold desc="getters and setters" defaultstate="collapsed">
    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    public CronJob getCronjob() {
        return cronjob;
    }

    public void setCronjob(CronJob cronjob) {
        this.cronjob = cronjob;
    }

    public Integer getCronjobid() {
        return cronjobid;
    }

    public void setCronjobid(Integer cronjobid) {
        this.cronjobid = cronjobid;
    }
    // </editor-fold>

}
