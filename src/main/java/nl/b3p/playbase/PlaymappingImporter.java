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

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import nl.b3p.playbase.ImportReport.ImportType;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Asset;
import nl.b3p.playbase.entities.Location;
import nl.b3p.playbase.entities.Project;
import nl.b3p.playbase.stripes.MatchActionBean;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Meine Toonen
 */
public class PlaymappingImporter extends Importer {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private Map<String, List<Integer>> agecategories;
    public PlaymappingImporter(Project project) {
        super(project);
        ArrayListHandler rsh = new ArrayListHandler();
        try {
            agecategories = new HashMap<>();

            agecategories.put(AGECATEGORY_TODDLER_KEY, new ArrayList<>());
            agecategories.put(AGECATEGORY_JUNIOR_KEY, new ArrayList<>());
            agecategories.put(AGECATEGORY_SENIOR_KEY, new ArrayList<>());

            List<Object[]> o = DB.qr().query("SELECT * from " + DB.LIST_AGECATEGORIES_TABLE, rsh);
            for (Object[] cat : o) {
                Integer id = (Integer) cat[0];
                String categorie = (String) cat[1];

                if (Arrays.asList(AGECATEGORY_TODDLER).contains(categorie)) {
                    agecategories.get(AGECATEGORY_TODDLER_KEY).add(id);
                } else if (Arrays.asList(AGECATEGORY_JUNIOR).contains(categorie)) {
                    agecategories.get(AGECATEGORY_JUNIOR_KEY).add(id);
                } else if (Arrays.asList(AGECATEGORY_SENIOR).contains(categorie)) {
                    agecategories.get(AGECATEGORY_SENIOR_KEY).add(id);
                } else {
                    throw new IllegalArgumentException("Found agecategory in db not defined in code");
                }
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot initialize playmapping processor:", ex);
        }
    }

    private static final Log log = LogFactory.getLog("PlaymappingProcessor");

    private final String AGECATEGORY_TODDLER_KEY = "AgeGroupToddlers";
    private final String AGECATEGORY_JUNIOR_KEY = "AgeGroupJuniors";
    private final String AGECATEGORY_SENIOR_KEY = "AgeGroupSeniors";

    private final String[] AGECATEGORY_TODDLER = {"0 - 5 jaar"};
    private final String[] AGECATEGORY_JUNIOR = {"6 - 11 jaar", "12 - 18 jaar"};
    private final String[] AGECATEGORY_SENIOR = {"Volwassenen", "Senioren"};

    public void init() {

    }

    public ImportReport processAssets(JSONArray assetsString, ImportReport report) throws NamingException, SQLException {
        Map<Integer,Set<Integer>> assetTypes = new HashMap<>();
        List<Asset> assets = parseAssets(assetsString, assetTypes);
        for (Asset asset : assets) {
            try {
                saveAsset(asset, report);
            } catch (NamingException | SQLException | IllegalArgumentException ex) {
                log.error("Cannot save asset: " + ex.getLocalizedMessage());
                report.addError(ex.getLocalizedMessage(), ImportType.ASSET);
            }
        }
        return report;
    }

    public List<String> processLocations(String temp, ImportReport report) throws NamingException, SQLException {
        List<Location> childLocations = parseChildLocations(temp);
        List<String> pm_guids = new ArrayList<>();
        for (Location childLocation : childLocations) {
            childLocation = mergeLocation(childLocation);
            pm_guids.add(childLocation.getPm_guid());
            saveLocation(childLocation, report);
        }
        return pm_guids;
    }
    
    private Location mergeLocation(Location loc) throws SQLException, NamingException{
        Location dbLoc = getExistingLocation(loc);
        if(dbLoc != null){
            dbLoc.setMunicipality(loc.getMunicipality());
            loc = MatchActionBean.mergeLocations(dbLoc, loc);
        }
        
        return loc;
    }
    
    public Location getExistingLocation(Location newLocation) throws NamingException, SQLException {
        Location loc;

        StringBuilder sb = new StringBuilder();
        sb.append("select * from ");

        sb.append(DB.LOCATION_TABLE);
        if (newLocation.getId() != null) {
            sb.append(" where id = '");
            sb.append(newLocation.getId());
        } else {
            sb.append(" where pm_guid = '");
            sb.append(newLocation.getPm_guid());
        }
        sb.append("';");
        loc = DB.qr().query(sb.toString(), locationHandler);
        return loc;
    }


    // <editor-fold desc="Assets" defaultstate="collapsed">
    protected List<Asset> parseAssets(JSONArray assetsArray, Map<Integer,Set<Integer>> assetTypes) throws NamingException, SQLException {
        List<Asset> assets = new ArrayList<>();
     //   JSONArray assetsArray = new JSONArray(assetsString);

        for (int i = 0; i < assetsArray.length(); i++) {
            JSONObject asset = assetsArray.getJSONObject(i);
            JSONObject location = asset.getJSONObject("Location");
            Integer locationId = getLocationId(location.getString("ID"));
            assets.add(parseAsset(asset, locationId, assetTypes));
            JSONArray linkedAssets = asset.getJSONArray("LinkedAssets");
            assets.addAll(parseAssets(linkedAssets,assetTypes));
        }
        return assets;
    }

    protected Asset parseAsset(JSONObject assetJSON, Integer locationId, Map<Integer,Set<Integer>> locationTypes) {
        Asset asset = new Asset();
        asset.setPm_guid(assetJSON.optString("ID"));
        asset.setLocation(locationId);
        asset.setName(assetJSON.optString("Name").replaceAll("\'", "\'\'"));
        JSONObject assetType = assetJSON.optJSONObject("AssetType");
        if(assetType != null){
            asset.setType_(getAssetType(assetType.optString("FullName")));
        }
        
        JSONObject manufacturer = assetJSON.optJSONObject("Manufacturer");
        if(manufacturer != null){
            asset.setManufacturer( manufacturer.optString("Name"));
        }
        
        JSONObject product = assetJSON.optJSONObject("Product");
        if(product != null){
            asset.setProduct(product.optString("ProductNumber"));
            asset.setSerialnumber(product.optString("SerialNumber"));
            asset.setProductid(product.optString("ID"));
            JSONObject productgroup = product.optJSONObject("ProductGroup");
            if(productgroup != null){
                asset.setProductvariantid(productgroup.optString("ID"));
            }
        }
        
        JSONObject material = assetJSON.optJSONObject("Material");
        if(material != null){
            asset.setMaterial(material.optString("Name"));
        }
        asset.setInstalleddate(assetJSON.optString("InstalledDate"));
        asset.setEndoflifeyear(assetJSON.optInt("EndOfLifeYear"));
        asset.setHeight(assetJSON.optInt("Height"));
        asset.setDepth(assetJSON.optInt("Depth"));
        asset.setWidth(assetJSON.optInt("Width"));
        asset.setFreefallheight( assetJSON.optInt("FreefallHeight"));
        asset.setSafetyzonelength(assetJSON.optInt("SafetyZoneLength"));
        asset.setSafetyzonewidth(assetJSON.optInt("SafetyZoneWidth"));
        asset.setPricepurchase( assetJSON.optInt("PricePurchase"));
        asset.setPriceinstallation( assetJSON.optInt("PriceInstallation"));
        asset.setPricereinvestment( assetJSON.optInt("PriceReInvestment"));
        asset.setPricemaintenance( assetJSON.optInt("PriceMaintenance"));
        asset.setPriceindexation(assetJSON.optInt("PriceIndexation"));
        asset.setLatitude(Double.parseDouble(assetJSON.optString("Lat").replaceAll(",", ".")));
        asset.setLongitude( Double.parseDouble(assetJSON.optString("Lng").replaceAll(",", ".")));
        asset.setDocuments( parseImagesAndWords(assetJSON.optJSONArray("Documents")));
        asset.setImages(parseImagesAndWords(assetJSON.optJSONArray("Images")));
        asset.setAgecategories(parseAgecategories(assetJSON));
        
        if (!locationTypes.containsKey(locationId)) {
            locationTypes.put(locationId, new HashSet<>());
        }
        if (asset.getType_() != null){
            locationTypes.get(locationId).add(assetTypeToLocationCategory.get(asset.getType_()));
        }
        // ToDo hyperlinks: asset.put("Hyperlinks", assetJSON.optJSONArray("Hyperlinks"));
        
        return asset;
    }
    
    protected Integer[] parseAgecategories(JSONObject assetJSON){
        
        boolean toddler = assetJSON.optBoolean(AGECATEGORY_TODDLER_KEY, false);
        boolean junior = assetJSON.optBoolean(AGECATEGORY_JUNIOR_KEY, false);
        boolean senior = assetJSON.optBoolean(AGECATEGORY_SENIOR_KEY, false);
        List<Integer> agecategoriesList = new ArrayList<>();
        
        if (toddler) {
            agecategoriesList.addAll(agecategories.get(AGECATEGORY_TODDLER_KEY));
        }

        if (junior) {
            agecategoriesList.addAll(agecategories.get(AGECATEGORY_JUNIOR_KEY));
        }

        if (senior) {
            agecategoriesList.addAll(agecategories.get(AGECATEGORY_SENIOR_KEY));
        }
        return agecategoriesList.toArray(new Integer[0]);
    }


    // </editor-fold>
    
    // <editor-fold desc="Locations" defaultstate="collapsed">
    protected List<Location> parseChildLocations(String locations) {
        List<Location> locs = new ArrayList<>();
        JSONArray childLocations = new JSONArray(locations);
        for (int i = 0; i < childLocations.length(); i++) {
            JSONObject childLocation = childLocations.getJSONObject(i);
            JSONArray cls = childLocation.getJSONArray("ChildLocations");
            if (cls.length() == 0) {
                locs.add(parseLocation(childLocation));
            } else {
                locs.addAll(parseChildLocations(cls.toString()));
            }

        }
        return locs;
    }

    protected Location parseLocation(JSONObject json) {
        Location location = new Location();
        
        location.setPm_guid(json.optString("ID"));
        location.setTitle(json.optString("Name").replaceAll("\'", "\'\'"));
        location.setStreet(json.optString("AddressLine1"));
        //location.setMunicipality( json.optString("Suburb"));
        location.setMunicipality( json.optString("City"));
        location.setArea(json.optString("Area"));
        location.setPostalcode(json.optString("PostCode"));
        String content = json.optString("Notes");
        content = content == null || content.isEmpty() ? null : content;
        location.setPm_content(content);
        //location.put("AssetCount", json.optInt("AssetCount"));
        location.setLatitude(Double.parseDouble(json.optString("Lat").replaceAll(",", ".")));
        location.setLongitude( Double.parseDouble(json.optString("Lng").replaceAll(",", ".")));
        location.setDocuments(parseImagesAndWords(json.optJSONArray("Documents")));
        location.setImages( parseImagesAndWords(json.optJSONArray("Images")));
        return location;
    }

    /**
     * Parse images and documents (=words).
     *
     * @param images The images or documents to be parsed
     * @return Returns the converted list with maps representing the documents or images
     */
    protected List<Map<String, Object>> parseImagesAndWords(JSONArray images) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < images.length(); i++) {
            JSONObject img = images.getJSONObject(i);
            Map<String, Object> image = parseImageAndWord(img);
            list.add(image);
        }
        return list;
    }

    protected Map<String, Object> parseImageAndWord(JSONObject image) {

        Map<String, Object> map = new HashMap<>();
        map.put("$id", image.optString("$id"));
        map.put("ID", image.optString("ID"));
        String url = image.optString("URI");
        url = url.replaceAll("&w=350&h=350", "");
        map.put("URI", url);
        map.put("Description", image.optString("Description"));
        try {
            map.put("LastUpdated", image.has("LastUpdated") ? sdf.parse(image.getString("LastUpdated")): null);
        } catch (ParseException ex) {
            log.debug("Cannot parse date: " + image.getString("LastUpdated"), ex);
        }
        return map;
    }
    
    protected Integer getLocationId(String pmguid) throws NamingException, SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("select id from ");

        sb.append(DB.LOCATION_TABLE);
        sb.append(" where pm_guid = '");
        sb.append(pmguid);

        sb.append("';");
        Integer id = (Integer) DB.qr().query(sb.toString(), new ScalarHandler<>());
        return id;
    }
    // </editor-fold>

    public void importString(String stringResult, String apiurl,ImportReport report ) throws NamingException, SQLException {
        if (stringResult != null) {
            ImportType type;

            if (apiurl.contains("Location")) {
                processLocations(stringResult, report);
                type = ImportType.LOCATION;
            } else if (apiurl.contains("Asset")) {
                JSONArray res = new JSONArray(stringResult);
                processAssets(res, report);
                type = ImportType.ASSET;
            } else {
                throw new IllegalArgumentException("Wrong url selected");
            }
            report.setImportedstring(type, stringResult);
        }
    }

    public ImportReport importJSONAssetsFromAPI(String username, String password, String apiurl, List<String> pm_guids,ImportReport report) throws SQLException, NamingException {
        JSONArray results = new JSONArray();
        for (String pm_guid : pm_guids) {
            String url = apiurl + pm_guid;
            String stringResult = getResponse(username, password, url, report);
            JSONArray result = new JSONArray(stringResult);
            
            for (Iterator<Object> iterator = result.iterator(); iterator.hasNext();) {
                Object next = iterator.next();
                results.put(next);
            }
        }
        

        processAssets(results, report);
        ImportType type = ImportType.ASSET;
        report.setImportedstring(type, results.toString());
        return report;
    }
    
    public List<String> importJSONLocationsFromAPI(String username, String password, String apiurl,ImportReport report) throws SQLException, NamingException {
        String stringResult = getResponse(username, password, apiurl, report);
        if(stringResult == null){
            return null;
        }
        List<String> locationIds = processLocations(stringResult, report);
        ImportType type = ImportType.LOCATION;
        report.setImportedstring(type, stringResult);
        return locationIds;
    }

}
