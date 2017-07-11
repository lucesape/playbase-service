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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
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

    private Gson gson;

    public MatchActionBean() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeSpecialFloatingPointValues();
        builder.registerTypeAdapter(Geometry.class, new GeometryGsonSerializer());
        gson = builder.create();
    }

    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
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
        GeometryJdbcConverter geometryConverter = null;
        try (Connection con = DB.getConnection()) {
            geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);
            ResultSetHandler<List<Location>> listHandler = new BeanListHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            ResultSetHandler<Location> handler = new BeanHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));

            Location playadvisorLoc = DB.qr().query("select * from " + DB.LOCATION_TABLE + "_playadvisor where id = ?", handler, playadvisorId);
            if (playadvisorLoc != null) {

                CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");

                NormalizedLevenshtein l = new NormalizedLevenshtein();

                List<Location> locs = DB.qr().query("select * from " + DB.LOCATION_TABLE + PlaymappingImporter.getPostfix() + " where pa_id is null", listHandler);
                JSONArray ar = new JSONArray();
                for (Location loc : locs) {
                    JSONObject obj = new JSONObject(gson.toJson(loc, Location.class));
                    Geometry end = loc.getGeom();

                    try {

                        if (end != null && playadvisorLoc.getGeom() != null) {
                            double distance = JTS.orthodromicDistance(playadvisorLoc.getGeom().getCoordinate(), end.getCoordinate(), crs);
                            obj.put("distance", distance / 1000);
                        } else {
                            obj.put("distance", "-");
                        }

                    } catch (TransformException ex) {
                        LOG.error("Error calculating distance: ", ex);
                    }
                    double similarity = l.similarity(playadvisorLoc.getTitle(), loc.getTitle()) * 10;
                    obj.put("similarity", Math.round(similarity * 10.0) / 10.0);
                    ar.put(obj);
                }
                result.put("data", ar);
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

    public Resolution save() {
        try (Connection con = DB.getConnection()) {
            GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);

            ResultSetHandler<Location> handler = new BeanHandler(Location.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            ResultSetHandler<List<Asset>> assHandler = new BeanListHandler(Asset.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
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
            
            
            DB.qr().update("delete from " + DB.LOCATION_TABLE + "_playadvisor where id = ?", playadvisorId);
        } catch (NamingException | SQLException ex) {
            LOG.error("cannot merge locations",ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MatchActionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return view();
    }
    
    protected Location mergeLocations(Location playadvisor, Location playmapping) throws NamingException, SQLException{
        playadvisor.setPm_guid(playmapping.getPm_guid());
        playadvisor.setId(playmapping.getId());
        
        
        return playadvisor;
    }

    protected void transferImages(Location playadvisor, Integer playmapping, PlaymappingImporter importer) throws NamingException, SQLException {
        List<Map<String,Object>> paImages = DB.qr().query("select * from " + DB.IMAGES_TABLE + "_playadvisor where location = ?", new MapListHandler(), playadvisorId);
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
    // </editor-fold>
}
