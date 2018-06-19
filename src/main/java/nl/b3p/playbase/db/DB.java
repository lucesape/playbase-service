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
package nl.b3p.playbase.db;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Matthijs Laan
 */
public class DB {
    private static final Log log = LogFactory.getLog("db");

    protected static final String JNDI_NAME = "java:/comp/env/jdbc/playbase-service";
    
    public static final String LOCATION_TABLE = "playservice_locations";
    public static final String LOCATION_CATEGORY_TABLE = "playservice_location_categories";
    public static final String LOCATION_AGE_CATEGORY_TABLE = "playservice_location_agecategories";
    public static final String LOCATION_ACCESSIBILITY_TABLE = "playservice_location_accessibility";
    public static final String LOCATION_FACILITIES_TABLE = "playservice_location_facilities";
    
    public static final String LIST_CATEGORY_TABLE = "playservice_categories_list";
    public static final String LIST_AGECATEGORIES_TABLE = "playservice_agecategories_list";
    public static final String LIST_ACCESSIBILITY_TABLE = "playservice_accessibility_list";
    public static final String LIST_FACILITIES_TABLE = "playservice_facilities_list";
    public static final String LIST_PARKING_TABLE = "playservice_parking_list";
    public static final String LIST_EQUIPMENT_TYPE_TABLE = "playservice_equipment_list";
    
    public static final String ASSETS_TABLE = "playservice_location_equipment";
    public static final String ASSETS_AGECATEGORIES_TABLE = "playservice_location_equipment_agecategories";
    public static final String ASSETS_TYPE_GROUP_LIST_TABLE = "playmapping_type_group";
    public static final String ASSETS_DOCUMENTS_TABLE = "playservice_documents";
    
    public static final String IMAGES_TABLE = "playservice_images";
    
    public static final String COMMENT_TABLE = "playservice_comment";
    
    public static final String PROJECT_TABLE = "project";

    public static final DataSource getDataSource(String jndiName) throws NamingException {
        InitialContext cxt = new InitialContext();
        log.trace("looking up JNDI resource " + jndiName);
        DataSource ds = (DataSource)cxt.lookup(jndiName);
        if(ds == null) {
            throw new NamingException("Data source " + jndiName + " not found, please configure the webapp container correctly according to the installation instructions");
        }
        return ds;
    }

    public static final Connection getConnection() throws NamingException, SQLException {
        return getDataSource(JNDI_NAME).getConnection();
    }

    public static final QueryRunner qr() throws NamingException {
         return new QueryRunner(getDataSource(JNDI_NAME));
    }
}
