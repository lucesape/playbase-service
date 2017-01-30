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
import org.postgresql.util.PGobject;

/**
 * 
 * {
 *   "type": "FeatureCollection",
 *   "features": [
 *     {
 *       "type": "Feature",
 *       "geometry": {
 *         "type": "MultiPolygon",
 *         "crs": {
 *           "type": "name",
 *           "properties": {
 *             "name": "EPSG:28992"
 *           }
 *         },
 *         "coordinates": [
 *           [
 *             [
 *               [
 *                 140418.72,
 *                 448788.34
 *               ],
 *               [
 *                 140406.04,
 *                 448759.82
 *               ]
 *             ]
 *           ]
 *         ]
 *       },
 *       "properties": {
 *         "naam": "3994DJ",
 *         "woonplaats": "Houten",
 *         "aantaladressen": 4,
 *         "budget": {
 *           "aantal": 0,
 *           "onderhoud80": null,
 *           "afschrijving": null,
 *           "beheer": null
 *         },
 *         "groepaantal": null,
 *         "installedyear": null,
 *         "vervangjaar": null,
 *         "endoflifeyear": null,
 *         "leveranciers": null
 *       }
 *     }
 *   ]
 * }
 * 
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
        JSONArray features = new JSONArray();
        fc.put("features", features);
        fc.put("type", "FeatureCollection");
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
                } else if(!"the_geom".equals(column.getKey())) {
                    props.put(column.getKey(), column.getValue());
                }
            }
        }
        return fc;
    }

    private JSONObject rowsToGeoJSONFeatureCollection2(List<Map<String, Object>> rows) {
        JSONObject fc = new JSONObject();
        JSONArray features = new JSONArray();
        fc.put("features", features);
        fc.put("type", "FeatureCollection");
        for (Map<String, Object> row : rows) {
            JSONObject feature = new JSONObject();
            features.put(feature);
            for (Map.Entry<String, Object> column : row.entrySet()) {
                if ("type".equals(column.getKey())) {
                    feature.put(column.getKey(), (String) column.getValue());
                } else if (column.getValue() instanceof PGobject) {
                    PGobject pgo = (PGobject) column.getValue();
                    if ("json".equalsIgnoreCase(pgo.getType())) {
                        JSONObject jo = new JSONObject((String) pgo.getValue());
                        feature.put(column.getKey(), jo);
                    } else {
                        feature.put(column.getKey(), (String) pgo.getValue());
                    }
                } else {
                    JSONObject jo = new JSONObject((String) column.getValue());
                    feature.put(column.getKey(), jo);
                }
            }
        }
        return fc;
    }
    
    
    private JSONObject rowsToJSON(List<Map<String,Object>> rows) {
        JSONObject props = new JSONObject();
        for(Map<String,Object> row: rows) {
            for(Map.Entry<String,Object> column: row.entrySet()) {
                props.put(column.getKey(), column.getValue());
            }
        }
        return props;
    }

    @DefaultHandler
    @DontValidate
    public Resolution edit() throws Exception {
        return new ForwardResolution(JSP);
    }

    public Resolution houten() {
        JSONObject result = new JSONObject();
        try {
//            List<Map<String,Object>> rows2 = DB.qr().query("select *, st_asgeojson(lg.the_geom,2,2) as geometry "
//                    + "from houten_pc6 AS lg", new MapListHandler());
            
            List<Map<String,Object>> rows = DB.qr().query(""
	      +" (                                                                                          "
              +"  SELECT                                                                                    "
              +"      'Feature'                       AS type,                                              "
              +"      ST_AsGeoJSON(lg.the_geom,2,2)::json AS geometry,                                      "
              +"      row_to_json(                                                                          "
              +"      (                                                                                     "
              +"          SELECT                                                                            "
              +"              l                                                                             "
              +"          FROM                                                                              "
              +"              (                                                                             "
              +"                  SELECT                                                                    "
              +"                      lg.postcode  AS naam,                                                 "
              +"                      lg.woonplaa_1 AS woonplaats,                                          "
              +"                      lg.aantaladressen AS aantaladressen,                                  "
              +"                      (                                                                     "
              +"                          SELECT                                                            "
              +"                              (row_to_json(budgetd) )                                       "
              +"                          FROM                                                              "
              +"                              (                                                             "
              +"                                  SELECT                                                    "
              +"                                      SUM(aantal)            AS aantal,                     "
              +"                                      SUM(onderhoud)::INT    AS onderhoud80,                "
              +"                                      SUM(afschrijving)::INT AS afschrijving,               "
              +"                                      SUM(beheer)::INT       AS beheer                      "
              +"                                  FROM                                                      "
              +"                                      (                                                     "
              +"                                          SELECT                                            "
              +"                                              COUNT(a.id)                 AS aantal,        "
              +"                                              SUM(a.onderhoudskosten)*0.8 AS                "
              +"                                                                            onderhoud,      "
              +"                                              SUM(a.afschrijving)     AS afschrijving,      "
              +"                                              SUM(a.aanschafwaarde)*0.035 AS beheer         "
              +"                                          FROM                                              "
              +"                                              v_playmapping_assets_compleet a               "
              +"                                          WHERE                                             "
              +"                                              ST_Contains(lg.the_geom, a.the_geom)          "
              +"                                          AND a.type <> 'veiligheidsondergrond'             "
              +"                                          UNION                                             "
              +"                                          SELECT                                            "
              +"                                              COUNT(a.id)                 AS aantal,        "
              +"                                              SUM(a.onderhoudskosten)*0.8 AS                "
              +"                                                                            onderhoud,      "
              +"                                              SUM(a.afschrijving)     AS afschrijving,      "
              +"                                              SUM(a.aanschafwaarde)*0.005 AS beheer         "
              +"                                          FROM                                              "
              +"                                              v_playmapping_assets_compleet a               "
              +"                                          WHERE                                             "
              +"                                              ST_Contains(lg.the_geom, a.the_geom)          "
              +"                                          AND a.type = 'veiligheidsondergrond') AS          "
              +"                                      foo ) AS budgetd ) AS budget ,                        "
              +"                      (                                                                     "
              +"                          SELECT                                                            "
              +"                              array_to_json(array_agg(row_to_json(groepaantald) ))          "
              +"                          FROM                                                              "
              +"                              (                                                             "
              +"                                  SELECT                                                    "
              +"                                      a.\"group\",                                            "
              +"                                      COUNT(a.id)                  AS aantal,               "
              +"                                      SUM(a.onderhoudskosten)::INT AS onderhoud,            "
              +"                                      SUM(a.afschrijving)::INT     AS afschrijving          "
              +"                                  FROM                                                      "
              +"                                      v_playmapping_assets_compleet a                       "
              +"                                  WHERE                                                     "
              +"                                      ST_Contains(lg.the_geom, a.the_geom)                  "
              +"                                  AND a.type <> 'veiligheidsondergrond'                     "
              +"                                  GROUP BY                                                  "
              +"                                      a.\"group\" ) AS groepaantald ) AS groepaantal ,        "
              +"                      (                                                                     "
              +"                          SELECT                                                            "
              +"                              array_to_json(array_agg(row_to_json(installedyeard)))         "
              +"                          FROM                                                              "
              +"                              (                                                             "
              +"                                  SELECT                                                    "
              +"                                      a.installedyear,                                      "
              +"                                      COUNT(a.id) AS aantal                                 "
              +"                                  FROM                                                      "
              +"                                      v_playmapping_assets_compleet a                       "
              +"                                  WHERE                                                     "
              +"                                      a.type <> 'veiligheidsondergrond'                     "
              +"                                  AND ST_Contains(lg.the_geom, a.the_geom)                  "
              +"                                  GROUP BY                                                  "
              +"                                      a.installedyear ) AS installedyeard ) AS              "
              +"                      installedyear ,                                                       "
              +"                      (                                                                     "
              +"                          SELECT                                                            "
              +"                              array_to_json(array_agg(row_to_json(vervangjaard)))           "
              +"                          FROM                                                              "
              +"                              (                                                             "
              +"                                  SELECT                                                    "
              +"                                      vervangjaar              AS vervangjaar,              "
              +"                                      SUM(aantal)              AS aantal,                   "
              +"                                      SUM(aanschafwaarde)*1.35 AS aanschafwaarde            "
              +"                                  FROM                                                      "
              +"                                      (                                                     "
              +"                                      (                                                     "
              +"                                          SELECT                                            "
              +"                                              vervangjaar1        AS vervangjaar,           "
              +"                                              COUNT(id)           AS aantal,                "
              +"                                              SUM(aanschafwaarde) AS aanschafwaarde         "
              +"                                          FROM                                              "
              +"                                              v_playmapping_assets_compleet a1              "
              +"                                          WHERE                                             "
              +"                                              type <> 'veiligheidsondergrond'               "
              +"                                          AND ST_Contains(lg.the_geom, a1.the_geom)         "
              +"                                          GROUP BY                                          "
              +"                                              vervangjaar1)                                 "
              +"                                  UNION                                                     "
              +"                                      (                                                     "
              +"                                          SELECT                                            "
              +"                                              vervangjaar2        AS vervangjaar,           "
              +"                                              COUNT(id)           AS aantal,                "
              +"                                              SUM(aanschafwaarde) AS aanschafwaarde         "
              +"                                          FROM                                              "
              +"                                              v_playmapping_assets_compleet a2              "
              +"                                          WHERE                                             "
              +"                                              type <> 'veiligheidsondergrond'               "
              +"                                          AND ST_Contains(lg.the_geom, a2.the_geom)         "
              +"                                          GROUP BY                                          "
              +"                                              vervangjaar2)                                 "
              +"                                  UNION                                                     "
              +"                                      (                                                     "
              +"                                          SELECT                                            "
              +"                                              vervangjaar3        AS vervangjaar,           "
              +"                                              COUNT(id)           AS aantal,                "
              +"                                              SUM(aanschafwaarde) AS aanschafwaarde         "
              +"                                          FROM                                              "
              +"                                              v_playmapping_assets_compleet a3              "
              +"                                          WHERE                                             "
              +"                                              type <> 'veiligheidsondergrond'               "
              +"                                          AND ST_Contains(lg.the_geom, a3.the_geom)         "
              +"                                          GROUP BY                                          "
              +"                                              vervangjaar3) ) AS foo                        "
              +"                                  WHERE                                                     "
              +"                                      vervangjaar < (date_part('year'::text, now()) +       "
              +"                                      11)                                                   "
              +"                                  GROUP BY                                                  "
              +"                                      vervangjaar                                           "
              +"                                  ORDER BY                                                  "
              +"                                      vervangjaar ) AS vervangjaard ) AS vervangjaar,       "
              +"                      (                                                                     "
              +"                          SELECT                                                            "
              +"                              array_to_json(array_agg(row_to_json(endoflifeyeard)))         "
              +"                          FROM                                                              "
              +"                              (                                                             "
              +"                                  SELECT                                                    "
              +"                                      a.endoflifeyear       AS endoflifeyear,               "
              +"                                      COUNT(a.id)           AS aantal,                      "
              +"                                      SUM(a.aanschafwaarde) AS aanschafwaarde_toestel       "
              +"                                  FROM                                                      "
              +"                                      v_playmapping_assets_compleet a                       "
              +"                                  WHERE                                                     "
              +"                                      type <> 'veiligheidsondergrond'                       "
              +"                                  AND a.endoflifeyear < (date_part('year'::text, now        "
              +"                                      ()) + 11)                                             "
              +"                                  AND ST_Contains(lg.the_geom, a.the_geom)                  "
              +"                                  GROUP BY                                                  "
              +"                                      a.endoflifeyear                                       "
              +"                                  ORDER BY                                                  "
              +"                                      a.endoflifeyear ) AS endoflifeyeard ) AS              "
              +"                      endoflifeyear,                                                        "
              +"                      (                                                                     "
              +"                          SELECT                                                            "
              +"                              array_to_json(array_agg(row_to_json(wd)))                     "
              +"                          FROM                                                              "
              +"                              (                                                             "
              +"                                  SELECT                                                    "
              +"                                      COUNT(a.id) AS aantal,                                "
              +"                                      a.manufacturer                                        "
              +"                                  FROM                                                      "
              +"                                      v_playmapping_assets_compleet a                       "
              +"                                  WHERE                                                     "
              +"                                      ST_Contains(lg.the_geom, a.the_geom)                  "
              +"                                  GROUP BY                                                  "
              +"                                      a.manufacturer ) wd ) AS leveranciers ) AS l ))       "
              +"      AS                                                       properties                   "
              +"  FROM                                                                                      "
              +"      houten_pc6 AS lg                                                                      "
              +"  )                                                                                         "                    
                    + "", new MapListHandler());

            result = rowsToGeoJSONFeatureCollection2(rows);
        } catch(Exception e) {
            log.error("Error getting houten data", e);
            result.put("error", "Fout ophalen houten data: " + e.getClass() + ": " + e.getMessage());

        }
        context.getResponse().addHeader("Access-Control-Allow-Origin", "*");
        return new StreamingResolution("application/json", result.toString(4));
    }
    
    
}
