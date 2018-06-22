<%@include file="/WEB-INF/jsp/taglibs.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<ul class="nav navbar-nav">
    <li${menuitem == 'index' ? ' class="active"' : ''}><a href="${contextPath}/admin/index.jsp">Start</a></li>
    <li class="dropdown">
        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Exporteren <span class="caret"></span></a>
        <ul class="dropdown-menu">
            <li${menuitem == 'json' ? ' class="active"' : ''}><stripes:link beanclass="nl.b3p.playbase.stripes.DashboardJSONActionBean">JSON</stripes:link></li>
            <li${menuitem == 'json2' ? ' class="active"' : ''}><stripes:link beanclass="nl.b3p.playbase.stripes.AggregationJSONActionBean">JSON2</stripes:link></li>
        </ul>
    </li>
    <li${menuitem == 'match' ? ' class="active"' : ''}>
        <stripes:link beanclass="nl.b3p.playbase.stripes.MatchActionBean">Match</stripes:link>
    </li>
    <li${menuitem == 'planner' ? ' class="active"' : ''}>
        <stripes:link beanclass="nl.b3p.playbase.stripes.ProjectActionBean">Project</stripes:link>
    </li>
    <li${menuitem == 'pushy' ? ' class="active"' : ''}>
        <stripes:link beanclass="nl.b3p.playbase.stripes.PlayadvisorRESTAPIActionBean" event="view">Pushit</stripes:link>
    </li>
</ul>