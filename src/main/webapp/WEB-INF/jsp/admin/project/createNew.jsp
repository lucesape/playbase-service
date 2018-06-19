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

<stripes:layout-render name="/WEB-INF/jsp/templates/admin.jsp" pageTitle="Maak nieuw project" menuitem="new-project">
    <stripes:layout-component name="head">
    </stripes:layout-component>
    <stripes:layout-component name="content">
        <h1>Nieuw project</h1>
        <jsp:include page="/WEB-INF/jsp/common/messages.jsp"/>     
        <div>
            <stripes:form beanclass="nl.b3p.playbase.stripes.ProjectActionBean"  class="form-horizontal">
                <table>
                    <stripes:hidden name="project.id"/>
                    <tr><td>Type</td><td> <stripes:select name="project.type_" id="projecttype">
                                <stripes:option value="">Selecteer</stripes:option>
                                <stripes:options-enumeration enum="nl.b3p.playbase.entities.ProjectType" />
                            </stripes:select></td></tr>
                    <tr><td>Cron expressie </td><td><stripes:text name="project.cronexpressie"/></td></tr>
                    <tr id="username"><td>Playmapping username</td><td><stripes:text name="project.username"/></td></tr>
                    <tr id="password"><td>Playmapping Password</td><td><stripes:text name="project.password"/></td></tr>
                    <tr id="exporthash"><td>Authentication key playadvisor</td><td><stripes:text name="project.authkey"/></td></tr>
                    <tr id="baseurl"><td>Base url playadvisor</td><td><stripes:text name="project.baseurl"/></td></tr>
                    <tr><td>Gemeentenaam</td><td><stripes:text name="project.name"/></td></tr>
                    <tr ><td>Mail adres</td><td><stripes:text name="project.mailaddress"/></td></tr>
                    <tr><td><stripes:submit name="saveNew" class="btn btn-primary">Opslaan</stripes:submit></td>
                        </tr>
                        <tr>
                            <td colspan="3">Log: <stripes:textarea cols="80" disabled="true" rows="30" name="project.log"/></td>
                    </tr>
                </table>
            </stripes:form>
        </div>
    </stripes:layout-component>
</stripes:layout-render>