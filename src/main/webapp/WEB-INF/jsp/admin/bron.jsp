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
        <stripes:form beanclass="nl.b3p.playbase.stripes.DashboardJSONActionBean" class="form-horizontal">
                <stripes:submit name="spelen" class="btn btn-primary">spelen</stripes:submit>
                <stripes:submit name="bomen" class="btn btn-primary">bomen</stripes:submit>
                <stripes:submit name="cancel" class="btn btn-default">Annuleren</stripes:submit>

               
                <div class="form-group">
                    <label class="col-sm-2 control-label">Gemeentenaam:</label>
                    <div class="col-sm-10"><stripes:text class="form-control" name="locationValue"/></div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">Aggregatieniveau: </label>
                    <div class="col-sm-10">
                        <stripes:select name="locationTable" class="form-control">
                            <c:forEach var="s" items="${actionBean.availableLocationTables}">
                                <stripes:option value="${s}" label="${s}"/>
                            </c:forEach>
<%--                        
                            <stripes:option value="">Geen</stripes:option>
--%>
                        </stripes:select>
                    </div>
                </div>                


         </stripes:form>
    </stripes:layout-component>
</stripes:layout-render>