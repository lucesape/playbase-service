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

<stripes:layout-render name="/WEB-INF/jsp/templates/admin.jsp" pageTitle="Import JSON" menuitem="apijson">
    <stripes:layout-component name="content">

        <h1>Import JSON uit Playmapping API</h1>
        
        <jsp:include page="/WEB-INF/jsp/common/messages.jsp"/>
        <stripes:form beanclass="nl.b3p.dashboard.service.admin.stripes.PlaymappingApiJSONActionBean" class="form-horizontal">
                <stripes:submit name="spelen" class="btn btn-primary">spelen</stripes:submit>
                <stripes:submit name="cancel" class="btn btn-default">Annuleren</stripes:submit>

               
                <div class="form-group">
                    <label class="col-sm-2 control-label">Klant GUID</label>
                    <div class="col-sm-10"><stripes:text class="form-control" name="locationGuid"/></div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">API URL</label>
                    <div class="col-sm-10"><stripes:text class="form-control" name="apiurl"/></div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">username</label>
                    <div class="col-sm-10"><stripes:text class="form-control" name="username"/></div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">password</label>
                    <div class="col-sm-10"><stripes:text class="form-control" name="password"/></div>
                </div>

         </stripes:form>
    </stripes:layout-component>
</stripes:layout-render>