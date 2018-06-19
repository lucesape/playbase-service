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
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingException;
import nl.b3p.playbase.entities.Asset;
import nl.b3p.playbase.entities.CronJob;
import nl.b3p.playbase.entities.Location;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Meine Toonen
 */
public class PlayadvisorImporter extends Importer {

    private static final Log log = LogFactory.getLog(PlayadvisorImporter.class);

    public PlayadvisorImporter(String project) {
        super(project);
        postfix = "_playadvisor";
    }

    public void initialLoad(CronJob job, ImportReport report, Connection con) throws NamingException, SQLException {
        try {
            // haal alle speelplekken voro dit project/gemeente op
            // Sla alle playadvisor plekken op
            // Bij nieuwe plekken, mail monique

            // Na merge: Stuur lijstje van ids van playbaseID vs playadvisor id terug naar playadvisor
            String url = job.getBaseurl() + "/wp-json/b3p/v1/playbase/" + this.getProject();
            HttpClient httpClient = HttpClientBuilder.create().build();

            HttpGet request = new HttpGet(url);
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization", "Basic " + job.getAuthkey());
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
                List<Location> locs = processLocations(stringResult, report, con);
                log.debug("Result: " + stringResult);
            }
        } catch (IOException ex) {
            log.error("Error while getting all speelplekken for project " + this.getProject(), ex);
            report.addError(ex.getLocalizedMessage(), ImportReport.ImportType.GENERAL);
        }
    }

    public List<Location> processLocations(String locs, ImportReport report, Connection con) throws NamingException, SQLException {
        List<Location> locations = new ArrayList<>();
        JSONArray ar = new JSONArray(locs);
        for (Iterator<Object> iterator = ar.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject) iterator.next();
            boolean hasProject = this.getProject() != null;
            String prevpostfix = postfix;
            Location l = parseLocation(obj);
            
            if (!hasProject) {
                this.setProject(l.getMunicipality().toLowerCase());
            }
            if( isProjectReady(this.getProject())){
                postfix = "";
            }
            
            processLocation(l, obj, report, con);
            locations.add(l);
            
            if (!hasProject) {
                this.setProject(null);
            }
            
            if(postfix.isEmpty()){
                postfix = prevpostfix;
            }

        }
        return locations;
    }
    
    private boolean isProjectReady(String project){
        return true;
    }

    protected void processLocation(Location location, JSONObject obj, ImportReport report, Connection con) throws NamingException, SQLException {
        int id = saveLocation(location, report);
        List<Asset> assets = parseAssets(location, obj.getJSONArray("Assets"), report, false, con);

        for (Asset asset : assets) {
            saveAsset(asset, report);
        }

        // saveLocationAgeCategory(location, Arrays.asList(location.getAgecategories()), locationAlreadyExists);
/*
        try {
            if (((String) locationMap.get(LOCATIONSUBTYPE)).length() > 0) {
                saveLocationType((String) locationMap.get(LOCATIONSUBTYPE), location, locationAlreadyExists);
            }
        } catch (IllegalArgumentException ex) {
            report.addError(ex.getLocalizedMessage() + ". Location is saved, but type is not.", ImportReport.ImportType.LOCATION);
        }

        try {
            if (((String) locationMap.get(FACILITIES)).length() > 0) {
                saveFacilities(location, (String) locationMap.get(FACILITIES), locationAlreadyExists);
            }
        } catch (IllegalArgumentException ex) {
            report.addError(ex.getLocalizedMessage() + ". Location is saved, but facilities are not.", ImportReport.ImportType.LOCATION);
        }

        try {
            if (((String) locationMap.get(ACCESSIBLITIY)).length() > 0) {
                saveAccessibility(id, (String) locationMap.get(ACCESSIBLITIY));
            }
        } catch (IllegalArgumentException ex) {
            report.addError(ex.getLocalizedMessage() + ". Location is saved, but accessiblity is not.", ImportReport.ImportType.LOCATION);
        }
         */
    }

    protected Location parseLocation(JSONObject obj) {
        Location loc = new Location();

        loc.setPa_title(obj.optString("Titel"));
        loc.setTitle(obj.optString("Titel"));
        loc.setPhone(obj.optString("Telefoon"));
        loc.setPa_content(obj.optString("Content"));
        loc.setWebsite(obj.optString("Website"));
        if (obj.has("Latitude")) {
            loc.setLatitude(obj.getDouble("Latitude"));
        }
        if (obj.has("Longitude")) {
            loc.setLongitude(obj.getDouble("Longitude"));
        }
        loc.setPa_id("" + obj.getInt("PlayadvisorID"));
        loc.setMunicipality(obj.getString("Plaats"));
        loc.setProject(this.getProject());
        loc.setCountry(obj.getString("Land"));
        loc.setStreet(obj.optString("Straat"));
        loc.setEmail(obj.optString("Email"));
        return loc;
    }

    protected List<Asset> parseAssets(Location location, JSONArray assetsArray, ImportReport report, boolean merged, Connection con) throws NamingException, SQLException {

        List<Asset> assets = new ArrayList<>();
        /*     if(merged){
               // Get possible previously saved assets
               List<String> nieuwList = new ArrayList<>();
               List<Asset> prevAssets = DB.qr().query(con,"SELECT * FROM " + DB.ASSETS_TABLE + " WHERE location = ?", assListHandler, location.getId());

               for (Iterator<Object> iterator = assetsArray.iterator(); iterator.hasNext();) {
                   String paAsset = (String) iterator.next();
                   boolean found = false;
                   Integer paEquipmentType = equipmentTypes.get(paAsset);
                   for (Asset prevAsset : prevAssets) {
                       Integer pmEquipmentType = equipmenttypePMtoPA.get(prevAsset.getType_());
                       if (Objects.equals(paEquipmentType, pmEquipmentType)) {
                           found = true;
                           prevAsset.setPa_guid(location.getPa_id());
                           assets.add(prevAsset);
                           break;
                       }
                   }
                   if (!found) {
                       nieuwList.add(paAsset);
                   }
               }
            // Filter list of assets from this instance based on previous assets
            assets = nieuwList;
        }*/
        // Save new assets
        for (Iterator<Object> iterator = assetsArray.iterator(); iterator.hasNext();) {
            String asset = (String) iterator.next();
            if (asset.isEmpty()) {
                continue;
            }
            Asset ass = new Asset();
            ass.setName(asset);
            ass.setPa_guid(location.getPa_id());
            ass.setLocation(location.getId());
            ass.setLatitude(location.getLatitude());
            ass.setLongitude(location.getLongitude());

            Integer[] cats = location.getAgecategories();

            ass.setAgecategories(cats);
            Integer equipmentType = getEquipmentType(asset);
            ass.setEquipment(equipmentType);
            ass.setType_(equipmenttypePAtoPM.get(equipmentType));
            assets.add(ass);
        }
        return assets;
    }

}
