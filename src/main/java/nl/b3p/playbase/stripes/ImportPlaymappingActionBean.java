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
import java.io.InputStream;
import java.sql.SQLException;
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
import net.sourceforge.stripes.validation.Validate;
import nl.b3p.playbase.ImportReport;
import nl.b3p.playbase.ImportReport.ImportType;
import nl.b3p.playbase.PlaymappingImporter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    private static final String JSP = "/WEB-INF/jsp/admin/import/playmapping.jsp";

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

    @Validate
    private String project;
    
    private PlaymappingImporter processor;

    @DefaultHandler
    @DontValidate
    public Resolution edit() throws Exception {
        return new ForwardResolution(JSP);
    }

    public Resolution importPM() throws NamingException, SQLException {
        ImportReport report;
        processor = new PlaymappingImporter(project);
        processor.init();
        if (file.equalsIgnoreCase("Via API")) {
            report = processor.importJSONFromAPI(getUsername(), getPassword(), getApiurl());
        } else {

            try {
                InputStream in = ImportPlaymappingActionBean.class.getResourceAsStream(file);
                String theString = IOUtils.toString(in, "UTF-8");
                in.close();
                report = new ImportReport();
                processor.importString(theString, apiurl, report);

            } catch (IOException ex) {
                log.error(ex);
                return new ForwardResolution(JSP);
            }
        }

        context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberInserted(ImportType.ASSET) + " " + ImportType.ASSET.toString() + " weggeschreven."));
        context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberInserted(ImportType.LOCATION) + " " + ImportType.LOCATION.toString() + " weggeschreven."));
        context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberUpdated(ImportType.ASSET) + " " + ImportType.ASSET.toString() + " geupdatet."));
        context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberUpdated(ImportType.LOCATION) + " " + ImportType.LOCATION.toString() + " geupdatet."));

        if (report.getErrors().size() > 0) {
            context.getMessages().add(new SimpleMessage("Er zijn " + report.getErrors(ImportType.ASSET).size() + " " + ImportType.ASSET.toString() + " mislukt:"));
            context.getMessages().add(new SimpleMessage("Er zijn " + report.getErrors(ImportType.LOCATION).size() + " " + ImportType.LOCATION.toString() + " mislukt:"));

            for (ImportType importType : report.getAllErrors().keySet()) {
                Set<String> errors = report.getAllErrors().get(importType);
                for (String error : errors) {
                    context.getMessages().add(new SimpleMessage(importType.toString() + ": " + error));
                }
            }
        }
        return new ForwardResolution(JSP);
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
    
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
    

    // </editor-fold> 

}
