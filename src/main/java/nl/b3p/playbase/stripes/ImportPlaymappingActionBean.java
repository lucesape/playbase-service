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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
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
import nl.b3p.playbase.ImportReport;
import nl.b3p.playbase.ImportReport.ImportType;
import nl.b3p.playbase.PlaymappingImporter;
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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

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
    
    @Validate
    private String file;
    
    private final PlaymappingImporter processor = new PlaymappingImporter();

    @DefaultHandler
    @DontValidate
    public Resolution edit() throws Exception {
        return new ForwardResolution(JSP);
    }

    public Resolution importPM() throws NamingException, SQLException {
            return collectJSON();
        /*try {
            InputStream in = ImportPlaymappingActionBean.class.getResourceAsStream(file);
            String theString = IOUtils.toString(in, "UTF-8");
            in.close();
            Resolution res = importString(theString);
            if(res != null){
                return res;
            }else{
            return new ForwardResolution(JSP);
            }
        } catch (IOException ex) {
            log.error(ex);
            return new ForwardResolution(JSP);
        }*/
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
        Resolution res = importString(stringResult);
        if(res != null){
            return res;
        }else{
            return new ForwardResolution(JSP);
        }
    }
    
    private Resolution importString(String stringResult) throws NamingException, SQLException {
        if (stringResult != null) {
            String type;
            ImportReport report = null;
            processor.init();
            if (apiurl.contains("Location")) {
                report = processor.processLocations(stringResult);
                type = "locaties";
            } else if (apiurl.contains("Asset")) {
                report = processor.processAssets(stringResult);
                type = "assets";
            } else {
                context.getValidationErrors().add("apiurl", new SimpleError("Wrong url selected."));
                return new ForwardResolution(JSP);
            }
            context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberInserted(ImportType.ASSET) + " " + ImportType.ASSET.toString() + " weggeschreven."));
            context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberInserted(ImportType.LOCATION) + " " + ImportType.LOCATION.toString() + " weggeschreven."));
            context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberUpdated(ImportType.ASSET) + " " + ImportType.ASSET.toString() + " geupdatet."));
            context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberUpdated(ImportType.LOCATION) + " " + ImportType.LOCATION.toString() + " geupdatet."));
            
            if(report.getErrors().size() > 0){
                context.getMessages().add(new SimpleMessage("Er zijn " + report.getErrors(ImportType.ASSET).size()+ " " + ImportType.ASSET.toString()  + " mislukt:"));
                context.getMessages().add(new SimpleMessage("Er zijn " + report.getErrors(ImportType.LOCATION).size()+ " " + ImportType.LOCATION.toString() + " mislukt:"));
                
                for (ImportType importType : report.getAllErrors().keySet()) {
                    Set<String> errors = report.getAllErrors().get(importType);
                    for (String error : errors) {
                        context.getMessages().add(new SimpleMessage(importType.toString() + ": " + error));
                    }
                }
            }
        }
        return null;
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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
    
    // </editor-fold> 

    
}
