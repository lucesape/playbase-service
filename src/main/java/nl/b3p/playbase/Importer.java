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
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import nl.b3p.loader.jdbc.GeometryJdbcConverter;
import nl.b3p.loader.jdbc.GeometryJdbcConverterFactory;
import nl.b3p.loader.util.DbUtilsGeometryColumnConverter;
import nl.b3p.playbase.ImportReport.ImportType;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Asset;
import nl.b3p.playbase.entities.Comment;
import nl.b3p.playbase.entities.Location;
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
    protected Map<String, Map<String, Integer>> locationTypes;
    protected Map<String, Integer> facilityTypes;
    protected Map<String, Integer> accessibilityTypes;
    protected Map<String, Integer> agecategoryTypes;
    protected Map<String, Integer> parkingTypes;
    
    public String postfix = "";

    public Importer() {

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

        
        try(Connection con = DB.getConnection()) {
            geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);
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
                if (!locationTypes.containsKey(main)) {
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
        try {
            parkingTypes = new HashMap<>();
            List<Object[]> o = DB.qr().query("SELECT id, parking from " + DB.LIST_PARKING_TABLE, rsh);
            for (Object[] type : o) {
                Integer id = (Integer) type[0];
                String parking = (String) type[1];
                parkingTypes.put(parking.toLowerCase(), id);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot initialize playadvisory parkingtypes:", ex);
        }
    }

    // <editor-fold desc="Locations" defaultstate="collapsed">
    public int saveLocation(Location location, ImportReport report) throws NamingException, SQLException {
        StringBuilder sb = new StringBuilder();
        boolean exists = locationExists(location);
        Object geom = null;
        try {
            geom = geometryConverter.createNativePoint(location.getLatitude(), location.getLongitude(), 4326);
        } catch (ParseException ex) {
            log.error("Cannot parse geometry", ex);
        } catch (NullPointerException ex) {
            log.info("no geom for asset");
        }
        ResultSetHandler<Location> handler = new BeanHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
        Location savedLocation = null;
        Integer id = null;
        if (!exists) {
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.LOCATION_TABLE).append(postfix);
            sb.append("(title,");
            sb.append("latitude,");
            sb.append("longitude,");
            sb.append("geom,");
            sb.append("averagerating,");
            sb.append("pa_id,");
            sb.append("pm_guid) ");
            sb.append("VALUES( ?,?,?,?,?,?,?);");

            savedLocation = DB.qr().insert(sb.toString(), handler, location.getTitle(), location.getLatitude(), location.getLongitude(), geom, location.getAveragerating() != null ? location.getAveragerating() : 0, location.getPa_id(), location.getPm_guid());
            id = savedLocation.getId();
            report.increaseInserted(ImportType.LOCATION);
            List<Map<String, Object>> images = location.getImages();
            saveImagesAndWords(images, null, savedLocation.getId(), DB.IMAGES_TABLE + postfix, true);
        } else {
            id = getLocationId(location);

            sb = new StringBuilder();
            sb.append("update ");
            sb.append(DB.LOCATION_TABLE).append(postfix);
            sb.append(" ");
            sb.append("SET title = ?,");
            sb.append("latitude = ?,");
            sb.append("longitude = ?,");
            sb.append("geom = ?,");
            sb.append("averagerating = ?,");
            sb.append("pa_id = ?,");
            sb.append("pm_guid = ?");
            sb.append("where id = ?;");

            DB.qr().update(sb.toString(), location.getTitle(), location.getLatitude(), location.getLongitude(), geom, location.getAveragerating() != null ? location.getAveragerating() : 0, location.getPa_id(), location.getPm_guid(), id);
            report.increaseUpdated(ImportType.LOCATION);
        }
        return id;
    }

    public void saveLocationAgeCategory(Integer location, List<Integer> agecategories, boolean removeBeforeAdding) throws NamingException, SQLException {
        if(removeBeforeAdding){
            DB.qr().update("DELETE FROM " + DB.LOCATION_AGE_CATEGORY_TABLE + postfix + " WHERE location = " + location);
        }
        for (Integer agecategory : agecategories) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.LOCATION_AGE_CATEGORY_TABLE).append(postfix);
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
    
    public void saveLocationType(Integer categoryId, Integer locationId) throws NamingException, SQLException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT ");
        sb.append("INTO ");
        sb.append(DB.LOCATION_CATEGORY_TABLE).append(postfix);
        sb.append("(");
        sb.append("location,");
        sb.append("category)");
        sb.append("VALUES( ");
        sb.append(locationId).append(",");
        sb.append(categoryId);
        sb.append(");");
        DB.qr().insert(sb.toString(), new ScalarHandler<>());
    }

    protected boolean locationExists(Location location) throws NamingException, SQLException {
        return getLocationId(location) != null;
    }

    protected Integer getLocationId(Location location) throws NamingException, SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("select id from ");

        sb.append(DB.LOCATION_TABLE + postfix);
        if (location.getPm_guid() != null) {
            sb.append(" where pm_guid = '");
            sb.append(location.getPm_guid());
        } else if (location.getPa_id() != null) {
            sb.append(" where pa_id = '");
            sb.append(location.getPa_id());
        } else {
            throw new IllegalArgumentException("No id found on location (either pa_id or pm_guid was not set)");
        }
        sb.append("';");
        Integer id = DB.qr().query(sb.toString(), new ScalarHandler<Integer>());
        return id;
    }
    
    protected Integer getLocationId(Comment comment) throws NamingException, SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("select id from ");

        sb.append(DB.LOCATION_TABLE).append(postfix);
        if (comment.getPost_id()!= null) {
            sb.append(" where pa_id = '");
            sb.append(comment.getPost_id());
        } else {
            throw new IllegalArgumentException("No id found on location (either pa_id or pm_guid was not set)");
        }
        sb.append("';");
        Integer id = DB.qr().query(sb.toString(), new ScalarHandler<>());
        return id;
    }

    public void saveFacilities(Integer locationId, Integer facilityId) throws NamingException, SQLException {

       
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.LOCATION_FACILITIES_TABLE).append(postfix);
            sb.append("(");
            sb.append("location,");
            sb.append("facility)");
            sb.append("VALUES( ");
            sb.append(locationId).append(",");
            sb.append(facilityId);
            sb.append(");");
            DB.qr().insert(sb.toString(), new ScalarHandler<>());
        
    }

    
    public void saveAccessibility(Integer locationId, Integer id) throws NamingException, SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT ");
        sb.append("INTO ");
        sb.append(DB.LOCATION_ACCESSIBILITY_TABLE).append(postfix);
        sb.append("(");
        sb.append("location,");
        sb.append("accessibility)");
        sb.append("VALUES( ");
        sb.append(locationId).append(",");
        sb.append(id);
        sb.append(");");
        DB.qr().insert(sb.toString(), new ScalarHandler<>());
    }

    // </editor-fold>
    
    // <editor-fold desc="Assets" defaultstate="collapsed">
    public void saveAsset(Asset asset, ImportReport report) throws NamingException, SQLException {
        Integer id = null;
        Object geom = null;

        ResultSetHandler<Asset> handler = new BeanHandler(Asset.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));

        try {
            geom = geometryConverter.createNativePoint(asset.getLatitude(), asset.getLongitude(), 4326);
        } catch (ParseException ex) {
            log.error("Cannot parse geometry", ex);
        } catch (NullPointerException ex) {
            log.info("no geom for asset");
        }
        if (assetExists(asset)) {

            id = getAssetId(asset);

            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE ").append(DB.ASSETS_TABLE).append(postfix);
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
            DB.qr().update(sb.toString(), asset.getInstalleddate(), asset.getLocation(), asset.getName(), asset.getType_(), asset.getEquipment(), asset.getLatitude(),
                    asset.getLongitude(), asset.getPriceindexation(), asset.getPriceinstallation(), asset.getPricemaintenance(), asset.getPricepurchase(), asset.getPricereinvestment(),
                    asset.getDepth(), asset.getWidth(), asset.getHeight(), asset.getEndoflifeyear(), asset.getFreefallheight(), asset.getSafetyzonelength(), asset.getSafetyzonewidth(),
                    asset.getManufacturer(), asset.getMaterial(), asset.getProduct(), asset.getProductid(), asset.getProductvariantid(), asset.getSerialnumber(), geom, asset.getPm_guid());
            report.increaseUpdated(ImportType.ASSET);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.ASSETS_TABLE).append(postfix);
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

            Asset as = DB.qr().insert(sb.toString(), handler, asset.getInstalleddate(), asset.getLocation(), asset.getName(), asset.getType_(), asset.getEquipment(), asset.getLatitude(),
                    asset.getLongitude(), asset.getPriceindexation(), asset.getPriceinstallation(), asset.getPricemaintenance(), asset.getPricepurchase(), asset.getPricereinvestment(),
                    asset.getDepth(), asset.getWidth(), asset.getHeight(), asset.getEndoflifeyear(), asset.getFreefallheight(), asset.getSafetyzonelength(), asset.getSafetyzonewidth(),
                    asset.getManufacturer(), asset.getMaterial(), asset.getProduct(), asset.getProductid(), asset.getProductvariantid(), asset.getSerialnumber(), geom, asset.getPm_guid());
            id = as.getId();
            report.increaseInserted(ImportType.ASSET);
        }

        saveAssetsAgeCategories(asset, id);

        Integer locationId = asset.getLocation();

        saveImagesAndWords(asset.getImages(), id, locationId, DB.IMAGES_TABLE + postfix, true);
        saveImagesAndWords(asset.getDocuments(), id, locationId, DB.ASSETS_DOCUMENTS_TABLE + postfix, true);
    }

    public void saveAssetsAgeCategories(Asset asset, Integer assetId) throws NamingException, SQLException {
        // delete old entries        
        DB.qr().update("DELETE FROM " + DB.ASSETS_AGECATEGORIES_TABLE  + postfix + " WHERE location_equipment = " + assetId);
        for (Integer agecategory : asset.getAgecategories()) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append(DB.ASSETS_AGECATEGORIES_TABLE).append(postfix);
            sb.append("(");
            sb.append("location_equipment,");
            sb.append("agecategory)");
            sb.append("VALUES( ");
            sb.append(assetId);
            sb.append(",");
            sb.append(agecategory);
            sb.append(");");
            DB.qr().insert(sb.toString(), new ScalarHandler<>());
        }
    }

    protected boolean assetExists(Asset asset) {
        return getAssetId(asset) != null;
    }

    protected Integer getAssetId(Asset asset) {
        try {
            ResultSetHandler<Asset> handler = new BeanHandler(Asset.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            Asset o;
            if (asset.getPm_guid() == null) {
                if(asset.getLocation() == null || asset.getEquipment() == null){
                    return null;
                }
                Integer equipment = asset.getEquipment();
                StringBuilder sb = new StringBuilder();
                sb.append("select * from ");
                sb.append(DB.ASSETS_TABLE).append(postfix);
                sb.append(" where location = ? and equipment = ?");

                o = DB.qr().query(sb.toString(), handler, asset.getLocation(), equipment);

            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("select * from ");
                sb.append(DB.ASSETS_TABLE).append(postfix);
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

    // </editor-fold>
    
    // <editor-fold desc="Comments" defaultstate="collapsed">
    
    public void saveComment(Comment comment, ImportReport report) throws NamingException, SQLException {
        Integer id = null;

        ResultSetHandler<Comment> handler = new BeanHandler(Comment.class);
        Integer locationid = comment.getLocation_id();
        if(locationid == null){
            locationid = getLocationId(comment);
            comment.setLocation_id(locationid);
        }
        if (locationid != null) {

            if (commentExists(comment)) {
                id = getCommentId(comment);

                StringBuilder sb = new StringBuilder();
                sb.append("UPDATE ").append(DB.COMMENT_TABLE).append(postfix);
                sb.append(" set playadvisor_id = ?,");
                sb.append("post_id = ?,");
                sb.append("location = ?,");
                sb.append("content = ?,");
                sb.append("stars = ?,");
                sb.append("author = ?,");
                sb.append("date = ?");
                sb.append(" WHERE id = ").append(id);
                DB.qr().update(sb.toString(), comment.getPlayadvisor_id(), comment.getPost_id(), locationid,
                        comment.getContent(), comment.getStars(), comment.getAuthor(), comment.getDate());
                report.increaseUpdated(ImportType.COMMENT);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("INSERT ");
                sb.append("INTO ");
                sb.append(DB.COMMENT_TABLE).append(postfix);
                sb.append("(");
                sb.append("playadvisor_id,");
                sb.append("post_id,");
                sb.append("location,");
                sb.append("content,");
                sb.append("stars,");
                sb.append("author,");
                sb.append("date) ");
                sb.append("VALUES( ?,?,?,?,?,?,?);");

                Comment cm = DB.qr().insert(sb.toString(), handler, comment.getPlayadvisor_id(), comment.getPost_id(), locationid,
                        comment.getContent(), comment.getStars(), comment.getAuthor(), comment.getDate());
                id = cm.getId();
                report.increaseInserted(ImportType.COMMENT);
            }
        }else{
            report.addError("Commentaar van onbekende locatie", ImportType.COMMENT);
        }
    }
    
    private boolean commentExists(Comment comment)throws NamingException, SQLException{
        return getCommentId(comment) != null;
    }
    
    private Integer getCommentId(Comment comment) throws NamingException, SQLException{
        StringBuilder sb = new StringBuilder();
        sb.append("select id from ");

        sb.append(DB.COMMENT_TABLE).append(postfix);
        if (comment.getPlayadvisor_id() != null) {
            sb.append(" where playadvisor_id = ");
            sb.append(comment.getPlayadvisor_id());
        } else {
            throw new IllegalArgumentException("No id found on location (either pa_id or pm_guid was not set)");
        }
        sb.append(";");
        Integer id = DB.qr().query(sb.toString(), new ScalarHandler<>());
        return id;
    }
    // </editor-fold>
    
    // <editor-fold desc="Helpers" defaultstate="collapsed">
    protected Integer getAssetType(String type) {
        Integer id = assetTypes.get(type);
        return id;
    }

    protected Integer getEquipmentType(String type) {
        Integer id = equipmentTypes.get(type);
        return id;
    }

    public void saveImagesAndWords(List<Map<String, Object>> images, Integer assetId, Integer locationId, String table, boolean removeBeforeAdding) throws NamingException, SQLException {
        if(removeBeforeAdding){
            DB.qr().update("DELETE FROM " + table + " WHERE equipment = " + assetId);
        }
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
                sb.append("VALUES(?,?,?,?,?);");
                DB.qr().insert(sb.toString(), new ScalarHandler<>(), image.get("Description"), image.get("URI"), locationId, assetId, image.get("ID"));
            }
        }
    }
    
    public String getPostfix() {
        return postfix;
    }
    // </editor-fold>

}
