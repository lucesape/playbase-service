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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.NamingException;
import nl.b3p.commons.csv.CsvFormatException;
import nl.b3p.commons.csv.CsvInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Meine Toonen
 */
public class PlayadvisorImporter extends Importer {

    private static final Log log = LogFactory.getLog("PlayadvisorProcesor");
    private Map<String, String> playadvisorColumnToPlaybase;
    private Map<Integer, String> indexToColumn;

    public PlayadvisorImporter() {
        playadvisorColumnToPlaybase = new HashMap<>();
        indexToColumn = new HashMap<>();
        playadvisorColumnToPlaybase.put("id", "pa_id");
        playadvisorColumnToPlaybase.put("Title", "title");
        playadvisorColumnToPlaybase.put("Content", "content");
        playadvisorColumnToPlaybase.put("Excerpt", "summary");
        playadvisorColumnToPlaybase.put("Date", "");
        playadvisorColumnToPlaybase.put("Post Type", "");
        playadvisorColumnToPlaybase.put("Permalink", "");
        playadvisorColumnToPlaybase.put("URL", "");
        playadvisorColumnToPlaybase.put("Title", "");
        playadvisorColumnToPlaybase.put("Caption", "");
        playadvisorColumnToPlaybase.put("Description", "");
        playadvisorColumnToPlaybase.put("Alt Text", "");
        playadvisorColumnToPlaybase.put("URL", "");
        playadvisorColumnToPlaybase.put("Speelplektype", "");
        playadvisorColumnToPlaybase.put("Landen", "");
        playadvisorColumnToPlaybase.put("Plaatsen", "");
        playadvisorColumnToPlaybase.put("Speeltoestellen", "");
        playadvisorColumnToPlaybase.put("Faciliteiten", "");
        playadvisorColumnToPlaybase.put("Leeftijden", "");
        playadvisorColumnToPlaybase.put("Parkeren", "");
        playadvisorColumnToPlaybase.put("Toegankelijkheid", "");
        playadvisorColumnToPlaybase.put("Ambassadeurs", "");
        playadvisorColumnToPlaybase.put("average_rating", "");
        playadvisorColumnToPlaybase.put("x_coordinaat", "");
        playadvisorColumnToPlaybase.put("y_coordinaat", "");
        playadvisorColumnToPlaybase.put("galerij", "");
        playadvisorColumnToPlaybase.put("_wp_attached_file", "");
        playadvisorColumnToPlaybase.put("_wp_attachment_metadata", "");
        playadvisorColumnToPlaybase.put("_wpml_media_duplicate", "");
        playadvisorColumnToPlaybase.put("_wpml_media_featured", "");
        playadvisorColumnToPlaybase.put("favoriet", "");
        playadvisorColumnToPlaybase.put("Status", "");
        playadvisorColumnToPlaybase.put("Author", "");
        playadvisorColumnToPlaybase.put("Slug", "");
        playadvisorColumnToPlaybase.put("Format", "");
        playadvisorColumnToPlaybase.put("Template", "");
        playadvisorColumnToPlaybase.put("Parent", "");
        playadvisorColumnToPlaybase.put("Parent Slug", "");
        playadvisorColumnToPlaybase.put("Order", "");
        playadvisorColumnToPlaybase.put("Comment Status", "");
        playadvisorColumnToPlaybase.put("Ping Status", "");
        playadvisorColumnToPlaybase.put("Samenspeelplek Enquete", "");
        playadvisorColumnToPlaybase.put("Test enquete monique", "");

    }

    public void importStream(InputStream in, ImportReport report) throws IOException, CsvFormatException {
        CsvInputStream cis = new CsvInputStream(new InputStreamReader(in));

        String[] s = cis.readRecord();
        //process header
        for (int i = 0; i < s.length; i++) {
            String col = s[i];
            
            String playbaseColumnName = playadvisorColumnToPlaybase.get(col);
            if(playbaseColumnName == null){
                playbaseColumnName = col;
            }
            indexToColumn.put(i, playbaseColumnName);
        }
        try {
            while ((s = cis.readRecord()) != null) {
                Map<String, Object> vals = parseRecord(s);
                saveLocation(vals, report);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot save location to db: ", ex);
        }
    }

    protected Map<String, Object> parseRecord(String[] record) {
        Map<String, Object> dbvalues = new HashMap<>();
        for (int i = 0; i < record.length; i++) {
            String val = record[i];
            String col = indexToColumn.get(i);
            dbvalues.put(col, val);
        }

        return dbvalues;
    }

}
