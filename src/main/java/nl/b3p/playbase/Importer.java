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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import nl.b3p.loader.jdbc.GeometryJdbcConverter;
import nl.b3p.loader.jdbc.GeometryJdbcConverterFactory;
import nl.b3p.playbase.db.DB;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Meine Toonen
 */
public abstract class Importer {
    private static final Log log = LogFactory.getLog("Importer");
    private GeometryJdbcConverter geometryConverter;
    
    private Map<String, Integer> assetTypes;
    private Map<String, Integer> equipmentTypes;
    
    protected Map<String,Map<String,Integer>> locationTypes;
    protected Map<String,Integer> facilityTypes;
    protected Map<String,Integer> accessibilityTypes;
    protected Map<String,Integer> agecategoryTypes;

    public Importer(){
       
        ArrayListHandler rsh = new ArrayListHandler();
        try {
            assetTypes = new HashMap<>();
            List<Object[]> o = DB.qr().query("SELECT id, catasset from " + DB.ASSETS_TYPE_GROUP_LIST_TABLE, rsh);
            for (Object[] type : o) {
                Integer id = (Integer) type[0];
                String cat = (String) type[1];
                assetTypes.put(cat, id);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot initialize playmapping assettypes:", ex);
        }
        try {
            equipmentTypes = new HashMap<>();
            List<Object[]> o = DB.qr().query("SELECT id, equipment from " + DB.LIST_EQUIPMENT_TYPE_TABLE, rsh);
            for (Object[] type : o) {
                Integer id = (Integer) type[0];
                String cat = (String) type[1];
                equipmentTypes.put(cat, id);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot initialize playmapping assettypes:", ex);
        }
       
        try {
            geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(DB.getConnection());
        } catch (NamingException | SQLException ex) {
            log.error("Cannot get geometryConverter: ", ex);
        }
         
        try {
            locationTypes = new HashMap<>();
            List<Object[]> o = DB.qr().query("SELECT id, category, main from " + DB.LIST_CATEGORY_TABLE, rsh);
            for (Object[] type : o) {
                Integer id = (Integer) type[0];
                String category = (String) type[1];
                String main = (String) type[2];
                if(!locationTypes.containsKey(main)){
                    locationTypes.put(main, new HashMap<String, Integer>());
                }
                locationTypes.get(main).put(category, id);
                
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot initialize playadvisor location types:", ex);
        }

        try {
            facilityTypes = new HashMap<>();
            List<Object[]> o = DB.qr().query("SELECT id, facility from " + DB.LIST_FACILITIES_TABLE, rsh);
            for (Object[] type : o) {
                Integer id = (Integer) type[0];
                String facility = (String) type[1];
                facilityTypes.put(facility, id);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot initialize playadvisory facilitytypes:", ex);
        }
        
        try {
            accessibilityTypes = new HashMap<>();
            List<Object[]> o = DB.qr().query("SELECT id, accessibility from " + DB.LIST_ACCESSIBILITY_TABLE, rsh);
            for (Object[] type : o) {
                Integer id = (Integer) type[0];
                String accesiblity = (String) type[1];
                accessibilityTypes.put(accesiblity.toLowerCase(), id);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot initialize playadvisory facilitytypes:", ex);
        }
        
         try {
            agecategoryTypes = new HashMap<>();
            List<Object[]> o = DB.qr().query("SELECT id, agecategory from " + DB.LIST_AGECATEGORIES_TABLE, rsh);
            for (Object[] type : o) {
                Integer id = (Integer) type[0];
                String agecategory = (String) type[1];
                agecategoryTypes.put(agecategory.toLowerCase(), id);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot initialize playadvisory facilitytypes:", ex);
        }
    }
    
    protected int saveLocation(Map<String, Object> location, ImportReport report) throws NamingException, SQLException {
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
        Integer id = null;
        if (!exists) {
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.LOCATION_TABLE);
            sb.append("(title,");
            sb.append("latitude,");
            sb.append("longitude,");
            sb.append("geom,");
            sb.append("pa_id,");
            sb.append("pm_guid) ");
            sb.append("VALUES( ");
            sb.append("\'").append(location.get("Name")).append("\',");
            sb.append(location.get("Lat")).append(",");
            sb.append(location.get("Lng")).append(",");
            sb.append("?,");
            sb.append("\'").append(location.get("pa_id")).append("\',");
            sb.append("\'").append(location.get("ID")).append("\');");
            id = DB.qr().insert(sb.toString(), new ScalarHandler<Integer>(),geom);
            report.increaseInserted();
            List<Map<String,Object>> images = (List<Map<String,Object>>) location.get("images");
            saveImagesAndWords(images, null, id, DB.IMAGES_TABLE);
        } else {
            sb = new StringBuilder();
            sb.append("select id from ");
            
            sb.append(DB.LOCATION_TABLE);
              if(location.containsKey("ID")){
                sb.append(" where pm_guid = '");
                sb.append(location.get("ID"));
            } else if (location.containsKey("pa_id")) {
                sb.append(" where pa_id = '");
                sb.append(location.get("pa_id"));
            }
            sb.append("';");
            id = (Integer) DB.qr().query(sb.toString(), new ScalarHandler<>());

            
            sb = new StringBuilder();
            sb.append("update ");
            sb.append(DB.LOCATION_TABLE);
            sb.append(" ");
            sb.append("SET title = ");
            sb.append("\'").append(location.get("Name")).append("\',");
            sb.append("latitude = ");
            sb.append(location.get("Lat")).append(",");
            sb.append("longitude = ");
            sb.append(location.get("Lng")).append(", ");
            sb.append("geom = ? ");
            
             if(location.containsKey("ID")){
                sb.append("where pm_guid = '");
                sb.append(location.get("ID"));
            } else if (location.containsKey("pa_id")) {
                sb.append("where pa_id = '");
                sb.append(location.get("pa_id"));
            }
            sb.append("';");
            DB.qr().update(sb.toString(),geom);
            report.increaseUpdated();
        }
        return id;        
    }

    protected boolean locationExists(Map<String, Object> location) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select * from ");
            sb.append(DB.LOCATION_TABLE);
            if(location.containsKey("ID")){
                sb.append(" where pm_guid = '");
                sb.append(location.get("ID"));
            } else if (location.containsKey("pa_id")) {
                sb.append(" where pa_id = '");
                sb.append(location.get("pa_id"));
            }
            sb.append("';");
            ArrayListHandler rsh = new ArrayListHandler();
            List<Object[]> o = DB.qr().query(sb.toString(), rsh);
            return o.size() > 0;
        } catch (NamingException | SQLException ex) {
            log.error("Cannot query if location exists: ", ex);
            return false;
        }
    }

    
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
        Integer equipmentTypeId = getEquipmentType(asset);
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
            sb.append("equipment = ");
            sb.append(equipmentTypeId).append(",");
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
            sb.append("equipment,");
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
            sb.append(equipmentTypeId).append(",");
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
        saveImagesAndWords((List<Map<String, Object>>)asset.get("Images"), id, locationId, DB.IMAGES_TABLE);
        saveImagesAndWords((List<Map<String, Object>>)asset.get("Documents"), id, locationId, DB.ASSETS_DOCUMENTS_TABLE);
    }
    
    protected boolean assetExists(Map<String, Object> asset) {
        try {
            if (asset.get("ID") == null) {
                int assetType = getEquipmentType(asset);
                StringBuilder sb = new StringBuilder();
                sb.append("select * from ");
                sb.append(DB.ASSETS_TABLE);
                sb.append(" where location = ? and equipment = ?");
            
                ArrayListHandler rsh = new ArrayListHandler();
                List<Object[]> o = DB.qr().query(sb.toString(), rsh, asset.get("LocationPAID"), assetType);
                return o.size() > 0;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("select * from ");
                sb.append(DB.ASSETS_TABLE);
                sb.append(" where pm_guid = '");
                sb.append(asset.get("ID"));
                sb.append("';");
                ArrayListHandler rsh = new ArrayListHandler();
                List<Object[]> o = DB.qr().query(sb.toString(), rsh);
                return o.size() > 0;
            }
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
  
    protected void saveAssetsAgeCategory(Integer location, List<Integer> agecategories) throws NamingException, SQLException {
        DB.qr().update("DELETE FROM " + DB.ASSETS_AGECATEGORIES_TABLE + " WHERE location_equipment = " + location);
        for (Integer agecategory : agecategories) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.ASSETS_AGECATEGORIES_TABLE);
            sb.append("(");
            sb.append("location_equipment,");
            sb.append("agecategory)");
            sb.append("VALUES( ");
            sb.append(location);
            sb.append(",");
            sb.append(agecategory);
            sb.append(");");
            DB.qr().insert(sb.toString(), new ScalarHandler<>());
        }
    }
  
    protected void saveLocationAgeCategory(Integer location, List<Integer> agecategories) throws NamingException, SQLException {
        for (Integer agecategory : agecategories) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.LOCATION_AGE_CATEGORY_TABLE);
            sb.append("(");
            sb.append("location,");
            sb.append("agecategory)");
            sb.append("VALUES( ");
            sb.append(location);
            sb.append(",");
            sb.append(agecategory);
            sb.append(");");
            DB.qr().insert(sb.toString(), new ScalarHandler<>());
        }
    }
    
    protected void saveImagesAndWords(List<Map<String, Object>> images, Integer assetId, Integer locationId, String table) throws NamingException, SQLException{
        DB.qr().update("DELETE FROM " + table + " WHERE equipment = " + assetId);
        if (images != null) {
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
    }    
    
    protected Integer getAssetType(Map<String, Object> asset){
        String type = (String)asset.get("AssetType");
        Integer id = assetTypes.get(type);
        return id;
    }
    
    protected Integer getEquipmentType(Map<String, Object> asset){
        String type = (String)asset.get("EquipmentType");
        Integer id = equipmentTypes.get(type);
        return id;
    }

    protected Integer getLocation(Map<String, Object> asset) throws NamingException, SQLException {
        ArrayListHandler rsh = new ArrayListHandler();
        String locationId = null;
        if(asset.containsKey("LocationPMID")){
            locationId = (String) asset.get("LocationPMID");
        }else if (asset.containsKey("LocationPAID")){
            // In Playadvisor we already retrieved the database id for the location.
            return (Integer)asset.get("LocationPAID");
        }
        
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
    
    protected abstract void saveAssetsAgeCategories(Map<String, Object> asset, Integer id) throws NamingException, SQLException;
}
