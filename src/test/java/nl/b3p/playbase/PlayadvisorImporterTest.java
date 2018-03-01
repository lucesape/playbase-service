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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import nl.b3p.commons.csv.CsvFormatException;
import nl.b3p.commons.csv.CsvInputStream;
import nl.b3p.playbase.ImportReport.ImportType;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.db.TestUtil;
import nl.b3p.playbase.entities.Location;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Meine Toonen
 */
public class PlayadvisorImporterTest extends TestUtil {

    private PlayadvisorImporter instance;

    public PlayadvisorImporterTest() {
        this.useDB = true;
    }

    @Before
    public void beforeTest() {
        instance = new PlayadvisorImporter("test");
    }

    /**
     * Test of importStream method, of class PlayadvisorImporter.
     */
    @Test
    public void testImportStreamSingle() throws Exception {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("playadvisor_single_location.csv");
        ImportReport report = new ImportReport();
        instance.importStream(in, report);
        in.close();
        assertEquals(0, report.getErrors().size());
        assertEquals(1, report.getNumberInserted(ImportType.LOCATION));
        assertEquals(4, report.getNumberInserted(ImportType.ASSET));
        List locations = DB.qr().query("Select * from " + DB.LOCATION_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(1, locations.size());
        List images = DB.qr().query("Select * from " + DB.IMAGES_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(1, images.size());
        List types = DB.qr().query("Select * from " + DB.LOCATION_CATEGORY_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(1, types.size());
        List facilities = DB.qr().query("Select * from " + DB.LOCATION_FACILITIES_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(3, facilities.size());
        List accessiblities = DB.qr().query("Select * from " + DB.LOCATION_ACCESSIBILITY_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(2, accessiblities.size());
        List assets = DB.qr().query("Select * from " + DB.ASSETS_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(4, assets.size());
        List assetsAgecategories = DB.qr().query("Select * from " + DB.ASSETS_AGECATEGORIES_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(8, assetsAgecategories.size());
        List locationAgecategories = DB.qr().query("Select * from " + DB.LOCATION_AGE_CATEGORY_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(2, locationAgecategories.size());
    }
    
    //@Test
    public void testImportStreamMulti() throws Exception {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("speelplekken_playadvisor.csv");
        ImportReport report = new ImportReport();
        instance.importStream(in, report);
        in.close();
        assertEquals(8939, report.getNumberInserted(ImportType.LOCATION));
    }

    /**
     * Test of parseRecord method, of class PlayadvisorImporter.
     */
    @Test
    public void testParseRecord() {
        String s = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24";
        String[] tokens = s.split(",");
        Map<String, Object> m = instance.parseRecord(tokens);
        assertNotNull(m);
        assertEquals(25, m.keySet().size());
        for (String key : m.keySet()) {
            assertNotNull(m.get(key));
        }
    }
    
    @Test
    public void testUpdateMergedLocation(){
        
    }

    @Test
    public void testUpdateLocation() throws Exception {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("playadvisor_single_location.csv");
        ImportReport report = new ImportReport();
        instance.importStream(in, report);
        in.close();
        assertEquals(1, report.getNumberInserted(ImportType.LOCATION));
        assertEquals(4, report.getNumberInserted(ImportType.ASSET));
        List locations = DB.qr().query("Select * from " + DB.LOCATION_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(1, locations.size());
        report = new ImportReport();
        in = PlaymappingImporterTest.class.getResourceAsStream("playadvisor_single_location.csv");
        instance.importStream(in, report);
        in.close();
        assertEquals(0, report.getErrors().size());
        assertEquals(0, report.getNumberInserted(ImportType.ASSET));
        assertEquals(0, report.getNumberInserted(ImportType.LOCATION));
        assertEquals(1, report.getNumberUpdated(ImportType.LOCATION));
        assertEquals(4, report.getNumberUpdated(ImportType.ASSET));
        locations = DB.qr().query("Select * from " + DB.LOCATION_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(1, locations.size());
        List types = DB.qr().query("Select * from " + DB.LOCATION_CATEGORY_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(1, types.size());
        List facilities = DB.qr().query("Select * from " + DB.LOCATION_FACILITIES_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(3, facilities.size());

        List accessiblities = DB.qr().query("Select * from " + DB.LOCATION_ACCESSIBILITY_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(2, accessiblities.size());

        List assets = DB.qr().query("Select * from " + DB.ASSETS_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(4, assets.size());
        List assetsAgecategories = DB.qr().query("Select * from " + DB.ASSETS_AGECATEGORIES_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(8, assetsAgecategories.size());
        List locationAgecategories = DB.qr().query("Select * from " + DB.LOCATION_AGE_CATEGORY_TABLE + instance.getPostfix(), new ArrayListHandler());
        assertEquals(2, locationAgecategories.size());

    }

    @Test
    public void testParseRecordRealData() throws IOException, CsvFormatException {
        String i = "30324,Speeltuinvereniging De Oranjetuin,\"<span class=C-6>De Oranjespeeltuin is de mooiste, gezelligste en groenste speeltuin in héél Barendrecht!<br>De speeltuin is sinds 1958 gevestigd in de Oranjewijk in Barendrecht. In de speeltuin zijn diverse glijbanen, klimtoestellen, schommels, een waterbak en een zandbak te vinden die het tot een waar speelparadijs voor de kinderen maken. De Oranjetuin is op een parkachtige wijze aangelegd en biedt een groene speelomgeving. In de speeltuin zijn eenvoudige consumpties in de vorm van koffie, thee, limonade en ijsjes te verkrijgen.<br></span>\",,2016-04-20,speelplek,http://playadvisor.b3p.nl/speelplek/openbare-speeltuin/speeltuinvereniging-%c2%93de-oranjetuin%c2%94/,http://playadvisor.b3p.nl/wp-content/uploads/2016/04/001-1.jpg|http://playadvisor.b3p.nl/wp-content/uploads/2016/04/001-1.jpg,001.jpg|001.jpg,|,|,|,,Speeltuinen>Openbare speeltuin,Nederland,Barendrecht,Duikelrek|Glijbaan|Klimtoestel|Wip,Bankje|Toilet|Verschoontafel,0 - 5 jaar|6 - 11 jaar,Gratis,Inclusive playground|Samenspeelplek,,5,4.5561209321022,51.8490438089326,\"a:1:{i:0;a:2:{s:13:\"\"attachment_id\"\";i:30309;s:3:\"\"url\"\";s:87:\"\"https://playadvisor.co/wp-content/uploads/2016/04/speeltuinverenigingdeoranjetuin-0.jpg\"\";}}\",,,,,,publish,1,speeltuinvereniging-%c2%93de-oranjetuin%c2%94,,,0,0,0,open,open,,";
        CsvInputStream cis = new CsvInputStream(new InputStreamReader(new ByteArrayInputStream(i.getBytes(StandardCharsets.UTF_8))));

        String[] s = cis.readRecord();

        Map<String, Object> input = instance.parseRecord(s);
        for (String key : input.keySet()) {
            assertNotNull(input.get(key));
        }
    }
    
    
    @Test
    public void testImportMonkeyTownEncodingIssue() throws Exception {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("monkeytown_encodingissue.csv");
        ImportReport report = new ImportReport();
        instance.importStream(in, report);
        in.close();
        assertEquals(1, report.getNumberInserted(ImportType.LOCATION));
        
        Location l = DB.qr().query("select * from " + DB.LOCATION_TABLE + "_playadvisor where pa_id = 31846", handler);
        assertNotNull(l);
        String content  = l.getPa_content();
        assertEquals(1218,content.length());
    }
    
    @Test
    public void testImportBeestenboelEncodingIssue() throws Exception {
        ImportReport report;
        try (InputStream in = PlaymappingImporterTest.class.getResourceAsStream("beestenboel_encodingissue.csv")) {
            report = new ImportReport();
            instance.importStream(in, report);
        }
        assertEquals(1, report.getNumberInserted(ImportType.LOCATION));
        
        Location l = DB.qr().query("select * from " + DB.LOCATION_TABLE + "_playadvisor where pa_id = 93746", handler);
        assertNotNull(l);
        String content  = l.getPa_content();
        assertEquals(1794,content.length());
    }

    @Test
    public void testParseMap() throws IOException, CsvFormatException {
        String i = "30324,Speeltuinvereniging De Oranjetuin,\"<span class=C-6>De Oranjespeeltuin is de mooiste, gezelligste en groenste speeltuin in héél Barendrecht!<br>De speeltuin is sinds 1958 gevestigd in de Oranjewijk in Barendrecht. In de speeltuin zijn diverse glijbanen, klimtoestellen, schommels, een waterbak en een zandbak te vinden die het tot een waar speelparadijs voor de kinderen maken. De Oranjetuin is op een parkachtige wijze aangelegd en biedt een groene speelomgeving. In de speeltuin zijn eenvoudige consumpties in de vorm van koffie, thee, limonade en ijsjes te verkrijgen.<br></span>\",,2016-04-20,speelplek,http://playadvisor.b3p.nl/speelplek/openbare-speeltuin/speeltuinvereniging-%c2%93de-oranjetuin%c2%94/,http://playadvisor.b3p.nl/wp-content/uploads/2016/04/001-1.jpg|http://playadvisor.b3p.nl/wp-content/uploads/2016/04/001-1.jpg,001.jpg|001.jpg,|,|,|,,Speeltuinen>Openbare speeltuin,Nederland,Barendrecht,Duikelrek|Glijbaan|Klimtoestel|Wip,Bankje|Toilet|Verschoontafel,0 - 5 jaar|6 - 11 jaar,Gratis,Inclusive playground|Samenspeelplek,,5,4.5561209321022,51.8490438089326,123,raam,2801,\"a:1:{i:0;a:2:{s:13:\"\"attachment_id\"\";i:30309;s:3:\"\"url\"\";s:87:\"\"https://playadvisor.co/wp-content/uploads/2016/04/speeltuinverenigingdeoranjetuin-0.jpg\"\";}}\",,,,,,publish,1,speeltuinvereniging-%c2%93de-oranjetuin%c2%94,,,0,0,0,open,open,,";
        CsvInputStream cis = new CsvInputStream(new InputStreamReader(new ByteArrayInputStream(i.getBytes(StandardCharsets.UTF_8))));

        String[] s = cis.readRecord();

        Map<String, Object> input = instance.parseRecord(s);

        Location l = instance.parseMap(input);
        assertEquals("30324", l.getPa_id());
        assertEquals("Speeltuinvereniging De Oranjetuin", l.getTitle());
        assertEquals(2, l.getAgecategories().length);
        assertNull(l.getArea());
        assertEquals("<span class=C-6>De Oranjespeeltuin is de mooiste, gezelligste en groenste speeltuin in héél Barendrecht!<br>De speeltuin is sinds 1958 gevestigd in de Oranjewijk in Barendrecht. In de speeltuin zijn diverse glijbanen, klimtoestellen, schommels, een waterbak en een zandbak te vinden die het tot een waar speelparadijs voor de kinderen maken. De Oranjetuin is op een parkachtige wijze aangelegd en biedt een groene speelomgeving. In de speeltuin zijn eenvoudige consumpties in de vorm van koffie, thee, limonade en ijsjes te verkrijgen.<br></span>", l.getPa_content());
        assertEquals("Nederland", l.getCountry());
        assertNull(l.getDocuments());
        assertNull(l.getEmail());
        assertEquals(1, l.getImages().size());
        assertNull(l.getId());
        assertEquals(51.8490438089326, l.getLatitude(), 0.01);
        assertEquals(4.5561209321022, l.getLongitude(), 0.01);
        assertEquals("Barendrecht", l.getMunicipality());
        assertNull( l.getNumber());
        assertNull( l.getNumberextra());
        assertEquals(1, (int) l.getParking());
        assertEquals("123", l.getPhone());
        assertNull( l.getPm_guid());
        assertEquals("2801", l.getPostalcode());
        assertEquals("raam", l.getStreet());
        assertNull( l.getSummary());
        assertEquals(5,(int) l.getAveragerating());
        assertEquals("http://playadvisor.b3p.nl/speelplek/openbare-speeltuin/speeltuinvereniging-%c2%93de-oranjetuin%c2%94/", l.getWebsite());

    }

    @Test
    public void testSavetoDB() throws IOException, CsvFormatException, NamingException, SQLException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("playadvisor_single_location.csv");
        ImportReport report = new ImportReport();
        instance.importStream(in, report);
        in.close();

        Location l = DB.qr().query("select * from " + DB.LOCATION_TABLE + "_playadvisor where pa_id = 30324", handler);
        assertEquals("30324", l.getPa_id());
        assertEquals("Speeltuinvereniging De Oranjetuin", l.getTitle());
        assertNull(l.getArea());
        assertEquals("<span class=C-6>De Oranjespeeltuin is de mooiste, gezelligste en groenste speeltuin in héél Barendrecht!<br>De speeltuin is sinds 1958 gevestigd in de Oranjewijk in Barendrecht. In de speeltuin zijn diverse glijbanen, klimtoestellen, schommels, een waterbak en een zandbak te vinden die het tot een waar speelparadijs voor de kinderen maken. De Oranjetuin is op een parkachtige wijze aangelegd en biedt een groene speelomgeving. In de speeltuin zijn eenvoudige consumpties in de vorm van koffie, thee, limonade en ijsjes te verkrijgen.<br></span>", l.getPa_content());
        assertEquals("Nederland", l.getCountry());
        assertNull(l.getEmail());
        assertEquals(new Integer(1),l.getId());
        assertEquals(51.8490438089326, l.getLatitude(), 0.01);
        assertEquals(4.5561209321022, l.getLongitude(), 0.01);
        assertEquals("Barendrecht", l.getMunicipality());
        assertNull( l.getNumber());
        assertNull( l.getNumberextra());
        assertEquals("123", l.getPhone());
        assertNull( l.getPm_guid());
        assertEquals("2801", l.getPostalcode());
        assertEquals("raam", l.getStreet());
        assertNull( l.getSummary());
        assertEquals(5,(int) l.getAveragerating());
        assertEquals("http://playadvisor.b3p.nl/speelplek/openbare-speeltuin/speeltuinvereniging-%c2%93de-oranjetuin%c2%94/", l.getWebsite());
        
        List li = DB.qr().query("select * from " + DB.LOCATION_AGE_CATEGORY_TABLE + "_playadvisor where location = ?", new ArrayListHandler(), l.getId());
        assertEquals(2, li.size());
        List ims = DB.qr().query("select * from " + DB.IMAGES_TABLE + "_playadvisor where location = ?", new MapListHandler(), l.getId());
        assertEquals(1, ims.size());
        assertEquals(1, (int) l.getParking());

    }

}
