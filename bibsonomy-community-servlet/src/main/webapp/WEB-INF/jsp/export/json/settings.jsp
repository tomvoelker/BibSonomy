<?xml version="1.0" ?>
<jsp:root version="2.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
	xmlns:layout="urn:jsptagdir:/WEB-INF/tags/layout"
	xmlns:tags="urn:jsptagdir:/WEB-INF/tags/tags"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:mtl="urn:jsptld:/WEB-INF/taglibs/mytaglib.tld"
	xmlns:output="urn:jsptagdir:/WEB-INF/tags/export/bibtex"> 
	
<jsp:directive.page contentType="application/json; charset=UTF-8" language="java" pageEncoding="UTF-8" session="true" />

	<c:set var="includes" value="${fn:split('clusters.annotation,clusters.bookmark,clusters.bibtex,clusters.tags,clusters.members',',')}"/>
	<c:set var="excludes" value="${fn:split('*.stem,bibtex,bookmark,context,tagcloud,commandName,duplicates,filter,layout,restrictToTags,sortPageOrder,sortPage,tagstype,requestedUser,pageTitle,notags,loginMethod,*.class',',')}"/>

	${mtl:serializeJSON(command, includes, excludes, false, true)}

 </jsp:root>