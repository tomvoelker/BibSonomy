<%@ page import="resources.Bookmark" %>

<%-- create artificial bookmark --%>
  <jsp:useBean id="book" class="resources.Bookmark" scope="request">
     <jsp:setProperty name="book" property="user"  value="${user.name}"/>
     <jsp:setProperty name="book" property="url"   value="${projectHome}"/>
     <jsp:setProperty name="book" property="title" value="${projectName}"/>
  </jsp:useBean>

<%
  book.setTags("bibtex folksonomy project");
  book.setDate(new Date());
%>

<%@include file="/boxes/bookmark_desc.jsp" %>

<div style="margin-top:.5em;"></div>
<%
  book.setTags("bibtex social bookmarking system");
%>

<%@include file="/boxes/bookmark_desc.jsp" %>     
