/*
 * Copyright (C) 2018 B3Partners B.V.
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

import java.io.IOException;
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
import net.sourceforge.stripes.action.JsonResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.RestActionBean;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import nl.b3p.loader.jdbc.GeometryJdbcConverter;
import nl.b3p.loader.jdbc.GeometryJdbcConverterFactory;
import nl.b3p.loader.util.DbUtilsGeometryColumnConverter;
import nl.b3p.playbase.ImportReport;
import nl.b3p.playbase.Importer;
import nl.b3p.playbase.PlayadvisorExporter;
import nl.b3p.playbase.PlayadvisorImporter;
import nl.b3p.playbase.PlaymappingImporter;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Location;
import nl.b3p.playbase.entities.Project;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Meine Toonen
 */
@RestActionBean
@UrlBinding("/rest/playadvisor/{location}")
public class PlayadvisorRESTAPIActionBean implements ActionBean {

    private static final Log log = LogFactory.getLog(PlayadvisorRESTAPIActionBean.class);

    private static final String JSP = "/WEB-INF/jsp/admin/playadvisor/view.jsp";
    protected ResultSetHandler<Location> locationHandler;

    private ActionBeanContext context;

    @Validate
    private Integer location;

    // <editor-fold desc="Getters and settesr" defaultstate="collapsed">
    public void setLocation(Integer location) {
        this.location = location;
    }

    public Integer getLocation() {
        return location;
    }

    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    // </editor-fold>
    @DefaultHandler
    public Resolution view() {
        return new ForwardResolution(JSP);
    }

    public Resolution post() {
        String logString = "";
        try (Connection con = DB.getConnection()) {
            ImportReport report = new ImportReport();
            String locationString = IOUtils.toString(context.getRequest().getInputStream(), "UTF-8");
            PlayadvisorImporter paimporter = new PlayadvisorImporter(null);

            JSONArray ar = new JSONArray();
            ar.put(new JSONObject(locationString));

            List<Location> loc = paimporter.processLocations(ar.toString(), report, con);
            logString = "Playadvisor:  " + System.lineSeparator() + report.toLog();
        } catch (NamingException | SQLException | IOException ex) {
            log.error("Cannot do initial load for playadvisor", ex);
            context.getValidationErrors().add("cronjob", new SimpleError("Error saving:" + ex.getLocalizedMessage()));
            logString = "Error for playadvisor: " + System.lineSeparator();
            logString += ex.getLocalizedMessage();
        }
        return new JsonResolution(logString);
    }

    public Resolution delete() {
        if (location != null) {
            try (Connection con = DB.getConnection()) {
                GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);
                locationHandler = new BeanHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
                Location loc;

                StringBuilder sb = new StringBuilder();
                sb.append("select * from ");

                sb.append(DB.LOCATION_TABLE);
                sb.append(" where id = '");
                sb.append(location);

                sb.append("';");
                loc = DB.qr().query(con, sb.toString(), locationHandler);
                if (loc != null) {
                    loc.setRemovedfromplayadvisor(true);
                    Project p = getProject(loc.getProject(), con);
                    Importer imp = new PlaymappingImporter(p);
                    ImportReport report = new ImportReport();
                    imp.saveLocation(loc, report);
                    List<String> errors = report.getErrors();
                    if (errors.isEmpty()) {
                        return new JsonResolution("Success");

                    } else {
                        return new JsonResolution(report.getAllErrors());
                    }
                } else {
                    return new JsonResolution("Error: invalid id given");

                }

            } catch (NamingException | SQLException ex) {
                log.error("Error updating locations", ex);
                return new JsonResolution(ex);
            }
        }
        return new JsonResolution("No location given.");
    }

    public Resolution updateLocation() {
        if (location != null) {
            try (Connection con = DB.getConnection()) {
                GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);
                locationHandler = new BeanHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
                Location loc;

                StringBuilder sb = new StringBuilder();
                sb.append("select * from ");

                sb.append(DB.LOCATION_TABLE);
                sb.append(" where id = '");
                sb.append(location);

                sb.append("';");
                loc = DB.qr().query(sb.toString(), locationHandler);

                PlayadvisorExporter pe = new PlayadvisorExporter();
                String result = pe.pushLocation(loc, con, getProject(loc.getProject(), con));
                context.getMessages().add(new SimpleMessage(result));
            } catch (NamingException | SQLException | IOException ex) {
                log.error("Error updating locations", ex);
            }
        }
        return new ForwardResolution(JSP);
    }

    protected final ResultSetHandler<Project> projectHandler = new BeanHandler(Project.class);

    protected Project getProject(String gemeente, Connection con) throws NamingException, SQLException {
        Project p = DB.qr().query(con, "SELECT id,cronexpressie,type_,username,password,name,log,lastrun,mailaddress,baseurl, status, authkey from " + DB.PROJECT_TABLE + " WHERE name = ?", projectHandler, gemeente);
        return p;
    }

    protected Project getProject(Integer projectID, Connection con) throws NamingException, SQLException {
        Project p = DB.qr().query(con, "SELECT id,cronexpressie,type_,username,password,name,log,lastrun,mailaddress,baseurl, status, authkey from " + DB.PROJECT_TABLE + " WHERE id = ?", projectHandler, projectID);
        return p;
    }

}
