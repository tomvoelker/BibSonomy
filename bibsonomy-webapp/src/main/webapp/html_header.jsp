<%--
 INTRODUCTION:
  This file is included at the top of every page that is displayed to the user.
  If a new page is created that is displayed to the user this file should be
  included with the <jsp:include ...> method.
  
  INPUT PARAMETER: title 
  
  Calling example:
  
  <jsp:include page="html_header.jsp">
    <jsp:param name="title" value="<%=requUser%>" />
  </jsp:include>
  
--%>

<%-- 
  This selects the correct values for basePath and stuff like that,
  depending on the Hostname (e.g. localhost vs. other)
--%>

<%@ page import="servlets.listeners.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/taglibs/mytaglib.tld" prefix="mtl" %>

<%--
	HTML starts here
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="/resources/css/style.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/faq.css">
    <link rel="icon" href="/resources/image/favicon.png" type="image/png">
    <script type="text/javascript" src="/resources/javascript/functions.js"></script>
    <script type="text/javascript" src="/resources/javascript/tooltip.js"></script>
    <script type="text/javascript" src="/resources/javascript/style.js"></script>
    <script type="text/javascript" src="/resources/javascript/chrome.js"></script>
	<script type="text/javascript" src="/resources/javascript/jquery_142_min.js"></script>
    <script type="text/javascript" src="/resources/javascript/jquery_form_224.js">&amp;nbsp;</script> 
	<script type="text/javascript" src="/resources/javascript/jquery_corner.js"></script>
	<script type="text/javascript" src="/resources/javascript/jquery_autocomplete.js"></script>    
	<script type="text/javascript" src="/resources/javascript/jquery.textarearesizer.js"></script>  
    <c:if test="${projectName == 'PUMA'}">
		<link rel="stylesheet" type="text/css" href="/resources_puma/css/puma.css">
    </c:if> 
    
    <script type="text/javascript" src="/resources/javascript/localized_strings_en.js"></script >
    <meta name="author" content="Knowledge and Data Engineering Group, University of Kassel, Germany">
    <meta name="copyright" content="Knowledge and Data Engineering Group, University of Kassel, Germany">
    <meta name="email" content="webmaster@bibsonomy.org">
    <meta name="keywords" lang="de" content="BibTeX, Bookmarks, Folksonomy, Tagging, Wissensverarbeitung, Wissensmanagement, Data Mining, Informatik, Universität Kassel">
    <meta name="keywords" lang="en" content="BibTeX, bookmarks, knowledge discovery, folksonomy, tagging, knowledge management, data mining, computer science, University of Kassel"> 
    <meta name="description" lang="de" content="Webapplikation des Fachgebiets Wissensverarbeitung, Universität Kassel">
    <meta name="description" lang="en" content="Webapplication of the Knowledge and Data Engineering Group, University of Kassel, Germany">
    <c:if test="${isResourceSite eq 'yes'}">
      <link rel="alternate" type="application/rss+xml" title="Bookmark RSS feed for <c:out value='/${requPath}' />" href="${projectHome}rss/<c:out value='${requPath}'/>">
      <link rel="alternate" type="application/rss+xml" title="Publication RSS feed for <c:out value='/${requPath}' />" href="${projectHome}publrss/<c:out value='${requPath}'/>">
      <link rel="alternate" type="application/rss+xml" title="BuRST RSS feed for <c:out value='/${requPath}' />" href="${projectHome}burst/<c:out value='${requPath}'/>">
      <link rel="unapi-server" type="application/xml" title="unAPI" href="${projectHome}unapi"/>
    </c:if>
    <link rel="alternate" type="application/atom+xml" title="BibSonomy Blog - Atom" href="http://bibsonomy.blogspot.com/feeds/posts/default">
    <link rel="alternate" type="application/rss+xml"  title="BibSonomy Blog - RSS"  href="http://bibsonomy.blogspot.com/feeds/posts/default?alt=rss">
    <title><c:out value="${projectName}" />::<c:out value="${param.title}"/></title>
  </head>
  
  <body>
  
  <div id="topNavBar">
	<c:choose>
		<c:when test="${!empty user.name}">
			logged in as <a href="/user/<c:out value="${user.name}" />"><c:out value="${user.name}" /></a> &middot;
	        <a href="/settings">settings</a> &middot;
	        <a href="/logout">logout</a> &middot;
		</c:when>
		<c:otherwise>
			<a href="/login">login</a> &middot;
			<a href="/register">register</a>
		</c:otherwise>
	</c:choose>
	
	<a href="/help" rel="Help">help</a> &middot;
	<a href="http://bibsonomy.blogspot.com/">blog</a> &middot;
	<a href="/help/contact">about</a> &middot;
	<a href="?lang=en"><img alt="en" src="/resources/image/lang_en.png"/></a>
  </div>

  <div id="main">

    <c:if test="${projectName == 'PUMA'}">
		<img id="headerlogoimage" src="/resources_puma/image/puma_2b_comic_150_3.png">
	</c:if>
	  
	<div id="main_header">
	
		<!-- Navigation -->
		<div id="heading" style="float:left">
  
  
<%--
<div style="position:absolute; top:40px; left:10px;  z-index:1; background:#ffb6c1; border:solid 1px #334d55; font-family: Verdana, Arial, sans-serif; font-size: small; vertical-align: center; padding: 1px 1px 1px 1px;"><a href="http://www.bibsonomy.org/help/doc/api.html">API</a> has been released. Beta Tests start right now.</div>    
--%>
  
<%--  
<div style="position:absolute; top:40px; left:10px;  z-index:1; background:#ffb6c1; border:solid 1px #334d55; font-family: Verdana, Arial, sans-serif; font-size: small; vertical-align: center; padding: 1px 1px 1px 1px;">${projectName} will be temporarily unavailable due to maintenance at 2007-02-21 between 7:00am and 19:00am CET.</div>    
--%>
