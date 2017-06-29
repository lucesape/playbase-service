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
        assertEquals(1,report.getNumberInserted());
        List a = DB.qr().query("Select * from " + DB.LOCATION_TABLE, new ArrayListHandler());
        assertEquals(1, a.size());
    }

    /**
     * Test of parseRecord method, of class PlayadvisorImporter.
     */
    @Test
    public void testParseRecord() {
        String headerString = "id,Title,Content,Excerpt,Date,\"Post Type\",Permalink,URL,Title,Caption,Description,\"Alt Text\",URL,Speelplektype,Landen,Plaatsen,Speeltoestellen,Faciliteiten,Leeftijden,Parkeren,Toegankelijkheid,Ambassadeurs,average_rating,x_coordinaat,y_coordinaat,galerij,_wp_attached_file,_wp_attachment_metadata,_wpml_media_duplicate,_wpml_media_featured,favoriet,Status,Author,Slug,Format,Template,Parent,\"Parent Slug\",Order,\"Comment Status\",\"Ping Status\",\"Samenspeelplek Enquete\",\"Test enquete monique\"";
        String s = "30324,\"Speeltuinvereniging De Oranjetuin\",\"<span class=C-6>De Oranjespeeltuin is de mooiste, gezelligste en groenste speeltuin in héél Barendrecht!<br>De speeltuin is sinds 1958 gevestigd in de Oranjewijk in Barendrecht. In de speeltuin zijn diverse glijbanen, klimtoestellen, schommels, een waterbak en een zandbak te vinden die het tot een waar speelparadijs voor de kinderen maken. De Oranjetuin is op een parkachtige wijze aangelegd en biedt een groene speelomgeving. In de speeltuin zijn eenvoudige consumpties in de vorm van koffie, thee, limonade en ijsjes te verkrijgen.<br></span>\",,2016-04-20,speelplek,http://playadvisor.b3p.nl/speelplek/openbare-speeltuin/speeltuinvereniging-%c2%93de-oranjetuin%c2%94/,http://playadvisor.b3p.nl/wp-content/uploads/2016/04/001-1.jpg|http://playadvisor.b3p.nl/wp-content/uploads/2016/04/001-1.jpg,001.jpg|001.jpg,|,|,|,,\"Speeltuinen>Openbare speeltuin\",Nederland,Barendrecht,,Bankje|Toilet|Verschoontafel,,,,,5,4.5561209321022,51.8490438089326,\"a:1:{i:0;a:2:{s:13:\"\"attachment_id\"\";i:30309;s:3:\"\"url\"\";s:87:\"\"https://playadvisor.co/wp-content/uploads/2016/04/speeltuinverenigingdeoranjetuin-0.jpg\"\";}}\",,,,,,publish,1,speeltuinvereniging-%c2%93de-oranjetuin%c2%94,,,0,0,0,open,open,,";
        String [] tokens = s.split(",");
        String [] header = headerString.split(",");
        instance.init(header);
        Map<String, Object> m = instance.parseRecord(tokens);
        assertNotNull(m);
        assertEquals(12, m.keySet().size());
        for (String key : m.keySet()) {
            assertNotNull(m.get(key));
        }
    }
    
}
