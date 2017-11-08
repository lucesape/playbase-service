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
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import nl.b3p.loader.jdbc.GeometryJdbcConverter;
import nl.b3p.loader.jdbc.GeometryJdbcConverterFactory;
import nl.b3p.loader.util.DbUtilsGeometryColumnConverter;
import nl.b3p.playbase.ImageDownloader;
import nl.b3p.playbase.db.DB;
import nl.b3p.playbase.entities.Asset;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
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

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private Map<Integer, String> equipmentTypes;
    private Map<Integer, Integer> assetTypes;
    private Map<Integer, Integer> assetTypeToLocationCategory;
    protected Map<Integer, List<String>> locationTypes;
    
    private Integer [] excl = {58,60,61,93,22,7,56,64,177,81,82,125,87,88,89,90,92,178,111,110,109,108,127,15,23,31,32,83,171,156,155,69,68,59,152,145,144,143,141,140,139,120,119,118,117,116,115};
    private List<Integer> excludedAssetTypes = Arrays.asList(excl);


    @Validate
    private String locationName;
    @Validate
    private String downloadlocation;
    @Validate
    private String project;

    @DefaultHandler
    public Resolution view() {
        return new ForwardResolution(JSP);
    }

    public Resolution export() throws IOException {
        final File f = File.createTempFile("locations_export", null);
        FileOutputStream fop = new FileOutputStream(f);
        final CsvOutputStream out = new CsvOutputStream(new OutputStreamWriter(fop), '|', false);

        initLists();

        String[] header = {"id", "Titel", "Content", "Samenvatting", "Latitude", "Longitude", "Straat", "Huisnummer", "Huisnummertoevoeging",
            "Postcode 4 cijfers", "Plaats", "Regio", "Land", "Website", "E-mail", "Telefoon", "Playadvisor id", "Image URL", "Image Caption", "Image Id", "Categorie",
            "Leeftijdscategorie", "Toegankelijkheid", "Faciliteiten", "Parkeren", "Assets", "newPlayGround"};

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
            String query = "SELECT id, coalesce(pa_title,title),coalesce(pm_content, pa_content),summary, latitude,longitude,street,number, numberextra,postalcode,"
                    + "municipality,    area,   coalesce(country,'Nederland'),website,email, phone, pa_id from " + DB.LOCATION_TABLE;
            List<Object[]> locations = null;
            if (locationName != null) {
                query += " where pa_title like ? or title like ?";
                String wildcard = "%" + locationName + "%";
                locations = DB.qr().query(query, rsh, wildcard, wildcard);
            } else if(project != null){
                query += " where project = ?";
                locations = DB.qr().query(query, rsh, project);
            }else{
                locations = DB.qr().query(query, rsh);
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
        int[] indexOfContentColumn = {2, 6};

        for (Object col : location) {
            if (id == null) {
                id = (Integer) col;
            }
            Object value = col;

            if (value == null) {
                record.add(null);
            } else {
                String valueString = value.toString();
                if (ArrayUtils.contains(indexOfContentColumn, index)) {
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
        retrieveAssets(id, record);
        retrieveYoungestAssetDate(id, record);
    }

    private void retrieveAssets(Integer id, List<String> record) throws NamingException, SQLException {

        try (Connection con = DB.getConnection()) {
            GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);
            ResultSetHandler<List<Asset>> assHandler = new BeanListHandler(Asset.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            List<Asset> assets = DB.qr().query("SELECT * FROM " + DB.ASSETS_TABLE + " WHERE location = ?", assHandler, id);
            Set<String> equipments = new HashSet<>();
            for (Asset asset : assets) {
                Integer type = asset.getType_();
                Integer equipment = assetTypes.get(type);
                String eq = equipmentTypes.get(equipment);
                if (eq != null) {
                    equipments.add(eq);
                }
            }

            String equipmentTypeString = "";
            for (String assetType : equipments) {
                if (equipmentTypeString.length() != 0) {
                    equipmentTypeString += SEPERATOR_CHAR;
                }
                equipmentTypeString += assetType;
            }
            record.add(equipmentTypeString);
        } catch (NamingException | SQLException ex) {
            log.error("Cannot get geometryConverter: ", ex);
        }

    }

    private void retrieveYoungestAssetDate(Integer id, List<String> record) throws NamingException, SQLException {
        ArrayListHandler rsh = new ArrayListHandler();
        List<Object[]> m = DB.qr().query("select installeddate from " + DB.ASSETS_TABLE + " where  location = " + id, rsh);
        int numNew = 0;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -2);
        Date mustBeAfter = c.getTime();
        for (Object[] asset : m) {
            String d = (String) asset[0];
            if (d == null || d.equals("")) {
                continue;
            }
            try {
                Date date = sdf.parse(d);
                if (mustBeAfter.before(date)) {
                    numNew++;
                }
            } catch (ParseException ex) {
                log.debug("Cannot parse date: " + d, ex);
            }
        }
        double ratio = (double) numNew / m.size();
        Boolean isNew = ratio > 0.3;
        record.add(isNew.toString());
    }

    protected void retrieveParking(Integer id, List<String> record) throws NamingException, SQLException {
        MapHandler rsh = new MapHandler();
        Map m = DB.qr().query("SELECT park.parking from " + DB.LOCATION_TABLE + " loc inner join " + DB.LIST_PARKING_TABLE + " park on loc.parking = park.id", rsh);
        if (m != null) {
            record.add((String) m.get("parking"));
        } else {
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
        Set<String> types = new HashSet<>();
        for (Object[] cat : cats) {
            if (!categories.isEmpty()) {
                categories += SEPERATOR_CHAR;
            }
            types.add((String) cat[0]);
            types.add((String) cat[1]);
        }

        try (Connection con = DB.getConnection()) {
            GeometryJdbcConverter geometryConverter = GeometryJdbcConverterFactory.getGeometryJdbcConverter(con);
            ResultSetHandler<List<Asset>> assHandler = new BeanListHandler(Asset.class, new BasicRowProcessor(new DbUtilsGeometryColumnConverter(geometryConverter)));
            List<Asset> assets = DB.qr().query("SELECT * FROM " + DB.ASSETS_TABLE + " WHERE location = ?", assHandler, id);
            for (Asset asset : assets) {
                Integer type = asset.getType_();
                if(excludedAssetTypes.contains(type) || type == null){
                    continue;
                }
                Integer eq = assetTypeToLocationCategory.get(type);
                List<String> locCats = locationTypes.get(eq);
                types.addAll(locCats);
            }
        } catch (NamingException | SQLException ex) {
            log.error("Cannot get geometryConverter: ", ex);
        }

        if (types.isEmpty()) {
            categories = "Openbare speeltuin";
        } else {
            categories = String.join(SEPERATOR_CHAR, types);
        }
        record.add(categories);
    }

    protected void retrieveImages(Integer id, List<String> record) throws NamingException, SQLException {
        ArrayListHandler rsh = new ArrayListHandler();

        List<Object[]> images = DB.qr().query("SELECT url, caption,pm_guid from " + DB.IMAGES_TABLE + " WHERE location = ? order by equipment desc, lastupdated desc", rsh, id);
        String urls = "";
        String captions = "";
        String ids = "";
        int index = 0;
        for (Object[] image : images) {
            String url = (String) valueOrEmptyString(image[0]);
            if (url.isEmpty()) {
                continue;
            }
            if (!urls.isEmpty()) {
                urls += SEPERATOR_CHAR;
                captions += SEPERATOR_CHAR;
                ids += SEPERATOR_CHAR;
            }
            String imageName = url.substring(url.lastIndexOf("/") + 1);
            if (imageName.contains("GetImage.ashx")) {
                imageName = "Image" + id + "-" + index + ".jpg";
            }
            urls += imageName;
            captions += valueOrEmptyString(image[1]);
            ids += valueOrEmptyString(image[2]);
            index++;
        }
        record.add(urls);
        record.add(captions);
        record.add(ids);
    }

    private Object valueOrEmptyString(Object value) {
        return value == null ? "" : value;
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

    public String getDownloadlocation() {
        return downloadlocation;
    }

    public void setDownloadlocation(String downloadlocation) {
        this.downloadlocation = downloadlocation;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
    //</editor-fold>
}
