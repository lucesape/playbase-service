/*
 * Copyright (C) 2016 - 2017 B3Partners B.V.
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.sql.DataSource;
import nl.b3p.loader.jdbc.GeometryJdbcConverter;
import nl.b3p.loader.jdbc.GeometryJdbcConverterFactory;
import nl.b3p.loader.util.DbUtilsGeometryColumnConverter;
import nl.b3p.playbase.entities.Location;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author Meine Toonen meinetoonen@b3partners.nl
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DB.class)
public class TestUtil {
    
    protected final static Log log = LogFactory.getLog(TestUtil.class);
    protected DataSource datasource;
    protected boolean useDB = false;
    protected boolean initData = false;
    protected boolean initAssetsData = false;
    
    protected QueryRunner run;
    
    protected GeometryJdbcConverter geometryconverter;
    protected ResultSetHandler<Location> handler;
    
    @Rule 
    public TestName testName = new TestName();
    
    @Before
    public void setUpClass() throws SQLException, IOException, NamingException {
        if(useDB){
            JDBCDataSource ds = new JDBCDataSource();
            String testname = testName.getMethodName();
            long randomizer = System.currentTimeMillis();
            ds.setUrl("jdbc:hsqldb:file:./target/unittest-hsqldb/" + testname + "_" + randomizer + "/db;shutdown=true");
            datasource = ds;
            initDB("schemaexport.sql");
            initDB("playadvisor_staging.sql");
            initDB("initdata.sql");
            if(initData){
                initDB("initdata_locations.sql");
                initDB("initdata_location_playadvisor.sql");
                if(initAssetsData){
                    initDB("initdata_assets.sql");
                }
            }
            geometryconverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(datasource.getConnection());
            handler = new BeanHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryconverter)));
            run = new QueryRunner(datasource, geometryconverter.isPmdKnownBroken());

            PowerMockito.mockStatic(DB.class);
            Mockito.when(DB.qr()).thenReturn(getDS());
            Mockito.when(DB.getConnection()).thenReturn(getCon());
        }
    }
    public QueryRunner getDS(){
        return new QueryRunner(datasource);
    }
    
    public Connection getCon() throws SQLException{
        return datasource.getConnection();
    }
    
    @Test
    public void stub(){
        
    }
    
    @After
    public void after() throws SQLException{
    }
    
    private void initDB(String file) throws IOException{
        try {
            Reader f = new InputStreamReader(TestUtil.class.getResourceAsStream(file));
            executeScript(f);
        } catch (SQLException sqle) {
            log.error("Error initializing testdb:",sqle);
        }

    }
    
    
    public void executeScript(Reader f) throws IOException, SQLException {
        Connection conn = null;

        try {            
            conn = (Connection) datasource.getConnection();
            ScriptRunner sr = new ScriptRunner(conn, true, true);
            sr.runScript(f);
            conn.commit();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
    
}
