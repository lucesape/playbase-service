/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.b3p.playbase.stripes;

import nl.b3p.playbase.stripes.ImportPlaymappingActionBean;
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
public class ImportPlaymappingActionBeanTest {
    
    
    private ImportPlaymappingActionBean instance;
    public ImportPlaymappingActionBeanTest() {
    
    }
    
    @Before
    public void init(){
        instance = new ImportPlaymappingActionBean();
    }
    

}
