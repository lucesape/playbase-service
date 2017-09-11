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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.StrictBinding;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;
import nl.b3p.commons.csv.CsvOutputStream;
import nl.b3p.playbase.db.DB;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Meine Toonen
 */
@StrictBinding
@UrlBinding("/action/export")
public class ExportActionBean implements ActionBean {

    private static final Log log = LogFactory.getLog(ExportActionBean.class);
    private static final String JSP = "/WEB-INF/jsp/admin/export/csv.jsp";

    private static final String SEPERATOR_CHAR = ",";
    private ActionBeanContext context;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    @Validate
    private String locationName;
    
    @DefaultHandler
    public Resolution view(){
        return new ForwardResolution(JSP);
    }

    public Resolution export() throws IOException {
        final File f = File.createTempFile("locations_export", null);
        FileOutputStream fop = new FileOutputStream(f);
        final CsvOutputStream out = new CsvOutputStream(new OutputStreamWriter(fop),'|', false);
        //String[] header = {"Titel", "Content", "SamenvattingTitel", "Content", "Samenvatting", "Latitude", "Longitude", "Straat", "Huisnummer", "Huisnummertoevoeging", "Postcode 4 cijfers", "Postcode 2 letters", "Plaats", "Regio", "Land", "Website", "E-mail", "Telefoon (landcode + 9 cijfers)", "Afbeeldingen", "Categorie", "Leeftijd", "Speeltoestellen", "Faciliteiten", "Toegankelijkheid", "ParkerenLatitude", "Longitude", "Straat", "Huisnummer", "Huisnummertoevoeging", "Postcode 4 cijfers", "Postcode 2 letters", "Plaats", "Regio", "Land", "Website", "E-mail", "Telefoon (landcode + 9 cijfers)", "Afbeeldingen", "Categorie", "Leeftijd", "Speeltoestellen", "Faciliteiten", "Toegankelijkheid", "Parkeren"};
        String[] header = {"id", "Titel", "Content", "Samenvatting", "Latitude", "Longitude", "Straat", "Huisnummer", "Huisnummertoevoeging",
            "Postcode 4 cijfers",/* "Postcode 2 letters",*/ 
            "Plaats", "Regio", "Land", "Website", "E-mail", "Telefoon", "Image URL", "Image Caption", "Image Id", "Categorie", 
            "Leeftijdscategorie", "Toegankelijkheid", 
            "Faciliteiten", "Parkeren", "youngestAssetDate"};
        out.writeRecord(header);
        List<List<String>> records = getRecords();
        for (List<String> record : records) {
            String[] ar = new String[0];
            ar = record.toArray(ar);
            out.writeRecord(ar);
        }
        out.flush();
        String filename = "Speeltuinen.csv";
        return new StreamingResolution("text/csv") {
            @Override
            public void stream(HttpServletResponse response) throws Exception {
                OutputStream out = context.getResponse().getOutputStream();
                IOUtils.copy(new FileInputStream(f), out);
                out.close();
            }
        }.setAttachment(true).setFilename(filename);

    }

    ///	 (landcode + 9 cijfers)	Afbeeldingen	Categorie	Leeftijd	Speeltoestellen	Faciliteiten	
    //Toegankelijkheid	Parkeren
    private List<List<String>> getRecords() {
        try {
            List<List<String>> records = new ArrayList<>();
            ArrayListHandler rsh = new ArrayListHandler();
            //  id  Titel	Content	Samenvatting	Latitude Longitude Straat Huisnummer Huisnummertoevoeging	Postcode 4 cijfers	Postcode 2 letters  Plaats	Regio	Land	Website	E-mail Telefoon
            String query = "SELECT id, title,content,summary, latitude,longitude,street,number, numberextra,postalcode,"
                    + "municipality,    area,   country,website,email, phone from " + DB.LOCATION_TABLE;
            List<Object[]> locations = null;
            if(locationName != null){
                query += " where title like ?";
                locations =DB.qr().query(query, rsh, "%" + locationName + "%");
            }else{
                locations =DB.qr().query(query, rsh);
            }
             
            for (Object[] location : locations) {
                records.add(getRecord(location));
            }
            return records;
        } catch (NamingException | SQLException ex) {
            log.error("Cannot get locations: ", ex);
            return null;
        }
    }

    private List<String> getRecord(Object[] location) throws NamingException, SQLException {
        List<String> record = new ArrayList<>();
        Integer id = null;
        int index = 0;
        int indexOfContentColumn = 2;
        for (Object col : location) {
            if (id == null) {
                id = (Integer) col;
            } 
            Object value = col;
            
            if (value == null) {
                record.add(null);
            } else {
                String valueString = value.toString();
                if (index == indexOfContentColumn) {
                    String s = 
                    valueString = valueString.replace("\n", "").replace("\r", "");
                }
                record.add(valueString);
            }
            index++;
        }
        retrieveReferencedTables(id, record);

        return record;
    }

