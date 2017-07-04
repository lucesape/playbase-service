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
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.db.TestUtil;
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

    @Before
    public void beforeTest() {
        instance = new PlaymappingImporter();
        instance.init();
    }

    @Test
    public void testGetChildLocation3Levels() throws IOException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("pijnacker3Locations.json");
        String location = IOUtils.toString(in);
        List<Map<String, Object>> returnValue = instance.parseChildLocations(location);
        assertEquals(195, returnValue.size());
    }

    @Test
    public void testGetChildLocation2Levels() throws IOException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("haarlemLocations.json");
        String location = IOUtils.toString(in);
        List<Map<String, Object>> returnValue = instance.parseChildLocations(location);
        assertEquals(273, returnValue.size());
    }

    @Test
    public void testParseLocation() {
        String loc = "{\"$id\": \"3\",\"ID\": \"98d66e1b-e2eb-44ea-ab1b-ce5999cb4309\",\"LastUpdated\": \"2015-01-08T11:18:10.613\",\"Name\": \"C117/1009 Wilsonplein 8\",\"AddressLine1\": \"Wilsonplein 8\\r\\nHaarlem\",\"Suburb\": \"\",\"City\": \"\",\"Area\": \"\",\"PostCode\": \"\",\"Ref\": \"C117/1009\",\"AssetCount\": 14,\"Lat\": \"52,37759\",\"Lng\": \"4,627348\",\"ChildLocations\": [],\"Images\": [  {    \"$id\": \"4\",    \"ID\": \"33ec50eb-eec8-452a-bf7c-1b54ca7543f8\",    \"LastUpdated\": \"2012-08-02T07:44:06\",    \"URI\": \"http://www.playmapping.com/GetImage.ashx?g=33ec50eb-eec8-452a-bf7c-1b54ca7543f8&w=350&h=350\",    \"Description\": \"\"  },  {    \"$id\": \"5\",    \"ID\": \"8af2bd67-5178-405f-9a97-444133bf370d\",    \"LastUpdated\": \"2012-08-02T07:44:37\",    \"URI\": \"http://www.playmapping.com/GetImage.ashx?g=8af2bd67-5178-405f-9a97-444133bf370d&w=350&h=350\",    \"Description\": \"\"  }],\"Documents\": []}";
        JSONObject location = new JSONObject(loc);
        Map<String, Object> real = instance.parseLocation(location);
        assertEquals("3", real.get("$id"));
        assertEquals("98d66e1b-e2eb-44ea-ab1b-ce5999cb4309", real.get("ID"));
        assertEquals("2015-01-08T11:18:10.613", real.get("LastUpdated"));
        assertEquals("C117/1009 Wilsonplein 8", real.get("Name"));
        assertEquals(52.37759, real.get("Lat"));
        assertEquals(4.627348, real.get("Lng"));
        assertEquals("Wilsonplein 8\r\nHaarlem", real.get("AddressLine1"));
        assertEquals("", real.get("Suburb"));
        assertEquals("", real.get("City"));
        assertEquals("", real.get("Area"));
        assertEquals("", real.get("PostCode"));
        assertEquals("C117/1009", real.get("Ref"));
        assertEquals(14, real.get("AssetCount"));
        assertEquals(0, ((JSONArray) real.get("ChildLocations")).length());
        assertEquals(0, ((List) real.get("Documents")).size());
        assertEquals(2, ((List) real.get("Images")).size());
    }

    @Test
    public void testParseAssets() throws IOException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("haarlemAssets.json");
        String location = IOUtils.toString(in);
        List<Map<String, Object>> returnValue = instance.parseAssets(location);
        assertEquals(3479, returnValue.size());
    }

    @Test
    public void testParseAsset() {
        String assetString = "{\"$id\": \"1\",\"ID\": \"dc5d0399-ccc4-4c22-9ab0-2a7b9ff80a19\",\"LocationID\": \"0d415c38-ca18-4d67-a716-00df56df8736\",\"LocationName\": \"Centrum\\\\C136/1005 Vroonhof 1\",\"LastUpdated\": \"2013-01-29T11:24:59.25\",\"Name\": \"Motorfiets\",\"AssetType\": \"Wiptoestellen/Type 2A - Enkelpunts - 1 richting\",\"Manufacturer\": \"KOMPAN A/S\",\"Product\": \"MOMENTS\\\\M130P\",\"SerialNumber\": \"04\",\"Material\": \"\",\"InstalledDate\": \"1988-01-01\",\"EndOfLifeYear\": -1,\"ProductID\": \"eeb6cc3b-0f77-40ac-8926-09588625244d\",\"ProductVariantID\": \"0ff2d747-6634-4ac5-861d-697ab4d80762\",\"Height\": 820,\"Depth\": 900,\"Width\": 360,\"FreefallHeight\": 560,\"SafetyZoneLength\": 3500,\"SafetyZoneWidth\": 2360,\"AgeGroupToddlers\": true,\"AgeGroupJuniors\": false,\"AgeGroupSeniors\": false,\"PricePurchase\": 700.0,\"PriceInstallation\": -1.0,\"PriceReInvestment\": -1.0,\"PriceMaintenance\": -1.0,\"PriceIndexation\": -1.0,\"Lat\": \"52,38138\",\"Lng\": \"4,641622\",\"Images\": [{\"$id\": \"3\",\"ID\": \"7f8ac724-1821-4bee-8f34-f8894deb5cac\",\"LastUpdated\": \"2010-07-06T18:15:08.453\",\"URI\": \"http://www.playmapping.com/GetImage.ashx?g=7f8ac724-1821-4bee-8f34-f8894deb5cac&w=350&h=350\",\"Description\": \"\"}],\"Documents\": [],\"Hyperlinks\": [],\"LinkedAssets\": []}";
        JSONObject assetJSON = new JSONObject(assetString);
        Map<String, Object> map = instance.parseAsset(assetJSON);
        assertEquals("1", map.get("$id"));
        assertEquals("dc5d0399-ccc4-4c22-9ab0-2a7b9ff80a19", map.get("ID"));
        assertEquals("0d415c38-ca18-4d67-a716-00df56df8736", map.get("LocationPMID"));
        assertEquals("Centrum\\C136/1005 Vroonhof 1", map.get("LocationName"));
        assertEquals("2013-01-29T11:24:59.25", map.get("LastUpdated"));
        assertEquals("Motorfiets", map.get("Name"));
        assertEquals("Wiptoestellen/Type 2A - Enkelpunts - 1 richting", map.get("AssetType"));
        assertEquals("KOMPAN A/S", map.get("Manufacturer"));
        assertEquals("MOMENTS\\M130P", map.get("Product"));
        assertEquals("04", map.get("SerialNumber"));
        assertEquals("", map.get("Material"));
        assertEquals("1988-01-01", map.get("InstalledDate"));
        assertEquals(-1, map.get("EndOfLifeYear"));
        assertEquals("eeb6cc3b-0f77-40ac-8926-09588625244d", map.get("ProductID"));
        assertEquals("0ff2d747-6634-4ac5-861d-697ab4d80762", map.get("ProductVariantID"));
        assertEquals(820, map.get("Height"));
        assertEquals(900, map.get("Depth"));
        assertEquals(360, map.get("Width"));
        assertEquals(560, map.get("FreefallHeight"));
        assertEquals(3500, map.get("SafetyZoneLength"));
        assertEquals(2360, map.get("SafetyZoneWidth"));
        assertEquals(true, map.get("AgeGroupToddlers"));
        assertEquals(false, map.get("AgeGroupJuniors"));
        assertEquals(false, map.get("AgeGroupSeniors"));
        assertEquals(700.0, map.get("PricePurchase"));
        assertEquals(-1.0, map.get("PriceInstallation"));
        assertEquals(-1.0, map.get("PriceReInvestment"));
        assertEquals(-1.0, map.get("PriceMaintenance"));
        assertEquals(-1.0, map.get("PriceIndexation"));
        assertEquals(52.38138, map.get("Lat"));
        assertEquals(4.641622, map.get("Lng"));
        assertEquals(0, ((List) map.get("Documents")).size());
        assertEquals(1, ((List) map.get("Images")).size());
        assertEquals(0, ((JSONArray) map.get("LinkedAssets")).length());
        assertEquals(0, ((JSONArray) map.get("Hyperlinks")).length());
    }

    @Test
    public void testParseAssetWithLinkedAsset() throws IOException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("singleAssetWithLinked.json");
        String asset = IOUtils.toString(in);
        List<Map<String, Object>> returnValue = instance.parseAssets(asset);
        assertEquals(2, returnValue.size());

    }

    @Test
    public void testSaveAssets() throws IOException, NamingException, SQLException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("singleAssetWithLinked.json");
        String asset = IOUtils.toString(in);
        ImportReport report= instance.processAssets(asset);
        
        assertEquals(0, report.getErrors().size());
        assertEquals(2, report.getNumberInserted());
        assertEquals(0, report.getNumberUpdated());
    }
    
    @Test
    public void testSaveAssetType() throws IOException, NamingException, SQLException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("singleAssetWithLinked.json");
        String assetString = IOUtils.toString(in);
        ImportReport report= instance.processAssets(assetString);
        
        assertEquals(0, report.getErrors().size());
        assertEquals(2, report.getNumberInserted());
        assertEquals(0, report.getNumberUpdated());
        
        List<Object[]> assets = DB.qr().query("select id, equipment from " + DB.ASSETS_TABLE, new ArrayListHandler());
        assertEquals(2, assets.size());
        for (Object[] asset : assets) {
            assertNotNull("equipmenttype not set.", asset[1]);
        }
    }

    @Test
    public void testUpdateAssets() throws IOException, NamingException, SQLException {
        InputStream in = PlaymappingImporterTest.class.getResourceAsStream("singleAssetWithLinked.json");
        String asset = IOUtils.toString(in);
        
        
        ImportReport report= instance.processAssets(asset);
        
        assertEquals(0, report.getErrors().size());
        assertEquals(2, report.getNumberInserted());
        assertEquals(0, report.getNumberUpdated());
        
        report= instance.processAssets(asset);
        
        assertEquals(0, report.getErrors().size());
        assertEquals(0, report.getNumberInserted());
        assertEquals(2, report.getNumberUpdated());
    }
}
