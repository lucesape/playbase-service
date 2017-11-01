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

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import nl.b3p.playbase.ImportReport.ImportType;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.db.TestUtil;
import nl.b3p.playbase.entities.Asset;
import nl.b3p.playbase.entities.Location;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author meine
 */
public class PlaymappingImporterTest extends TestUtil {

    public PlaymappingImporterTest() {
        this.useDB = true;
        this.initData = true;
    }

    public PlaymappingImporter instance;
    private ImportReport report;

    @Before
    public void beforeTest() {
        instance = new PlaymappingImporter();
        instance.init();
        report = new ImportReport();
    }

    @Test
    public void testGetChildLocation3Levels() throws IOException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("pijnacker3Locations.json");
        String location = IOUtils.toString(in);
        List<Location> returnValue = instance.parseChildLocations(location);
        assertEquals(195, returnValue.size());
    }

    @Test
    public void testGetChildLocation2Levels() throws IOException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("haarlemLocations.json");
        String location = IOUtils.toString(in);
        List<Location> returnValue = instance.parseChildLocations(location);
        assertEquals(273, returnValue.size());
    }

    @Test
    public void testParseLocation() {
        String loc = "{\"$id\": \"3\",\"ID\": \"98d66e1b-e2eb-44ea-ab1b-ce5999cb4309\",\"LastUpdated\": \"2015-01-08T11:18:10.613\",\"Name\": \"C117/1009 Wilsonplein 8\",\"AddressLine1\": \"Wilsonplein 8\\r\\nHaarlem\",\"Suburb\": \"\",\"City\": \"\",\"Area\": \"\",\"PostCode\": \"\",\"Ref\": \"C117/1009\",\"AssetCount\": 14,\"Lat\": \"52,37759\",\"Lng\": \"4,627348\",\"ChildLocations\": [],\"Images\": [  {    \"$id\": \"4\",    \"ID\": \"33ec50eb-eec8-452a-bf7c-1b54ca7543f8\",    \"LastUpdated\": \"2012-08-02T07:44:06\",    \"URI\": \"http://www.playmapping.com/GetImage.ashx?g=33ec50eb-eec8-452a-bf7c-1b54ca7543f8&w=350&h=350\",    \"Description\": \"\"  },  {    \"$id\": \"5\",    \"ID\": \"8af2bd67-5178-405f-9a97-444133bf370d\",    \"LastUpdated\": \"2012-08-02T07:44:37\",    \"URI\": \"http://www.playmapping.com/GetImage.ashx?g=8af2bd67-5178-405f-9a97-444133bf370d&w=350&h=350\",    \"Description\": \"\"  }],\"Documents\": []}";
        JSONObject location = new JSONObject(loc);
        Location real = instance.parseLocation(location);
        assertEquals("98d66e1b-e2eb-44ea-ab1b-ce5999cb4309", real.getPm_guid());
        //assertEquals("2015-01-08T11:18:10.613", real.get("LastUpdated"));
        assertEquals("C117/1009 Wilsonplein 8", real.getTitle());
        assertEquals(52.37759, real.getLatitude(), 0.01);
        assertEquals(4.627348, real.getLongitude(), 0.01);
        assertEquals("Wilsonplein 8\r\nHaarlem", real.getStreet());
        //assertEquals("", real.get("Suburb"));
        assertEquals("", real.getMunicipality());
        assertEquals("", real.getArea());
        assertEquals("", real.getPostalcode());
        ////assertEquals("C117/1009", real.get("Ref"));
        //assertEquals(14, real.get("AssetCount"));
        //assertEquals(0, ((JSONArray) real.get("ChildLocations")).length());
        assertEquals(0, ((List) real.getDocuments()).size());
        assertEquals(2, ((List) real.getImages()).size());
        assertEquals("http://www.playmapping.com/GetImage.ashx?g=33ec50eb-eec8-452a-bf7c-1b54ca7543f8", real.getImages().get(0).get("URI"));
        assertEquals("http://www.playmapping.com/GetImage.ashx?g=8af2bd67-5178-405f-9a97-444133bf370d", real.getImages().get(1).get("URI"));
    }

    @Test
    public void testParseAssets() throws IOException, NamingException, SQLException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("haarlemAssets.json");
        Map<Integer,Set<Integer>> assetType = new HashMap<>();
        String location = IOUtils.toString(in);
        List<Asset> returnValue = instance.parseAssets(location, assetType);
        assertEquals(3479, returnValue.size());
    }

    @Test
    public void testParseAsset() {
        String assetString = "{\"$id\": \"1\",\"ID\": \"dc5d0399-ccc4-4c22-9ab0-2a7b9ff80a19\",\"LocationID\": \"0d415c38-ca18-4d67-a716-00df56df8736\",\"LocationName\": \"Centrum\\\\C136/1005 Vroonhof 1\",\"LastUpdated\": \"2013-01-29T11:24:59.25\",\"Name\": \"Motorfiets\",\"AssetType\": \"Wiptoestellen/Type 2A - Enkelpunts - 1 richting\",\"Manufacturer\": \"KOMPAN A/S\",\"Product\": \"MOMENTS\\\\M130P\",\"SerialNumber\": \"04\",\"Material\": \"\",\"InstalledDate\": \"1988-01-01\",\"EndOfLifeYear\": -1,\"ProductID\": \"eeb6cc3b-0f77-40ac-8926-09588625244d\",\"ProductVariantID\": \"0ff2d747-6634-4ac5-861d-697ab4d80762\",\"Height\": 820,\"Depth\": 900,\"Width\": 360,\"FreefallHeight\": 560,\"SafetyZoneLength\": 3500,\"SafetyZoneWidth\": 2360,\"AgeGroupToddlers\": true,\"AgeGroupJuniors\": false,\"AgeGroupSeniors\": false,\"PricePurchase\": 700.0,\"PriceInstallation\": -1.0,\"PriceReInvestment\": -1.0,\"PriceMaintenance\": -1.0,\"PriceIndexation\": -1.0,\"Lat\": \"52,38138\",\"Lng\": \"4,641622\",\"Images\": [{\"$id\": \"3\",\"ID\": \"7f8ac724-1821-4bee-8f34-f8894deb5cac\",\"LastUpdated\": \"2010-07-06T18:15:08.453\",\"URI\": \"http://www.playmapping.com/GetImage.ashx?g=7f8ac724-1821-4bee-8f34-f8894deb5cac&w=350&h=350\",\"Description\": \"\"}],\"Documents\": [],\"Hyperlinks\": [],\"LinkedAssets\": []}";
        JSONObject assetJSON = new JSONObject(assetString);
        Map<Integer,Set<Integer>> assetType = new HashMap<>();
        Asset map = instance.parseAsset(assetJSON, 3882, assetType);
        assertEquals("dc5d0399-ccc4-4c22-9ab0-2a7b9ff80a19", map.getPm_guid());
        //assertEquals("0d415c38-ca18-4d67-a716-00df56df8736", map.get("LocationPMID"));
        //assertEquals("Centrum\\C136/1005 Vroonhof 1", map.get("LocationName"));
        //assertEquals("2013-01-29T11:24:59.25", map.getL("LastUpdated"));
        assertEquals("Motorfiets", map.getName());
        assertEquals(183, (int)map.getType_());
        assertEquals("KOMPAN A/S", map.getManufacturer());
        assertEquals("MOMENTS\\M130P", map.getProduct());
        assertEquals("04", map.getSerialnumber());
        assertEquals("", map.getMaterial());
        assertEquals("1988-01-01", map.getInstalleddate());
        assertEquals(-1, (int)map.getEndoflifeyear());
        assertEquals("eeb6cc3b-0f77-40ac-8926-09588625244d", map.getProductid());
        assertEquals("0ff2d747-6634-4ac5-861d-697ab4d80762", map.getProductvariantid());
        assertEquals(820, (int)map.getHeight());
        assertEquals(900, (int)map.getDepth());
        assertEquals(360, (int)map.getWidth());
        assertEquals(560, (int)map.getFreefallheight());
        assertEquals(3500, (int)map.getSafetyzonelength());
        assertEquals(2360, (int)map.getSafetyzonewidth());
        assertEquals(1, map.getAgecategories().length);
        assertEquals(1, (int)map.getAgecategories()[0]);
        assertEquals(700.0, (int)map.getPricepurchase(), 0.01);
        assertEquals(-1.0, (int)map.getPriceinstallation(), 0.01);
        assertEquals(-1.0, (int)map.getPricereinvestment(), 0.01);
        assertEquals(-1.0, (int)map.getPricemaintenance(), 0.01);
        assertEquals(-1.0,(int)map.getPriceindexation(), 0.01);
        assertEquals(52.38138, map.getLatitude(), 0.01);
        assertEquals(4.641622, map.getLongitude(), 0.01);
        assertEquals(0, map.getDocuments().size());
        assertEquals(1, map.getImages().size());
        List<Map<String,Object>> images = map.getImages();
        Map<String,Object> image = images.get(0);
        assertNotNull(image.get("LastUpdated"));
        Date d = (Date)image.get("LastUpdated");
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        assertEquals(2010,cal.get(Calendar.YEAR));
        assertEquals(6,cal.get(Calendar.MONTH));
        assertEquals(6,cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(18,cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(15,cal.get(Calendar.MINUTE));
        
        //assertEquals(0, ((JSONArray) map.get("Hyperlinks")).length());
    }

    @Test
    public void testParseAssetWithLinkedAsset() throws IOException, NamingException, SQLException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("singleAssetWithLinked.json");
        String asset = IOUtils.toString(in);
        Map<Integer,Set<Integer>> assetType = new HashMap<>();
        List<Asset> returnValue = instance.parseAssets(asset, assetType);
        assertEquals(2, returnValue.size());

    }

    @Test
    public void testSaveAssets() throws IOException, NamingException, SQLException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("singleAssetWithLinked.json");
        String asset = IOUtils.toString(in);
        instance.processAssets(asset, report);
        
        assertEquals(0, report.getErrors().size());
        assertEquals(2, report.getNumberInserted(ImportType.ASSET));
        assertEquals(0, report.getNumberInserted(ImportType.LOCATION));
        assertEquals(0, report.getNumberUpdated(ImportType.ASSET));
        assertEquals(0, report.getNumberUpdated(ImportType.LOCATION));
        
        List<Object[]> assets = DB.qr().query("select id, equipment from " + DB.ASSETS_TABLE, new ArrayListHandler());
        assertEquals(2, assets.size());
    }
    
    @Test
    public void testSaveAssetType() throws IOException, NamingException, SQLException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("singleAssetWithLinked.json");
        String assetString = IOUtils.toString(in);
        instance.processAssets(assetString, report);
        
        assertEquals(0, report.getErrors().size());
        assertEquals(2, report.getNumberInserted(ImportType.ASSET));
        assertEquals(0, report.getNumberInserted(ImportType.LOCATION));
        assertEquals(0, report.getNumberUpdated(ImportType.ASSET));
        assertEquals(0, report.getNumberUpdated(ImportType.LOCATION));
        
        List<Object[]> assets = DB.qr().query("select location, category from " + DB.LOCATION_CATEGORY_TABLE, new ArrayListHandler());
        assertEquals(0, assets.size());
        for (Object[] asset : assets) {
            assertNotNull("equipmenttype not set.", asset[1]);
        }
    }

    //@Test
    public void testUpdateAssets() throws IOException, NamingException, SQLException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("singleAssetWithLinked.json");
        String asset = IOUtils.toString(in);
        
        
        instance.processAssets(asset, report);
        
        assertEquals(0, report.getErrors().size());
        assertEquals(2, report.getNumberInserted(ImportType.ASSET));
        assertEquals(0, report.getNumberInserted(ImportType.LOCATION));
        assertEquals(0, report.getNumberUpdated(ImportType.ASSET));
        assertEquals(0, report.getNumberUpdated(ImportType.LOCATION));
        report = new ImportReport();
        instance.processAssets(asset, report);
        
        assertEquals(0, report.getErrors().size());
        assertEquals(0, report.getNumberInserted(ImportType.ASSET));
        assertEquals(0, report.getNumberInserted(ImportType.LOCATION));
        assertEquals(2, report.getNumberUpdated(ImportType.ASSET));
        assertEquals(0, report.getNumberUpdated(ImportType.LOCATION));
    }
}
