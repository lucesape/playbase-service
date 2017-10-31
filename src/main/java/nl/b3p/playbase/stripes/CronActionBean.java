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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.CronJob;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

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

    @Validate
    private Integer cronjobid;

    @ValidateNestedProperties({
        @Validate(field = "cronexpressie"),
        @Validate(field = "username"),
        @Validate(field = "password"),
        @Validate(field = "project"),
        @Validate(field = "id"),
        @Validate(field = "type_", converter = EnumeratedTypeConverter.class, required = true, on = "save")
    })
    private CronJob cronjob = new CronJob();

    @DefaultHandler
    public Resolution view() {
        if (cronjobid != null) {
            try {
                cronjob = DB.qr().query("SELECT * from " + DB.CRONJOB_TABLE + " WHERE id = ?", cronHandler, cronjobid);
            } catch (NamingException | SQLException ex) {
                log.error("Cannot load cronjob", ex);
            }

        }
        return new ForwardResolution(JSP);
    }
    
    public Resolution nieuw(){
        cronjob = new CronJob();
        cronjobid = null;
        return view();
    }
    
    public Resolution removeCron(){
        try {
            DB.qr().query("delete from " + DB.CRONJOB_TABLE + " WHERE id = ?", cronHandler, cronjob.getId());
        } catch (NamingException | SQLException ex) {
            log.error("Cannot delete cronjob", ex);
            context.getValidationErrors().add("cronjob", new SimpleError("Error deleting:", ex.getLocalizedMessage()));
        }
        return nieuw();
    }

    public Resolution save() {

        try {
            StringBuilder sb = new StringBuilder();
            if (cronjob.getId() == null) {
                sb.append("INSERT ");
                sb.append("INTO ");
                sb.append(DB.CRONJOB_TABLE);
                sb.append("(");
                sb.append("type_,");
                sb.append("username,");
                sb.append("password,");
                sb.append("project,");
                sb.append("cronexpressie) ");
                sb.append("VALUES(  ?,?,?,?,?);");

                cronjob = DB.qr().insert(sb.toString(), cronHandler, cronjob.getType_().name(), cronjob.getUsername(), cronjob.getPassword(), cronjob.getProject(), cronjob.getCronexpressie());
            } else {
                sb.append("update ");
                sb.append(DB.CRONJOB_TABLE);
                sb.append(" set ");
                sb.append("type_ = ?,");
                sb.append("username = ?,");
                sb.append("password = ?,");
                sb.append("project = ?,");
                sb.append("cronexpressie = ?");
                sb.append(" where id = ?");

                DB.qr().update(sb.toString(), cronjob.getType_().name(), cronjob.getUsername(), cronjob.getPassword(), cronjob.getProject(), cronjob.getCronexpressie(), cronjob.getId());
 
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot save cronjob", ex);
            context.getValidationErrors().add("cronjob", new SimpleError("Error saving:", ex.getLocalizedMessage()));
        }
        return view();
    }

    public Resolution tabledata() {
        JSONObject result = new JSONObject();

        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        Gson gson = builder.create();

        try (Connection con = DB.getConnection()) {

            ResultSetHandler<List<CronJob>> handler = new BeanListHandler(CronJob.class);
            String sql = "select * from " + DB.CRONJOB_TABLE;
            List<CronJob> jobs = DB.qr().query(sql, handler);
            JSONArray ar = new JSONArray();
            for (CronJob job : jobs) {
                ar.put(new JSONObject(gson.toJson(job, CronJob.class)));
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
