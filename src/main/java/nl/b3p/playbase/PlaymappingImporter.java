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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Meine Toonen
 */
public class PlaymappingImporter extends Importer{

    private static final Log log = LogFactory.getLog("PlaymappingProcessor");
    

    public void init() {
        
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
     * Parse images and documents (=words).
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
