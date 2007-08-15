<%@ page language="java" %>
<%@ page import="java.lang.*,java.util.*" %>
<%@ page import="servlets.listeners.*" %>
<%@ page import="helpers.*" %>
<%@ page import="resources.*" %>
<%@ page contentType="text/html;charset=UTF-8" %> 
<%@ page pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="/WEB-INF/taglibs/mytaglib.tld" prefix="mtl" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<%
    response.setHeader("Pragma","no-cache");
    response.setHeader("Cache-Control","no-cache");
    response.setDateHeader("Expires",-1);
    response.setDateHeader("Last-Modified",0);
%>
<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/> 
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>


<%--
  HTML starts here
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="content-Type" content="text/html; charset=UTF-8" >
    <link rel="stylesheet" type="text/css" href="http://pil.phys.uniroma1.it/~ciro/bibsonomy_statphy23.css">
    
    <meta name="author" content="TAGora project">
    <meta name="email" content="info@tagora-project.eu">
    <meta name="keywords" lang="en" content="statphy23, statphys, abstracts, abstract book, conference, proceedings, BibTeX, bookmarks, folksonomy, tagging, TAGora, TAGora project, pil group, pil, university of kassel, bibsonomy"> 
    <meta name="description" lang="en" content="Statphys23 conference abstracts on the BibSonomy bookmark/BibTeX tagging and sharing system">
    <title>Statphys23 on BibSonomy</title>

    <link rel="alternate" type="application/atom+xml" title="BibSonomy Blog - Atom" href="http://bibsonomy.blogspot.com/feeds/posts/default">
    <link rel="alternate" type="application/rss+xml"  title="BibSonomy Blog - RSS"  href="http://bibsonomy.blogspot.com/feeds/posts/default?alt=rss">
  </head>
  
