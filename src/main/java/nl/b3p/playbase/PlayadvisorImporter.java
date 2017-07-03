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
import nl.b3p.playbase.db.DB;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Meine Toonen
 */
public class PlayadvisorImporter extends Importer {

    private static final Log log = LogFactory.getLog("PlayadvisorProcesor");
    private Map<Integer, String> playadvisorColumnToPlaybase;

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
                Map<String, Object> location = parseRecord(s);

                int id = saveLocation(location, report);

                List<Map<String, Object>> assets = parseAssets(location, id);

                for (Map<String, Object> asset : assets) {
                    saveAsset(asset, report);
                }

                if (location.containsKey(AGECATEGORIES)) {
                    saveAgecategories(id, location);
                }

                if (location.containsKey(LOCATIONSUBTYPE)) {
                    saveLocationType(location, id);
                }
                if (location.containsKey(FACILITIES)) {
                    saveFacilities(id, location);
                }
                if (location.containsKey(ACCESSIBLITIY)) {
                    saveAccessibility(id, location);
                }

            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot save location to db: ", ex);
            report.addError(ex.getLocalizedMessage());
        }
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

    private List<Map<String, Object>> parseAssets(Map<String, Object> location, Integer locationId) throws NamingException, SQLException {

    //    DB.qr().update("DELETE FROM " + DB.ASSETS_TABLE + " WHERE location = " + locationId);
        List<Map<String, Object>> assets = new ArrayList<>();
        String assetString = (String) location.get("assets");
        String[] assetArray = assetString.split("\\|");
        for (String asset : assetArray) {
            Map<String, Object> assMap = new HashMap<>();
            assMap.put("Name", asset);
            assMap.put("LocationPAID", locationId);
            assMap.put(AGECATEGORIES, location.get(AGECATEGORIES));
            assets.add(assMap);
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
    protected void saveFacilities(Integer locationId, Map<String, Object> location) throws NamingException, SQLException {

        DB.qr().update("DELETE FROM " + DB.LOCATION_FACILITIES_TABLE + " WHERE location = " + locationId);
        String facilitiesString = (String) location.get(FACILITIES);
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

    protected void saveAgecategories(Integer locationId, Map<String, Object> location) throws NamingException, SQLException {
        DB.qr().update("DELETE FROM " + DB.LOCATION_AGE_CATEGORY_TABLE + " WHERE location = " + locationId);
        String agecategoriesString = (String) location.get(AGECATEGORIES);
        String[] agecategories = agecategoriesString.split("\\|");

        for (String agecategory : agecategories) {
            Integer agecategoryId = agecategoryTypes.get(agecategory);
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.LOCATION_AGE_CATEGORY_TABLE);
            sb.append("(");
            sb.append("location,");
            sb.append("agecategory)");
            sb.append("VALUES( ");
            sb.append(locationId).append(",");
            sb.append(agecategoryId);
            sb.append(");");
            DB.qr().insert(sb.toString(), new ScalarHandler<>());
        }
    }

    protected void saveAccessibility(Integer locationId, Map<String, Object> location) throws NamingException, SQLException {

        DB.qr().update("DELETE FROM " + DB.LOCATION_ACCESSIBILITY_TABLE + " WHERE location = " + locationId);
        String accessiblitiesString = (String) location.get(ACCESSIBLITIY);
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

    protected void saveLocationType(Map<String, Object> location, Integer id) throws NamingException, SQLException {
        String type = (String) location.get(LOCATIONSUBTYPE);
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

    @Override
    protected void saveAssetsAgeCategories(Map<String, Object> asset, Integer location) throws NamingException, SQLException {
        String agecategoriesString = (String) asset.get("agecategories");
        if (agecategoriesString != null) {
            String[] agecategories = agecategoriesString.split("\\|");
            List<Integer> ids = new ArrayList<>();
            for (String agecategory : agecategories) {
                int id = agecategoryTypes.get(agecategory);
                ids.add(id);
            }
            saveAssetsAgeCategory(location, ids);
        }
    }
    // </editor-fold>
}
