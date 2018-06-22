/*
 * Copyright (C) 2018 B3Partners B.V.
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import nl.b3p.loader.jdbc.GeometryJdbcConverter;
import nl.b3p.loader.jdbc.GeometryJdbcConverterFactory;
import nl.b3p.loader.util.DbUtilsGeometryColumnConverter;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Asset;
import nl.b3p.playbase.entities.Project;
import nl.b3p.playbase.entities.Location;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Meine Toonen
 */
public class PlayadvisorExporter {

    private static final Log log = LogFactory.getLog(PlayadvisorExporter.class);

    public PlayadvisorExporter() {
        initLists();
    }

    public void export(List<Location> locations, Connection con, Project job) {
        for (Location location : locations) {
            try {
                pushLocation(location, con, job);
            } catch (IOException | SQLException | NamingException ex) {
                log.error("Error exporting to export", ex);
            }
        }

    }

    public String pushLocation(Location loc, Connection con, Project job) throws IOException, SQLException, NamingException {
        return pushLocation(createLocationJSON(loc, con), loc.getId(),job);
    }

    public String pushLocation(JSONObject location, Integer id, Project job) throws IOException {
        String url = job.getBaseurl() + "/wp-json/b3p/v1/playbase/" + id;
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpPost request = new HttpPost(url);
        StringEntity params = new StringEntity(location.toString(), ContentType.APPLICATION_JSON);
        request.addHeader("content-type", "application/json");
        request.addHeader("Authorization", "Basic " + job.getAuthkey());
        request.setEntity(params);
        HttpResponse response = httpClient.execute(request);
        StatusLine sl = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        int statusCode = sl.getStatusCode();
        if (statusCode != 200) {
            String statusLine = response.getStatusLine().getReasonPhrase();

            log.debug("Error: " + statusLine);
            throw new IOException(statusLine);

        } else {
            String stringResult = EntityUtils.toString(entity);
            log.debug("Result: " + stringResult);
            return stringResult;
        }
    }

    private void processResponse(JSONObject obj) {

    }

    // <editor-fold desc="Helper functions for creating a correct JSON" defaultstate="collapsed">
    private final Integer[] excl = {58, 60, 61, 93, 22, 7, 56, 64, 177, 81, 82, 125, 87, 88, 89, 90, 92, 178, 111, 110, 109, 108, 127, 15, 23, 31, 32, 83, 171, 156, 155, 69, 68, 59, 152, 145, 144, 143, 141, 140, 139, 120, 119, 118, 117, 116, 115};
    private final List<Integer> excludedAssetTypes = Arrays.asList(excl);
    protected Map<Integer, List<String>> locationTypes;
    private Map<Integer, Integer> assetTypes;
    private Map<Integer, Integer> assetTypeToLocationCategory;
    private Map<Integer, String> equipmentTypes;

    protected JSONObject createLocationJSON(Location loc, Connection con) throws SQLException, NamingException {
        JSONObject obj = loc.toPlayadvisorJSON();
        Integer id = loc.getId();

        retrieveCategories(id, obj, con);

        retrieveAssets(id, obj, con);

        retrieveImages(id, obj, con);
     /*   retrieveAgeCategories(id, obj, con);
        retrieveAccessibilities(id, obj, con);
        retrieveFacilities(id, obj, con);
        retrieveParking(id, obj, con);
        retrieveYoungestAssetDate(id, obj, con);*/

        return obj;
    }
    
