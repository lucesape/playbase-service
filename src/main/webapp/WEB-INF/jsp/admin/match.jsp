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

<stripes:layout-render name="/WEB-INF/jsp/templates/admin.jsp" pageTitle="Match playadvisor met playmapping" menuitem="match">
    <stripes:layout-component name="head">
        <script type="text/javascript">
            var url = "${contextPath}/action/match/";
        </script>

        <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.15/css/jquery.dataTables.min.css" />

        <script type="text/javascript" src="https://cdn.datatables.net/1.10.15/js/jquery.dataTables.js"></script>
        <script type="text/javascript" src="${contextPath}/public/js/match.js"></script>
    </stripes:layout-component>
    <stripes:layout-component name="content">

        <h1>Match Playadvisor met playmapping</h1>

        <jsp:include page="/WEB-INF/jsp/common/messages.jsp"/>
        <div>
            <div class="tablesDiv"> 
                <div class="playadvisorClass">
                    <table id="playbasetable" class="display" cellspacing="0" width="100%">
                        <thead>
                            <tr>
                                <th>Naam</th>
                                <th>Playadvisor id</th>
                            </tr>
                        </thead>
                        <tfoot>
                            <tr>
                                <th>Naam</th>
                                <th>playadvisor id</th>
                            </tr>
                        </tfoot>
                    </table>
                </div>
                <div class="playmappingClass">
                    <table id="playmappingtable" class="display" cellspacing="0" width="100%">
                        <thead>
                            <tr>
                                <th>Naam </th>
                                <th>Score (0 - 10) </th>
                                <th>Afstand (km) </th>
                                <th>Naam gelijkheid (0 t/m 10)</th>
                            </tr>
                        </thead>
                        <tfoot>
                            <tr>
                                <th>Naam </th>
                                <th>Score (0 - 10) </th>
                                <th>Afstand (km) </th>
                                <th>Naam gelijkheid (0 t/m 10)</th>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
            <div>
                <stripes:form beanclass="nl.b3p.playbase.stripes.MatchActionBean"> 
                    <stripes:hidden name="playadvisorId" id="playadvisorId"/>
                    <stripes:hidden name="playmappingId" id="playmappingId" />
                    Playadvisor: <span id="playadvisor"></span> <br/>
                    Playmapping: <span id="playmapping"></span> <br/>
                    <stripes:select name="method">
                        <stripes:option value="add" label="Toevoegen"/>
                        <stripes:option value="merge" label="Samenvoegen"/>
                    </stripes:select>
                    <stripes:submit name="save" class="btn btn-primary">Opslaan</stripes:submit> <br/>
                    Automatische merge score: <stripes:text name="automaticMergeScore" value="10.0"/> <br/>
                    Gebruik afstand: <stripes:checkbox name="useDistance"/> <br/>
                    Gebruik plaatjes van playadvisor: <stripes:checkbox name="useImagesFromPlayadvisor"/> <br/>
                    <stripes:submit class="btn btn-default" name="autoMerge">Automerge</stripes:submit> <br/>
                    <stripes:submit class="btn btn-default" name="addAll">Alles toevoegen</stripes:submit> <br/>
                    
                </stripes:form>
            </div>
        </div>
    </stripes:layout-component>
</stripes:layout-render>