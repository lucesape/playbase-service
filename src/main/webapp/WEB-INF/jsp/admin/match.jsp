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

        <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.15/css/jquery.dataTables.min.css" />

        <script type="text/javascript" src="https://cdn.datatables.net/1.10.15/js/jquery.dataTables.js"></script>
        <script type="text/javascript" src="${contextPath}/public/js/match.js"></script>
    </stripes:layout-component>
    <stripes:layout-component name="content">

        <h1>Match Playadvisor met playmapping</h1>

        <jsp:include page="/WEB-INF/jsp/common/messages.jsp"/>
        <div style="display:flex; height:400px;"> 
            <div style="width:40%;">
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
            <div style="height:40%; margin-left: 50px;">
                <table id="playmappingtable" class="display" cellspacing="0" width="100%">
                    <thead>
                        <tr>
                            <th>Naam </th>
                            <th>Playmapping id</th>
                            <th>Afstand (km) </th>
                            <th>Naam gelijkheid (0 t/m 10)</th>
                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th>Naam </th>
                            <th>Playmapping id</th>
                            <th>Afstand (km) </th>
                            <th>Naam gelijkheid (0 t/m 10)</th>
                        </tr>
                    </tfoot>
                </table>
            </div>
        </div>
        <div>
            sadfasdf
        </div>
    </stripes:layout-component>
</stripes:layout-render>