    protected void retrieveImages(Integer id,  JSONObject location, Connection con) throws NamingException, SQLException {
        ArrayListHandler rsh = new ArrayListHandler();

        List<Object[]> images = DB.qr().query(con,"SELECT url, caption,pa_id,id from " + DB.IMAGES_TABLE + " WHERE location = ? order by equipment desc, lastupdated desc", rsh, id);

        JSONArray imgs = new JSONArray();
        int index = 0;
        for (Object[] image : images) {
            String url = (String) valueOrEmptyString(image[0]);
            if (url.isEmpty()) {
                continue;
            }
        
            String imageName = url.substring(url.lastIndexOf("/") + 1);
            if (imageName.contains("GetImage.ashx")) {
                imageName = "Image" + id + "-" + index + ".jpg";
            }
            Integer imageId = (Integer)image[3];
            JSONObject img = new JSONObject();
            img.put("Path", imageName);
            img.put("PlaybaseID", imageId);
            img.put("PlayadvisorID",valueOrEmptyString(image[2]));
            imgs.put(img);
            index++;
        }
        location.put("Images",imgs);
    }

    
    private void retrieveAssets(Integer id, JSONObject location, Connection con) throws NamingException, SQLException {
        GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);
        ResultSetHandler<List<Asset>> assHandler = new BeanListHandler(Asset.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
        List<Asset> assets = DB.qr().query(con, "SELECT * FROM " + DB.ASSETS_TABLE + " WHERE location = ?", assHandler, id);
        Set<String> equipments = new HashSet<>();
        for (Asset asset : assets) {
            Integer type = asset.getType_();
            Integer equipment = assetTypes.get(type);
            String eq = equipmentTypes.get(equipment);
            if (eq != null) {
                equipments.add(eq);
            }
        }
        Set<String> types = new HashSet<>();
        for (String assetType : equipments) {
            types.add(assetType);
        }
        location.put("Assets",new JSONArray(types));
    }

    protected void retrieveCategories(Integer id, JSONObject location, Connection con) throws NamingException, SQLException {
        ArrayListHandler rsh = new ArrayListHandler();
        List<Object[]> cats = DB.qr().query(con, "SELECT cat.main, cat.category from " + DB.LOCATION_CATEGORY_TABLE + " loc inner join " + DB.LIST_CATEGORY_TABLE + " cat on cat.id = loc.category  WHERE location = " + id, rsh);
        Set<String> types = new HashSet<>();

        for (Object[] cat : cats) {
            types.add((String) cat[0]);
            types.add((String) cat[1]);
        }

        GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);
        ResultSetHandler<List<Asset>> assHandler = new BeanListHandler(Asset.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
        List<Asset> assets = DB.qr().query(con, "SELECT * FROM " + DB.ASSETS_TABLE + " WHERE location = ?", assHandler, id);
        for (Asset asset : assets) {
            Integer type = asset.getType_();
            if (excludedAssetTypes.contains(type) || type == null) {
                continue;
            }
            Integer eq = assetTypeToLocationCategory.get(type);
            List<String> locCats = locationTypes.get(eq);
            types.addAll(locCats);
        }

        if (types.isEmpty()) {
            types.add("Openbare speeltuin");
        }

        location.put("Categorieen", new JSONArray(types));
    }

    private void initLists() {
        ArrayListHandler rsh = new ArrayListHandler();

        try {
            equipmentTypes = new HashMap<>();
            List<Object[]> o = DB.qr().query("SELECT id, equipment from " + DB.LIST_EQUIPMENT_TYPE_TABLE, rsh);
            for (Object[] type : o) {
                Integer id = (Integer) type[0];
                String cat = (String) type[1];
                equipmentTypes.put(id, cat);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot initialize playmapping assettypes:", ex);
        }

        try {
            locationTypes = new HashMap<>();
            List<Object[]> o = DB.qr().query("SELECT id, category, main from " + DB.LIST_CATEGORY_TABLE, rsh);
            for (Object[] type : o) {
                Integer id = (Integer) type[0];
                String category = (String) type[1];
                String main = (String) type[2];

                locationTypes.put(id, new ArrayList<>());
                locationTypes.get(id).add(category);
                locationTypes.get(id).add(main);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot initialize playadvisor location types:", ex);
        }

        try {
            assetTypes = new HashMap<>();
            assetTypeToLocationCategory = new HashMap<>();
            List<Object[]> o = DB.qr().query("SELECT id, equipment_type, locationcategory from " + DB.ASSETS_TYPE_GROUP_LIST_TABLE, rsh);
            for (Object[] type : o) {
                Integer id = (Integer) type[0];
                Integer equipmentType = (Integer) type[1];
                Integer locationcategory = (Integer) type[2];
                assetTypes.put(id, equipmentType);
                assetTypeToLocationCategory.put(id, locationcategory);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot initialize playmapping assettypes:", ex);
        }

    }

    
    private Object valueOrEmptyString(Object value) {
        return value == null ? "" : value;
    }

    // </editor-fold>
}
