package nl.b3p.dashboard.service.admin.stripes;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.CustomScoped;
import javax.naming.NamingException;
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
    
    private final static List availableLocationTables = Arrays.asList("buurten2015", "wijken2015", "gemeenten2015", "houten_pc6");
    private final static Map<String, List> fieldsLocationTables = new HashMap<>();
    static {
        fieldsLocationTables.put("buurten2015", Arrays.asList("\"GM_NAAM\":gemeente", "the_geom", "\"BU_NAAM\":naam", "\"AANT_INW\":inwoneraantal"));
        fieldsLocationTables.put("wijken2015", Arrays.asList("\"GM_NAAM\":gemeente", "the_geom", "\"WK_NAAM\":naam", "\"AANT_INW\":inwoneraantal"));
        fieldsLocationTables.put("gemeenten2015", Arrays.asList("\"GM_NAAM\":gemeente", "the_geom", "\"AANT_INW\":inwoneraantal"));
        fieldsLocationTables.put("houten_pc6", Arrays.asList("woonplaa_1:woonplaats", "the_geom", "postcode:naam", "aantaladressen:aantaladressen"));
    }

    public List getAvailableLocationTables() {
        return availableLocationTables;
    }

    @Validate(required=true)
    private String locationTable;
    @Validate(required=true)
    private String locationValue;
    
    @ValidationMethod()
    public void validateLocations(ValidationErrors errors) {
        if(!"Houten".equals(locationValue)) {
            errors.add("locationValue", new SimpleError(("Geen geldige location ingevuld!")));
        }
    }
    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }


    public String getLocationTable() {
        return locationTable;
    }

    public void setLocationTable(String locationTable) {
        this.locationTable = locationTable;
    }

    public String getLocationValue() {
        return locationValue;
    }

    public void setLocationValue(String locationValue) {
        this.locationValue = locationValue;
    }
    
    @DefaultHandler
    @DontValidate
    public Resolution edit() throws Exception {
        return new ForwardResolution(JSP);
    }

    public Resolution spelen() throws NamingException, SQLException {
                
        JSONObject result = new JSONObject();
        try {
            
            List<Map<String,Object>> rows = getSpelenAggregatie(getLocationTable(), getLocationValue());
            
            if (!"gemeenten2015".equalsIgnoreCase(getLocationTable())) {
                //voeg gemeenteoverzicht toe als detail is gevraagd
                rows.addAll(getSpelenAggregatie("gemeenten2015", getLocationValue()));
            }

            result = rowsToGeoJSONFeatureCollection(rows);
        } catch(Exception e) {
            log.error("Error getting spelen data", e);
            result.put("error", "Fout ophalen spelen data: " + e.getClass() + ": " + e.getMessage());

        }
        context.getResponse().addHeader("Access-Control-Allow-Origin", "*");
        return new StreamingResolution("application/json", result.toString(4));
    }
    
    public Resolution bomen() throws NamingException, SQLException {
        
        JSONObject result = new JSONObject();
        try {
            
            List<Map<String,Object>> rows = getSpelenAggregatie(getLocationTable(), getLocationValue());
            
            if (!"gemeenten2015".equalsIgnoreCase(getLocationTable())) {
                //voeg gemeenteoverzicht toe als detail is gevraagd
                rows.addAll(getSpelenAggregatie("gemeenten2015", getLocationValue()));
            }

            result = rowsToGeoJSONFeatureCollection(rows);
        } catch(Exception e) {
            log.error("Error getting bomen data", e);
            result.put("error", "Fout ophalen bomen data: " + e.getClass() + ": " + e.getMessage());

        }
        context.getResponse().addHeader("Access-Control-Allow-Origin", "*");
        return new StreamingResolution("application/json", result.toString(4));
    }
    
    protected List<Map<String,Object>> getSpelenAggregatie(String locTable, String locValue) throws NamingException, SQLException {
        String assetsTable = "v_playmapping_assets_compleet";
        final List<String> id = new ArrayList<>();
        id.add(locValue); 
        Array toSelect = DB.getConnection().createArrayOf("text", id.toArray());
        
        List<String> locationFields = fieldsLocationTables.get(locTable);
        String checkColumn = locationFields.get(0).split(":")[0];
        String geomColumn = locationFields.get(1).split(":")[0];
        
        StringBuilder sb = new StringBuilder();

        sb.append(""
          +"  SELECT                                                                                    "
          +"      'Feature'                       AS type,                                              "
          +"      ST_AsGeoJSON(lg." + geomColumn + ",2,2)::json AS geometry,                            "
          +"      row_to_json(                                                                          "
          +"      (                                                                                     "
          +"          SELECT                                                                            "
          +"              l                                                                             "
          +"          FROM                                                                              "
          +"              (                                                                             "
          +"                  SELECT                                                                    ");

        for (String field : locationFields) {
            if (field.equalsIgnoreCase(locationFields.get(1))) {
                //skip geometry field
                continue;
            }
            sb.append(" lg.").append(field.split(":")[0]).append(" As ").append(field.split(":")[1]).append(", ");
        }

        sb.append(""        
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
          +"                                              " + assetsTable +           " a               "
          +"                                          WHERE                                             "
          +"                                              ST_Contains(lg." + geomColumn + ", a.the_geom)"
          +"                                          AND a.type <> 'veiligheidsondergrond'             "
          +"                                          UNION                                             "
          +"                                          SELECT                                            "
          +"                                              COUNT(a.id)                 AS aantal,        "
          +"                                              SUM(a.onderhoudskosten)*0.8 AS                "
          +"                                                                            onderhoud,      "
          +"                                              SUM(a.afschrijving)     AS afschrijving,      "
          +"                                              SUM(a.aanschafwaarde)*0.005 AS beheer         "
          +"                                          FROM                                              "
          +"                                              " + assetsTable +           " a               "
          +"                                          WHERE                                             "
          +"                                              ST_Contains(lg." + geomColumn + ", a.the_geom)"
          +"                                          AND a.type = 'veiligheidsondergrond') AS          "
          +"                                      foo ) AS budgetd ) AS budget ,                        "
          +"                      (                                                                     "
          +"                          SELECT                                                            "
          +"                              array_to_json(array_agg(row_to_json(groepaantald) ))          "
          +"                          FROM                                                              "
          +"                              (                                                             "
          +"                                  SELECT                                                    "
          +"                                      a.\"group\",                                          "
          +"                                      COUNT(a.id)                  AS aantal,               "
          +"                                      SUM(a.onderhoudskosten)::INT AS onderhoud,            "
          +"                                      SUM(a.afschrijving)::INT     AS afschrijving          "
          +"                                  FROM                                                      "
          +"                                      " + assetsTable +           " a                       "
          +"                                  WHERE                                                     "
          +"                                      ST_Contains(lg." + geomColumn + ", a.the_geom)        "
          +"                                  AND a.type <> 'veiligheidsondergrond'                     "
          +"                                  GROUP BY                                                  "
          +"                                      a.\"group\" ) AS groepaantald ) AS groepaantal ,      "
          +"                      (                                                                     "
          +"                          SELECT                                                            "
          +"                              array_to_json(array_agg(row_to_json(installedyeard)))         "
          +"                          FROM                                                              "
          +"                              (                                                             "
          +"                                  SELECT                                                    "
          +"                                      a.installedyear,                                      "
          +"                                      COUNT(a.id) AS aantal                                 "
          +"                                  FROM                                                      "
          +"                                      " + assetsTable +           " a                       "
          +"                                  WHERE                                                     "
          +"                                      a.type <> 'veiligheidsondergrond'                     "
          +"                                  AND ST_Contains(lg." + geomColumn + ", a.the_geom)        "
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
          +"                                              " + assetsTable +           " a1              "
          +"                                          WHERE                                             "
          +"                                              type <> 'veiligheidsondergrond'               "
          +"                                          AND ST_Contains(lg." +geomColumn+ ", a1.the_geom) "
          +"                                          GROUP BY                                          "
          +"                                              vervangjaar1)                                 "
          +"                                  UNION                                                     "
          +"                                      (                                                     "
          +"                                          SELECT                                            "
          +"                                              vervangjaar2        AS vervangjaar,           "
          +"                                              COUNT(id)           AS aantal,                "
          +"                                              SUM(aanschafwaarde) AS aanschafwaarde         "
          +"                                          FROM                                              "
          +"                                              " + assetsTable +           " a2              "
          +"                                          WHERE                                             "
          +"                                              type <> 'veiligheidsondergrond'               "
          +"                                          AND ST_Contains(lg." +geomColumn+ ", a2.the_geom) "
          +"                                          GROUP BY                                          "
          +"                                              vervangjaar2)                                 "
          +"                                  UNION                                                     "
          +"                                      (                                                     "
          +"                                          SELECT                                            "
          +"                                              vervangjaar3        AS vervangjaar,           "
          +"                                              COUNT(id)           AS aantal,                "
          +"                                              SUM(aanschafwaarde) AS aanschafwaarde         "
          +"                                          FROM                                              "
          +"                                              " + assetsTable +           " a3              "
          +"                                          WHERE                                             "
          +"                                              type <> 'veiligheidsondergrond'               "
          +"                                          AND ST_Contains(lg." +geomColumn+ ", a3.the_geom) "
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
          +"                                      " + assetsTable +           " a                       "
          +"                                  WHERE                                                     "
          +"                                      type <> 'veiligheidsondergrond'                       "
          +"                                  AND a.endoflifeyear < (date_part('year'::text, now        "
          +"                                      ()) + 11)                                             "
          +"                                  AND ST_Contains(lg." + geomColumn + ", a.the_geom)        "
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
          +"                                      " + assetsTable +           " a                       "
          +"                                  WHERE                                                     "
          +"                                      ST_Contains(lg." + geomColumn + ", a.the_geom)        "
          +"                                  GROUP BY                                                  "
          +"                                      a.manufacturer ) wd ) AS leveranciers ) AS l ))       "
          +"      AS                                                       properties                   "
          +"  FROM                                                                                      "
          +"      " + locTable + " AS lg                                                                "
          +"  WHERE                                                                                     "
          +"      lg." + checkColumn + " = ANY(?)                                                       ");

        List<Map<String,Object>> rows = DB.qr().query(sb.toString(), new MapListHandler(), toSelect);

        return rows;
    }
    
    protected List<Map<String,Object>> getBomenAggregatie(String locTable, String locValue) throws NamingException, SQLException {
        String assetsTable = "v_bomen_jaarverloop";
        final List<String> id = new ArrayList<>();
        id.add(locValue); 
        Array toSelect = DB.getConnection().createArrayOf("text", id.toArray());
        
        List<String> locationFields = fieldsLocationTables.get(locTable);
        String checkColumn = locationFields.get(0).split(":")[0];
        String geomColumn = locationFields.get(1).split(":")[0];
        
        StringBuilder sb = new StringBuilder();

        sb.append(""
          +"  SELECT 'Feature' As type                                                                              "
          +"		    , ST_AsGeoJSON(lg." +geomColumn+ ",2,2)::json As geometry                               "
          +"		    , row_to_json((SELECT l FROM (                                                          "
          +"                      SELECT                                                                            ");

        for (String field : locationFields) {
            if (field.equalsIgnoreCase(locationFields.get(1))) {
                //skip geometry field
                continue;
            }
            sb.append(" lg.").append(field.split(":")[0]).append(" As ").append(field.split(":")[1]).append(", ");
        }

        sb.append(""        
          +"                      (SELECT COUNT(a.boomid)                                                           "
          +"                                          FROM                                                          "
          +"                                              " + assetsTable + " a                                     "
          +"                                          WHERE                                                         "
          +"                                              ST_Contains(lg." +geomColumn+ ", a.the_geom)              "
          +"                                              and a.jaarnr=0                                            "
          +"                                          ) AS bomenaantal,                                             "
          +"		      (                                                                                     "
          +"                          SELECT                                                                        "
          +"                              array_to_json(array_agg(row_to_json(kostend)))                            "
          +"                          FROM                                                                          "
          +"                              (                                                                         "
          +"                                  SELECT                                                                "
          +"                                      jaar            AS jaar,                                          "
          +"                                      SUM(kosten)::INT       AS kosten                                  "
          +"                                  FROM                                                                  "
          +"                                      (                                                                 "
          +"                                          SELECT                                                        "
          +"                                              (date_part('year'::text, now()) + a.jaarnr) AS jaar,      "
          +"                                              SUM(a.prijs) AS kosten                                    "
          +"                                          FROM                                                          "
          +"                                              " + assetsTable + " a                                     "
          +"                                          WHERE                                                         "
          +"                                              ST_Contains(lg." +geomColumn+ ", a.the_geom)              "
          +"                                          GROUP BY a.jaarnr) AS foo                                     "
          +"                                      GROUP BY                                                          "
          +"                                      jaar                                                              "
          +"                                  ORDER BY                                                              "
          +"                                      jaar) AS kostend ) AS kosten,                                     "
          +"	              (                                                                                     "
          +"                          SELECT                                                                        "
          +"                              array_to_json(array_agg(row_to_json(eindbeeldd)))                         "
          +"                          FROM                                                                          "
          +"                              (                                                                         "
          +"                                  SELECT                                                                "
          +"                                      trim(trailing from eindbeeld, ' ') AS eindbeeld,                  "
          +"                                      SUM(kosten)::INT       AS kosten,                                 "
          +"                                      SUM(aantal)	AS aantal                                           "
          +"                                  FROM                                                                  "
          +"                                      (                                                                 "
          +"                                          SELECT                                                        "
          +"                                              a.eindbeeld            AS eindbeeld,                      "
          +"                                              SUM(a.prijs) AS kosten,                                   "
          +"                                              COUNT(a.boomid) as aantal                                 "
          +"                                          FROM                                                          "
          +"                                              " + assetsTable + " a                                     "
          +"                                          WHERE                                                         "
          +"                                              ST_Contains(lg." +geomColumn+ ", a.the_geom)              "
          +"                                              and a.jaarnr = 0                                          "
          +"                                          GROUP BY a.eindbeeld) AS foo                                  "
          +"                                      GROUP BY                                                          "
          +"                                      eindbeeld                                                         "
          +"                                  ORDER BY                                                              "
          +"                                      eindbeeld) AS eindbeeldd ) AS eindbeeld,                          "
          +"		      (                                                                                     "
          +"                          SELECT                                                                        "
          +"                              array_to_json(array_agg(row_to_json(maatregelen_kortd)))                  "
          +"                          FROM                                                                          "
          +"                              (                                                                         "
          +"                                  SELECT                                                                "
          +"                                      trim(trailing from maatregelen_kort, ' ') AS maatregelen_kort,    "
          +"                                      SUM(kosten)::INT       AS kosten,                                 "
          +"                                      SUM(aantal)	AS aantal                                           "
          +"                                  FROM                                                                  "
          +"                                      (                                                                 "
          +"                                          SELECT                                                        "
          +"                                              a.maatregelen_kort AS maatregelen_kort,                   "
          +"                                              SUM(a.prijs) AS kosten,                                   "
          +"                                              COUNT(a.boomid) as aantal                                 "
          +"                                          FROM                                                          "
          +"                                              " + assetsTable + " a                                     "
          +"                                          WHERE                                                         "
          +"                                              ST_Contains(lg." +geomColumn+ ", a.the_geom)              "
          +"                                              and a.jaarnr = 0                                          "
          +"                                          GROUP BY a.maatregelen_kort) AS foo                           "
          +"                                      GROUP BY                                                          "
          +"                                      maatregelen_kort                                                  "
          +"                                  ORDER BY                                                              "
          +"                                      maatregelen_kort) AS maatregelen_kortd ) AS maatregelen_kort,     "
          +"		      (                                                                                     "
          +"                          SELECT                                                                        "
          +"                              array_to_json(array_agg(row_to_json(boomhoogted)))                        "
          +"                          FROM                                                                          "
          +"                              (                                                                         "
          +"                                  SELECT                                                                "
          +"                                      trim(trailing from boomhoogte, ' ') AS boomhoogte,                "
          +"                                      SUM(kosten)::INT       AS kosten,                                 "
          +"                                      SUM(aantal)	AS aantal                                           "
          +"                                  FROM                                                                  "
          +"                                      (                                                                 "
          +"                                          SELECT                                                        "
          +"                                              a.boomhoogte            AS boomhoogte,                    "
          +"                                              SUM(a.prijs) AS kosten,                                   "
          +"                                              COUNT(a.boomid) as aantal                                 "
          +"                                          FROM                                                          "
          +"                                              " + assetsTable + " a                                     "
          +"                                          WHERE                                                         "
          +"                                              ST_Contains(lg." +geomColumn+ ", a.the_geom)              "
          +"                                              and a.jaarnr = 0                                          "
          +"                                          GROUP BY a.boomhoogte) AS foo                                 "
          +"                                      GROUP BY                                                          "
          +"                                      boomhoogte                                                        "
          +"                                  ORDER BY                                                              "
          +"                                      boomhoogte) AS boomhoogted ) AS boomhoogte                        "
          +"                                                                                                        "
          +"                    ) AS l )) AS  properties                                                            "
          +"  FROM                                                                                                  "
          +"      " + locTable + " AS lg                                                                            "
          +"  WHERE                                                                                                 "
          +"      lg." + checkColumn + " = ANY(?)                                                                   ");

        List<Map<String,Object>> rows = DB.qr().query(sb.toString(), new MapListHandler(), toSelect);

        return rows;
    }
    
    private JSONObject rowsToGeoJSONFeatureCollection(List<Map<String, Object>> rows) {
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
    
}
