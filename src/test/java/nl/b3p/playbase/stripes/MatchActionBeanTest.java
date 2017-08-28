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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import javax.naming.NamingException;
import nl.b3p.commons.csv.CsvFormatException;
import nl.b3p.playbase.ImportReport;
import nl.b3p.playbase.PlayadvisorImporter;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.db.TestUtil;
import nl.b3p.playbase.entities.Location;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author Meine Toonen
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DB.class)
public class MatchActionBeanTest extends TestUtil{
    
    private MatchActionBean instance;
    private static final Log log = LogFactory.getLog(MatchActionBeanTest.class);

    private final Integer playmappingId = 42488; // Beltmolen
    private final Integer playadvisorId = 1; //Speeltuin Assendorp
    
    public MatchActionBeanTest() {
        useDB = true;
        initData = true;
        //initAssetsData = true;
        instance = new MatchActionBean();
    }
    
    //@Test
    public void testMerge() throws NamingException, SQLException{
        List origlocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE, new ArrayListHandler());
        int origSize = origlocations.size();
        List palocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE + "_playadvisor", new ArrayListHandler());
        assertEquals(1, palocations.size());
        
        mergeLocations();
        
        origlocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE, new ArrayListHandler());
        assertEquals(origSize, origlocations.size());
        
        palocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE + "_playadvisor", new ArrayListHandler());
        assertEquals(0, palocations.size());
    }
    
    //@Test
    public void testAdd() throws NamingException, SQLException{
        
        List origlocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE, new ArrayListHandler());
        int origSize = origlocations.size();
        
        List palocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE + "_playadvisor", new ArrayListHandler());
        assertEquals(1, palocations.size());
        
        addLocations();
        
        origlocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE, new ArrayListHandler());
        assertEquals(origSize + 1, origlocations.size());
        
        palocations = DB.qr().query("Select * from " + DB.LOCATION_TABLE + "_playadvisor", new ArrayListHandler());
        assertEquals(0, palocations.size());
    }

    //@Test
    public void testFieldsAfterMerge() throws NamingException, SQLException {
        mergeLocations();
        Location pl = DB.qr().query("select * from " + DB.LOCATION_TABLE + " where id = ?", handler, playmappingId);

        assertEquals("Speeltuin Assendorp", pl.getTitle());
    }
    
    //@Test
    public void testImagesAfterMerge()throws NamingException, SQLException {
        List<Object[]> imagesBefore = DB.qr().query("select * from " + DB.IMAGES_TABLE + " where location = ?", new ArrayListHandler(), playmappingId);
        int size = imagesBefore.size();
        mergeLocations();
        
        List<Object[]> images = DB.qr().query("select * from " + DB.IMAGES_TABLE + " where location = ?", new ArrayListHandler(), playmappingId);
        assertEquals(size + 3, images.size());
        
        
        List<Object[]> imagesPa = DB.qr().query("select * from " + DB.IMAGES_TABLE + "_playadvisor where id = ?", new ArrayListHandler(), playadvisorId);
        assertEquals(0, imagesPa.size());
    }
    
    //@Test
    public void testImagesAfterAdd()throws NamingException, SQLException {
        List<Object[]> imagesBefore = DB.qr().query("select * from " + DB.IMAGES_TABLE , new ArrayListHandler());
        int size = imagesBefore.size();
        addLocations();
        
        List<Object[]> images = DB.qr().query("select * from " + DB.IMAGES_TABLE, new ArrayListHandler());
        assertEquals(size + 3, images.size());
        
        
        List<Object[]> imagesPa = DB.qr().query("select * from " + DB.IMAGES_TABLE + "_playadvisor where id = ?", new ArrayListHandler(), playadvisorId);
        assertEquals(0, imagesPa.size());
    }
    
    //@Test
    public void testFacilitiesAfterMerge()throws NamingException, SQLException {
        List<Object[]> facilitiesBefore = DB.qr().query("select * from " + DB.LOCATION_FACILITIES_TABLE + " where location = ?", new ArrayListHandler(), playmappingId);
        int size = facilitiesBefore.size();
        mergeLocations();
        
        List<Object[]> facilities = DB.qr().query("select * from " + DB.LOCATION_FACILITIES_TABLE + " where location = ?", new ArrayListHandler(), playmappingId);
        assertEquals(size + 2, facilities.size());
        
        
        List<Object[]> facPa = DB.qr().query("select * from " + DB.LOCATION_FACILITIES_TABLE + "_playadvisor where location = ?", new ArrayListHandler(), playadvisorId);
        assertEquals(0, facPa.size());
    }
    
    //@Test
    public void testFacilitiesAfterAdd()throws NamingException, SQLException {
        List<Object[]> imagesBefore = DB.qr().query("select * from " + DB.LOCATION_FACILITIES_TABLE , new ArrayListHandler());
        int size = imagesBefore.size();
        addLocations();
        
        List<Object[]> images = DB.qr().query("select * from " + DB.LOCATION_FACILITIES_TABLE, new ArrayListHandler());
        assertEquals(size + 2, images.size());
        
        
        List<Object[]> imagesPa = DB.qr().query("select * from " + DB.LOCATION_FACILITIES_TABLE + "_playadvisor where location = ?", new ArrayListHandler(), playadvisorId);
        assertEquals(0, imagesPa.size());
    }

    //@Test
    public void testAccessibilitiesAfterMerge()throws NamingException, SQLException {
        List<Object[]> facilitiesBefore = DB.qr().query("select * from " + DB.LOCATION_ACCESSIBILITY_TABLE + " where location = ?", new ArrayListHandler(), playmappingId);
        int size = facilitiesBefore.size();
        mergeLocations();
        
        List<Object[]> facilities = DB.qr().query("select * from " + DB.LOCATION_ACCESSIBILITY_TABLE + " where location = ?", new ArrayListHandler(), playmappingId);
        assertEquals(size + 1, facilities.size());
        
        
        List<Object[]> facPa = DB.qr().query("select * from " + DB.LOCATION_ACCESSIBILITY_TABLE + "_playadvisor where location = ?", new ArrayListHandler(), playadvisorId);
        assertEquals(0, facPa.size());
    }
    
    //@Test
    public void testAccessibilitiesAfterAdd()throws NamingException, SQLException {
        List<Object[]> imagesBefore = DB.qr().query("select * from " + DB.LOCATION_ACCESSIBILITY_TABLE , new ArrayListHandler());
        int size = imagesBefore.size();
        addLocations();
        
        List<Object[]> images = DB.qr().query("select * from " + DB.LOCATION_ACCESSIBILITY_TABLE, new ArrayListHandler());
        assertEquals(size + 1, images.size());
        
        
        List<Object[]> imagesPa = DB.qr().query("select * from " + DB.LOCATION_ACCESSIBILITY_TABLE + "_playadvisor where location = ?", new ArrayListHandler(), playadvisorId);
        assertEquals(0, imagesPa.size());
    }

    //@Test
    public void testLocationAgecategoriesAfterMerge()throws NamingException, SQLException {
        List<Object[]> facilitiesBefore = DB.qr().query("select * from " + DB.LOCATION_AGE_CATEGORY_TABLE + " where location = ?", new ArrayListHandler(), playmappingId);
        int size = facilitiesBefore.size();
        mergeLocations();
        
        List<Object[]> facilities = DB.qr().query("select * from " + DB.LOCATION_AGE_CATEGORY_TABLE + " where location = ?", new ArrayListHandler(), playmappingId);
        assertEquals(size + 2, facilities.size());
        
        
        List<Object[]> facPa = DB.qr().query("select * from " + DB.LOCATION_AGE_CATEGORY_TABLE + "_playadvisor where location = ?", new ArrayListHandler(), playadvisorId);
        assertEquals(0, facPa.size());
    }
    
    //@Test
    public void testLocationAgecategoriesAfterAdd()throws NamingException, SQLException {
        List<Object[]> imagesBefore = DB.qr().query("select * from " + DB.LOCATION_AGE_CATEGORY_TABLE , new ArrayListHandler());
        int size = imagesBefore.size();
        addLocations();
        
        List<Object[]> images = DB.qr().query("select * from " + DB.LOCATION_AGE_CATEGORY_TABLE, new ArrayListHandler());
        assertEquals(size + 2, images.size());
        
        
        List<Object[]> imagesPa = DB.qr().query("select * from " + DB.LOCATION_AGE_CATEGORY_TABLE + "_playadvisor where location = ?", new ArrayListHandler(), playadvisorId);
        assertEquals(0, imagesPa.size());
    }

    

    //@Test
    public void testLocationCategoriesAfterMerge()throws NamingException, SQLException {
        List<Object[]> facilitiesBefore = DB.qr().query("select * from " + DB.LOCATION_CATEGORY_TABLE + " where location = ?", new ArrayListHandler(), playmappingId);
        int size = facilitiesBefore.size();
        mergeLocations();
        
        List<Object[]> facilities = DB.qr().query("select * from " + DB.LOCATION_CATEGORY_TABLE + " where location = ?", new ArrayListHandler(), playmappingId);
        assertEquals(size + 2, facilities.size());
        
        List<Object[]> facPa = DB.qr().query("select * from " + DB.LOCATION_CATEGORY_TABLE + "_playadvisor where location = ?", new ArrayListHandler(), playadvisorId);
        assertEquals(0, facPa.size());
    }
    
    //@Test
    public void testLocationCategoriesAfterAdd()throws NamingException, SQLException {
        List<Object[]> imagesBefore = DB.qr().query("select * from " + DB.LOCATION_CATEGORY_TABLE , new ArrayListHandler());
        int size = imagesBefore.size();
        addLocations();
        
        List<Object[]> images = DB.qr().query("select * from " + DB.LOCATION_CATEGORY_TABLE, new ArrayListHandler());
        assertEquals(size + 2, images.size());
        
        List<Object[]> imagesPa = DB.qr().query("select * from " + DB.LOCATION_CATEGORY_TABLE + "_playadvisor where location = ?", new ArrayListHandler(), playadvisorId);
        assertEquals(0, imagesPa.size());
    }

    //@Test
    public void testLocationEquipmentAfterMerge()throws NamingException, SQLException {
        List<Object[]> facilitiesBefore = DB.qr().query("select * from " + DB.ASSETS_TABLE + " where location = ?", new ArrayListHandler(), playmappingId);
        int size = facilitiesBefore.size();
        mergeLocations();
        
        List<Object[]> facilities = DB.qr().query("select * from " + DB.ASSETS_TABLE + " where location = ?", new ArrayListHandler(), playmappingId);
        assertEquals(size + 1, facilities.size());
        
        List<Object[]> facPa = DB.qr().query("select * from " + DB.ASSETS_TABLE + "_playadvisor where location = ?", new ArrayListHandler(), playadvisorId);
        assertEquals(0, facPa.size());
    }
    
    //@Test
    public void testLocationEquipmentAfterAdd()throws NamingException, SQLException {
        List<Object[]> imagesBefore = DB.qr().query("select * from " + DB.ASSETS_TABLE , new ArrayListHandler());
        int size = imagesBefore.size();
        addLocations();
        
        List<Object[]> images = DB.qr().query("select * from " + DB.ASSETS_TABLE, new ArrayListHandler());
        assertEquals(size + 1, images.size());
        
        List<Object[]> imagesPa = DB.qr().query("select * from " + DB.ASSETS_TABLE + "_playadvisor where location = ?", new ArrayListHandler(), playadvisorId);
        assertEquals(0, imagesPa.size());
    }
    

    //@Test
    public void testLocationEquipmentAgecategoriesAfterMerge()throws NamingException, SQLException {
        List<Object[]> facilitiesBefore = DB.qr().query("select * from " + DB.ASSETS_AGECATEGORIES_TABLE , new ArrayListHandler());
        int size = facilitiesBefore.size();
        mergeLocations();
        
        List<Object[]> facilities = DB.qr().query("select * from " + DB.ASSETS_AGECATEGORIES_TABLE , new ArrayListHandler());
        assertEquals(size + 1, facilities.size());
        
        List<Object[]> facPa = DB.qr().query("select * from " + DB.ASSETS_AGECATEGORIES_TABLE + "_playadvisor", new ArrayListHandler());
        assertEquals(0, facPa.size());
    }

    private void mergeLocations() {
        instance.setPlayadvisorId(playadvisorId);
        instance.setPlaymappingId(playmappingId);
        instance.setMethod("merge");

        instance.save();
    }
    
    

    private void addLocations() {
        instance.setPlayadvisorId(playadvisorId);
        instance.setPlaymappingId(null);
        instance.setMethod("add");

        instance.save();
    }



    @Test
    public void testUpdateMergePlayadvisorRecord() throws NamingException, SQLException, IOException, CsvFormatException{
        // merge 2 records
        mergeLocations();
        //Location pl = DB.qr().query("select * from " + DB.LOCATION_TABLE + " where id = ?", handler, playmappingId);
    
        // update playadvisor record
        String updateString = "93744,\"Speeltuin tAssendorp\",\"<p class=\"\"MsoNormal\"\">Speeltuin Assendorp is Ambassadeur Samen Spelen van de NSGKSpeeltuinbende. Dit betekent dat alle kinderen, met én zonder handicap, hier heerlijk samen kunnen spelen en dat despeeltuin allerlei activiteiten ontplooit om het samen spelen te bevorderen.</p> <p class=\"\"MsoNormal\"\">De speeltuin heeft een goed aangepast toilet met stevigeverschoontafel. Er is een ruime kantine waar bij nat weer geschuild kan worden.Er loopt een goed berijdbaar én zichtbaar pad door de hele speeltuin. Er is éénin- uitgang, op strategische plekken staan bankjes. Natuurlijk zijn demedewerkers heel gastvrij en daardoor voelt iedereen zich welkom.</p> <p class=\"\"MsoNormal\"\">Samenspeeltoppers zijn het springkussen, de spannendewilgentunnel, het kasteel van de zwarte panter met haar kruip door sluipdoor enstrategisch uitzicht<a name=\"\"_GoBack\"\"></a>, de heerlijke schommels en nog veelmeer.</p> Maandag t/m vrijdag: 14.00u - 17.30u Zaterdag: 14.00u - 17.00u Zon- en feestdagen: gesloten\",,2016-04-21,speelplek,http://playadvisor.b3p.nl/speelplek/openbare-speeltuin/speeltuin-assendorp/,http://playadvisor.b3p.nl/wp-content/uploads/2016/04/001-2950.jpg,001.jpg,,,,,\"Speeltuinen>Openbare speeltuin\",Nederland,Zwolle,,,,,\"Inclusive playground|Invalidentoilet|Samenspeelplek\",,0,6.1101436,52.5003761,\"a:1:{i:0;a:2:{s:13:\"\"attachment_id\"\";i:93665;s:3:\"\"url\"\";s:68:\"\"//playadvisor.co/wp-content/uploads/2016/04/speeltuinassendorp-0.jpg\"\";}}\",,,,,,publish,279,speeltuin-assendorp,,,0,0,0,open,open,,";
        
                
        PlayadvisorImporter painstance = new PlayadvisorImporter();

        painstance.importStream(new ByteArrayInputStream(updateString.getBytes(StandardCharsets.UTF_8)), new ImportReport());
        
        Location pl = DB.qr().query("select * from " + DB.LOCATION_TABLE + " where id = ?", handler, playmappingId);
        assertEquals("Speeltuin tAssendorp", pl.getTitle() );

    }    
}
