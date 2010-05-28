<?xml version="1.0" ?>
<jsp:root version="2.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:resources="urn:jsptagdir:/WEB-INF/tags/resources"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
	xmlns:layout="urn:jsptagdir:/WEB-INF/tags/layout"
	xmlns:clusters="urn:jsptagdir:/WEB-INF/tags/clusters"
    xmlns:user="urn:jsptagdir:/WEB-INF/tags/resources/user"
    xmlns:users="urn:jsptagdir:/WEB-INF/tags/users"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:mtl="urn:jsptld:/WEB-INF/taglibs/mytaglib.tld"> 
	
	<jsp:directive.page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" session="true" />

	<ul>
		<c:forEach var="cluster" varStatus="status" items="${command.clusters}">
				<li class="clusterSettings${status.count-1}">
					<!-- display the cluster -->						
					<!-- ${fn:escapeXml(tag.name)} -->
					<clusters:cloud cluster="${cluster}"/>
					<div id="slider${status.count-1}"><c:out value=" "/></div>
				</li>
				<c:out value=" "/>						
		</c:forEach>		
	</ul>
	
</jsp:root>