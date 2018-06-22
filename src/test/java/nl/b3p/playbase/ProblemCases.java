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

import nl.b3p.playbase.db.TestUtil;
import nl.b3p.playbase.entities.Project;

/**
 *
 * @author Meine Toonen
 */
public class ProblemCases extends TestUtil{
    
    
    private PlayadvisorImporter paInstance;

    public ProblemCases() {
        paInstance = new PlayadvisorImporter(new Project("test"));
    }
    
   /* @Test(expected = IllegalArgumentException.class)
    public void testSterrenburg() throws IOException, CsvFormatException{
        String i = "104414,\"Speeltuin Sterrenburg\",\"De speelplek ligt in de wijk Sterrenburg 1 in de gemeente Dordrecht\",51.78818,2016-06-13,speelplek,http://playadvisor.b3p.nl/speelplek/openbare-speeltuin/speeltuin-sterrenburg/,,,,,,,\"Speeltuinen>Openbare speeltuin\",,Dordrecht,,,Combinatietoestel|Wip,,,,0,4.68491,51.78818,a:0:{},,,,,,publish,256,speeltuin-sterrenburg,,,0,0,0,open,open,,";
        
        CsvInputStream cis = new CsvInputStream(new InputStreamReader(new ByteArrayInputStream(i.getBytes(StandardCharsets.UTF_8))));

        String[] s = cis.readRecord();

        Map<String, Object> input = paInstance.parseRecord(s);
        Location l = paInstance.parseMap(input);

    }*/
}
