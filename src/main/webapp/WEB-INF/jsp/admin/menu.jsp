<%@include file="/WEB-INF/jsp/taglibs.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<ul class="nav navbar-nav">
    <li${menuitem == 'index' ? ' class="active"' : ''}><a href="${contextPath}/admin/index.jsp">Start</a></li>
    <li class="dropdown">
        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Exporteren <span class="caret"></span></a>
        <ul class="dropdown-menu">
            <li${menuitem == 'json' ? ' class="active"' : ''}><stripes:link beanclass="nl.b3p.playbase.stripes.DashboardJSONActionBean">JSON</stripes:link></li>
        </ul>
    </li>
    <li class="dropdown">
        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Importeren <span class="caret"></span></a>
        <ul class="dropdown-menu">
            <li${menuitem == 'importPlaymapping' ? ' class="active"' : ''}><stripes:link beanclass="nl.b3p.playbase.stripes.ImportPlaymappingActionBean">Playmapping</stripes:link></li>
            <li${menuitem == 'importPlayadvisor' ? ' class="active"' : ''}><stripes:link beanclass="nl.b3p.playbase.stripes.ImportPlayadvisorActionBean">Playadvisor</stripes:link></li>
        </ul>
    </li>
    <li${menuitem == 'match' ? ' class="active"' : ''}>
        <stripes:link beanclass="nl.b3p.playbase.stripes.MatchActionBean">Match</stripes:link>
    </li>
  
</ul>