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
        String location = "";
        InputStream in = PlaymappingApiJSONActionBeanTest.class.getResourceAsStream("pijnacker3.json");
        location = IOUtils.toString(in);
        List<Map<String,Object>> returnValue = instance.getChildLocation(location);
        assertEquals(195, returnValue.size());
    }
    
    @Test
    public void testGetChildLocation2Levels() throws IOException{
        String location = "";
        InputStream in = PlaymappingApiJSONActionBeanTest.class.getResourceAsStream("haarlem.json");
        location = IOUtils.toString(in);
        List<Map<String,Object>> returnValue = instance.getChildLocation(location);
        assertEquals(273, returnValue.size());
    }
    
    
}
