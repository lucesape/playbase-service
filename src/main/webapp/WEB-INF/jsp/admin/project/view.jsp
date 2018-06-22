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

<stripes:layout-render name="/WEB-INF/jsp/templates/admin.jsp" pageTitle="Projectoverzicht" menuitem="project">
    <stripes:layout-component name="head">
        <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.15/css/jquery.dataTables.min.css" />

        <script type="text/javascript" src="https://cdn.datatables.net/1.10.15/js/jquery.dataTables.js"></script>
        <script type="text/javascript" src="${contextPath}/public/js/cron.js"></script>
    </stripes:layout-component>
    <stripes:layout-component name="content">
        <h1>Projecten</h1>
        <jsp:include page="/WEB-INF/jsp/common/messages.jsp"/>

        <div class="playadvisorClass">
            <table id="playbasetable" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>Naam</th>
                        <th>Type</th>
                        <th>Expressie</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tfoot>
                    <tr>
                        <th>Naam</th>
                        <th>Type</th>
                        <th>Expressie</th>
                        <th>Status</th>
                    </tr>
                </tfoot>
            </table>
        </div>
        <div>
            <stripes:form beanclass="nl.b3p.playbase.stripes.ProjectActionBean" class="form-horizontal">
                <c:if test="${not empty actionBean.project.id}">
                    <table>
                    <stripes:hidden name="project.id"/>
                    <tr><td>Type</td><td> <stripes:select name="project.type_" id="projecttype">
                                <stripes:option value="">Selecteer</stripes:option>
                                <stripes:options-enumeration enum="nl.b3p.playbase.entities.ProjectType" />
                            </stripes:select></td></tr>
                    <tr><td>Status</td><td> <stripes:select name="project.status">
                        <stripes:option value="">Selecteer</stripes:option>
                        <stripes:options-enumeration enum="nl.b3p.playbase.entities.Status" />
                    </stripes:select></td></tr>
                   <tr><td>Cron expressie </td><td><stripes:text name="project.cronexpressie"/></td></tr>
                    <tr id="username"><td>Playmapping username</td><td><stripes:text name="project.username"/></td></tr>
                    <tr id="password"><td>Playmapping Password</td><td><stripes:text name="project.password"/></td></tr>
                    <tr id="exporthash"><td>Authentication key playadvisor</td><td><stripes:text name="project.authkey"/></td></tr>
                    <tr><td>Pad voor plaatjes</td><td><stripes:text name="project.imagepath"/></td></tr>
                    <tr id="baseurl"><td>Base url playadvisor</td><td><stripes:text name="project.baseurl"/></td></tr>
                    <tr><td>Gemeentenaam</td><td><stripes:text name="project.name"/></td></tr>
                    <tr ><td>Mail adres</td><td><stripes:text name="project.mailaddress"/></td></tr>
                    <tr><td><stripes:submit name="save" class="btn btn-primary">Opslaan</stripes:submit></td>
                        <td><c:if test="${not empty actionBean.project.id}"><stripes:submit name="removeCron" class="btn btn-danger">Verwijder</stripes:submit>
                        <td><stripes:submit name="doInitialLoad" class="btn btn-danger">doInitialLoad</stripes:submit>
                        <stripes:submit name="downloadString" class="btn btn-default">Download</stripes:submit></c:if></td>
                    </tr>
                    <tr>
                        <td colspan="3">Log: <stripes:textarea cols="80" disabled="true" rows="30" name="project.log"/></td>
                    </tr>
                    </table></c:if>
                <c:if test="${empty actionBean.project.id}"><stripes:submit name="nieuw" class="btn btn-default">Nieuw</stripes:submit></c:if>
                    <script type="text/javascript">
                          var url = "${contextPath}/action/project/";
                          var type = "${actionBean.project.type_}";
                    </script>
            </stripes:form>
        </div>
    </stripes:layout-component>
</stripes:layout-render>