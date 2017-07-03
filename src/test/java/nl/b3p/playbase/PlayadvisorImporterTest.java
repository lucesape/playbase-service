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
package nl.b3p.playbase;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.db.TestUtil;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Meine Toonen
 */
public class PlayadvisorImporterTest extends TestUtil{
    
    private PlayadvisorImporter instance;
    
    public PlayadvisorImporterTest() {
        this.useDB = true;
    }

    @Before
    public void beforeTest(){
        instance = new PlayadvisorImporter();
    }
    /**
     * Test of importStream method, of class PlayadvisorImporter.
     */
    @Test
    public void testImportStream() throws Exception {
        InputStream in = PlaymappingProcessorTest.class.getResourceAsStream("playadvisor_single_location.csv");
        ImportReport report = new ImportReport("locaties");
        instance.importStream(in, report);
        in.close();
        assertEquals(1,report.getNumberInserted());
        List locations = DB.qr().query("Select * from " + DB.LOCATION_TABLE, new ArrayListHandler());
        assertEquals(1, locations.size());
        List images = DB.qr().query("Select * from " + DB.IMAGES_TABLE, new ArrayListHandler());
        assertEquals(2, images.size());
        List types = DB.qr().query("Select * from " + DB.LOCATION_CATEGORY_TABLE , new ArrayListHandler());
        assertEquals(1, types.size());
    }

    /**
     * Test of parseRecord method, of class PlayadvisorImporter.
     */
    @Test
    public void testParseRecord() {
        String s = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24";
        String [] tokens = s.split(",");
        Map<String, Object> m = instance.parseRecord(tokens);
        assertNotNull(m);
        assertEquals(25, m.keySet().size());
        for (String key : m.keySet()) {
            assertNotNull(m.get(key));
        }
    }
    
    @Test
    public void testUpdateLocation() throws Exception {
        InputStream in = PlaymappingProcessorTest.class.getResourceAsStream("playadvisor_single_location.csv");
        ImportReport report = new ImportReport("locaties");
        instance.importStream(in, report);
        in.close();
        assertEquals(1,report.getNumberInserted());
        List locations = DB.qr().query("Select * from " + DB.LOCATION_TABLE, new ArrayListHandler());
        assertEquals(1, locations.size());
        report = new ImportReport("locaties");
        in = PlaymappingProcessorTest.class.getResourceAsStream("playadvisor_single_location.csv");
        instance.importStream(in, report);
        in.close();
        assertEquals(0, report.getNumberInserted());
        assertEquals(1, report.getNumberUpdated());
        locations = DB.qr().query("Select * from " + DB.LOCATION_TABLE, new ArrayListHandler());
        assertEquals(1, locations.size());
        List types = DB.qr().query("Select * from " + DB.LOCATION_CATEGORY_TABLE , new ArrayListHandler());
        assertEquals(1, types.size());
    }

}
