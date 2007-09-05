<%@ page language="java" %>
<%@ page import="java.lang.*,java.util.*" %>
<%@ page import="servlets.listeners.*" %>
<%@ page import="helpers.*" %>
<%@ page import="resources.*" %>
<%@ page contentType="text/html;charset=UTF-8" %> 
<%@ page pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="/WEB-INF/src/tags/mytaglib.tld" prefix="mtl" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/> 

<%
    response.setHeader("Pragma","no-cache");
    response.setHeader("Cache-Control","no-cache");
    response.setDateHeader("Expires",-1);
    response.setDateHeader("Last-Modified",0);
%>