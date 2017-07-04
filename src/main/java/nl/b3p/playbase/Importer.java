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
import nl.b3p.loader.util.DbUtilsGeometryColumnConverter;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Asset;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
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

    
    protected void saveAsset(Asset asset, ImportReport report) throws NamingException, SQLException {
        
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
        Integer locationId = asset.getLocation();//getLocation(asset);
        Integer assetTypeId = asset.getType_(); //getAssetType(asset);
        Integer equipmentTypeId = asset.getEquipment();// getEquipmentType(asset);
        Integer id = null;
        Object geom = null;
        
        ResultSetHandler<Asset> handler = new BeanHandler(Asset.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
        
        try {
            geom = geometryConverter.createNativePoint(asset.getLatitude(), asset.getLongitude(), 4326);
        } catch (ParseException ex) {
            log.error("Cannot parse geometry", ex);
        }catch (NullPointerException ex){
            log.info("no geom for asset");
        }
        if (assetExists(asset)) {
         
            id = getId(asset);

            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE ").append(DB.ASSETS_TABLE);
            sb.append(" set installeddate = ?,");
            sb.append("location = ?,");
            sb.append("name = ?,");
            sb.append("type_ = ?,");
            sb.append("equipment = ?,");
            sb.append("latitude = ?,");
            sb.append("longitude = ?,");
            sb.append("priceindexation = ?,");
            sb.append("priceinstallation = ?,");
            sb.append("pricemaintenance = ?,");
            sb.append("pricepurchase = ?,");
            sb.append("pricereinvestment = ?,");
            sb.append("depth = ?,");
            sb.append("width = ?,");
            sb.append("height = ?,");
            sb.append("endoflifeyear = ?,");
            sb.append("freefallheight = ?,");
            sb.append("safetyzonelength = ?,");
            sb.append("safetyzonewidth = ?,");
            sb.append("manufacturer = ?,");
            sb.append("material = ?,");
            sb.append("product = ?,");
            sb.append("productid = ?,");
            sb.append("productvariantid = ?,");
            sb.append("serialnumber = ?,");
            sb.append("geom = ?,");
            sb.append("pm_guid = ?");
            sb.append(" WHERE id = ").append(id);
            DB.qr().update(sb.toString(),asset.getInstalleddate(),asset.getLocation(), asset.getName(),asset.getType_(), asset.getEquipment(), asset.getLatitude(), 
                    asset.getLongitude(), asset.getPriceindexation(), asset.getPriceinstallation(), asset.getPricemaintenance(), asset.getPricepurchase(), asset.getPricereinvestment(),
                    asset.getDepth(), asset.getWidth(), asset.getHeight(), asset.getEndoflifeyear(), asset.getFreefallheight(), asset.getSafetyzonelength(), asset.getSafetyzonewidth(), 
                    asset.getManufacturer(),asset.getMaterial(), asset.getProduct(), asset.getProductid(), asset.getProductvariantid(), asset.getSerialnumber(),geom, asset.getPm_guid());
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
            sb.append("VALUES(  ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
    
            Asset as = DB.qr().insert(sb.toString(), handler,asset.getInstalleddate(),asset.getLocation(), asset.getName(),asset.getType_(), asset.getEquipment(), asset.getLatitude(), 
                    asset.getLongitude(), asset.getPriceindexation(), asset.getPriceinstallation(), asset.getPricemaintenance(), asset.getPricepurchase(), asset.getPricereinvestment(),
                    asset.getDepth(), asset.getWidth(), asset.getHeight(), asset.getEndoflifeyear(), asset.getFreefallheight(), asset.getSafetyzonelength(), asset.getSafetyzonewidth(), 
                    asset.getManufacturer(),asset.getMaterial(), asset.getProduct(), asset.getProductid(), asset.getProductvariantid(), asset.getSerialnumber(),geom, asset.getPm_guid());
            id = as.getId();
            report.increaseInserted();
        }
        
        
        saveAssetsAgeCategories(asset, id);
        saveImagesAndWords(asset.getImages(), id, locationId, DB.IMAGES_TABLE);
        saveImagesAndWords(asset.getDocuments(), id, locationId, DB.ASSETS_DOCUMENTS_TABLE);
    }
    
    protected boolean assetExists(Asset asset) {
       return getId(asset) != null;
    }
    
    protected Integer getId(Asset asset){
         try {
            ResultSetHandler<Asset> handler = new BeanHandler(Asset.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            Asset o;
            if (asset.getPm_guid() == null) {
                int equipment = asset.getEquipment();
                StringBuilder sb = new StringBuilder();
                sb.append("select * from ");
                sb.append(DB.ASSETS_TABLE);
                sb.append(" where location = ? and equipment = ?");
            
                o = DB.qr().query(sb.toString(), handler, asset.getLocation(), equipment);
                
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("select * from ");
                sb.append(DB.ASSETS_TABLE);
                sb.append(" where pm_guid = '");
                sb.append(asset.getPm_guid());
                sb.append("';");
                o = DB.qr().query(sb.toString(), handler);
            }
            return o != null ? o.getId() : null;
        } catch (NamingException | SQLException ex) {
            log.error("Cannot query if asset exists: ", ex);
            return null;
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
  
    protected void saveAssetsAgeCategory(Integer location, Integer[] agecategories) throws NamingException, SQLException {
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
    
    protected Integer getAssetType(String type){
        Integer id = assetTypes.get(type);
        return id;
    }
    
    protected Integer getEquipmentType(String type){
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

    protected void saveAssetsAgeCategories(Asset asset, Integer location) throws NamingException, SQLException {
        // delete old entries
        DB.qr().update("DELETE FROM " + DB.ASSETS_AGECATEGORIES_TABLE + " WHERE location_equipment = " + location);
        saveAssetsAgeCategory(location, asset.getAgecategories());
    }

}
