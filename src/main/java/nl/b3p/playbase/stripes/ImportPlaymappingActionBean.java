package nl.b3p.playbase.stripes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.action.StrictBinding;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import nl.b3p.playbase.db.DB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;

/**
 * Export van JSON files voor gebruik in dashboard
 *
 *
 * @author Chris van Lith
 * @author Meine Toonen
 */
@StrictBinding
@UrlBinding("/action/importPlaymapping")
public class ImportPlaymappingActionBean implements ActionBean {

    private ActionBeanContext context;

    private static final Log log = LogFactory.getLog(ImportPlaymappingActionBean.class);

    private static final String JSP = "/WEB-INF/jsp/admin/playmapping.jsp";

    @Validate
    private String locationGuid;
    @Validate(required = true)
    private String username;
    @Validate(required = true)
    private String password;
    @Validate(required = true)
    private String apiurl;

    @DefaultHandler
    @DontValidate
    public Resolution edit() throws Exception {
        return new ForwardResolution(JSP);
    }

    public Resolution importPM() throws NamingException, SQLException {
        return collectJSON();
    }

    private Resolution collectJSON() throws SQLException, NamingException {

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setStaleConnectionCheckEnabled(false)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .setConnectionRequestTimeout(60)
                .build();

        HttpClientBuilder hcb = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig);

        HttpClientContext httpContext = HttpClientContext.create();
        if (getUsername() != null && getPassword() != null) {
            String hostname = null; //any
            int port = -1; //any
            String scheme = "http"; //default
            URL aURL;
            try {
                aURL = new URL(getApiurl());
                hostname = aURL.getHost();
                port = aURL.getPort();
                scheme = aURL.getProtocol();
            } catch (MalformedURLException ex) {
                // ignore
            }

            CredentialsProvider credentialsProvider
                    = new BasicCredentialsProvider();
            Credentials defaultcreds
                    = new UsernamePasswordCredentials(getUsername(), getPassword());
            AuthScope authScope
                    = new AuthScope(hostname, port);
            credentialsProvider.setCredentials(authScope, defaultcreds);

            hcb = hcb.setDefaultCredentialsProvider(credentialsProvider);

            //preemptive not possible without hostname
            if (hostname != null) {
                // Create AuthCache instance for preemptive authentication
                AuthCache authCache = new BasicAuthCache();
                BasicScheme basicAuth = new BasicScheme();
                HttpHost targetHost = new HttpHost(hostname, port, scheme);
                authCache.put(targetHost, basicAuth);
                // Add AuthCache to the execution context
                httpContext.setCredentialsProvider(credentialsProvider);
                httpContext.setAuthCache(authCache);
                log.debug("Preemptive credentials: hostname: " + hostname
                        + ", port: " + port
                        + ", username: " + getUsername()
                        + ", password: ****.");
            }

        }

        HttpClient hc = hcb.build();

        HttpGet request = new HttpGet(getApiurl());
        request.setHeader("Accept-Language", "NL");
        request.setHeader("Accept", "application/json");

        String stringResult = null;
        HttpResponse response = null;
        try {
            response = hc.execute(request, httpContext);
            int statusCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();
            if (statusCode != 200) {
                context.getValidationErrors().add("status", new SimpleError("Could not retrieve JSON. Status " + statusCode + ". Reason given: " + response.getStatusLine().getReasonPhrase()));
            } else {
                //InputStream is = entity.getContent();
                stringResult = EntityUtils.toString(entity);
            }
        } catch (IOException ex) {
            log.debug("Exception False: ", ex);
            context.getValidationErrors().add("status", new SimpleError("Could not retrieve JSON." + ex.getLocalizedMessage()));
        } finally {
            if (hc instanceof CloseableHttpClient) {
                try {
                    ((CloseableHttpClient) hc).close();
                } catch (IOException ex) {
                    log.info("Error closing HttpClient: " + ex.getLocalizedMessage());
                }
            }
            if (response instanceof CloseableHttpResponse) {
                try {
                    ((CloseableHttpResponse) response).close();
                } catch (IOException ex) {
                    log.info("Error closing HttpResponse: " + ex.getLocalizedMessage());
                }
            }
        }

