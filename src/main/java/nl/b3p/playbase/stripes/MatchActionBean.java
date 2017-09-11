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
package nl.b3p.playbase.stripes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vividsolutions.jts.geom.Geometry;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.StrictBinding;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;
import nl.b3p.loader.jdbc.GeometryJdbcConverter;
import nl.b3p.loader.jdbc.GeometryJdbcConverterFactory;
import nl.b3p.loader.util.DbUtilsGeometryColumnConverter;
import nl.b3p.playbase.ImportReport;
import nl.b3p.playbase.PlaymappingImporter;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Asset;
import nl.b3p.playbase.entities.Comment;
import nl.b3p.playbase.entities.Location;
import nl.b3p.playbase.util.GeometryGsonSerializer;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Meine Toonen
 */
@StrictBinding
@UrlBinding("/action/match/{$event}")
public class MatchActionBean implements ActionBean {

    private ActionBeanContext context;

    private static final String JSP = "/WEB-INF/jsp/admin/match.jsp";
    private static final Log LOG = LogFactory.getLog("MatchActionBean");

    @Validate
    private Integer playadvisorId;

    @Validate
    private Integer playmappingId;

    @Validate
    private String method;
    
    @Validate
    private double automaticMergeScore =  10.0;

    private final Gson gson;

    public MatchActionBean() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        builder.registerTypeAdapter(Geometry.class, new GeometryGsonSerializer());
        gson = builder.create();
    }
    
    public Resolution addAll() {
        try (Connection con = DB.getConnection()) {
            GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);
            ResultSetHandler<List<Location>> handler = new BeanListHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            String sql = "select * from " + DB.LOCATION_TABLE + "_playadvisor";
            List<Location> locs = DB.qr().query(sql, handler);
            method = "add";
            for (Location loc : locs) {
                playadvisorId = loc.getId();
                save();
            }
        } catch ( NamingException | SQLException ex) {
            LOG.error("Cannot get geometryConverter: ", ex);
        }
        return view();
    }
    
    public Resolution autoMerge(){
        JSONObject result = new JSONObject();
        // Haal alle playadvisor records op
        // Per PA record
            // Haal PM records op
            // Vind score met >= automaticMergeScore
                // merge

             
        try (Connection con = DB.getConnection()) {
            GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);
            ResultSetHandler<List<Location>> handler = new BeanListHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            String sql = "select * from " + DB.LOCATION_TABLE + "_playadvisor";
            List<Location> locs = DB.qr().query(sql, handler);
            JSONArray ar = new JSONArray();
            result.put("messages", ar);
            method = "merge";
            for (Location loc : locs) {
                List<JSONObject> pms = getPlaymappingData(loc, geometryConverter);
                boolean found = false;
                double maxScore = -100;
                double minDist = 100;
                for (JSONObject pm : pms) {
                    double score = pm.getDouble("score");
                    String distanceString = pm.getString("distance");
                    Double distance = distanceString.equals("-") ? 100 : Double.parseDouble(pm.getString("distance"));
                    maxScore = Math.max(score, maxScore);
                    minDist = Math.min(distance, minDist);
                    
                    if( score > automaticMergeScore || distance < 0.05 ){
                        context.getMessages().add(new SimpleMessage("Gelinked: " + loc.getTitle() + " aan " + pm.getString("title")));
                        playmappingId = pm.getInt("id");
                        playadvisorId = loc.getId();
                        save();
                        found = true;
                        break;
                        //save
                    }
                }
                
                if(!found){
                    context.getMessages().add(new SimpleMessage("Not found: " + loc.getTitle() + ". Max score: " + maxScore + ". Kleinste afstand: " + minDist));
                }
            }
            

        } catch (FactoryException | NamingException | SQLException ex) {
            LOG.error("Cannot get geometryConverter: ", ex);
            result.put("message", "Cannot get geometryConverter: " + ex.getLocalizedMessage());
        }
        return view();
    }

    @DefaultHandler
    public Resolution view() {

        return new ForwardResolution(JSP);
    }

    public Resolution dataPlayadvisor() {
        JSONObject result = new JSONObject();
        try (Connection con = DB.getConnection()) {
            GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);
            ResultSetHandler<List<Location>> handler = new BeanListHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            String sql = "select * from " + DB.LOCATION_TABLE + "_playadvisor";
            List<Location> locs = DB.qr().query(sql, handler);
            JSONArray ar = new JSONArray();
            for (Location loc : locs) {
                ar.put(new JSONObject(gson.toJson(loc, Location.class)));
            }
            result.put("data", ar);

        } catch (NamingException | SQLException ex) {
            LOG.error("Cannot get geometryConverter: ", ex);
            result.put("message", "Cannot get geometryConverter: " + ex.getLocalizedMessage());
        }

        StreamingResolution res = new StreamingResolution("application/json", result.toString(4));
        res.setFilename("");
        res.setAttachment(true);
        return res;
    }

    public Resolution dataPlaymapping() throws FactoryException {
        JSONObject result = new JSONObject();
        try (Connection con = DB.getConnection()) {
            GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);
            ResultSetHandler<Location> handler = new BeanHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));

            Location playadvisorLoc = DB.qr().query("select * from " + DB.LOCATION_TABLE + "_playadvisor where id = ?", handler, playadvisorId);
            if (playadvisorLoc != null) {

                List<JSONObject> locations = getPlaymappingData(playadvisorLoc, geometryConverter);
                result.put("data", new JSONArray(locations));
            } else {
                result.put("message", "playadvisor location not found.");
            }

        } catch (NamingException | SQLException ex) {
            LOG.error("Cannot get geometryConverter: ", ex);
            result.put("message", "Cannot get geometryConverter: " + ex.getLocalizedMessage());
        }

        StreamingResolution res = new StreamingResolution("application/json", result.toString(4));
        res.setFilename("");
        res.setAttachment(true);
        return res;
    }
    
    private List<JSONObject> getPlaymappingData(Location playadvisorLoc, GeometryJdbcConverter geometryConverter) throws FactoryException, NamingException, SQLException {
        ResultSetHandler<List<Location>> listHandler = new BeanListHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");

        NormalizedLevenshtein l = new NormalizedLevenshtein();

        List<Location> locs = DB.qr().query("select * from " + DB.LOCATION_TABLE + " where pa_id is null", listHandler);
        JSONArray ar = new JSONArray();
        List<JSONObject> locations = new ArrayList<>();
        for (Location loc : locs) {
            JSONObject obj = new JSONObject(gson.toJson(loc, Location.class));
            Geometry end = loc.getGeom();
            double distanceScore = 3;
            try {
                // not changed when there is no distance. 3 is the penalty for no distance, to prevent skewed results

                if (end != null && playadvisorLoc.getGeom() != null) {
                    double distance = JTS.orthodromicDistance(playadvisorLoc.getGeom().getCoordinate(), end.getCoordinate(), crs) / 1000;
                    obj.put("distance", String.format("%.2f", distance));
                    // 
                    distanceScore = Math.min(distance * 2, 10);
                } else {
                    obj.put("distance", "-");
                }

            } catch (TransformException ex) {
                LOG.error("Error calculating distance: ", ex);
            }
            double similarity = l.similarity(playadvisorLoc.getTitle(), loc.getTitle()) * 10;
            obj.put("similarity", Math.round(similarity * 10.0) / 10.0);
            double score = 10 - ((10 - similarity) / 2.7) - distanceScore;
            obj.put("score", String.format("%.2f", score));
            locations.add(obj);
        }
        return locations;
    }

    public Resolution save() {
        try (Connection con = DB.getConnection()) {
            GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);

            ResultSetHandler<Location> handler = new BeanHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            ResultSetHandler<List<Asset>> assHandler = new BeanListHandler(Asset.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            ResultSetHandler<List<Comment>> commentHandler = new BeanListHandler(Comment.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            Location playadvisorLoc = DB.qr().query("select * from " + DB.LOCATION_TABLE + "_playadvisor where id = ?", handler, playadvisorId);
            Location playmappingLoc = DB.qr().query("select * from " + DB.LOCATION_TABLE + " where id = ?", handler, playmappingId);
            
            PlaymappingImporter importer = new PlaymappingImporter();
            
            Location toSave = null;
            if(method.equals("merge")){
                toSave = mergeLocations(playadvisorLoc, playmappingLoc);
                
            }else if(method.equals("add")){
                toSave = playadvisorLoc;
                toSave.setId(null);
            }
            Integer locationId = importer.saveLocation(toSave, new ImportReport());
            transferImages(playadvisorLoc, locationId, importer);
            transferFacilities(playadvisorLoc, locationId, importer);
            transferAccessibilities(playadvisorLoc, locationId, importer);
            transferLocationAgecategories(playadvisorLoc, locationId, importer);
            transferLocationCategories(playadvisorLoc, locationId, importer);
            transferLocationEquipment(playadvisorLoc, locationId, importer, assHandler);
            transferComments(playadvisorLoc, playmappingLoc, importer, commentHandler);
            
            DB.qr().update("delete from " + DB.LOCATION_TABLE + "_playadvisor where id = ?", playadvisorId);
        } catch (NamingException | SQLException | UnsupportedEncodingException ex) {
            LOG.error("cannot merge locations",ex);
        }
        return view();
    }
    
    protected Location mergeLocations(Location playadvisor, Location playmapping) throws NamingException, SQLException{
        playmapping.setPa_id(playadvisor.getPa_id());
        playmapping.setAveragerating(playadvisor.getAveragerating());
        playmapping.setContent(playadvisor.getContent());
        playmapping.setCountry(playadvisor.getCountry());
        playmapping.setMunicipality(playadvisor.getMunicipality());
        playmapping.setPa_title(playadvisor.getPa_title());
        return playmapping;
    }

    
    protected void transferComments(Location playadvisor, Location playmapping, PlaymappingImporter importer, ResultSetHandler<List<Comment>> commentHandler) throws NamingException, SQLException {
        List<Comment> comments = DB.qr().query("select * from " + DB.COMMENT_TABLE + "_playadvisor where location = ?", commentHandler, playadvisorId);
        for (Comment comment : comments) {
            importer.saveComment(comment, new ImportReport());
        }
        DB.qr().update("delete from " + DB.COMMENT_TABLE + "_playadvisor where location = ?", playadvisorId);
    }
    
    protected void transferImages(Location playadvisor, Integer playmapping, PlaymappingImporter importer) throws NamingException, SQLException {
        //), image.get("Description"), image.get("URI"), locationId, assetId, image.get("ID"));
        List<Map<String,Object>> paImages = DB.qr().query("select caption as \"Description\", url as \"URI\", id as \"ID\" from " + DB.IMAGES_TABLE + "_playadvisor where location = ?", new MapListHandler(), playadvisorId);
        importer.saveImagesAndWords(paImages, null, playmapping, DB.IMAGES_TABLE, false);
        DB.qr().update("delete from " + DB.IMAGES_TABLE + "_playadvisor where location = ?", playadvisorId);
    }

    protected void transferFacilities(Location playadvisor, Integer playmapping, PlaymappingImporter importer) throws NamingException, SQLException {
        List<Map<String,Object>> paFacilities = DB.qr().query("select location, facility from " + DB.LOCATION_FACILITIES_TABLE + "_playadvisor where location = ?", new MapListHandler(), playadvisorId);
        for (Map<String, Object> facility : paFacilities) {
            importer.saveFacilities(playmapping, (Integer)facility.get("facility"));
        }
        DB.qr().update("delete from " + DB.LOCATION_FACILITIES_TABLE + "_playadvisor where location = ?", playadvisorId);
    }

    protected void transferAccessibilities(Location playadvisor, Integer playmapping, PlaymappingImporter importer) throws NamingException, SQLException {
        List<Map<String,Object>> paAccessibilities = DB.qr().query("select location, accessibility from " + DB.LOCATION_ACCESSIBILITY_TABLE + "_playadvisor where location = ?", new MapListHandler(), playadvisorId);
        for (Map<String, Object> acc : paAccessibilities) {
            importer.saveAccessibility(playmapping, (Integer)acc.get("accessibility"));
        }
        DB.qr().update("delete from " + DB.LOCATION_ACCESSIBILITY_TABLE + "_playadvisor where location = ?", playadvisorId);
    }

    protected void transferLocationAgecategories(Location playadvisor, Integer playmapping, PlaymappingImporter importer) throws NamingException, SQLException {
        List<Map<String,Object>> paAccessibilities = DB.qr().query("select location, agecategory from " + DB.LOCATION_AGE_CATEGORY_TABLE + "_playadvisor where location = ?", new MapListHandler(), playadvisorId);
        List<Integer> ids = new ArrayList<>();
        for (Map<String, Object> paAccessibility : paAccessibilities) {
            ids.add((Integer)paAccessibility.get("agecategory"));
        }
        importer.saveLocationAgeCategory( playmapping, ids, false);
        DB.qr().update("delete from " + DB.LOCATION_AGE_CATEGORY_TABLE + "_playadvisor where location = ?", playadvisorId);
    }

    protected void transferLocationCategories(Location playadvisor, Integer playmapping, PlaymappingImporter importer) throws NamingException, SQLException, UnsupportedEncodingException {
        List<Map<String,Object>> paAccessibilities = DB.qr().query("select location, category from " + DB.LOCATION_CATEGORY_TABLE + "_playadvisor where location = ?", new MapListHandler(), playadvisorId);
        for (Map<String, Object> paAccessibility : paAccessibilities) {
            importer.saveLocationType((Integer)paAccessibility.get("category"), playmapping);
        }
        DB.qr().update("delete from " + DB.LOCATION_CATEGORY_TABLE + "_playadvisor where location = ?", playadvisorId);
    }

    protected void transferLocationEquipment(Location playadvisor, Integer playmapping, PlaymappingImporter importer,ResultSetHandler<List<Asset>> assHandler) throws NamingException, SQLException, UnsupportedEncodingException {
        List<Asset> assets = DB.qr().query("select * from " + DB.ASSETS_TABLE + "_playadvisor where location = ?", assHandler, playadvisorId);
        for (Asset asset : assets) {
            transferLocationEquipmentAgecategory( asset);
            asset.setId(null);
            asset.setLocation(playmapping);
            importer.saveAsset(asset, new ImportReport());            
        }
        DB.qr().update("delete from " + DB.ASSETS_TABLE + "_playadvisor where location = ?", playadvisorId);
    }

    protected void transferLocationEquipmentAgecategory(Asset asset) throws NamingException, SQLException, UnsupportedEncodingException {
        List<Map<String,Object>> agecategories = DB.qr().query("select * from " + DB.ASSETS_AGECATEGORIES_TABLE + "_playadvisor where location_equipment = ?",  new MapListHandler(), asset.getId());
        Integer[] ids = new Integer[agecategories.size()];
        
        for (int i = 0; i < agecategories.size(); i++) {
            Map<String,Object> cat = agecategories.get(i);
            ids[i] = (Integer)cat.get("agecategory");
        }
        asset.setAgecategories(ids);
        DB.qr().update("delete from " + DB.ASSETS_AGECATEGORIES_TABLE + "_playadvisor where location_equipment = ?", asset.getId());
    }

    //<editor-fold desc="Getters and Setters" defaultstate="collapsed">
    public Integer getPlayadvisorId() {
        return playadvisorId;
    }

    public void setPlayadvisorId(Integer playadvisorId) {
        this.playadvisorId = playadvisorId;
    }

    public Integer getPlaymappingId() {
        return playmappingId;
    }

    public void setPlaymappingId(Integer playmappingId) {
        this.playmappingId = playmappingId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
    
    public double getAutomaticMergeScore() {
        return automaticMergeScore;
    }

    public void setAutomaticMergeScore(double automaticMergeScore) {
        this.automaticMergeScore = automaticMergeScore;
    }
    
    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }
    // </editor-fold>

}
