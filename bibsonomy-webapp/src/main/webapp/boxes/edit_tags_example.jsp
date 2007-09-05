<%@ page import="resources.Bookmark" %>

<%-- create artificial bookmark --%>
  <jsp:useBean id="resource" class="resources.Bookmark" scope="request">
     <jsp:setProperty name="resource" property="user"  value="${user.name}"/>
     <jsp:setProperty name="resource" property="url"   value="${projectHome}"/>
     <jsp:setProperty name="resource" property="title" value="${projectName}"/>
  </jsp:useBean>

<%
  resource.setTags("bibtex folksonomy project");
  resource.setDate(new Date());
%>

<%@include file="/boxes/bookmark_desc.jsp" %>

<div style="margin-top:.5em;"></div>
<%
  resource.setTags("bibtex social bookmarking system");
%>

<%@include file="/boxes/bookmark_desc.jsp" %>     
