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
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import nl.b3p.commons.csv.CsvFormatException;
import nl.b3p.commons.csv.CsvInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Meine Toonen
 */
public class PlayadvisorImporter extends Importer {

    private static final Log log = LogFactory.getLog("PlayadvisorProcesor");
    private Map<Integer, String> playadvisorColumnToPlaybase;

    public PlayadvisorImporter() {
        playadvisorColumnToPlaybase = new HashMap<>();
        playadvisorColumnToPlaybase.put(0, "pa_id");
        playadvisorColumnToPlaybase.put(1, "title");
        playadvisorColumnToPlaybase.put(2, "content");
        playadvisorColumnToPlaybase.put(3, "summary");
        playadvisorColumnToPlaybase.put(4, "");
        playadvisorColumnToPlaybase.put(5, "locationtype");
        playadvisorColumnToPlaybase.put(6, "website");
        playadvisorColumnToPlaybase.put(7, "imageurl");
        playadvisorColumnToPlaybase.put(8, "imagetitle");
        playadvisorColumnToPlaybase.put(9, "imagecaption");
        playadvisorColumnToPlaybase.put(10, "imagedescription");
        playadvisorColumnToPlaybase.put(11, "imagealttext");
        playadvisorColumnToPlaybase.put(12, "url_2");
        playadvisorColumnToPlaybase.put(13, "locationsubtype");
        playadvisorColumnToPlaybase.put(14, "country");
        playadvisorColumnToPlaybase.put(15, "municipality");
        playadvisorColumnToPlaybase.put(16, "assets");
        playadvisorColumnToPlaybase.put(17, "facilities");
        playadvisorColumnToPlaybase.put(18, "agecategories");
        playadvisorColumnToPlaybase.put(19, "parking");
        playadvisorColumnToPlaybase.put(20, "accessiblity");
        playadvisorColumnToPlaybase.put(21, "ambassadors");
        playadvisorColumnToPlaybase.put(22, "average_rating");
        playadvisorColumnToPlaybase.put(23, "Lng");
        playadvisorColumnToPlaybase.put(24, "Lat");
    }
    
    public void init(String[] header){
    }

    public void importStream(InputStream in, ImportReport report) throws IOException, CsvFormatException {
        CsvInputStream cis = new CsvInputStream(new InputStreamReader(in));

        String[] s = cis.readRecord();
        init(s);
        try {
            while ((s = cis.readRecord()) != null) {
                Map<String, Object> vals = parseRecord(s);
                saveLocation(vals, report);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot save location to db: ", ex);
        }
    }

    protected Map<String, Object> parseRecord(String[] record) {
        Map<String, Object> dbvalues = new HashMap<>();
        String [] imageUrls = null;
        String [] imageDescriptions = null;
        for (int i = 0; i < record.length; i++) {
            String val = record[i];
            String col = playadvisorColumnToPlaybase.get(i);
            Object value = val;
            if(col == null){
                continue;
            }
            switch (col){
                case "imageurl":
                    imageUrls = val.split(",");
                    break;
                case "imagetitle":
                    break;
                case "imagecaption":
                    break;
                case "imagedescription":
                    imageDescriptions = val.split(",");
                    break;
                case "imagealttext":
                    break;
                case "assets":
                    break;
                case "facilities":
                    break;
                case "accessiblity":
                    break;
                case "Lng":
                case "Lat":
                    value = Double.parseDouble(val);
                    break;
                default:
                    break;
            }
            dbvalues.put(col, value);
        }
        List<Map<String,Object>> images = parseImages(imageUrls, imageDescriptions);
        dbvalues.put("images", images);
        
        return dbvalues;
    }
    
    private List<Map<String,Object>> parseImages(String [] imageUrls,String [] imageDescriptions){
        List<Map<String,Object>> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.length; i++) {
            String imageUrl = imageUrls[i];
            String description = imageDescriptions[i];
            Map<String,Object> image = parseImage(imageUrl, description);
            images.add(image);
            
        }
        return images;
    }
    
    private Map<String,Object> parseImage(String imageUrl,String  imageDescription){
        Map<String,Object> image = new HashMap<>();
        image.put("Description", imageDescription);
        image.put("URI", imageUrl);
        
        return image;
    }

}
