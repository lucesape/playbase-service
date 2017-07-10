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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.naming.NamingException;
import nl.b3p.loader.jdbc.GeometryJdbcConverter;
import nl.b3p.loader.jdbc.GeometryJdbcConverterFactory;
import nl.b3p.loader.util.DbUtilsGeometryColumnConverter;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.db.TestUtil;
import nl.b3p.playbase.entities.Location;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Meine Toonen
 */
public class MatchActionBeanTest extends TestUtil{
    
    private MatchActionBean instance;
    public MatchActionBeanTest() {
        useDB = true;
        initData = true;
        //initAssetsData = true;
        instance = new MatchActionBean();
    }

    @Test
    public void testMerge() throws NamingException, SQLException{
        List origlocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE, new ArrayListHandler());
        assertEquals(485, origlocations.size());
        
        List palocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE + "_playadvisor", new ArrayListHandler());
        assertEquals(1, palocations.size());
        
        Integer playmappingId = 42488; // Beltmolen
        Integer playadvisorId = 1; //Speeltuin Assendorp
        instance.setPlayadvisorId(playadvisorId);
        instance.setPlaymappingId(playmappingId);
        instance.setMethod("merge");
        
        instance.save();
        
        origlocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE, new ArrayListHandler());
        assertEquals(485, origlocations.size());
        
        palocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE + "_playadvisor", new ArrayListHandler());
        assertEquals(0, palocations.size());
    }
    
    @Test
    public void testAdd() throws NamingException, SQLException{
        
        List origlocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE, new ArrayListHandler());
        assertEquals(485, origlocations.size());
        
        List palocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE + "_playadvisor", new ArrayListHandler());
        assertEquals(1, palocations.size());
        
        Integer playmappingId = null; // Beltmolen
        Integer playadvisorId = 1;//Speeltuin Assendorp
        instance.setPlayadvisorId(playadvisorId);
        instance.setPlaymappingId(playmappingId);
        instance.setMethod("add");
        
        instance.save();
        
        origlocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE, new ArrayListHandler());
        assertEquals(486, origlocations.size());
        
        palocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE + "_playadvisor", new ArrayListHandler());
        assertEquals(0, palocations.size());
    }
    
    @Test
    public void testFieldsAfterMerge() {

        try (Connection con = DB.getConnection()) {
            GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);

            ResultSetHandler<Location> handler = new BeanHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            Integer playmappingId = 42488; // Beltmolen
            Integer playadvisorId = 1; //Speeltuin Assendorp
            instance.setPlayadvisorId(playadvisorId);
            instance.setPlaymappingId(playmappingId);
            instance.setMethod("merge");

            instance.save();

            Location pl = DB.qr().query("select * from " + DB.LOCATION_TABLE + " where id = ?", handler, playmappingId);
            
            assertEquals("Speeltuin Assendorp", pl.getTitle());
        } catch (NamingException | SQLException ex) {
            fail(ex.getLocalizedMessage());
        }

    }
    
}
