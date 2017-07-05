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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Asset;
import nl.b3p.playbase.entities.Location;
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

    private Map<String, List<Integer>> agecategories;
    public PlaymappingImporter() {
        super();
        ArrayListHandler rsh = new ArrayListHandler();
        try {
            agecategories = new HashMap<>();

            agecategories.put(AGECATEGORY_TODDLER_KEY, new ArrayList<Integer>());
            agecategories.put(AGECATEGORY_JUNIOR_KEY, new ArrayList<Integer>());
            agecategories.put(AGECATEGORY_SENIOR_KEY, new ArrayList<Integer>());

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

    public ImportReport processAssets(String assetsString) throws NamingException, SQLException {
        
        List<Asset> assets = parseAssets(assetsString);
        ImportReport report = new ImportReport("assets");
        for (Asset asset : assets) {
            try {
                saveAsset(asset, report);
            } catch (NamingException | SQLException | IllegalArgumentException ex) {
                log.error("Cannot save asset: " + ex.getLocalizedMessage());
                report.addError(ex.getLocalizedMessage());
            }
        }
        return report;
    }

    public ImportReport processLocations(String temp) throws NamingException, SQLException {
        List<Location> childLocations = parseChildLocations(temp);
        ImportReport report = new ImportReport("locaties");
        for (Location childLocation : childLocations) {
            saveLocation(childLocation, report);
        }
        return report;
    }

    // <editor-fold desc="Assets" defaultstate="collapsed">
    protected List<Asset> parseAssets(String assetsString) throws NamingException, SQLException {
        List<Asset> assets = new ArrayList<>();
        JSONArray assetsArray = new JSONArray(assetsString);

        for (int i = 0; i < assetsArray.length(); i++) {
            JSONObject asset = assetsArray.getJSONObject(i);
            Integer locationId = getLocationId(asset.getString("LocationID"));
            assets.add(parseAsset(asset, locationId));
            String linkedAssets = asset.getJSONArray("LinkedAssets").toString();
            assets.addAll(parseAssets(linkedAssets));
        }
        return assets;
    }

    protected Asset parseAsset(JSONObject assetJSON, Integer locationId) {
        Asset asset = new Asset();
        asset.setPm_guid(assetJSON.optString("ID"));
        asset.setLocation(locationId);
        asset.setName(assetJSON.optString("Name").replaceAll("\'", "\'\'"));
        asset.setType_(getAssetType(assetJSON.optString("AssetType")));
        asset.setManufacturer( assetJSON.optString("Manufacturer"));
        asset.setProduct(assetJSON.optString("Product"));
        asset.setSerialnumber(assetJSON.optString("SerialNumber"));
        asset.setMaterial(assetJSON.optString("Material"));
        asset.setInstalleddate(assetJSON.optString("InstalledDate"));
        asset.setEndoflifeyear(assetJSON.optInt("EndOfLifeYear"));
        asset.setProductid(assetJSON.optString("ProductID"));
        asset.setProductvariantid(assetJSON.optString("ProductVariantID"));
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
        /*location.put("$id", json.optString("$id"));
        location.put("ID", json.optString("ID"));
        location.put("LastUpdated", json.optString("LastUpdated"));
        location.put("Name", json.optString("Name").replaceAll("\'", "\'\'"));
        location.put("AddressLine1", json.optString("AddressLine1"));
        location.put("Suburb", json.optString("Suburb"));
        location.put("City", json.optString("City"));
        location.put("Area", json.optString("Area"));
        location.put("PostCode", json.optString("PostCode"));
        location.put("Ref", json.optString("Ref"));
        location.put("AssetCount", json.optInt("AssetCount"));
        location.put("Lat", Double.parseDouble(json.optString("Lat").replaceAll(",", ".")));
        location.put("Lng", Double.parseDouble(json.optString("Lng").replaceAll(",", ".")));
        location.put("ChildLocations", json.optJSONArray("ChildLocations"));
        location.put("Documents", parseImagesAndWords(json.optJSONArray("Documents")));
        location.put("Images", parseImagesAndWords(json.optJSONArray("Images")));*/
        return location;
    }

    /**
     * Parse images and documents (=words).
     *
     * @param images
     * @return
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
        map.put("LastUpdated", image.optString("LastUpdated"));
        map.put("URI", image.optString("URI"));
        map.put("Description", image.optString("Description"));
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

}
