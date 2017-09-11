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
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import nl.b3p.commons.csv.CsvFormatException;
import nl.b3p.commons.csv.CsvInputStream;
import nl.b3p.playbase.ImportReport.ImportType;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Asset;
import nl.b3p.playbase.entities.Comment;
import nl.b3p.playbase.entities.Location;
import org.apache.commons.lang3.StringEscapeUtils;
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

    private Map<String, String> parkingMapping;
    
    public PlayadvisorImporter() {
        super();
        postfix = "_playadvisor";
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
        playadvisorColumnToPlaybase.put(12, "url");
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

        parkingMapping = new HashMap<>();
        parkingMapping.put("Betaald", "ja - betaald");
        parkingMapping.put("Gratis", "ja - gratis");
        
        // Strange english <> dutch mixup
        locationTypes.get("Speeltuinen").put("Indoor speeltuin", locationTypes.get("Speeltuinen").get("Binnenspeeltuin"));
        
    }

    public void init(String[] header) {
    }
    
    public void importComments(InputStream in, ImportReport report) throws IOException, CsvFormatException {
        CsvInputStream cis = new CsvInputStream(new InputStreamReader(in));

        String[] s = null;
        do {
            s = cis.readRecord();
        } while (s.length <= 1);
        try {
            while ((s = cis.readRecord()) != null) {
                Comment c = processComment(s, report);
                saveComment(c, report);
                if (c == null) {
                    int a = 0;
                }
            }
        } catch (NamingException | SQLException ex) {
            LOG.error("Cannot save location to db: ", ex);
            report.addError(ex.getLocalizedMessage(), ImportType.COMMENT);
        }
    }
    
    public void importStream(InputStream in, ImportReport report) throws IOException, CsvFormatException {
        CsvInputStream cis = new CsvInputStream(new InputStreamReader(in));

        String[] s = null;
        do {
            s = cis.readRecord();
        } while (s.length <= 1);

        init(s);
        try {
            while ((s = cis.readRecord()) != null) {
                Location l = processRecord(s, report);
                if(l == null){
                    int a = 0;
                }
            }
        } catch (NamingException | SQLException ex) {
            LOG.error("Cannot save location to db: ", ex);
            report.addError(ex.getLocalizedMessage(), ImportType.LOCATION);
        }
    }

    protected Location processRecord(String[] s, ImportReport report) throws NamingException, SQLException, UnsupportedEncodingException {
        Map<String, Object> locationMap = parseRecord(s);
        Location location = null;
        try {
            location = parseMap(locationMap);
        } catch (IllegalArgumentException e) {
            report.addError(e.getLocalizedMessage(), ImportType.LOCATION);
            return null;
        }
        int id = saveLocation(location, report);
        location.setId(id);
        List<Asset> assets = parseAssets(location, locationMap);

        for (Asset asset : assets) {
            saveAsset(asset, report);
        }

        saveLocationAgeCategory(id, Arrays.asList(location.getAgecategories()), true);
   
        try {
            if (((String) locationMap.get(LOCATIONSUBTYPE)).length() > 0) {
                saveLocationType((String) locationMap.get(LOCATIONSUBTYPE), id);
            }
        } catch (IllegalArgumentException ex) {
            report.addError(ex.getLocalizedMessage() + ". Location is saved, but type is not.", ImportType.LOCATION);
        }
        
        try {
            if (((String) locationMap.get(FACILITIES)).length() > 0) {
                saveFacilities(id, (String) locationMap.get(FACILITIES));
            }
        } catch (IllegalArgumentException ex) {
            report.addError(ex.getLocalizedMessage() + ". Location is saved, but facilities are not.", ImportType.LOCATION);
        }
        
        try {
            if (((String) locationMap.get(ACCESSIBLITIY)).length() > 0) {
                saveAccessibility(id, (String) locationMap.get(ACCESSIBLITIY));
            }
        } catch (IllegalArgumentException ex) {
            report.addError(ex.getLocalizedMessage() + ". Location is saved, but accessiblity is not.", ImportType.LOCATION);
        }
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
                    value = val == null || val.isEmpty() ? null : Double.parseDouble(val);
                    break;
                case "average_rating":
                    value = val == null || val.isEmpty() ? null : Integer.parseInt(val);
                default:
                    break;
            }
            dbvalues.put(col, value);
        }
        List<Map<String, Object>> images = parseImages(imageUrls, imageDescriptions);
        dbvalues.put("images", images);

        return dbvalues;
    }

    protected Location parseMap(Map<String, Object> lM) {
        Location l = new Location();
        l.setPa_id((String) lM.get("pa_id"));
        l.setArea((String) lM.get("area"));
        l.setCountry((String) lM.get("country"));
        l.setContent((String) lM.get("content"));
        l.setEmail((String) lM.get("e-mail"));
        l.setImages((List<Map<String, Object>>) lM.get("images"));
        l.setDocuments((List<Map<String, Object>>) lM.get("documents"));
        l.setMunicipality((String) lM.get("municipality"));
        l.setNumber((String) lM.get("number"));
        l.setNumberextra((String) lM.get("NumberExtra"));
        l.setPhone((String) lM.get("Phone"));
        l.setPostalcode((String) lM.get("Postcode"));
        l.setStreet((String) lM.get("Street"));
        l.setSummary((String) lM.get("Excerpt"));
        l.setPa_title((String) lM.get("title"));
        l.setWebsite((String) lM.get("website"));
        l.setLatitude(lM.get("Lat") != null ? (Double) lM.get("Lat") : null);
        l.setLongitude(lM.get("Lng") != null ? (Double) lM.get("Lng") : null);
        l.setAveragerating(lM.get("average_rating") != null ? (Integer)lM.get("average_rating") : 0);

        String parking = (String) lM.get("parking");
        l.setParking(parkingTypes.get(parkingMapping.get(parking)));
        String agecats = (String) lM.get(AGECATEGORIES);
        String[] agecategories = agecats.split("\\|");
        List<Integer> ids = new ArrayList<>();
        for (String agecategory : agecategories) {
            if (agecategory.length() > 0) {
                Integer id = agecategoryTypes.get(agecategory.toLowerCase());
                if (id == null) {
                    throw new IllegalArgumentException("Agecategory >" + agecategory + "< does not exist. Location with title >" + l.getTitle() + "< not saved.");
                }
                ids.add(id);
            }
        }
        l.setAgecategories(ids.toArray(new Integer[0]));

        return l;
    }

    private List<Asset> parseAssets(Location location, Map<String, Object> locationMap) throws NamingException, SQLException {
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
        Set<String> urls = new HashSet<>();
        for (int i = 0; i < imageUrls.length; i++) {
            String imageUrl = imageUrls[i];
            if(urls.contains(imageUrl)){
                continue;
            }else{
                urls.add(imageUrl);
            }
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

    
    private Comment processComment(String [] s, ImportReport report)throws NamingException, SQLException{
        Comment c = new Comment();
        c.setPlayadvisor_id(Integer.parseInt(s[0]));
        c.setContent(s[2]);
        c.setDate(s[8]);
        c.setPost_id(Integer.parseInt(s[4]));
        c.setAuthor(s[5]);
        Integer stars = s[3].isEmpty() ? null :Integer.parseInt(s[3]);
        c.setStars(stars);
        return c;
    }

    
    // <editor-fold desc="Saving of string-concatenated multivalues" defaultstate="collapsed">
    protected void saveFacilities(Integer locationId, String facilitiesString) throws NamingException, SQLException {

        DB.qr().update("DELETE FROM " + DB.LOCATION_FACILITIES_TABLE + postfix + " WHERE location = " + locationId);

        String[] facilities = facilitiesString.split("\\|");
        for (String facility : facilities) {
            Integer facilityId = facilityTypes.get(facility);
            if (facilityId == null) {
                throw new IllegalArgumentException("Unknown facility given: " + facility + ". Cannot save facilities for location with id: " + locationId);
            }
            this.saveFacilities(locationId, facilityId);
        }
    }

    protected void saveAccessibility(Integer locationId, String accessiblitiesString) throws NamingException, SQLException {

        DB.qr().update("DELETE FROM " + DB.LOCATION_ACCESSIBILITY_TABLE + postfix + " WHERE location = " + locationId);
        String[] accessibilities = accessiblitiesString.split("\\|");

        for (String accessiblity : accessibilities) {
            String acc = accessiblity.toLowerCase();
            acc = acc.contains(">") ? acc.substring(acc.indexOf(">") + 1) : acc;
            Integer id = accessibilityTypes.get(acc);
            if (id == null) {
                throw new IllegalArgumentException("Unknown accessibilty given: " + acc + ". Cannot save types for location with id: " + locationId);
            }
            this.saveAccessibility(locationId, id);
        }
    }

    protected void saveLocationType(String typeString, Integer id) throws NamingException, SQLException, UnsupportedEncodingException {
        DB.qr().update("DELETE FROM " + DB.LOCATION_CATEGORY_TABLE + postfix + " WHERE location = " + id);

        String[] types = typeString.split("\\|");
        for (String type : types) {
            int index = type.indexOf(">");
            String main;
            String category;
            if (index == -1) {
                main = type;
                category = type;
            } else {
                main = type.substring(0, index);
                category = type.substring(index + 1);
            }
            main = StringEscapeUtils.unescapeHtml4(main);
            Integer categoryId = locationTypes.containsKey(main) ? locationTypes.get(main).get(category) : null;
            
            if (categoryId == null) {
                throw new IllegalArgumentException("Unknown category given: main:" + main + ", subcategory: " + category+ ". Cannot save types for location with id: " + id);
            }
            this.saveLocationType(categoryId, id);
        }
    }

    // </editor-fold>
}
