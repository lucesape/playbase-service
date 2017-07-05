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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import nl.b3p.commons.csv.CsvFormatException;
import nl.b3p.commons.csv.CsvInputStream;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Asset;
import nl.b3p.playbase.entities.Location;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Meine Toonen
 */
public class PlayadvisorImporter extends Importer {

    private static final Log LOG = LogFactory.getLog("PlayadvisorProcesor");
    private final Map<Integer, String> playadvisorColumnToPlaybase;

    private static final String LOCATIONSUBTYPE = "locationsubtype";
    private static final String FACILITIES = "facilities";
    private static final String ACCESSIBLITIY = "accessibility";
    private static final String AGECATEGORIES = "agecategories";

    public PlayadvisorImporter() {
        super();
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
        playadvisorColumnToPlaybase.put(13, LOCATIONSUBTYPE);
        playadvisorColumnToPlaybase.put(14, "country");
        playadvisorColumnToPlaybase.put(15, "municipality");
        playadvisorColumnToPlaybase.put(16, "assets");
        playadvisorColumnToPlaybase.put(17, FACILITIES);
        playadvisorColumnToPlaybase.put(18, AGECATEGORIES);
        playadvisorColumnToPlaybase.put(19, "parking");
        playadvisorColumnToPlaybase.put(20, ACCESSIBLITIY);
        playadvisorColumnToPlaybase.put(21, "ambassadors");
        playadvisorColumnToPlaybase.put(22, "average_rating");
        playadvisorColumnToPlaybase.put(23, "Lng");
        playadvisorColumnToPlaybase.put(24, "Lat");
    }

    public void init(String[] header) {
    }

    public void importStream(InputStream in, ImportReport report) throws IOException, CsvFormatException {
        CsvInputStream cis = new CsvInputStream(new InputStreamReader(in));

        String[] s = cis.readRecord();
        init(s);
        try {
            while ((s = cis.readRecord()) != null) {
                Location l = processRecord(s, report);
            }
        } catch (NamingException | SQLException ex) {
            LOG.error("Cannot save location to db: ", ex);
            report.addError(ex.getLocalizedMessage());
        }
    }
    
    protected Location processRecord(String[] s, ImportReport report) throws NamingException, SQLException {
        Map<String, Object> locationMap = parseRecord(s);

        Location location = parseMap(locationMap);

        int id = saveLocation(location, report);
        List<Asset> assets = parseAssets(location, locationMap);

        for (Asset asset : assets) {
            saveAsset(asset, report);
        }

        saveLocationAgeCategory(id, Arrays.asList(location.getAgecategories()));
        saveLocationType((String) locationMap.get(LOCATIONSUBTYPE), id);

        saveFacilities(id, (String) locationMap.get(FACILITIES));
        saveAccessibility(id, (String) locationMap.get(ACCESSIBLITIY));
        return location;
    }

    protected Map<String, Object> parseRecord(String[] record) {
        Map<String, Object> dbvalues = new HashMap<>();
        String[] imageUrls = null;
        String[] imageDescriptions = null;
        for (int i = 0; i < record.length; i++) {
            String val = record[i];
            String col = playadvisorColumnToPlaybase.get(i);
            Object value = val;
            if (col == null) {
                continue;
            }
            switch (col) {
                case "imageurl":
                    imageUrls = val.split("\\|");
                    break;
                case "imagetitle":
                    break;
                case "imagecaption":
                    break;
                case "imagedescription":
                    imageDescriptions = val.split("\\|");
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
        List<Map<String, Object>> images = parseImages(imageUrls, imageDescriptions);
        dbvalues.put("images", images);

        return dbvalues;
    }

    protected Location parseMap(Map<String, Object> locationMap){
        Location location = new Location();
        
        return location;
    }
    
    private List<Asset> parseAssets( Location location, Map<String,Object> locationMap) throws NamingException, SQLException {
        List<Asset> assets = new ArrayList<>();
        String assetString = (String) locationMap.get("assets");
        String[] assetArray = assetString.split("\\|");
        for (String asset : assetArray) {
            Asset ass = new Asset();
            ass.setName(asset);
            ass.setLocation(location.getId());
            Integer[] cats = location.getAgecategories();
            
            ass.setAgecategories(cats);
            ass.setEquipment(getEquipmentType(asset));
            assets.add(ass);
        }
        return assets;
    }

    private List<Map<String, Object>> parseImages(String[] imageUrls, String[] imageDescriptions) {
        List<Map<String, Object>> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.length; i++) {
            String imageUrl = imageUrls[i];
            String description = imageDescriptions.length == imageUrls.length ? imageDescriptions[i] : null;
            Map<String, Object> image = parseImage(imageUrl, description);
            images.add(image);

        }
        return images;
    }

    private Map<String, Object> parseImage(String imageUrl, String imageDescription) {
        Map<String, Object> image = new HashMap<>();
        image.put("Description", imageDescription);
        image.put("URI", imageUrl);

        return image;
    }

    // <editor-fold desc="Saving of string-concatenated multivalues" defaultstate="collapsed">
    protected void saveFacilities(Integer locationId, String facilitiesString) throws NamingException, SQLException {

        DB.qr().update("DELETE FROM " + DB.LOCATION_FACILITIES_TABLE + " WHERE location = " + locationId);

        String[] facilities = facilitiesString.split("\\|");
        for (String facility : facilities) {
            Integer facilityId = facilityTypes.get(facility);
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.LOCATION_FACILITIES_TABLE);
            sb.append("(");
            sb.append("location,");
            sb.append("facility)");
            sb.append("VALUES( ");
            sb.append(locationId).append(",");
            sb.append(facilityId);
            sb.append(");");
            DB.qr().insert(sb.toString(), new ScalarHandler<>());
        }
    }

    protected void saveAccessibility(Integer locationId, String  accessiblitiesString) throws NamingException, SQLException {

        DB.qr().update("DELETE FROM " + DB.LOCATION_ACCESSIBILITY_TABLE + " WHERE location = " + locationId);
        String[] accessibilities = accessiblitiesString.split("\\|");

        for (String accessiblity : accessibilities) {
            Integer id = accessibilityTypes.get(accessiblity.toLowerCase());
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.LOCATION_ACCESSIBILITY_TABLE);
            sb.append("(");
            sb.append("location,");
            sb.append("accessibility)");
            sb.append("VALUES( ");
            sb.append(locationId).append(",");
            sb.append(id);
            sb.append(");");
            DB.qr().insert(sb.toString(), new ScalarHandler<>());
        }
    }

    protected void saveLocationType(String type, Integer id) throws NamingException, SQLException {
        String main = type.substring(0, type.indexOf(">"));
        String category = type.substring(type.indexOf(">") + 1);
        Integer categoryId = locationTypes.get(main).get(category);
        DB.qr().update("DELETE FROM " + DB.LOCATION_CATEGORY_TABLE + " WHERE location = " + id);
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT ");
        sb.append("INTO ");
        sb.append(DB.LOCATION_CATEGORY_TABLE);
        sb.append("(");
        sb.append("location,");
        sb.append("category)");
        sb.append("VALUES( ");
        sb.append(id).append(",");
        sb.append(categoryId);
        sb.append(");");
        DB.qr().insert(sb.toString(), new ScalarHandler<>());
    }

    // </editor-fold>
}
