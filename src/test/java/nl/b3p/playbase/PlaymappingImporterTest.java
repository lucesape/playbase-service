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
import nl.b3p.playbase.entities.Project;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
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
        instance = new PlaymappingImporter(new Project("test"));
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
        List<Asset> returnValue = instance.parseAssets(new JSONArray(location), assetType);
        assertEquals(1029, returnValue.size());
    }

    @Test
    public void testParseAsset() {
        String assetString = "{\"$id\": \"1\",\"AgeGroupJuniors\": true,\"AgeGroupSeniors\": true,\"AgeGroupToddlers\": true,\"AssetType\": {\"$id\": \"6\",\"FullName\": \"Speeltoestellen/Sporttoestellen/Doel\",\"ID\": \"604af399-9067-4529-8e4c-016efbd4717a\",\"Name\": \"Doel\",\"ParentID\": \"1f60c63b-14cf-4fd8-9142-0e9ae4809863\"},\"ContactCertifier\": null,\"ContactInstaller\": null,\"ContactSupplier\": null,\"CustomProperties\": [],\"Depth\": 3200,\"Documents\": [],\"EndOfLifeYear\": -1,\"FreefallHeight\": -1,\"Height\": 3700,\"Hyperlinks\": [],\"ID\": \"b0cf9c08-d32c-46aa-902b-12f66e1a5c0b\",\"Images\": [],\"InstalledDate\": \"\",\"Lat\": \"0\",\"LinkedAssets\": [],\"Lng\": \"0\",\"Location\": {\"$id\": \"2\",\"AddressLine1\": \"Aambeeldstraat\",\"Area\": \"\",\"AssetCount\": 17,\"ChildLocations\": [],\"City\": \"Mechelen\",\"ContactEmail\": \"\",\"ContactName\": \"\",\"ContactPhone\": \"\",\"Documents\": [],\"ID\": \"12205c77-b489-44fe-b319-4dc23dba8f6e\",\"Images\": [{\"$id\": \"3\",\"Description\": \"\",\"ID\": \"fbee55c6-7eaa-47f7-b294-215d204d5d89\",\"URI\": \"http://www.playmapping.com/GetImage.ashx?g=fbee55c6-7eaa-47f7-b294-215d204d5d89&w=350&h=350\"},{\"$id\": \"4\",\"Description\": \"\",\"ID\": \"d261ad1b-5fd5-436c-b66d-a547a8c34f7a\",\"URI\": \"http://www.playmapping.com/GetImage.ashx?g=d261ad1b-5fd5-436c-b66d-a547a8c34f7a&w=350&h=350\"},{\"$id\": \"5\",\"Description\": \"\",\"ID\": \"310488be-5e7b-47e9-bc9a-ac5850ab9dd8\",\"URI\": \"http://www.playmapping.com/GetImage.ashx?g=310488be-5e7b-47e9-bc9a-ac5850ab9dd8&w=350&h=350\"}],\"Lat\": \"0\",\"Lng\": \"0\",\"Name\": \"Aambeeldstraat\",\"Notes\": \"\",\"ParentLocationID\": \"f51c5944-1ba8-4ab6-8115-6658da380fba\",\"PostCode\": \"\",\"Ref\": \"AAM\",\"Suburb\": \"Arsenaal\"},\"Manufacturer\": {\"$id\": \"7\",\"ID\": \"a48b530d-419a-4b38-a00b-79495daaa0d7\",\"Name\": \"Boerplay\"},\"Material\": null,\"Name\": \"Basketbalpaal 1 en doel\",\"PriceIndexation\": -1,\"PriceInstallation\": -1,\"PriceMaintenance\": -1,\"PricePurchase\": -1,\"PriceReInvestment\": -1,\"Product\": {\"$id\": \"8\",\"AssetType\": \"Speeltoestellen/Sporttoestellen/Doel\",\"Description\": \"\",\"ID\": \"5fa2189b-bb75-4545-8f0e-140d4ce3b5ab\",\"ImageURL\": \"http://www.playmapping.com/getimage.ashx?g=e5e01363-b53c-4333-981c-82fe3ac87d1d\",\"ProductGroup\": {\"$id\": \"9\",\"ID\": \"453a76af-00e7-4f4c-9708-aa4fde71bca0\",\"Manufacturer\": {\"$id\": \"10\",\"ID\": \"a48b530d-419a-4b38-a00b-79495daaa0d7\",\"Name\": \"Boerplay\"},\"Name\": \"Sporttoestellen\"},\"ProductNumber\": \"SPT.230.038\",\"SerialNumber\": \"\"},\"ProductVariant\": null,\"SafetyZoneLength\": -1,\"SafetyZoneWidth\": -1,\"SerialNumber\": \"\",\"Width\": 3300}";
        JSONObject assetJSON = new JSONObject(assetString);
        Map<Integer,Set<Integer>> assetType = new HashMap<>();
        Asset map = instance.parseAsset(assetJSON, 3882, assetType);
        assertEquals("b0cf9c08-d32c-46aa-902b-12f66e1a5c0b", map.getPm_guid());
        //assertEquals("0d415c38-ca18-4d67-a716-00df56df8736", map.get("LocationPMID"));
        //assertEquals("Centrum\\C136/1005 Vroonhof 1", map.get("LocationName"));
        //assertEquals("2013-01-29T11:24:59.25", map.getL("LastUpdated"));
        assertEquals("Basketbalpaal 1 en doel", map.getName());
        assertEquals(null, map.getType_());// foutieve typering: Speeltoestellen/Sporttoestellen/Doel bestaat niet. Sporttoestellen/Doel wel...
        assertEquals("Boerplay", map.getManufacturer());
        assertEquals("SPT.230.038", map.getProduct());
        assertEquals("", map.getSerialnumber());
        assertEquals(null, map.getMaterial());
        assertEquals("", map.getInstalleddate());
        assertEquals(-1, (int)map.getEndoflifeyear());
        assertEquals("5fa2189b-bb75-4545-8f0e-140d4ce3b5ab", map.getProductid());
        assertEquals("453a76af-00e7-4f4c-9708-aa4fde71bca0", map.getProductvariantid());
        assertEquals(3700, (int)map.getHeight());
        assertEquals(3200, (int)map.getDepth());
        assertEquals(3300, (int)map.getWidth());
        assertEquals(-1, (int)map.getFreefallheight());
        assertEquals(-1, (int)map.getSafetyzonelength());
        assertEquals(-1, (int)map.getSafetyzonewidth());
        assertEquals(5, map.getAgecategories().length);
        assertEquals(1, (int)map.getAgecategories()[0]);
        assertEquals(-1, (int)map.getPricepurchase(), 0.01);
        assertEquals(-1.0, (int)map.getPriceinstallation(), 0.01);
        assertEquals(-1.0, (int)map.getPricereinvestment(), 0.01);
        assertEquals(-1.0, (int)map.getPricemaintenance(), 0.01);
        assertEquals(-1.0,(int)map.getPriceindexation(), 0.01);
        assertEquals(0, map.getLatitude(), 0.01);
        assertEquals(0, map.getLongitude(), 0.01);
        assertEquals(0, map.getDocuments().size());
        assertEquals(0, map.getImages().size());
        /*List<Map<String,Object>> images = map.getImages();
        Map<String,Object> image = images.get(0);
        assertNotNull(image.get("LastUpdated"));
        Date d = (Date)image.get("LastUpdated");
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        assertEquals(2010,cal.get(Calendar.YEAR));
        assertEquals(6,cal.get(Calendar.MONTH));
        assertEquals(6,cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(18,cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(15,cal.get(Calendar.MINUTE));*/
        
        //assertEquals(0, ((JSONArray) map.get("Hyperlinks")).length());
    }

    @Test
    public void testParseAssetWithLinkedAsset() throws IOException, NamingException, SQLException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("singleAssetWithLinked.json");
        String asset = IOUtils.toString(in);
        Map<Integer,Set<Integer>> assetType = new HashMap<>();
        List<Asset> returnValue = instance.parseAssets(new JSONArray(asset), assetType);
        assertEquals(2, returnValue.size());

    }

    @Test
    public void testSaveAssets() throws IOException, NamingException, SQLException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("singleAssetWithLinked.json");
        String asset = IOUtils.toString(in);
        instance.processAssets(new JSONArray(asset), report);
        
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
        instance.processAssets(new JSONArray(assetString), report);
        
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
        
        
        instance.processAssets(new JSONArray(asset), report);
        
        assertEquals(0, report.getErrors().size());
        assertEquals(2, report.getNumberInserted(ImportType.ASSET));
        assertEquals(0, report.getNumberInserted(ImportType.LOCATION));
        assertEquals(0, report.getNumberUpdated(ImportType.ASSET));
        assertEquals(0, report.getNumberUpdated(ImportType.LOCATION));
        report = new ImportReport();
        instance.processAssets(new JSONArray(asset), report);
        
        assertEquals(0, report.getErrors().size());
        assertEquals(0, report.getNumberInserted(ImportType.ASSET));
        assertEquals(0, report.getNumberInserted(ImportType.LOCATION));
        assertEquals(2, report.getNumberUpdated(ImportType.ASSET));
        assertEquals(0, report.getNumberUpdated(ImportType.LOCATION));
    }
}
