<%--
Copyright (C) 2016 B3Partners B.V.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@include file="/WEB-INF/jsp/taglibs.jsp"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<stripes:layout-render name="/WEB-INF/jsp/templates/admin.jsp" pageTitle="Export JSON" menuitem="json">
    <stripes:layout-component name="content">

        <h1>Export JSON</h1>
        
        <jsp:include page="/WEB-INF/jsp/common/messages.jsp"/>
        <stripes:form beanclass="nl.b3p.dashboard.service.admin.stripes.DashboardJSONActionBean" class="form-horizontal">
                <stripes:submit name="houten" class="btn btn-primary">houten</stripes:submit>
                <stripes:submit name="wbbk" class="btn btn-primary">wbbk</stripes:submit>
                <stripes:submit name="cancel" class="btn btn-default">Annuleren</stripes:submit>

                <%--div class="form-group">
                    <label class="col-sm-2 control-label">Tabblad:</label>
                    <div class="col-sm-10">
                        <stripes:text class="form-control" name="tab"/>
                        <p class="help-block">Het tabblad in het kaartlagen scherm waar de laag kan worden in/uitgeschakeld.</p>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">Naam:</label>
                    <div class="col-sm-10"><stripes:text class="form-control" name="name"/></div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">Inzetbalk knop: </label>
                    <div class="col-sm-10">
                        <stripes:select name="layerToggleKey" class="form-control">
                            <stripes:option value="">Geen</stripes:option>
                            <stripes:option value="Basis">Basis (groen)</stripes:option>
                            <stripes:option value="Brandweer">Brandweer (rood)</stripes:option>
                            <stripes:option value="Water">Water (blauw)</stripes:option>
                            <stripes:option value="Gebouw">Gebouw (zwart)</stripes:option>
                        </stripes:select>
                        <p class="help-block">Indien de laag niet gekoppeld is aan een inzetbalk knop kan de laag worden in- en uitgeschakeld via het kaartlagen scherm. Als de laag gekoppeld is aan een knop kan de laag alleen worden geschakeld via de knop.</p>
                    </div>
                </div>                
                <div class="form-group">
                    <label class="col-sm-2 control-label">Instellingen:</label>                    
                    <div class="col-sm-10">
                        <div class="checkbox">
                            <label><stripes:checkbox name="layer.enabled"/>Beschikbaar</label>
                        </div>

                        <div class="checkbox">
                            <label><stripes:checkbox name="visible"/>Standaard ingeschakeld</label>
                        </div>                        
                        <div class="checkbox">
                            <label><stripes:checkbox name="dpiConversionEnabled"/>ArcGIS naar MapServer DPI conversie</label>
                        </div>                        
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">Index:</label>
                    <div class="col-sm-10">
                        <stripes:text class="form-control" name="layer.index" size="3" maxlength="3"/>
                        <p class="help-block">De index bepaalt of een laag bovenop of onderop een andere laag wordt getoond (een hogere index tov andere laag betekent bovenop)</p>
                    </div>
                </div --%>

         </stripes:form>
    </stripes:layout-component>
</stripes:layout-render>