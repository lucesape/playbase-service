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

import com.vividsolutions.jts.io.ParseException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import nl.b3p.loader.jdbc.GeometryJdbcConverter;
import nl.b3p.loader.jdbc.GeometryJdbcConverterFactory;
import nl.b3p.playbase.db.DB;
import org.apache.commons.dbutils.QueryRunner;
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
public class PlaymappingProcessor {

    private static final Log log = LogFactory.getLog("PlaymappingProcessor");
    private Map<String, List<Integer>> agecategories;
    private Map<String, Integer> types;

    private final String AGECATEGORY_TODDLER_KEY = "AgeGroupToddlers";
    private final String AGECATEGORY_JUNIOR_KEY = "AgeGroupJuniors";
    private final String AGECATEGORY_SENIOR_KEY = "AgeGroupSeniors";

    private final String[] AGECATEGORY_TODDLER = {"0 - 5 jaar"};
    private final String[] AGECATEGORY_JUNIOR = {"6 - 11 jaar", "12 - 18 jaar"};
    private final String[] AGECATEGORY_SENIOR = {"Volwassenen", "Senioren"};
    
    private GeometryJdbcConverter geometryConverter;

    public void init() {
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
        try {
            types = new HashMap<>();
            List<Object[]> o = DB.qr().query("SELECT id, catasset from " + DB.ASSETS_TYPE_GROUP_LIST_TABLE, rsh);
            for (Object[] type : o) {
                Integer id = (Integer) type[0];
                String cat = (String) type[1];
                types.put(cat, id);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot initialize playmapping processor:", ex);
        }
        try {
            geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(DB.getConnection());
        } catch (NamingException | SQLException ex) {
            log.error("Cannot get geometryConverter: ", ex);
        } 
    }

    public ImportReport processAssets(String assetsString) throws NamingException, SQLException {
        List<Map<String, Object>> assets = parseAssets(assetsString);
        ImportReport report = new ImportReport("assets");
        for (Map<String, Object> asset : assets) {
            try{
                saveAsset(asset, report);
            }catch(NamingException | SQLException | IllegalArgumentException ex){
                log.error("Cannot save asset: " + ex.getLocalizedMessage());
                report.addError(ex.getLocalizedMessage());
            }
        }
        return report;
    }

    public ImportReport processLocations(String temp) throws NamingException, SQLException {
        List<Map<String, Object>> childLocations = parseChildLocations(temp);
        ImportReport report = new ImportReport("locaties");
        for (Map<String, Object> childLocation : childLocations) {
            saveLocation(childLocation, report);
        }
        return report;
    }

    // <editor-fold desc="Assets" defaultstate="collapsed">
    protected void saveAsset(Map<String, Object> asset, ImportReport report) throws NamingException, SQLException {
        
        // xlocation
        // xagecategories
        // xcategories
        // xequipment
        // ximages
        // xdocuments
        // linked assets
        // facilities
        // accessibility
        // check if asset exists
        // ja: update 
       // nee: insert
        Integer locationId = getLocation(asset);
        Integer assetTypeId = getAssetType(asset);
        Integer id = null;
        Object geom = null;
        
        try {
            geom = geometryConverter.createNativePoint((double)asset.get("Lat"), (double)asset.get("Lng"), 4326);
        } catch (ParseException ex) {
            log.error("Cannot parse geometry", ex);
        }catch (NullPointerException ex){
            log.info("no geom for asset");
        }
        if (assetExists(asset)) {
         
            StringBuilder sb = new StringBuilder();
            sb.append("select id from ");
            sb.append(DB.ASSETS_TABLE);
            sb.append(" where pm_guid = '");
            sb.append(asset.get("ID"));
            sb.append("';");
            id = (Integer) DB.qr().query(sb.toString(), new ScalarHandler<>());

            int a = 0;
            sb = new StringBuilder();
            sb.append("UPDATE ").append(DB.ASSETS_TABLE);
            sb.append(" set installeddate = ");
            valueOrNull(sb, "InstalledDate", asset);
            sb.append("location = ");
            sb.append(locationId).append(",");
            sb.append("name = ");
            valueOrNull(sb, "Name", asset);
            sb.append("type_ = ");
            sb.append(assetTypeId).append(",");
            sb.append("latitude = ");
            sb.append(asset.get("Lat")).append(",");
            sb.append("longitude = ");
            sb.append(asset.get("Lng")).append(",");
            sb.append("priceindexation = ");
            sb.append(asset.get("PriceIndexation")).append(",");
            sb.append("priceinstallation = ");
            sb.append(asset.get("PriceInstallation")).append(",");
            sb.append("pricemaintenance = ");
            sb.append(asset.get("PriceMaintenance")).append(",");
            sb.append("pricepurchase = ");
            sb.append(asset.get("PricePurchase")).append(",");
            sb.append("pricereinvestment = ");
            sb.append(asset.get("PriceReInvestment")).append(",");
            sb.append("depth = ");
            sb.append(asset.get("Depth")).append(",");
            sb.append("width = ");
            sb.append(asset.get("Width")).append(",");
            sb.append("height = ");
            sb.append(asset.get("Height")).append(",");
            sb.append("endoflifeyear = ");
            sb.append(asset.get("EndOfLifeYear")).append(",");
            sb.append("freefallheight = ");
            sb.append(asset.get("FreefallHeight")).append(",");
            sb.append("safetyzonelength = ");
            sb.append(asset.get("SafetyZoneLength")).append(",");
            sb.append("safetyzonewidth = ");
            sb.append(asset.get("SafetyZoneWidth")).append(",");
            sb.append("manufacturer = ");
            valueOrNull(sb, "Manufacturer", asset);
            sb.append("material = ");
            valueOrNull(sb, "Material", asset);
            sb.append("product = ");
            valueOrNull(sb, "Product", asset);
            sb.append("productid = ");
            valueOrNull(sb, "ProductID", asset);
            sb.append("productvariantid = ");
            valueOrNull(sb, "ProductVariantID", asset);
            sb.append("serialnumber = ");
            valueOrNull(sb, "SerialNumber", asset);
            sb.append("geom = ?,");
            sb.append("pm_guid = ");
            sb.append("'").append(asset.get("ID")).append("'");
            sb.append(" WHERE id = ").append(id);
            DB.qr().update(sb.toString(),geom);
            report.increaseUpdated();            
        }else{
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.ASSETS_TABLE);
            sb.append("(");
            sb.append("installeddate,");
            sb.append("location,");
            sb.append("name,");
            sb.append("type_,");
            sb.append("latitude,");
            sb.append("longitude,");
            sb.append("priceindexation,");
            sb.append("priceinstallation,");
            sb.append("pricemaintenance,");
            sb.append("pricepurchase,");
            sb.append("pricereinvestment,");
            sb.append("depth,");
            sb.append("width,");
            sb.append("height,");
            sb.append("endoflifeyear,");
            sb.append("freefallheight,");
            sb.append("safetyzonelength,");
            sb.append("safetyzonewidth,");
            sb.append("manufacturer,");
            sb.append("material,");
            sb.append("product,");
            sb.append("productid,");
            sb.append("productvariantid,");
            sb.append("serialnumber,");
            sb.append("geom,");
            sb.append("pm_guid) ");
            sb.append("VALUES( ");
            valueOrNull(sb, "InstalledDate", asset);
            sb.append(locationId).append(",");
            valueOrNull(sb, "Name", asset);
            sb.append(assetTypeId).append(",");
            sb.append(asset.get("Lat")).append(",");
            sb.append(asset.get("Lng")).append(",");
            sb.append(asset.get("PriceIndexation")).append(",");
            sb.append(asset.get("PriceInstallation")).append(",");
            sb.append(asset.get("PriceMaintenance")).append(",");
            sb.append(asset.get("PricePurchase")).append(",");
            sb.append(asset.get("PriceReInvestment")).append(",");
            sb.append(asset.get("Depth")).append(",");
            sb.append(asset.get("Width")).append(",");
            sb.append(asset.get("Height")).append(",");
            sb.append(asset.get("EndOfLifeYear")).append(",");
            sb.append(asset.get("FreefallHeight")).append(",");
            sb.append(asset.get("SafetyZoneLength")).append(",");
            sb.append(asset.get("SafetyZoneWidth")).append(",");
            valueOrNull(sb, "Manufacturer", asset);
            valueOrNull(sb, "Material", asset);
            valueOrNull(sb, "Product", asset);
            valueOrNull(sb, "ProductID", asset);
            valueOrNull(sb, "ProductVariantID", asset);
            valueOrNull(sb, "SerialNumber", asset);
            sb.append("?,");
            sb.append("'").append(asset.get("ID")).append("');");
            id = DB.qr().insert(sb.toString(), new ScalarHandler<Integer>(),geom);
            report.increaseInserted();
        }
        
        
        saveAssetsAgeCategories(asset, id);
        saveImagesAndWords((List<Map<String, Object>>)asset.get("Images"), id, locationId, DB.ASSETS_IMAGES_TABLE);
        saveImagesAndWords((List<Map<String, Object>>)asset.get("Documents"), id, locationId, DB.ASSETS_DOCUMENTS_TABLE);
    }
    
