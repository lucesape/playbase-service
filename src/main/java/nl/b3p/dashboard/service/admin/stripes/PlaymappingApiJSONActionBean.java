package nl.b3p.dashboard.service.admin.stripes;

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
import nl.b3p.dashboard.service.server.db.DB;
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
@UrlBinding("/action/apijson")
public class PlaymappingApiJSONActionBean implements ActionBean {

    private ActionBeanContext context;

    private static final Log log = LogFactory.getLog(PlaymappingApiJSONActionBean.class);
    
    private static final String JSP = "/WEB-INF/jsp/admin/playmapping.jsp";

    @Validate
    private String locationGuid;
    @Validate(required=true)
    private String username;
    @Validate(required=true)
    private String password;
    @Validate(required=true)
    private String apiurl;
    
    @DefaultHandler
    @DontValidate
    public Resolution edit() throws Exception {
        return new ForwardResolution(JSP);
    }

    public Resolution importPM() throws NamingException, SQLException {
        return collectJSON ();
    }
    
    private Resolution collectJSON () throws SQLException, NamingException {
        
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
                    =                    new BasicCredentialsProvider();
            Credentials defaultcreds = 
                    new UsernamePasswordCredentials(getUsername(), getPassword());
            AuthScope authScope = 
                    new AuthScope(hostname, port);
            credentialsProvider.setCredentials(authScope, defaultcreds);

            hcb = hcb.setDefaultCredentialsProvider(credentialsProvider);
            
            //preemptive not possible without hostname
            if (hostname!=null) {
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

        JSONObject result = new JSONObject();
        result.put("guid", getLocationGuid());
        
        String stringResult = null;
        HttpResponse response = null;
        try {
            response = hc.execute(request, httpContext);
            int statusCode = response.getStatusLine().getStatusCode();
            
            HttpEntity entity = response.getEntity();
            if (statusCode != 200) {
                result.put("status code", statusCode);
                result.put("reason", response.getStatusLine().getReasonPhrase());
            } else {
                //InputStream is = entity.getContent();
                stringResult = EntityUtils.toString(entity);
            }
        } catch (IOException ex) {
            log.debug("Exception False: ", ex);
            result.put("error", ex.getLocalizedMessage());
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
        
        if (stringResult == null) {
            stringResult = result.toString(4);
        } else {
            //moet later beter
            String uq = "INSERT INTO temp_json (values) VALUES (?);";
            int retval = DB.qr().update(uq, stringResult);
            if (retval == 1) {
                result.put("status", "JSON weggeschreven naar DB");
                // uitzoeken of het een locatie is of asset
                String type = "";
                if(apiurl.contains("Location")){
                    retval = refillLocationsApiTable(stringResult);
                    type = "locaties";
                }else if(apiurl.contains("Asset")){
                    retval = refillAssetsApiTable();
                    type = "assets";
                }else{
                    context.getValidationErrors().add("apiurl", new SimpleError("Wrong url selected."));
                    return new ForwardResolution(JSP);
                }
                result.put("aantal", retval);
                context.getMessages().add(new SimpleMessage("Er zijn " + retval + " " + type + " weggeschreven."));
                
            } else {
                //result.put("aantal", retval);
                context.getValidationErrors().add("status", new SimpleError("JSON niet weggeschreven naar DB"));
                //result.put("status", "JSON niet weggeschreven naar DB");
            }
            stringResult = result.toString(4);
        }
        return new ForwardResolution(JSP);
    }
    
    private int refillAssetsApiTable() throws NamingException, SQLException {
        StringBuilder sb = new StringBuilder();
        
        //leeg maken
        sb.append("truncate pm_assets_api;");
        int retval = DB.qr().update(sb.toString());
       /* if (retval <= 0) {
            return retval;
        }
        */
        //vullen vanuit temp_json
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
        sb.append("      \"lng\",");
        sb.append("      \"groep\"");
        sb.append(") ");
        sb.append("select ");
        sb.append("	 values->>'ID' as id,");
        sb.append("      values->>'LocationID' as gassetlocationid,");
        sb.append("      values->>'LocationName' as sassetlocationname,");
        sb.append("      values->>'LastUpdated' as dupdated,");
        sb.append("      values->>'Name' as name,");
        sb.append("      values->>'AssetType' as assettype,");
        sb.append("      values->>'Manufacturer' as smanufacturername,");
        sb.append("      values->>'SerialNumber' as sserialnumber,");
        sb.append("      (values->>'InstalledDate') as dinstalled,");
        sb.append("      (values->>'EndOfLifeYear')::numeric as endoflifeyear,");
        sb.append("      (values->>'SafetyZoneLength')::numeric as safetyzonelength,");
        sb.append("      (values->>'SafetyZoneWidth')::numeric as safetyzonewidth,");
        sb.append("      values->>'AgeGroupToddlers' as agegrouptoddlers,");
        sb.append("      values->>'AgeGroupJuniors' as agegroupjuniors,");
        sb.append("      values->>'AgeGroupSeniors' as agegroupseniors,");
        sb.append("      (values->>'PricePurchase')::numeric as fpurchaceprice,");
        sb.append("      (values->>'PriceInstallation')::numeric as finstallationprice,");
        sb.append("      (values->>'PriceReInvestment')::numeric as freinvestmentcost,");
        sb.append("      (values->>'PriceMaintenance')::numeric as fmaintenancecost,");
        sb.append("      (values->>'PriceIndexation')::numeric as fpriceindexation,");
        sb.append("      replace(values->>'Lat',',','.')::numeric as flattitude,");
        sb.append("      replace(values->>'Lng',',','.')::numeric as flongitude,");
        sb.append("      (values->'CustomProperties'->>'Groep') as groep ");
        sb.append("from   (");
        sb.append("           select json_array_elements(values::json) as values ");
        sb.append("           from   temp_json ");
        sb.append("       ) a;");
        
        retval = DB.qr().update(sb.toString());
        return retval;
    }
    
    private int refillLocationsApiTable(String temp) throws NamingException, SQLException {
        //vindt childlocations
        StringBuilder sb = new StringBuilder();
        sb.append("insert into temp_json_2 (\"values\") ");
        sb.append("  select json_array_elements(values::json #> '{0,\"ChildLocations\"}') as values from   temp_json; ");
        int retval = DB.qr().update(sb.toString());
        if (retval <= 0) {
            return retval;
        }

        //vindt childlocations van childlocations
        sb = new StringBuilder();
        sb.append("insert into temp_json_3 (\"values\") ");
        sb.append("	select json_array_elements(json_extract_path_text(values::json,'ChildLocations')::json) as values from   temp_json_2; ");
        retval = DB.qr().update(sb.toString());
        if (retval <= 0) {
            return retval;
        }

        //insert childlocations from childlocations from childlocations
        sb = new StringBuilder();
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
        sb.append("SELECT");
        sb.append("    VALUES->>'ID'                            AS id,");
        sb.append("    VALUES->>'Name'                          AS name,");
        sb.append("    VALUES->>'LastUpdated'                   AS dupdated,");
        sb.append("    REPLACE(VALUES->>'Lat',',','.')::NUMERIC AS lat,");
        sb.append("    REPLACE(VALUES->>'Lng',',','.')::NUMERIC AS lng ");
        sb.append("FROM ");
        sb.append("    (");
        sb.append("        SELECT ");
        sb.append("            json_array_elements(json_extract_path_text(VALUES::json,'ChildLocations')::json) AS ");
        sb.append("            VALUES ");
        sb.append("        FROM ");
        sb.append("            temp_json_2 ) a;");
        retval = DB.qr().update(sb.toString());
        return retval;
    }
    
    protected List<Map<String, Object>> getChildLocation(String locations){
        List<Map<String,Object>> locs = new ArrayList<>();
        JSONArray childLocations = new JSONArray(locations);
        for (int i = 0; i < childLocations.length(); i++) {
            JSONObject childLocation = childLocations.getJSONObject(i);
            JSONArray cls = childLocation.getJSONArray("ChildLocations");
            if(cls.length() == 0){
                locs.add(parseLocation(childLocation));
            }else{
                locs.addAll(getChildLocation(cls.toString()));
            }
            
        }
        return locs;
    }
    
    protected Map<String,Object> parseLocation(JSONObject location){
        return new HashMap<>();
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
