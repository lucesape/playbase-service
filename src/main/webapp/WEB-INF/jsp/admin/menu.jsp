<%@include file="/WEB-INF/jsp/taglibs.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<stripes:useActionBean var="staticBean" beanclass="nl.b3p.dashboard.service.admin.stripes.StaticViewerActionBean" event="info"/>

<ul class="nav navbar-nav">
    <li${menuitem == 'index' ? ' class="active"' : ''}><a href="${contextPath}/admin/index.jsp">Start</a></li>
    <li class="dropdown">
        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Instellingen <span class="caret"></span></a>
        <ul class="dropdown-menu">
            <li${menuitem == 'layers' ? ' class="active"' : ''}><stripes:link beanclass="nl.b3p.dashboard.service.admin.stripes.DashboardJSONActionBean">JSON</stripes:link></li>
        </ul>
    </li>
  
</ul>