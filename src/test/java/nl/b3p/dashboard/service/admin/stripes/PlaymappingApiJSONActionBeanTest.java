/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.b3p.dashboard.service.admin.stripes;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author meine
 */
public class PlaymappingApiJSONActionBeanTest {
    
    
    private PlaymappingApiJSONActionBean instance;
    public PlaymappingApiJSONActionBeanTest() {
    
    }
    
    @Before
    public void init(){
        instance = new PlaymappingApiJSONActionBean();
    }
    
    @Test
    public void testGetChildLocation3Levels() throws IOException{
        InputStream in = PlaymappingApiJSONActionBeanTest.class.getResourceAsStream("pijnacker3Locations.json");
        String location = IOUtils.toString(in);
        List<Map<String,Object>> returnValue = instance.parseChildLocations(location);
        assertEquals(195, returnValue.size());
    }
    
    @Test
    public void testGetChildLocation2Levels() throws IOException{
        InputStream in = PlaymappingApiJSONActionBeanTest.class.getResourceAsStream("haarlemLocations.json");
        String location = IOUtils.toString(in);
        List<Map<String,Object>> returnValue = instance.parseChildLocations(location);
        assertEquals(273, returnValue.size());
    }
    
    @Test
    public void testParseLocation(){
        String loc = "{\"$id\": \"3\",\"ID\": \"98d66e1b-e2eb-44ea-ab1b-ce5999cb4309\",\"LastUpdated\": \"2015-01-08T11:18:10.613\",\"Name\": \"C117/1009 Wilsonplein 8\",\"AddressLine1\": \"Wilsonplein 8\\r\\nHaarlem\",\"Suburb\": \"\",\"City\": \"\",\"Area\": \"\",\"PostCode\": \"\",\"Ref\": \"C117/1009\",\"AssetCount\": 14,\"Lat\": \"52,37759\",\"Lng\": \"4,627348\",\"ChildLocations\": [],\"Images\": [  {    \"$id\": \"4\",    \"ID\": \"33ec50eb-eec8-452a-bf7c-1b54ca7543f8\",    \"LastUpdated\": \"2012-08-02T07:44:06\",    \"URI\": \"http://www.playmapping.com/GetImage.ashx?g=33ec50eb-eec8-452a-bf7c-1b54ca7543f8&w=350&h=350\",    \"Description\": \"\"  },  {    \"$id\": \"5\",    \"ID\": \"8af2bd67-5178-405f-9a97-444133bf370d\",    \"LastUpdated\": \"2012-08-02T07:44:37\",    \"URI\": \"http://www.playmapping.com/GetImage.ashx?g=8af2bd67-5178-405f-9a97-444133bf370d&w=350&h=350\",    \"Description\": \"\"  }],\"Documents\": []}";
        JSONObject location = new JSONObject (loc);
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
        assertEquals(0, ((JSONArray)real.get("ChildLocations")).length());
        assertEquals(0, ((JSONArray)real.get("Documents")).length());
        assertEquals(2, ((JSONArray)real.get("Images")).length());
    }
    
    @Test
    public void testParseAssets(){
        
    }
    
}
