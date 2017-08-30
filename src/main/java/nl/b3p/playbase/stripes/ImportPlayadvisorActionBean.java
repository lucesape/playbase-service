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
import java.util.Set;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.action.StrictBinding;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;
import nl.b3p.commons.csv.CsvFormatException;
import nl.b3p.playbase.ImportReport;
import nl.b3p.playbase.PlayadvisorImporter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Meine Toonen
 */
@StrictBinding
@UrlBinding("/action/importPlayadvisor")
public class ImportPlayadvisorActionBean implements ActionBean {

    private static final Log log = LogFactory.getLog("ImportPlayadvisorActionBean");
    private ActionBeanContext context;
    private static final String JSP = "/WEB-INF/jsp/admin/playadvisor.jsp";

    private PlayadvisorImporter processor = new PlayadvisorImporter();

    @Validate
    private FileBean csv;

    @Validate
    private FileBean comments;

    @Validate
    private String file;

    // <editor-fold desc="Getters and setters" defaultstate="collapsed">
    public ActionBeanContext getContext() {
        return context;
    }

    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    public FileBean getCsv() {
        return csv;
    }

    public void setCsv(FileBean csv) {
        this.csv = csv;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public FileBean getComments() {
        return comments;
    }

    public void setComments(FileBean comments) {
        this.comments = comments;
    }

    // </editor-fold>
    
    @DefaultHandler
    public Resolution view(){
        return new ForwardResolution(JSP);
    }
    
    public Resolution importLocations() {
        try {
            ImportReport report = new ImportReport();
            if (csv != null) {
                processor.importStream(csv.getInputStream(), report);
            }
            if (file != null) {
                try {
                    InputStream in = ImportPlayadvisorActionBean.class.getResourceAsStream(file);
                    processor.importStream(in, report);
                    in.close();
                } catch (IOException ex) {
                    log.error(ex);
                    return new ForwardResolution(JSP);
                }
            }
            if(comments != null){
                processor.importComments(comments.getInputStream(), report);
            }
            
            context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberInserted(ImportReport.ImportType.ASSET) + " " + ImportReport.ImportType.ASSET.toString() + " weggeschreven."));
            context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberInserted(ImportReport.ImportType.LOCATION) + " " + ImportReport.ImportType.LOCATION.toString() + " weggeschreven."));
            context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberInserted(ImportReport.ImportType.COMMENT) + " " + ImportReport.ImportType.COMMENT.toString() + " weggeschreven."));
            context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberUpdated(ImportReport.ImportType.ASSET) + " " + ImportReport.ImportType.ASSET.toString() + " geupdatet."));
            context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberUpdated(ImportReport.ImportType.LOCATION) + " " + ImportReport.ImportType.LOCATION.toString() + " geupdatet."));
            context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumberUpdated(ImportReport.ImportType.COMMENT) + " " + ImportReport.ImportType.COMMENT.toString() + " geupdatet."));

            if (report.getErrors().size() > 0) {
                context.getMessages().add(new SimpleMessage("Er zijn " + report.getErrors(ImportReport.ImportType.ASSET).size() + " " + ImportReport.ImportType.ASSET.toString() + " mislukt:"));
                context.getMessages().add(new SimpleMessage("Er zijn " + report.getErrors(ImportReport.ImportType.LOCATION).size() + " " + ImportReport.ImportType.LOCATION.toString() + " mislukt:"));
                context.getMessages().add(new SimpleMessage("Er zijn " + report.getNumErrors(ImportReport.ImportType.COMMENT) + " " + ImportReport.ImportType.COMMENT.toString() + " mislukt:"));

                for (ImportReport.ImportType importType : report.getAllErrors().keySet()) {
                    Set<String> errors = report.getAllErrors().get(importType);
                    for (String error : errors) {
                        context.getMessages().add(new SimpleMessage(importType.toString() + ": " + error));
                    }
                }
            }
        } catch (IOException | CsvFormatException ex) {
            log.error("Cannot import playadvisor csv: ", ex);
        }
        return new ForwardResolution(JSP);
    }

}