    protected boolean assetExists(Map<String, Object> asset) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select * from ");
            sb.append(DB.ASSETS_TABLE);
            sb.append(" where pm_guid = '");
            sb.append(asset.get("ID"));
            sb.append("';");
            ArrayListHandler rsh = new ArrayListHandler();
            List<Object[]> o = DB.qr().query(sb.toString(), rsh);
            return o.size() > 0;
        } catch (NamingException | SQLException ex) {
            log.error("Cannot query if asset exists: ", ex);
            return false;
        }
    }
    
    protected void valueOrNull (StringBuilder sb, String type,Map<String, Object> valueMap){
        String value = (String)valueMap.get(type);
        
        if(value == null || value.isEmpty()){
            sb.append("null,");
        }else{
            sb.append("'").append(value).append("',");
        }
    }
    
    protected Integer getAssetType(Map<String, Object> asset){
        String type = (String)asset.get("AssetType");
        Integer id = types.get(type);
        return id;
    }

    protected Integer getLocation(Map<String, Object> asset) throws NamingException, SQLException {
        ArrayListHandler rsh = new ArrayListHandler();
        String locationId = (String) asset.get("LocationID");
        List<Object[]> o = DB.qr().query("SELECT id FROM " + DB.LOCATION_TABLE + " WHERE pm_guid = '" + locationId + "'", rsh);
        if(o.isEmpty()){
            String locationName = (String) asset.get("LocationName");
            throw new IllegalArgumentException ("Kan geen locatie vinden met naam (id) " + locationName + "(" + locationId + ")");
        }else if (o.size() == 1){
            return (Integer) o.get(0)[0];
        }else{
            throw new IllegalArgumentException ("Meerdere locaties gevonden met id " + locationId);
        }
    }

    protected void saveAssetsAgeCategories(Map<String, Object> asset, Integer id) throws NamingException, SQLException {
        boolean toddler = (boolean) asset.get(AGECATEGORY_TODDLER_KEY);
        boolean junior = (boolean) asset.get(AGECATEGORY_JUNIOR_KEY);
        boolean senior = (boolean) asset.get(AGECATEGORY_SENIOR_KEY);

        // delete old entries
        DB.qr().update("DELETE FROM " + DB.ASSETS_AGECATEGORIES_TABLE + " WHERE location_equipment = " + id);
        if (toddler) {
            saveAgeCategory(id, agecategories.get(AGECATEGORY_TODDLER_KEY));
        }

        if (junior) {
            saveAgeCategory(id, agecategories.get(AGECATEGORY_JUNIOR_KEY));
        }

        if (senior) {
            saveAgeCategory(id, agecategories.get(AGECATEGORY_SENIOR_KEY));
        }
    }

    protected void saveAgeCategory(Integer id, List<Integer> agecategories) throws NamingException, SQLException {
        for (Integer agecategory : agecategories) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.ASSETS_AGECATEGORIES_TABLE);
            sb.append("(");
            sb.append("location_equipment,");
            sb.append("agecategory)");
            sb.append("VALUES( ");
            sb.append(id);
            sb.append(",");
            sb.append(agecategory);
            sb.append(");");
            DB.qr().insert(sb.toString(), new ScalarHandler<>());
        }
    }
    
    protected void saveImagesAndWords(List<Map<String, Object>> images, Integer assetId, Integer locationId, String table) throws NamingException, SQLException{
        DB.qr().update("DELETE FROM " + table + " WHERE equipment = " + assetId);
        for (Map<String, Object> image : images) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(table);
            sb.append("(");
            sb.append("caption,");
            sb.append("url,");
            sb.append("location,");
            sb.append("equipment,");
            sb.append("pm_guid)");
            sb.append("VALUES( ");
            valueOrNull(sb, "Description", image);
            sb.append("'").append(image.get("URI")).append("',");
            sb.append(locationId).append(",");
            sb.append(assetId).append(",");
            sb.append("'").append(image.get("ID")).append("');");
            DB.qr().insert(sb.toString(), new ScalarHandler<>());
        }
    }

    protected List<Map<String, Object>> parseAssets(String assetsString) {
        List<Map<String, Object>> assets = new ArrayList<>();
        JSONArray assetsArray = new JSONArray(assetsString);

        for (int i = 0; i < assetsArray.length(); i++) {
            JSONObject asset = assetsArray.getJSONObject(i);
            assets.add(parseAsset(asset));
            String linkedAssets = asset.getJSONArray("LinkedAssets").toString();
            assets.addAll(parseAssets(linkedAssets));
        }
        return assets;
    }

    protected Map<String, Object> parseAsset(JSONObject assetJSON) {
        Map<String, Object> asset = new HashMap<>();
        asset.put("$id", assetJSON.optString("$id"));
        asset.put("ID", assetJSON.optString("ID"));
        asset.put("LocationID", assetJSON.optString("LocationID"));
        asset.put("LocationName", assetJSON.optString("LocationName").replaceAll("\'", "\'\'"));
        asset.put("LastUpdated", assetJSON.optString("LastUpdated"));
        asset.put("Name", assetJSON.optString("Name").replaceAll("\'", "\'\'"));
        asset.put("AssetType", assetJSON.optString("AssetType"));
        asset.put("Manufacturer", assetJSON.optString("Manufacturer"));
        asset.put("Product", assetJSON.optString("Product"));
        asset.put("SerialNumber", assetJSON.optString("SerialNumber"));
        asset.put("Material", assetJSON.optString("Material"));
        asset.put("InstalledDate", assetJSON.optString("InstalledDate"));
        asset.put("EndOfLifeYear", assetJSON.optInt("EndOfLifeYear"));
        asset.put("ProductID", assetJSON.optString("ProductID"));
        asset.put("ProductVariantID", assetJSON.optString("ProductVariantID"));
        asset.put("Height", assetJSON.optInt("Height"));
        asset.put("Depth", assetJSON.optInt("Depth"));
        asset.put("Width", assetJSON.optInt("Width"));
        asset.put("FreefallHeight", assetJSON.optInt("FreefallHeight"));
        asset.put("SafetyZoneLength", assetJSON.optInt("SafetyZoneLength"));
        asset.put("SafetyZoneWidth", assetJSON.optInt("SafetyZoneWidth"));
        asset.put("AgeGroupToddlers", assetJSON.optBoolean("AgeGroupToddlers"));
        asset.put("AgeGroupJuniors", assetJSON.optBoolean("AgeGroupJuniors"));
        asset.put("AgeGroupSeniors", assetJSON.optBoolean("AgeGroupSeniors"));
        asset.put("PricePurchase", assetJSON.optDouble("PricePurchase"));
        asset.put("PriceInstallation", assetJSON.optDouble("PriceInstallation"));
        asset.put("PriceReInvestment", assetJSON.optDouble("PriceReInvestment"));
        asset.put("PriceMaintenance", assetJSON.optDouble("PriceMaintenance"));
        asset.put("PriceIndexation", assetJSON.optDouble("PriceIndexation"));
        asset.put("Lat", Double.parseDouble(assetJSON.optString("Lat").replaceAll(",", ".")));
        asset.put("Lng", Double.parseDouble(assetJSON.optString("Lng").replaceAll(",", ".")));
        asset.put("Documents", parseImagesAndWords(assetJSON.optJSONArray("Documents")));
        asset.put("Hyperlinks", assetJSON.optJSONArray("Hyperlinks"));
        asset.put("Images", parseImagesAndWords(assetJSON.optJSONArray("Images")));
        asset.put("LinkedAssets", assetJSON.optJSONArray("LinkedAssets"));
        return asset;
    }
    // </editor-fold>

    // <editor-fold desc="Locations" defaultstate="collapsed">
    protected void saveLocation(Map<String, Object> location, ImportReport report) throws NamingException, SQLException {
        StringBuilder sb = new StringBuilder();
        boolean exists = locationExists(location);        
        Object geom = null;
        try {
            geom = geometryConverter.createNativePoint((double)location.get("Lat"), (double)location.get("Lng"), 4326);
        } catch (ParseException ex) {
            log.error("Cannot parse geometry", ex);
        }catch (NullPointerException ex){
            log.info("no geom for asset");
        }
        if (!exists) {
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.LOCATION_TABLE);
            sb.append("(title,");
            sb.append("latitude,");
            sb.append("longitude,");
            sb.append("geom,");
            sb.append("pm_guid) ");
            sb.append("VALUES( ");
            sb.append("\'").append(location.get("Name")).append("\',");
            sb.append(location.get("Lat")).append(",");
            sb.append(location.get("Lng")).append(",");
            sb.append("?,");
            sb.append("\'").append(location.get("ID")).append("\');");
            report.increaseInserted();
            DB.qr().update(sb.toString(),geom);
        } else {
            sb = new StringBuilder();
            sb.append("update ");
            sb.append(DB.LOCATION_TABLE);
            sb.append(" ");
            sb.append("SET title = ");
            sb.append("\'").append(location.get("Name")).append("\',");
            sb.append("latitude = ");
            sb.append(location.get("Lat")).append(",");
            sb.append("longitude = ");
            sb.append(location.get("Lng")).append("");
            sb.append("geom = ?,");
            sb.append(" where pm_guid = '");
            sb.append(location.get("ID"));
            sb.append("';");
            report.increaseUpdated();
            DB.qr().update(sb.toString(),geom);
        }
    }

    protected boolean locationExists(Map<String, Object> location) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select * from ");
            sb.append(DB.LOCATION_TABLE);
            sb.append(" where pm_guid = '");
            sb.append(location.get("ID"));
            sb.append("';");
            ArrayListHandler rsh = new ArrayListHandler();
            List<Object[]> o = DB.qr().query(sb.toString(), rsh);
            return o.size() > 0;
        } catch (NamingException | SQLException ex) {
            log.error("Cannot query if location exists: ", ex);
            return false;
        }
    }

    protected List<Map<String, Object>> parseChildLocations(String locations) {
        List<Map<String, Object>> locs = new ArrayList<>();
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

    protected Map<String, Object> parseLocation(JSONObject json) {
        Map<String, Object> location = new HashMap<>();
        location.put("$id", json.optString("$id"));
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
        location.put("Images", parseImagesAndWords(json.optJSONArray("Images")));
        return location;
    }

    /**
     * Parse images and documents
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
    // </editor-fold>

}
