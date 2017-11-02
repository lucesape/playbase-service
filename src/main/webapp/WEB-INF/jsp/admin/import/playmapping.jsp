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

<stripes:layout-render name="/WEB-INF/jsp/templates/admin.jsp" pageTitle="Import Playmapping" menuitem="importPlaymapping">
    <stripes:layout-component name="content">

        <h1>Import JSON uit Playmapping API</h1>
        
        <jsp:include page="/WEB-INF/jsp/common/messages.jsp"/>
        <stripes:form beanclass="nl.b3p.playbase.stripes.ImportPlaymappingActionBean" class="form-horizontal">
                <stripes:submit name="importPM" class="btn btn-primary">Importeer</stripes:submit>
                <stripes:submit name="cancel" class="btn btn-default">Annuleren</stripes:submit>
                <div class="form-group">
                    <label class="col-sm-2 control-label">API URL</label>
                    <div class="col-sm-10">
                        <stripes:select name="apiurl" >
                            <stripes:option value="https://api.playmapping.com/CustomerLocation/GetAll" label="Locations"/>
                            <stripes:option value="https://api.playmapping.com/CustomerAsset/GetAll" label="Assets"/>
                        </stripes:select>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">File</label>
                    <div class="col-sm-10">
                        <stripes:select name="file" >
                            <stripes:option label="Via API"/>
                            <stripes:option value="assets.json" label="AllAssets"/>
                            <stripes:option value="singleAssetWithLinked.json" label="SingleAsset"/>
                            <stripes:option value="locations.json" label="AllLocations"/>
                            <stripes:option value="location.json" label="SingleLocation"/>
                        </stripes:select>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">username</label>
                    <div class="col-sm-10"><stripes:text class="form-control" value="Chrisvanlith" name="username"/></div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">password</label>
                    <div class="col-sm-10"><stripes:text class="form-control" value="Chris.234!" name="password"/></div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">Project</label>
                    <div class="col-sm-10"><stripes:text class="form-control" value="groningen" name="project"/></div>
                </div>

         </stripes:form>
    </stripes:layout-component>
</stripes:layout-render>