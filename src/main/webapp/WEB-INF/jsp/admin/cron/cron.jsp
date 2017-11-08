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

<stripes:layout-render name="/WEB-INF/jsp/templates/admin.jsp" pageTitle="Planner voor automagische taken" menuitem="planner">
    <stripes:layout-component name="head">
        <script type="text/javascript">
              var url = "${contextPath}/action/cron/";
        </script>
        <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.15/css/jquery.dataTables.min.css" />

        <script type="text/javascript" src="https://cdn.datatables.net/1.10.15/js/jquery.dataTables.js"></script>
        <script type="text/javascript" src="${contextPath}/public/js/cron.js"></script>
    </stripes:layout-component>
    <stripes:layout-component name="content">
        <h1>Planner</h1>
        <jsp:include page="/WEB-INF/jsp/common/messages.jsp"/>

        <div class="playadvisorClass">
            <table id="playbasetable" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>Project</th>
                        <th>Type</th>
                        <th>Expressie</th>
                        <th>Vorige run</th>
                        <th>Volgende run</th>
                    </tr>
                </thead>
                <tfoot>
                    <tr>
                        <th>Project</th>
                        <th>Type</th>
                        <th>Expressie</th>
                        <th>Vorige run</th>
                        <th>Volgende run</th>
                    </tr>
                </tfoot>
            </table>
        </div>
        <div>
            <stripes:form beanclass="nl.b3p.playbase.stripes.CronActionBean" class="form-horizontal">
                <table>
                    <stripes:hidden name="cronjob.id"/>
                    <tr><td>Type</td><td> <stripes:select name="cronjob.type_">
                                <stripes:option value="">Selecteer</stripes:option>
                                <stripes:options-enumeration enum="nl.b3p.playbase.cron.CronType" />
                            </stripes:select></td></tr>
                    <tr><td>Cron expressie </td><td><stripes:text name="cronjob.cronexpressie"/></td></tr>
                    <tr><td>User/[import|export] id</td><td><stripes:text name="cronjob.username"/></td></tr>
                    <tr><td>Password/[import|export] key </td><td><stripes:text name="cronjob.password"/></td></tr>
                    <tr><td>export Hash /download location </td><td><stripes:text name="cronjob.exporthash"/></td></tr>
                    <tr><td>Base url </td><td><stripes:text name="cronjob.baseurl"/></td></tr>
                    <tr><td>Project</td><td><stripes:text name="cronjob.project"/></td></tr>
                    <tr><td>Mail adres</td><td><stripes:text name="cronjob.mailaddress"/></td></tr>
                    <tr><td><stripes:submit name="save" class="btn btn-primary">Opslaan</stripes:submit></td>
                        <td><stripes:submit name="nieuw" class="btn btn-default">Nieuw</stripes:submit></td>
                        <td><c:if test="${not empty actionBean.cronjob.id}"><stripes:submit name="removeCron" class="btn btn-danger">Verwijder</stripes:submit>
                       <stripes:submit name="runNow" class="btn btn-warning">Voer nu uit</stripes:submit>
                        <stripes:submit name="downloadString" class="btn btn-default">Download</stripes:submit></c:if></td>
                    </tr>
                    <tr>
                        <td colspan="3">Log: <stripes:textarea cols="80" disabled="true" rows="30" name="cronjob.log"/></td>
                    </tr>
                    </table>

            </stripes:form>
        </div>
    </stripes:layout-component>
</stripes:layout-render>