    /*
Afbeeldingen
Categorie
Leeftijd
Speeltoestellen
Faciliteiten
Toegankelijkheid
Parkeren
     */
    protected void retrieveReferencedTables(Integer id, List<String> record) throws NamingException, SQLException {
        retrieveImages(id, record);
        retrieveCategories(id, record);
        retrieveAgeCategories(id, record);
        retrieveAccessibilities(id, record);
        retrieveFacilities(id, record);
        retrieveParking(id, record);
        retrieveYoungestAssetDate(id, record);
    }
    
    private void retrieveYoungestAssetDate(Integer id, List<String> record) throws NamingException, SQLException {
        MapHandler rsh = new MapHandler();

        Map m = DB.qr().query("select max(to_timestamp(installeddate, 'yyyy-MM-dd')) as youngestAssetDate from " +DB.ASSETS_TABLE + " where installeddate <> '' and location = " + id, rsh);
        Object o = m.get("youngestAssetDate");
        if(o != null){
            record.add(sdf.format(o));
        }else{
            record.add(null);
        }
    }

    protected void retrieveParking(Integer id, List<String> record) throws NamingException, SQLException {
        MapHandler rsh = new MapHandler();
        Map m = DB.qr().query("SELECT park.parking from " + DB.LOCATION_TABLE + " loc inner join " + DB.LIST_PARKING_TABLE + " park on loc.parking = park.id", rsh);
        if(m != null){
            record.add((String)m.get("parking"));
        }else{
            record.add(null);
        }
    }

    protected void retrieveFacilities(Integer id, List<String> record) throws NamingException, SQLException {
        ArrayListHandler rsh = new ArrayListHandler();
        List<Object[]> facilities = DB.qr().query("SELECT cat.facility from " + DB.LOCATION_FACILITIES_TABLE + " loc inner join " + DB.LIST_FACILITIES_TABLE + " as cat on cat.id = loc.facility  WHERE location = " + id, rsh);
        String facilitiesString = "";
        for (Object[] fac : facilities) {
            if (!facilitiesString.isEmpty()) {
                facilitiesString += SEPERATOR_CHAR;
            }
            facilitiesString += fac[0];
        }
        record.add(facilitiesString);
    }

    protected void retrieveAccessibilities(Integer id, List<String> record) throws NamingException, SQLException {
        ArrayListHandler rsh = new ArrayListHandler();
        List<Object[]> cats = DB.qr().query("SELECT cat.accessibility from " + DB.LOCATION_ACCESSIBILITY_TABLE + " loc inner join " + DB.LIST_ACCESSIBILITY_TABLE + " as cat on cat.id = loc.accessibility  WHERE location = " + id, rsh);
        String categories = "";
        for (Object[] cat : cats) {
            if (!categories.isEmpty()) {
                categories += SEPERATOR_CHAR;
            }
            categories += cat[0];
        }
        record.add(categories);
    }

    protected void retrieveAgeCategories(Integer id, List<String> record) throws NamingException, SQLException {
        ArrayListHandler rsh = new ArrayListHandler();
        List<Object[]> cats = DB.qr().query("SELECT cat.agecategory from " + DB.LOCATION_AGE_CATEGORY_TABLE + " loc inner join " + DB.LIST_AGECATEGORIES_TABLE + " as cat on cat.id = loc.agecategory  WHERE location = " + id, rsh);
        String categories = "";
        for (Object[] cat : cats) {
            if (!categories.isEmpty()) {
                categories += SEPERATOR_CHAR;
            }
            categories += cat[0];
        }
        record.add(categories);
    }

    protected void retrieveCategories(Integer id, List<String> record) throws NamingException, SQLException {
        ArrayListHandler rsh = new ArrayListHandler();
        List<Object[]> cats = DB.qr().query("SELECT cat.main, cat.category from " + DB.LOCATION_CATEGORY_TABLE + " loc inner join " + DB.LIST_CATEGORY_TABLE + " cat on cat.id = loc.category  WHERE location = " + id, rsh);
        String categories = "";
        for (Object[] cat : cats) {
            if (!categories.isEmpty()) {
                categories += SEPERATOR_CHAR;
            }
            categories += cat[0] + ">" + cat[1];
        }
        record.add(categories);
    }

    protected void retrieveImages(Integer id, List<String> record) throws NamingException, SQLException {
        ArrayListHandler rsh = new ArrayListHandler();
        
        List<Object[]> images = DB.qr().query("SELECT url, caption,pm_guid from " + DB.IMAGES_TABLE + " WHERE location = " + id, rsh);
        String urls = "";
        String captions = "";
        String ids = "";
        for (Object[] image : images) {
            String url = (String)valueOrEmptyString(image[0]);
            if(url.isEmpty()){
                continue;
            }
            if (!urls.isEmpty()) {
                urls += SEPERATOR_CHAR;
                captions += SEPERATOR_CHAR;
                ids += SEPERATOR_CHAR;
            }
            urls += url;
            captions += valueOrEmptyString(image[1]);
            ids += valueOrEmptyString(image[2]);
        }
        record.add(urls);
        record.add(captions);
        record.add(ids);

    }
    
    private Object valueOrEmptyString(Object value){
        return value == null ? "" : value;
    }

    //<editor-fold desc="Getters enzo" defaultstate="collapsed">
    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }
    
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    //</editor-fold>
}