        if (stringResult != null) {
            int retval;
            String type;
            if (apiurl.contains("Location")) {
                retval = refillLocationsApiTable(stringResult);
                type = "locaties";
            } else if (apiurl.contains("Asset")) {
                retval = refillAssetsApiTable(stringResult);
                type = "assets";
            } else {
                context.getValidationErrors().add("apiurl", new SimpleError("Wrong url selected."));
                return new ForwardResolution(JSP);
            }
            context.getMessages().add(new SimpleMessage("Er zijn " + retval + " " + type + " weggeschreven."));
        }
        return new ForwardResolution(JSP);
    }

    private int refillAssetsApiTable(String assetsString) throws NamingException, SQLException {
        StringBuilder sb;
        List<Map<String, Object>> assets = parseAssets(assetsString);
        int retval = 0;
        for (Map<String, Object> asset : assets) {
            sb = new StringBuilder();
            sb.append("insert into pm_assets_api (");
            sb.append("	 \"id\",");
            sb.append("      \"locationid\",");
            sb.append("      \"locationname\",");
            sb.append("      \"lastupdated\",");
            sb.append("      \"name\",");
            sb.append("      \"assettype\",");
            sb.append("      \"manufacturer\",");
            sb.append("      \"serialnumber\",");
            sb.append("      \"installeddate\",");
            sb.append("      \"endoflifeyear\",");
            sb.append("      \"safetyzonelength\",");
            sb.append("      \"safetyzonewidth\",");
            sb.append("      \"agegrouptoddlers\",");
            sb.append("      \"agegroupjuniors\",");
            sb.append("      \"agegroupseniors\",");
            sb.append("      \"pricepurchase\",");
            sb.append("      \"priceinstallation\",");
            sb.append("      \"pricereinvestment\",");
            sb.append("      \"pricemaintenance\",");
            sb.append("      \"priceindexation\",");
            sb.append("      \"lat\",");
            sb.append("      \"lng\"");
            sb.append(") ");
            sb.append("VALUES (");
            sb.append("'").append(asset.get("ID")).append("',");
            sb.append("'").append(asset.get("LocationID")).append("',");
            sb.append("'").append(asset.get("LocationName")).append("',");
            sb.append("'").append(asset.get("LastUpdated")).append("',");
            sb.append("'").append(asset.get("Name")).append("',");
            sb.append("'").append(asset.get("AssetType")).append("',");
            sb.append("'").append(asset.get("Manufacturer")).append("',");
            sb.append("'").append(asset.get("SerialNumber")).append("',");
            sb.append("'").append(asset.get("InstalledDate")).append("',");
            sb.append("'").append(asset.get("EndOfLifeYear")).append("',");
            sb.append("'").append(asset.get("SafetyZoneLength")).append("',");
            sb.append("'").append(asset.get("SafetyZoneWidth")).append("',");
            sb.append("'").append(asset.get("AgeGroupToddlers")).append("',");
            sb.append("'").append(asset.get("AgeGroupJuniors")).append("',");
            sb.append("'").append(asset.get("AgeGroupSeniors")).append("',");
            sb.append("'").append(asset.get("PricePurchase")).append("',");
            sb.append("'").append(asset.get("PriceInstallation")).append("',");
            sb.append("'").append(asset.get("PriceReInvestment")).append("',");
            sb.append("'").append(asset.get("PriceMaintenance")).append("',");
            sb.append("'").append(asset.get("PriceIndexation")).append("',");
            sb.append("'").append(asset.get("Lat")).append("',");
            sb.append("'").append(asset.get("Lng")).append("');");
            retval += DB.qr().update(sb.toString());
        }
        return retval;
    }

    private int refillLocationsApiTable(String temp) throws NamingException, SQLException {
        List<Map<String, Object>> childLocations = parseChildLocations(temp);
        int retval = 0;
        for (Map<String, Object> childLocation : childLocations) {
            //insert childlocations from childlocations from childlocations
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT ");
            sb.append("INTO ");
            sb.append("    pm_locations_api ");
            sb.append("    (");
            sb.append("        \"id\",");
            sb.append("        \"name\",");
            sb.append("        \"lastupdated\",");
            sb.append("        \"lat\",");
            sb.append("        \"lng\"");
            sb.append("    )");
            sb.append("    VALUES(");
            sb.append("    ");
            sb.append("\'").append(childLocation.get("ID")).append("\',");
            sb.append("\'").append(childLocation.get("LastUpdated")).append("\',");
            sb.append("\'").append(childLocation.get("Name")).append("\',");
            sb.append("").append(childLocation.get("Lat")).append(",");
            sb.append("").append(childLocation.get("Lng")).append("");
            sb.append(");");
            retval += DB.qr().update(sb.toString());
        }
        return retval;
    }

    protected List<Map<String, Object>> parseAssets(String assetsString) {
        List<Map<String, Object>> assets = new ArrayList<>();
        JSONArray assetsArray = new JSONArray(assetsString);

        for (int i = 0; i < assetsArray.length(); i++) {
            JSONObject asset = assetsArray.getJSONObject(i);
            assets.add(parseAsset(asset));

        }
        return assets;
    }

    protected Map<String, Object> parseAsset(JSONObject assetJSON) {
        Map<String, Object> asset = new HashMap<>();
        asset.put("$id", assetJSON.optString("$id"));
        asset.put("ID", assetJSON.optString("ID"));
        asset.put("LocationID", assetJSON.optString("LocationID"));
        asset.put("LocationName", assetJSON.optString("LocationName").replaceAll("\'", "\'\'"));
        asset.put("LastUpdated", assetJSON.optString("LastUpdated"));
        asset.put("Name", assetJSON.optString("Name").replaceAll("\'", "\'\'"));
        asset.put("AssetType", assetJSON.optString("AssetType"));
        asset.put("Manufacturer", assetJSON.optString("Manufacturer"));
        asset.put("Product", assetJSON.optString("Product"));
        asset.put("SerialNumber", assetJSON.optString("SerialNumber"));
        asset.put("Material", assetJSON.optString("Material"));
        asset.put("InstalledDate", assetJSON.optString("InstalledDate"));
        asset.put("EndOfLifeYear", assetJSON.optInt("EndOfLifeYear"));
        asset.put("ProductID", assetJSON.optString("ProductID"));
        asset.put("ProductVariantID", assetJSON.optString("ProductVariantID"));
        asset.put("Height", assetJSON.optInt("Height"));
        asset.put("Depth", assetJSON.optInt("Depth"));
        asset.put("Width", assetJSON.optInt("Width"));
        asset.put("FreefallHeight", assetJSON.optInt("FreefallHeight"));
        asset.put("SafetyZoneLength", assetJSON.optInt("SafetyZoneLength"));
        asset.put("SafetyZoneWidth", assetJSON.optInt("SafetyZoneWidth"));
        asset.put("AgeGroupToddlers", assetJSON.optBoolean("AgeGroupToddlers"));
        asset.put("AgeGroupJuniors", assetJSON.optBoolean("AgeGroupJuniors"));
        asset.put("AgeGroupSeniors", assetJSON.optBoolean("AgeGroupSeniors"));
        asset.put("PricePurchase", assetJSON.optDouble("PricePurchase"));
        asset.put("PriceInstallation", assetJSON.optDouble("PriceInstallation"));
        asset.put("PriceReInvestment", assetJSON.optDouble("PriceReInvestment"));
        asset.put("PriceMaintenance", assetJSON.optDouble("PriceMaintenance"));
        asset.put("PriceIndexation", assetJSON.optDouble("PriceIndexation"));
        asset.put("Lat", Double.parseDouble(assetJSON.optString("Lat").replaceAll(",", ".")));
        asset.put("Lng", Double.parseDouble(assetJSON.optString("Lng").replaceAll(",", ".")));
        asset.put("Documents", assetJSON.optJSONArray("Documents"));
        asset.put("Hyperlinks", assetJSON.optJSONArray("Hyperlinks"));
        asset.put("Images", parseImages(assetJSON.optJSONArray("Images")));
        return asset;
    }

    protected List<Map<String, Object>> parseChildLocations(String locations) {
        List<Map<String, Object>> locs = new ArrayList<>();
        JSONArray childLocations = new JSONArray(locations);
        for (int i = 0; i < childLocations.length(); i++) {
            JSONObject childLocation = childLocations.getJSONObject(i);
            JSONArray cls = childLocation.getJSONArray("ChildLocations");
            if (cls.length() == 0) {
                locs.add(parseLocation(childLocation));
            } else {
                locs.addAll(parseChildLocations(cls.toString()));
            }

        }
        return locs;
    }

    protected Map<String, Object> parseLocation(JSONObject json) {
        Map<String, Object> location = new HashMap<>();
        location.put("$id", json.optString("$id"));
        location.put("ID", json.optString("ID"));
        location.put("LastUpdated", json.optString("LastUpdated"));
        location.put("Name", json.optString("Name").replaceAll("\'", "\'\'"));
        location.put("AddressLine1", json.optString("AddressLine1"));
        location.put("Suburb", json.optString("Suburb"));
        location.put("City", json.optString("City"));
        location.put("Area", json.optString("Area"));
        location.put("PostCode", json.optString("PostCode"));
        location.put("Ref", json.optString("Ref"));
        location.put("AssetCount", json.optInt("AssetCount"));
        location.put("Lat", Double.parseDouble(json.optString("Lat").replaceAll(",", ".")));
        location.put("Lng", Double.parseDouble(json.optString("Lng").replaceAll(",", ".")));
        location.put("ChildLocations", json.optJSONArray("ChildLocations"));
        location.put("Documents", json.optJSONArray("Documents"));
        location.put("Images", parseImages(json.optJSONArray("Images")));
        return location;
    }

    protected List<Map<String, Object>> parseImages(JSONArray images) {
        List<Map<String, Object>> imagesList = new ArrayList<>();
        for (int i = 0; i < images.length(); i++) {
            JSONObject img = images.getJSONObject(i);
            Map<String, Object> image = parseImage(img);
            imagesList.add(image);
        }
        return imagesList;
    }

    protected Map<String, Object> parseImage(JSONObject image) {
        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("$id", image.optString("$id"));
        imageMap.put("ID", image.optString("ID"));
        imageMap.put("LastUpdated", image.optString("LastUpdated"));
        imageMap.put("URI", image.optString("URI"));
        imageMap.put("Description", image.optString("Description"));
        return imageMap;
    }

    // <editor-fold desc="Getters and setters" defaultstate="collapsed">
    /**
     * @return the locationGuid
     */
    public String getLocationGuid() {
        return locationGuid;
    }

    /**
     * @param locationGuid the locationGuid to set
     */
    public void setLocationGuid(String locationGuid) {
        this.locationGuid = locationGuid;
    }

    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the apiurl
     */
    public String getApiurl() {
        return apiurl;
    }

    /**
     * @param apiurl the apiurl to set
     */
    public void setApiurl(String apiurl) {
        this.apiurl = apiurl;
    }

    // </editor-fold> 
}
