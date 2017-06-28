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
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
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
    
    

    // </editor-fold>
    public Resolution importLocations() {
        try {
            ImportReport report = new ImportReport("locaties");
            if(csv != null){
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
        } catch (IOException | CsvFormatException ex) {
            log.error("Cannot import playadvisor csv: ", ex);
        }
        return new ForwardResolution(JSP);
    }

}
