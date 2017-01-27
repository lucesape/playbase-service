package nl.b3p.dashboard.service.admin.stripes;

import java.util.List;
import java.util.Map;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.validation.*;
import nl.b3p.dashboard.service.server.db.DB;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Chris van Lith
 */
@StrictBinding
@UrlBinding("/action/json")
public class DashboardJSONActionBean implements ActionBean {
    private ActionBeanContext context;

    private static final Log log = LogFactory.getLog(DashboardJSONActionBean.class);
    
    private static final String JSP = "/WEB-INF/jsp/admin/bron.jsp";
    
    @Validate
    private Integer id;

    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private JSONObject rowsToGeoJSONFeatureCollection(List<Map<String,Object>> rows) {
        JSONObject fc = new JSONObject();
        fc.put("type", "FeatureCollection");
        JSONArray features = new JSONArray();
        fc.put("features", features);
        for(Map<String,Object> row: rows) {
            JSONObject feature = new JSONObject();
            feature.put("type", "Feature");
            features.put(feature);
            JSONObject props = new JSONObject();
            feature.put("properties", props);
            for(Map.Entry<String,Object> column: row.entrySet()) {
                if("geometry".equals(column.getKey())) {
                    JSONObject geometry = new JSONObject((String)column.getValue());
                    feature.put("geometry", geometry);
                } else {
                    props.put(column.getKey(), column.getValue());
                }
            }
        }
        return fc;
    }

    @DefaultHandler
    @DontValidate
    public Resolution edit() throws Exception {
        return new ForwardResolution(JSP);
    }
    
    public Resolution wbbks() {
        JSONObject result = new JSONObject();
        result.put("success", false);
        try {
            List<Map<String,Object>> rows = DB.qr().query("select id, locatie, adres, plaatsnaam, st_xmin(geom)||','||st_ymin(geom)||','||st_xmax(geom)||','||st_ymax(geom) as bounds, st_asgeojson(st_centroid(geom)) as geometry from vrh.wdbk_waterbereikbaarheidskaart where locatie is not null order by locatie", new MapListHandler());

            result.put("wbbk", rowsToGeoJSONFeatureCollection(rows));
            result.put("success", true);
        } catch(Exception e) {
            log.error("Error getting VRH wbbk data", e);
            result.put("error", "Fout ophalen WBBK data: " + e.getClass() + ": " + e.getMessage());

        }
        context.getResponse().addHeader("Access-Control-Allow-Origin", "*");
        return new StreamingResolution("application/json", result.toString(4));
    }

    public Resolution wbbk() {
        JSONObject result = new JSONObject();
        result.put("success", false);
        try {
            List<Map<String,Object>> rows = DB.qr().query("select *, st_asgeojson(geom) as geometry from vrh.wdbk_waterbereikbaarheidskaart where id = ?", new MapListHandler(), id);
            if(rows.isEmpty()) {
                result.put("error", "WBBK met ID " + id + " niet gevonden");
            } else {
                JSONObject attributes = new JSONObject();
                result.put("attributes", attributes);

                JSONObject wbbkVlak = new JSONObject();
                wbbkVlak.put("type", "Feature");
                JSONObject props = new JSONObject();
                wbbkVlak.put("properties", props);
                props.put("id", "wbbk_" + id);
                props.put("type", "Dieptevlak tot 4 meter");

                Map<String,Object> row = rows.get(0);
                for(Map.Entry<String,Object> column: row.entrySet()) {
                    if("geometry".equals(column.getKey())) {
                        wbbkVlak.put("geometry", new JSONObject((String)column.getValue()));
                    } else if(!"geom".equals(column.getKey())) {
                        attributes.put(column.getKey(), column.getValue());
                    }
                }

                rows = DB.qr().query("select id, symboolcod, symboolgro, bijzonderh, st_asgeojson(geom) as geometry from vrh.voorzieningen_water where dbk_object = ?", new MapListHandler(), id);
                result.put("symbolen", rowsToGeoJSONFeatureCollection(rows));

                rows = DB.qr().query("select id, type, bijzonderh, opmerkinge, st_asgeojson(geom) as geometry from vrh.overige_lijnen where dbk_object = ?", new MapListHandler(), id);
                result.put("lijnen", rowsToGeoJSONFeatureCollection(rows));

                rows = DB.qr().query("select id, type, bijzonderh, st_asgeojson(geom) as geometry from vrh.overige_vlakken where dbk_object = ? order by \n" +
                    "   case type \n" +
                    "   when 'Dieptevlak 4 tot 9 meter' then -3 \n" +
                    "   when 'Dieptevlak 9 tot 15 meter' then -2\n" +
                    "   when 'Dieptevlak 15 meter en dieper' then -1\n" +
                    "   else id\n" +
                    "   end asc", new MapListHandler(), id);

                JSONObject vlakken = new JSONObject();
                vlakken.put("type", "FeatureCollection");
                JSONArray vlakkenFeatures = new JSONArray();
                vlakken.put("features", vlakkenFeatures);
                vlakkenFeatures.put(wbbkVlak);
                JSONObject vlakkenFC = rowsToGeoJSONFeatureCollection(rows);
                JSONArray features = vlakkenFC.getJSONArray("features");
                for(int i = 0; i < features.length(); i++) {
                    vlakkenFeatures.put(features.get(i));
                }
                vlakkenFC.put("features", vlakken);
                result.put("vlakken", vlakken);

                result.put("success", true);
            }
        } catch(Exception e) {
            log.error("Error getting VRH wbbk data", e);
            result.put("error", "Fout ophalen WBBK data: " + e.getClass() + ": " + e.getMessage());

        }
        context.getResponse().addHeader("Access-Control-Allow-Origin", "*");
        return new StreamingResolution("application/json", result.toString(4));
    }
}
