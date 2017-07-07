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

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.validation.*;
import nl.b3p.playbase.db.DB;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.util.PGobject;

/**
 * Export van JSON files voor gebruik in dashboard
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
    private static final String SPELEN = "spelen";
    private static final String BOMEN = "bomen";
    
    private final static List availableLocationTables = Arrays.asList("buurt_2016", "wijk_2016", "gemeent_2016", "houten_pc6");
    private final static Map<String, List> fieldsLocationTables = new HashMap<>();
    static {
        fieldsLocationTables.put("buurt_2016", Arrays.asList("\"GM_NAAM\":gemeente", "the_geom", "\"BU_NAAM\":naam", "\"AANT_INW\":inwoneraantal"));
        fieldsLocationTables.put("wijk_2016", Arrays.asList("\"GM_NAAM\":gemeente", "the_geom", "\"WK_NAAM\":naam", "\"AANT_INW\":inwoneraantal"));
        fieldsLocationTables.put("gemeent_2016", Arrays.asList("\"GM_NAAM\":gemeente", "the_geom","\"GM_NAAM\":naam", "\"AANT_INW\":inwoneraantal"));
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
//        if(!"Houten".equals(locationValue)) {
//            errors.add("locationValue", new SimpleError(("Geen geldige location ingevuld!")));
//        }
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
        return writeAggregatie (SPELEN);
    }
    
    public Resolution bomen() throws NamingException, SQLException {
        return writeAggregatie (BOMEN);
    }

    private Resolution writeAggregatie (String aggregatie) {
        JSONObject result = new JSONObject();
        try {
            
            List<Map<String,Object>> rows = getAggregatie(aggregatie, "snaptogrid", getLocationTable(), getLocationValue());
            
            if (!"gemeenten2015".equalsIgnoreCase(getLocationTable())) {
                //voeg gemeenteoverzicht toe als detail is gevraagd
                rows.addAll(getAggregatie(aggregatie, "centroid", "gemeenten2015", getLocationValue()));
            }

            result = rowsToGeoJSONFeatureCollection(rows);
        } catch(Exception e) {
            log.error("Error getting " + aggregatie, e);
            result.put("error", "Fout ophalen "+ aggregatie+ ": " 
                    + e.getClass() + ": " + e.getMessage());

        }
        context.getResponse().addHeader("Access-Control-Allow-Origin", "*");
        String name = aggregatie + "_" + getLocationTable() + "_" + getLocationValue()+ ".json";
        StreamingResolution res =  new StreamingResolution("application/json", result.toString(4));
        res.setFilename(name);
        res.setAttachment(true);
        return res;        
    }
    
    private List<Map<String,Object>> getAggregatie(String aggregatie, String geomConverter, String locTable, String locValue) throws NamingException, SQLException {
        final List<String> id = new ArrayList<>();
        id.add(locValue); 
        Connection con = DB.getConnection();
        Array toSelect = con.createArrayOf("text", id.toArray());
        
        List<String> locationFields = fieldsLocationTables.get(locTable);
        String checkColumn = locationFields.get(0).split(":")[0];
        String geomColumn = locationFields.get(1).split(":")[0];
        
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append("'Feature' AS type,");
        //geometrie als json
        sb.append("ST_AsGeoJSON(");
        if (geomConverter==null) {
            sb.append("lg.").append(geomColumn);
        } else if ("centroid".equalsIgnoreCase(geomConverter)) {
            sb.append("ST_Centroid(");
            sb.append("lg.").append(geomColumn);
            sb.append(")");
        } else if ("snaptogrid".equalsIgnoreCase(geomConverter)) {
            sb.append("ST_SnapToGrid(");
            sb.append("lg.").append(geomColumn);
            sb.append(",1)");
        }
        sb.append(",2,2)::json AS geometry,");
        // properties als json
        sb.append("row_to_json((SELECT l FROM ");
        // begin subselects
        sb.append("(SELECT ");
        // velden hoofdtabel
        for (String field : locationFields) {
            if (field.equalsIgnoreCase(locationFields.get(1))) {
                //skip geometry field
                continue;
            }
            sb.append(" lg.").append(field.split(":")[0]).append(" As ").append(field.split(":")[1]).append(", ");
        }
        // velden aggregaties
        String assetsTable;
        switch (aggregatie) {
            case SPELEN:
                //assetsTable = "v_pm_assets_compleet"; //v_playmapping_assets_compleet
                assetsTable = "v_pm_assets_api_compleet"; 
                sb.append(spelenBudgetSubselect(assetsTable, geomColumn)).append(",");
                sb.append(spelenGroepaantalSubselect(assetsTable, geomColumn)).append(",");
                sb.append(spelenInstalledyearSubselect(assetsTable, geomColumn)).append(",");
                sb.append(spelenVervangjaarSubselect(assetsTable, geomColumn)).append(",");
                sb.append(spelenEndoflifeyearSubselect(assetsTable, geomColumn)).append(",");
                sb.append(spelenLeveranciersSubselect(assetsTable, geomColumn));
                
                break;
            case BOMEN:
                assetsTable = "v_bomen_jaarverloop";
                sb.append(bomenBomenaantalSubselect(assetsTable, geomColumn)).append(",");
                sb.append(bomenKostenSubselect(assetsTable, geomColumn)).append(",");
                sb.append(bomenEindbeeldSubselect(assetsTable, geomColumn)).append(",");
                sb.append(bomenMaatregelenKortSubselect(assetsTable, geomColumn)).append(",");
                sb.append(bomenBoomhoogteSubselect(assetsTable, geomColumn));
                break;
            default:
                throw new UnsupportedOperationException();
        }
        
        // einde subselects
        sb.append(") AS l )) AS properties ");
        
        sb.append("FROM ").append(locTable).append(" AS lg ");
        sb.append("WHERE lg.").append(checkColumn).append(" = ANY(?) ");

        List<Map<String,Object>> rows = DB.qr().query(sb.toString(), new MapListHandler(), toSelect);
        con.close();
        return rows;
    }
    
    private String spelenBudgetSubselect(String assetsTable, String geomColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        sb.append(" SELECT ");
        sb.append("     (row_to_json(budgetd) ) ");
        sb.append(" FROM ");
        sb.append("     ( ");
        sb.append("         SELECT ");
        sb.append("             SUM(aantal)            AS aantal, ");
        sb.append("             SUM(onderhoud)::INT    AS onderhoud80, ");
        sb.append("             SUM(afschrijving)::INT AS afschrijving, ");
        sb.append("             SUM(beheer)::INT       AS beheer, ");
        sb.append("             SUM(aanschafwaarde)::INT       AS aanschafwaarde ");
        sb.append("         FROM ");
        sb.append("             ( ");
        sb.append("                 SELECT ");
        sb.append("                     COUNT(a.id)                 AS aantal, ");
        sb.append("                     SUM(a.onderhoudskosten)*0.8 AS ");
        sb.append("                                                   onderhoud, ");
        sb.append("                     SUM(a.afschrijving)     AS afschrijving, ");
        sb.append("                     SUM(a.aanschafwaarde)*0.035 AS beheer, ");
        sb.append("                     SUM(a.aanschafwaarde) AS aanschafwaarde ");
        sb.append("                 FROM ");
        sb.append("                     ").append(assetsTable).append(" a ");
        sb.append("                 WHERE ");
        sb.append("                     ST_Contains(lg.").append(geomColumn).append(", a.the_geom) ");
        sb.append("                 AND a.type <> 'veiligheidsondergrond' ");
        sb.append("                 UNION ");
        sb.append("                 SELECT ");
        sb.append("                     COUNT(a.id)                 AS aantal, ");
        sb.append("                     SUM(a.onderhoudskosten)*0.8 AS ");
        sb.append("                                                   onderhoud, ");
        sb.append("                     SUM(a.afschrijving)     AS afschrijving, ");
        sb.append("                     SUM(a.aanschafwaarde)*0.005 AS beheer, ");
        sb.append("                     SUM(a.aanschafwaarde) AS aanschafwaarde ");
        sb.append("                 FROM ");
        sb.append("                     ").append(assetsTable).append(" a ");
        sb.append("                 WHERE ");
        sb.append("                     ST_Contains(lg.").append(geomColumn).append(", a.the_geom) ");
        sb.append("                 AND a.type = 'veiligheidsondergrond') AS ");
        sb.append("             foo ) AS budgetd ) AS budget ");
        return sb.toString();
    }
    
    private String spelenGroepaantalSubselect(String assetsTable, String geomColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("        array_to_json(array_agg(row_to_json(groepaantald) ))  ");
        sb.append(" FROM ");
        sb.append("        (  ");
        sb.append("            SELECT ");
        sb.append("                a.groep, ");
        sb.append("                COUNT(a.id)                  AS aantal, ");
        sb.append("                SUM(a.onderhoudskosten)::INT AS onderhoud,  ");
        sb.append("                SUM(a.afschrijving)::INT     AS afschrijving,  ");
        sb.append("                SUM(a.aanschafwaarde)::INT     AS aanschafwaarde  ");
        sb.append("            FROM  ");
        sb.append("                ").append(assetsTable).append(" a ");
        sb.append("            WHERE  ");
        sb.append("                ST_Contains(lg.").append(geomColumn).append(", a.the_geom) ");
        sb.append("            AND a.type <> 'veiligheidsondergrond' ");
        sb.append("            GROUP BY ");
        sb.append("                a.groep ) AS groepaantald ) AS groepaantal ");
        return sb.toString();
    }
 
    private String spelenInstalledyearSubselect(String assetsTable, String geomColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("    array_to_json(array_agg(row_to_json(installedyeard))) ");
        sb.append(" FROM ");
        sb.append("    ( ");
        sb.append("     SELECT ");
        sb.append("         a.installedyear, ");
        sb.append("         COUNT(a.id) AS aantal ");
        sb.append("     FROM  ");
        sb.append("          ").append(assetsTable).append(" a  ");
        sb.append("     WHERE ");
        sb.append("          a.type <> 'veiligheidsondergrond'  ");
        sb.append("     AND ST_Contains(lg.").append(geomColumn).append(", a.the_geom) ");
        sb.append("     GROUP BY ");
        sb.append("        a.installedyear ) AS installedyeard ) AS installedyear ");
        return sb.toString();
    }
    
    private String spelenVervangjaarSubselect(String assetsTable, String geomColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("     array_to_json(array_agg(row_to_json(vervangjaard))) ");
        sb.append(" FROM ");
        sb.append("     ( ");
        sb.append("         SELECT ");
        sb.append("             vervangjaar              AS vervangjaar, ");
        sb.append("             SUM(aantal)              AS aantal, ");
        sb.append("             SUM(aanschafwaarde)*1.35 AS aanschafwaarde ");
        sb.append("         FROM ");
        sb.append("             ( ");
        sb.append("             ( ");
        sb.append("                 SELECT ");
        sb.append("                     vervangjaar1        AS vervangjaar, ");
        sb.append("                     COUNT(id)           AS aantal, ");
        sb.append("                     SUM(aanschafwaarde) AS aanschafwaarde ");
        sb.append("                 FROM  ");
        sb.append("                     ").append(assetsTable).append(" a1 ");
        sb.append("                 WHERE ");
        sb.append("                     type <> 'veiligheidsondergrond' ");
        sb.append("                 AND ST_Contains(lg.").append(geomColumn).append(", a1.the_geom) ");
        sb.append("                 GROUP BY ");
        sb.append("                     vervangjaar1) ");
        sb.append("         UNION ");
        sb.append("             ( ");
        sb.append("                 SELECT ");
        sb.append("                     vervangjaar2        AS vervangjaar, ");
        sb.append("                     COUNT(id)           AS aantal, ");
        sb.append("                     SUM(aanschafwaarde) AS aanschafwaarde ");
        sb.append("                 FROM ");
        sb.append("                     ").append(assetsTable).append(" a2 ");
        sb.append("                 WHERE ");
        sb.append("                     type <> 'veiligheidsondergrond' ");
        sb.append("                 AND ST_Contains(lg.").append(geomColumn).append(", a2.the_geom) ");
        sb.append("                 GROUP BY ");
        sb.append("                     vervangjaar2) ");
        sb.append("         UNION ");
        sb.append("             ( ");
        sb.append("                 SELECT ");
        sb.append("                     vervangjaar3        AS vervangjaar, ");
        sb.append("                     COUNT(id)           AS aantal, ");
        sb.append("                     SUM(aanschafwaarde) AS aanschafwaarde ");
        sb.append("                 FROM ");
        sb.append("                     ").append(assetsTable).append(" a3 ");
        sb.append("                 WHERE ");
        sb.append("                     type <> 'veiligheidsondergrond'  ");
        sb.append("                 AND ST_Contains(lg.").append(geomColumn).append(", a3.the_geom) ");
        sb.append("                 GROUP BY ");
        sb.append("                     vervangjaar3) ) AS foo  ");
        sb.append("         WHERE ");
        sb.append("             vervangjaar < (date_part('year'::text, now()) + 11) ");
        sb.append("         GROUP BY ");
        sb.append("             vervangjaar ");
        sb.append("         ORDER BY ");
        sb.append("             vervangjaar ) AS vervangjaard ) AS vervangjaar ");
        return sb.toString();
    }

    private String spelenEndoflifeyearSubselect(String assetsTable, String geomColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("         array_to_json(array_agg(row_to_json(endoflifeyeard))) ");
        sb.append(" FROM ");
        sb.append("         ( ");
        sb.append("             SELECT ");
        sb.append("                 a.endoflifeyear       AS endoflifeyear, ");
        sb.append("                 COUNT(a.id)           AS aantal, ");
        sb.append("                 SUM(a.aanschafwaarde) AS aanschafwaarde_toestel ");
        sb.append("             FROM ");
        sb.append("                 ").append(assetsTable).append(" a ");
        sb.append("             WHERE ");
        sb.append("                 type <> 'veiligheidsondergrond' ");
        sb.append("             AND a.endoflifeyear < (date_part('year'::text, now()) + 11) ");
        sb.append("             AND ST_Contains(lg.").append(geomColumn).append(", a.the_geom) ");
        sb.append("             GROUP BY ");
        sb.append("                 a.endoflifeyear ");
        sb.append("             ORDER BY  ");
        sb.append("                 a.endoflifeyear ) AS endoflifeyeard ) AS endoflifeyear ");
    
       return sb.toString();
    }

    private String spelenLeveranciersSubselect(String assetsTable, String geomColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("          array_to_json(array_agg(row_to_json(wd))) ");
        sb.append("      FROM ");
        sb.append("          ( ");
        sb.append("              SELECT ");
        sb.append("                  COUNT(a.id) AS aantal, ");
        sb.append("                  a.manufacturer ");
        sb.append("              FROM   ");
        sb.append("                  ").append(assetsTable).append(" a ");
        sb.append("              WHERE ");
        sb.append("                  ST_Contains(lg.").append(geomColumn).append(", a.the_geom) ");
        sb.append("              GROUP BY ");
        sb.append("                  a.manufacturer ) wd ) AS leveranciers ");
        return sb.toString();
    }
                  
    private String bomenBomenaantalSubselect(String assetsTable, String geomColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append(" (SELECT COUNT(a.boomid) ");
        sb.append("  FROM ");
        sb.append("      ").append(assetsTable).append(" a  ");
        sb.append("  WHERE ");
        sb.append("      ST_Contains(lg.").append(geomColumn).append(", a.the_geom) ");
        sb.append("      and a.jaarnr=0 ");
        sb.append(" ) AS bomenaantal ");
        return sb.toString();
    }

    private String bomenKostenSubselect(String assetsTable, String geomColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("     array_to_json(array_agg(row_to_json(kostend))) ");
        sb.append(" FROM ");
        sb.append("     (  ");
        sb.append("         SELECT  ");
        sb.append("             jaar            AS jaar, ");
        sb.append("             SUM(kosten)::INT       AS kosten  ");
        sb.append("         FROM ");
        sb.append("             ( ");
        sb.append("                 SELECT ");
        sb.append("                     (date_part('year'::text, now()) + a.jaarnr) AS jaar, ");
        sb.append("                     SUM(a.prijs) AS kosten ");
        sb.append("                 FROM ");
        sb.append("                     ").append(assetsTable).append(" a ");
        sb.append("                 WHERE ");
        sb.append("                     ST_Contains(lg.").append(geomColumn).append(", a.the_geom) ");
        sb.append("                 GROUP BY a.jaarnr) AS foo ");
        sb.append("             GROUP BY ");
        sb.append("             jaar  ");
        sb.append("         ORDER BY  ");
        sb.append("             jaar) AS kostend ) AS kosten ");
        return sb.toString();
    }

    private String bomenEindbeeldSubselect(String assetsTable, String geomColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("     array_to_json(array_agg(row_to_json(eindbeeldd))) ");
        sb.append(" FROM  ");
        sb.append("     (  ");
        sb.append("         SELECT  ");
        sb.append("             trim(trailing from eindbeeld, ' ') AS eindbeeld, ");
        sb.append("             SUM(kosten)::INT       AS kosten,  ");
        sb.append("             SUM(aantal)	AS aantal ");
        sb.append("         FROM ");
        sb.append("             ( ");
        sb.append("                 SELECT ");
        sb.append("                     a.eindbeeld            AS eindbeeld,  ");
        sb.append("                     SUM(a.prijs) AS kosten, ");
        sb.append("                     COUNT(a.boomid) as aantal  ");
        sb.append("                 FROM  ");
        sb.append("                     ").append(assetsTable).append(" a  ");
        sb.append("                 WHERE  ");
        sb.append("                     ST_Contains(lg.").append(geomColumn).append(", a.the_geom) ");
        sb.append("                     and a.jaarnr = 0 ");
        sb.append("                 GROUP BY a.eindbeeld) AS foo ");
        sb.append("             GROUP BY ");
        sb.append("             eindbeeld ");
        sb.append("         ORDER BY ");
        sb.append("             eindbeeld) AS eindbeeldd ) AS eindbeeld ");
        return sb.toString();
    }

    private String bomenMaatregelenKortSubselect(String assetsTable, String geomColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("     array_to_json(array_agg(row_to_json(maatregelen_kortd))) ");
        sb.append(" FROM  ");
        sb.append("     ( ");
        sb.append("         SELECT ");
        sb.append("             trim(trailing from maatregelen_kort, ' ') AS maatregelen_kort, ");
        sb.append("             SUM(kosten)::INT       AS kosten,  ");
        sb.append("             SUM(aantal)	AS aantal ");
        sb.append("         FROM  ");
        sb.append("             (  ");
        sb.append("                 SELECT ");
        sb.append("                     a.maatregelen_kort AS maatregelen_kort, ");
        sb.append("                     SUM(a.prijs) AS kosten, ");
        sb.append("                     COUNT(a.boomid) as aantal ");
        sb.append("                 FROM ");
        sb.append("                     ").append(assetsTable).append(" a ");
        sb.append("                 WHERE ");
        sb.append("                     ST_Contains(lg.").append(geomColumn).append(", a.the_geom) ");
        sb.append("                     and a.jaarnr = 0 ");
        sb.append("                 GROUP BY a.maatregelen_kort) AS foo ");
        sb.append("             GROUP BY ");
        sb.append("             maatregelen_kort ");
        sb.append("         ORDER BY ");
        sb.append("             maatregelen_kort) AS maatregelen_kortd ) AS maatregelen_kort ");
        return sb.toString();
    }

    private String bomenBoomhoogteSubselect(String assetsTable, String geomColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("     array_to_json(array_agg(row_to_json(boomhoogted))) ");
        sb.append(" FROM  ");
        sb.append("     ( ");
        sb.append("         SELECT ");
        sb.append("             trim(trailing from boomhoogte, ' ') AS boomhoogte,  ");
        sb.append("             SUM(kosten)::INT       AS kosten,  ");
        sb.append("             SUM(aantal)	AS aantal ");
        sb.append("         FROM  ");
        sb.append("             (  ");
        sb.append("                 SELECT ");
        sb.append("                     a.boomhoogte            AS boomhoogte,  ");
        sb.append("                     SUM(a.prijs) AS kosten, ");
        sb.append("                     COUNT(a.boomid) as aantal ");
        sb.append("                 FROM ");
        sb.append("                     ").append(assetsTable).append(" a ");
        sb.append("                 WHERE ");
        sb.append("                     ST_Contains(lg.").append(geomColumn).append(", a.the_geom) ");
        sb.append("                     and a.jaarnr = 0  ");
        sb.append("                 GROUP BY a.boomhoogte) AS foo  ");
        sb.append("             GROUP BY ");
        sb.append("             boomhoogte ");
        sb.append("         ORDER BY ");
        sb.append("             boomhoogte) AS boomhoogted ) AS boomhoogte ");
        return sb.toString();
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
