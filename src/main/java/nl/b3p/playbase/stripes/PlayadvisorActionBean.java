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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.JsonResolution;
import net.sourceforge.stripes.action.POST;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.RestActionBean;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;
import nl.b3p.loader.jdbc.GeometryJdbcConverter;
import nl.b3p.loader.jdbc.GeometryJdbcConverterFactory;
import nl.b3p.loader.util.DbUtilsGeometryColumnConverter;
import nl.b3p.playbase.PlayadvisorExporter;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Location;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Meine Toonen
 */
@RestActionBean
@UrlBinding("/rest/playadvisor/{location}")
public class PlayadvisorActionBean implements ActionBean {

    private static final Log log = LogFactory.getLog(PlayadvisorActionBean.class);

    private static final String JSP = "/WEB-INF/jsp/admin/playadvisor/view.jsp";
    protected ResultSetHandler<Location> locationHandler;
    // Below is needed for all action beans.
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

    public Resolution post() {

        return new JsonResolution(1);
    }
    
    public Resolution delete(){
        return new JsonResolution(1);
        
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
                pe.pushLocation(loc,con);
            } catch (NamingException | SQLException | IOException ex) {
                log.error("Error updating locations", ex);
            }
        }
        return new ForwardResolution(JSP);
    }
}
