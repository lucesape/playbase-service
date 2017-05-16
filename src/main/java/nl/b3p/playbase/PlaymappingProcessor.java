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
import nl.b3p.playbase.db.DB;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author meine
 */
public class PlaymappingProcessor {
    
    public int processAssets(String assetsString) throws NamingException, SQLException {
        StringBuilder sb;
        List<Map<String, Object>> assets = parseAssets(assetsString);
        int retval = 0;
        for (Map<String, Object> asset : assets) {
            sb = new StringBuilder();
            sb.append("insert into pm_assets_api (");
            sb.append("	 \"id\",");
            sb.append("      \"locationid\",");
            sb.append("      \"locationname\",");
            sb.append("      \"lastupdated\",");
            sb.append("      \"name\",");
            sb.append("      \"assettype\",");
            sb.append("      \"manufacturer\",");
            sb.append("      \"serialnumber\",");
            sb.append("      \"installeddate\",");
            sb.append("      \"endoflifeyear\",");
            sb.append("      \"safetyzonelength\",");
            sb.append("      \"safetyzonewidth\",");
            sb.append("      \"agegrouptoddlers\",");
            sb.append("      \"agegroupjuniors\",");
            sb.append("      \"agegroupseniors\",");
            sb.append("      \"pricepurchase\",");
            sb.append("      \"priceinstallation\",");
            sb.append("      \"pricereinvestment\",");
            sb.append("      \"pricemaintenance\",");
            sb.append("      \"priceindexation\",");
            sb.append("      \"lat\",");
            sb.append("      \"lng\"");
            sb.append(") ");
            sb.append("VALUES (");
            sb.append("'").append(asset.get("ID")).append("',");
            sb.append("'").append(asset.get("LocationID")).append("',");
            sb.append("'").append(asset.get("LocationName")).append("',");
            sb.append("'").append(asset.get("LastUpdated")).append("',");
            sb.append("'").append(asset.get("Name")).append("',");
            sb.append("'").append(asset.get("AssetType")).append("',");
            sb.append("'").append(asset.get("Manufacturer")).append("',");
            sb.append("'").append(asset.get("SerialNumber")).append("',");
            sb.append("'").append(asset.get("InstalledDate")).append("',");
            sb.append("'").append(asset.get("EndOfLifeYear")).append("',");
            sb.append("'").append(asset.get("SafetyZoneLength")).append("',");
            sb.append("'").append(asset.get("SafetyZoneWidth")).append("',");
            sb.append("'").append(asset.get("AgeGroupToddlers")).append("',");
            sb.append("'").append(asset.get("AgeGroupJuniors")).append("',");
            sb.append("'").append(asset.get("AgeGroupSeniors")).append("',");
            sb.append("'").append(asset.get("PricePurchase")).append("',");
            sb.append("'").append(asset.get("PriceInstallation")).append("',");
            sb.append("'").append(asset.get("PriceReInvestment")).append("',");
            sb.append("'").append(asset.get("PriceMaintenance")).append("',");
            sb.append("'").append(asset.get("PriceIndexation")).append("',");
            sb.append("'").append(asset.get("Lat")).append("',");
            sb.append("'").append(asset.get("Lng")).append("');");
            retval += DB.qr().update(sb.toString());
        }
        return retval;
    }

    public int processLocations(String temp) throws NamingException, SQLException {
        List<Map<String, Object>> childLocations = parseChildLocations(temp);
        int retval = 0;
        for (Map<String, Object> childLocation : childLocations) {
            //insert childlocations from childlocations from childlocations
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append("    pm_locations_api ");
            sb.append("    (");
            sb.append("        \"id\",");
            sb.append("        \"name\",");
            sb.append("        \"lastupdated\",");
            sb.append("        \"lat\",");
            sb.append("        \"lng\"");
            sb.append("    )");
            sb.append("    VALUES(");
            sb.append("    ");
            sb.append("\'").append(childLocation.get("ID")).append("\',");
            sb.append("\'").append(childLocation.get("LastUpdated")).append("\',");
            sb.append("\'").append(childLocation.get("Name")).append("\',");
            sb.append("").append(childLocation.get("Lat")).append(",");
            sb.append("").append(childLocation.get("Lng")).append("");
            sb.append(");");
            retval += DB.qr().update(sb.toString());
        }
        return retval;
    }

    protected List<Map<String, Object>> parseAssets(String assetsString) {
        List<Map<String, Object>> assets = new ArrayList<>();
        JSONArray assetsArray = new JSONArray(assetsString);

        for (int i = 0; i < assetsArray.length(); i++) {
            JSONObject asset = assetsArray.getJSONObject(i);
            assets.add(parseAsset(asset));
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
        asset.put("Documents", assetJSON.optJSONArray("Documents"));
        asset.put("Hyperlinks", assetJSON.optJSONArray("Hyperlinks"));
        asset.put("Images", parseImages(assetJSON.optJSONArray("Images")));
        return asset;
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
        location.put("Documents", json.optJSONArray("Documents"));
        location.put("Images", parseImages(json.optJSONArray("Images")));
        return location;
    }

    protected List<Map<String, Object>> parseImages(JSONArray images) {
        List<Map<String, Object>> imagesList = new ArrayList<>();
        for (int i = 0; i < images.length(); i++) {
            JSONObject img = images.getJSONObject(i);
            Map<String, Object> image = parseImage(img);
            imagesList.add(image);
        }
        return imagesList;
    }

    protected Map<String, Object> parseImage(JSONObject image) {
        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("$id", image.optString("$id"));
        imageMap.put("ID", image.optString("ID"));
        imageMap.put("LastUpdated", image.optString("LastUpdated"));
        imageMap.put("URI", image.optString("URI"));
        imageMap.put("Description", image.optString("Description"));
        return imageMap;
    }

}
