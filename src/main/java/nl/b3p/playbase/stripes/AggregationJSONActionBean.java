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
@UrlBinding("/action/json2")
public class AggregationJSONActionBean implements ActionBean {

    private ActionBeanContext context;

    private static final Log log = LogFactory.getLog(AggregationJSONActionBean.class);

    private static final String JSP = "/WEB-INF/jsp/admin/jsonclient.jsp";
    private static final String SPELEN = "spelen";
    private static final String BOMEN = "bomen";

    @Validate(required = true)
    private String cqlfilter = "location_guid = '001d51fb-40b3-4ed1-afb3-1b1336e02e43'";
    @Validate(required = true)
    private String description = "testrun";

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

    public String getCqlfilter() {
        return cqlfilter;
    }

    public void setCqlfilter(String cqlfilter) {
        this.cqlfilter = cqlfilter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DefaultHandler
    @DontValidate
    public Resolution edit() throws Exception {
        return new ForwardResolution(JSP);
    }

    public Resolution spelen() throws NamingException, SQLException {
        return writeAggregatie(SPELEN);
    }

    public Resolution bomen() throws NamingException, SQLException {
        return writeAggregatie(BOMEN);
    }

    private Resolution writeAggregatie(String aggregatie) {
        JSONObject result = new JSONObject();
        try {

            List<Map<String, Object>> rows = null;
            switch (aggregatie) {
                case SPELEN:
                    rows = getSpelenAggregatie();
                    break;
                case BOMEN:
                    rows = getBomenAggregatie();
                    break;
                default:
                    throw new UnsupportedOperationException();
            }

            result = rowsToGeoJSONFeatureCollection(rows);
        } catch (Exception e) {
            log.error("Error getting " + aggregatie, e);
            result.put("error", "Fout ophalen " + aggregatie + ": "
                    + e.getClass() + ": " + e.getMessage());

        }
        context.getResponse().addHeader("Access-Control-Allow-Origin", "*");
        String name = aggregatie + "_" + getDescription() + ".json";
        StreamingResolution res = new StreamingResolution("application/json", result.toString(4));
        res.setFilename(name);
        res.setAttachment(true);
        return res;
    }

    private List<Map<String, Object>> getSpelenAggregatie() throws NamingException, SQLException {

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append("'Feature' AS type,");

        // properties als json
        sb.append("row_to_json((SELECT l FROM ");
        // begin subselects
        sb.append("(SELECT ");
        // velden hoofdtabel
        sb.append("'").append(getDescription()).append("' As ").append("description").append(", ");
        // velden aggregaties
        String assetsTable;
        //assetsTable = "v_pm_assets_compleet"; //v_playmapping_assets_compleet
        assetsTable = "v_playservice_location_equipment_compleet";
        sb.append(spelenBudgetSubselect(assetsTable)).append(",");
        sb.append(spelenGroepaantalSubselect(assetsTable)).append(",");
        sb.append(spelenInstalledyearSubselect(assetsTable)).append(",");
        sb.append(spelenVervangjaarSubselect(assetsTable)).append(",");
        sb.append(spelenEndoflifeyearSubselect(assetsTable)).append(",");
        sb.append(spelenLeveranciersSubselect(assetsTable));

        // einde subselects
        sb.append(") AS l )) AS properties ");

        List<Map<String, Object>> rows = DB.qr().query(sb.toString(), new MapListHandler());
        return rows;
    }

    private String spelenBudgetSubselect(String assetsTable) {
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
        sb.append("                     COUNT(guid)                 AS aantal, ");
        sb.append("                     SUM(onderhoudskosten)*0.8 AS ");
        sb.append("                                                   onderhoud, ");
        sb.append("                     SUM(afschrijving)     AS afschrijving, ");
        sb.append("                     SUM(aanschafwaarde)*0.035 AS beheer, ");
        sb.append("                     SUM(aanschafwaarde) AS aanschafwaarde ");
        sb.append("                 FROM ");
        sb.append("                     ").append(assetsTable);
        sb.append("                 WHERE  ").append(getCqlfilter());
        sb.append("                 AND type <> 'veiligheidsondergrond'");
        sb.append("                 UNION ");
        sb.append("                 SELECT ");
        sb.append("                     COUNT(guid)                 AS aantal, ");
        sb.append("                     SUM(onderhoudskosten)*0.8 AS ");
        sb.append("                                                   onderhoud, ");
        sb.append("                     SUM(afschrijving)     AS afschrijving, ");
        sb.append("                     SUM(aanschafwaarde)*0.005 AS beheer, ");
        sb.append("                     SUM(aanschafwaarde) AS aanschafwaarde ");
        sb.append("                 FROM ");
        sb.append("                     ").append(assetsTable);
        sb.append("                 WHERE  ").append(getCqlfilter());
        sb.append("                 AND type = 'veiligheidsondergrond') AS ");
        sb.append("             foo ) AS budgetd ) AS budget ");
        return sb.toString();
    }

    private String spelenGroepaantalSubselect(String assetsTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("        array_to_json(array_agg(row_to_json(groepaantald) ))  ");
        sb.append(" FROM ");
        sb.append("        (  ");
        sb.append("            SELECT ");
        sb.append("                groep, ");
        sb.append("                COUNT(guid)                  AS aantal, ");
        sb.append("                SUM(onderhoudskosten)::INT AS onderhoud,  ");
        sb.append("                SUM(afschrijving)::INT     AS afschrijving,  ");
        sb.append("                SUM(aanschafwaarde)::INT     AS aanschafwaarde  ");
        sb.append("            FROM  ");
        sb.append("                ").append(assetsTable);
        sb.append("            WHERE  ").append(getCqlfilter());
        sb.append("            AND type <> 'veiligheidsondergrond' ");
        sb.append("            GROUP BY ");
        sb.append("                groep ) AS groepaantald ) AS groepaantal ");
        return sb.toString();
    }

    private String spelenInstalledyearSubselect(String assetsTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("    array_to_json(array_agg(row_to_json(installedyeard))) ");
        sb.append(" FROM ");
        sb.append("    ( ");
        sb.append("     SELECT ");
        sb.append("         installedyear, ");
        sb.append("         COUNT(guid) AS aantal ");
        sb.append("     FROM  ");
        sb.append("          ").append(assetsTable);
        sb.append("     WHERE ");
        sb.append("          type <> 'veiligheidsondergrond'  ");
        sb.append("          AND ").append(getCqlfilter());
        sb.append("     GROUP BY ");
        sb.append("        installedyear ) AS installedyeard ) AS installedyear ");
        return sb.toString();
    }

    private String spelenVervangjaarSubselect(String assetsTable) {
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
        sb.append("                     COUNT(guid)           AS aantal, ");
        sb.append("                     SUM(aanschafwaarde) AS aanschafwaarde ");
        sb.append("                 FROM  ");
        sb.append("                     ").append(assetsTable);
        sb.append("                 WHERE ");
        sb.append("                     type <> 'veiligheidsondergrond' ");
        sb.append("                     AND ").append(getCqlfilter());
        sb.append("                 GROUP BY ");
        sb.append("                     vervangjaar1) ");
        sb.append("         UNION ");
        sb.append("             ( ");
        sb.append("                 SELECT ");
        sb.append("                     vervangjaar2        AS vervangjaar, ");
        sb.append("                     COUNT(guid)           AS aantal, ");
        sb.append("                     SUM(aanschafwaarde) AS aanschafwaarde ");
        sb.append("                 FROM ");
        sb.append("                     ").append(assetsTable);
        sb.append("                 WHERE ");
        sb.append("                     type <> 'veiligheidsondergrond' ");
        sb.append("                     AND ").append(getCqlfilter());
        sb.append("                 GROUP BY ");
        sb.append("                     vervangjaar2) ");
        sb.append("         UNION ");
        sb.append("             ( ");
        sb.append("                 SELECT ");
        sb.append("                     vervangjaar3        AS vervangjaar, ");
        sb.append("                     COUNT(guid)           AS aantal, ");
        sb.append("                     SUM(aanschafwaarde) AS aanschafwaarde ");
        sb.append("                 FROM ");
        sb.append("                     ").append(assetsTable);
        sb.append("                 WHERE ");
        sb.append("                     type <> 'veiligheidsondergrond'  ");
        sb.append("                     AND ").append(getCqlfilter());
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

    private String spelenEndoflifeyearSubselect(String assetsTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("         array_to_json(array_agg(row_to_json(endoflifeyeard))) ");
        sb.append(" FROM ");
        sb.append("         ( ");
        sb.append("             SELECT ");
        sb.append("                 endoflifeyear       AS endoflifeyear, ");
        sb.append("                 COUNT(guid)           AS aantal, ");
        sb.append("                 SUM(aanschafwaarde) AS aanschafwaarde_toestel ");
        sb.append("             FROM ");
        sb.append("                 ").append(assetsTable);
        sb.append("             WHERE ");
        sb.append("                 type <> 'veiligheidsondergrond' ");
        sb.append("             AND endoflifeyear < (date_part('year'::text, now()) + 11) ");
        sb.append("             AND ").append(getCqlfilter());
        sb.append("             GROUP BY ");
        sb.append("                 endoflifeyear ");
        sb.append("             ORDER BY  ");
        sb.append("                 endoflifeyear ) AS endoflifeyeard ) AS endoflifeyear ");

        return sb.toString();
    }

    private String spelenLeveranciersSubselect(String assetsTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("(  ");
        sb.append(" SELECT ");
        sb.append("          array_to_json(array_agg(row_to_json(wd))) ");
        sb.append("      FROM ");
        sb.append("          ( ");
        sb.append("              SELECT ");
        sb.append("                  COUNT(guid) AS aantal, ");
        sb.append("                  manufacturer ");
        sb.append("              FROM   ");
        sb.append("                  ").append(assetsTable);
        sb.append("              WHERE ").append(getCqlfilter());
        sb.append("              GROUP BY ");
        sb.append("                  manufacturer ) wd ) AS leveranciers ");
        return sb.toString();
    }

    private List<Map<String, Object>> getBomenAggregatie() throws NamingException, SQLException {

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append("'Feature' AS type,");
        // properties als json
        sb.append("row_to_json((SELECT l FROM ");
        // begin subselects
        sb.append("(SELECT ");
        // velden hoofdtabel
        sb.append(" lg.description").append(" As ").append("description").append(", ");
        // velden aggregaties
        String assetsTable;
        assetsTable = "v_bomen_jaarverloop";
        sb.append(bomenBomenaantalSubselect(assetsTable)).append(",");
        sb.append(bomenKostenSubselect(assetsTable)).append(",");
        sb.append(bomenEindbeeldSubselect(assetsTable)).append(",");
        sb.append(bomenMaatregelenKortSubselect(assetsTable)).append(",");
        sb.append(bomenBoomhoogteSubselect(assetsTable));

        // einde subselects
        sb.append(") AS l )) AS properties ");

        List<Map<String, Object>> rows = DB.qr().query(sb.toString(), new MapListHandler());
        return rows;
    }

    private String bomenBomenaantalSubselect(String assetsTable) {
        StringBuilder sb = new StringBuilder();
        sb.append(" (SELECT COUNT(a.boomid) ");
        sb.append("  FROM ");
        sb.append("      ").append(assetsTable).append(" a  ");
        sb.append("  WHERE ").append(getCqlfilter());
        sb.append("      and a.jaarnr=0 ");
        sb.append(" ) AS bomenaantal ");
        return sb.toString();
    }

    private String bomenKostenSubselect(String assetsTable) {
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
        sb.append("                 WHERE ").append(getCqlfilter());
        sb.append("                 GROUP BY a.jaarnr) AS foo ");
        sb.append("             GROUP BY ");
        sb.append("             jaar  ");
        sb.append("         ORDER BY  ");
        sb.append("             jaar) AS kostend ) AS kosten ");
        return sb.toString();
    }

    private String bomenEindbeeldSubselect(String assetsTable) {
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
        sb.append("                 WHERE ").append(getCqlfilter());
        sb.append("                     and a.jaarnr = 0 ");
        sb.append("                 GROUP BY a.eindbeeld) AS foo ");
        sb.append("             GROUP BY ");
        sb.append("             eindbeeld ");
        sb.append("         ORDER BY ");
        sb.append("             eindbeeld) AS eindbeeldd ) AS eindbeeld ");
        return sb.toString();
    }

    private String bomenMaatregelenKortSubselect(String assetsTable) {
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
        sb.append("                 WHERE ").append(getCqlfilter());
        sb.append("                     and a.jaarnr = 0 ");
        sb.append("                 GROUP BY a.maatregelen_kort) AS foo ");
        sb.append("             GROUP BY ");
        sb.append("             maatregelen_kort ");
        sb.append("         ORDER BY ");
        sb.append("             maatregelen_kort) AS maatregelen_kortd ) AS maatregelen_kort ");
        return sb.toString();
    }

    private String bomenBoomhoogteSubselect(String assetsTable) {
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
        sb.append("                 WHERE ").append(getCqlfilter());
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
