/*
 * Copyright (C) 2018 B3Partners B.V.
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
import org.json.JSONObject;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author Meine Toonen
 */
public class PlayadvisorExporterTest {
    
    private PlayadvisorExporter instance;
    
    public PlayadvisorExporterTest() {
    }

    @Before
    public void initTest(){
        instance = new PlayadvisorExporter();
    }
    
    @Test
    public void testExport() {
        try {
            //JSONObject location = new JSONObject("{      \"Leeftijdscategorie\" : [],      \"Telefoon\" : \"\",      \"Straat\" : \"Gilze-Rijenhof 22184\",      \"Images\" : [         {            \"url\" : null,            \"attachment_id\" : null         },         {            \"url\" : null,            \"attachment_id\" : null         },         {            \"attachment_id\" : null,            \"url\" : null         },         {            \"url\" : null,            \"attachment_id\" : null         },         {            \"attachment_id\" : null,            \"url\" : null         },         {            \"url\" : null,            \"attachment_id\" : null         },         {            \"attachment_id\" : null,            \"url\" : null         },         {            \"attachment_id\" : null,            \"url\" : null         }      ],      \"Land\" : \"\",      \"Content\" : \"\",      \"Toegankelijkheid\" : [],      \"Titel\" : \"Speeltuin Gilze-Rijenhof in Nootdorp\",      \"Plaats\" : \"\",      \"Samenvatting\" : \"\",      \"Website\" : \"\",      \"Regio\" : \"\",      \"Email\" : \"\",      \"Categorien\" : [         \"Openbare speeltuin\",         \"Speeltuinen\"      ],      \"Faciliteiten\" : [],      \"Assets\" : [         \"Draaitoestel\",         \"Schommel\",         \"Speelhuis\"      ],      \"id\" : \"72567\",      \"Longitude\" : \"4.383566\",      \"newPlayGround\" : \"false\",      \"Latitude\" : \"52.04646\",      \"Parkeren\" : [],      \"PlayadvisorID\" : 88721   }");
            JSONObject location = new JSONObject("{\"PlaybaseID\":\"72619\",\"Titel\":\"bij pannenkoekenhuis Soete suikerbol\",\"Content\":\"<br>\",\"Samenvatting\":\"\",\"Latitude\":\"52.0065051\",\"Longitude\":\"4.4511693\",\"Straat\":\"Ade 23\",\"Plaats\":\"\",\"Regio\":\"\",\"Land\":\"Nederland\",\"Website\":\"\",\"Email\":\"\",\"Telefoon\":\"\",\"PlayadvisorID\":98609,\"Images\":[{\"PlaybaseID\":-1,\"PlayadvisorID\":\"38\",\"Path\":\"http:\\/\\/playadvisor.b3p.nl\\/wp-content\\/uploads\\/2016\\/04\\/001.jpg\"}],\"Categorien\":[\"Openbare speeltuin\",\"Speeltuinen\"],\"Leeftijdscategorie\":[],\"Toegankelijkheid\":[],\"Faciliteiten\":[],\"Parkeren\":[],\"Assets\":[],\"newPlayGround\":\"false\"}");
            instance.pushLocation(location, 98609,null);
            int a = 0;
        } catch (IOException ex) {
            fail(ex.getLocalizedMessage());
        }
    }
    
}
