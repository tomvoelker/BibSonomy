<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" %>
<%@ page import="java.lang.*,java.util.*" %>
<%@ page contentType="application/xml;charset=UTF-8" %> 
<%@ page pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>
<jsp:useBean id="now" class="java.util.Date" />

<%-------------------------- bookmarks -----------------------%>
<posts update='<fmt:formatDate type="both" value="${now}" pattern="yyyy-MM-dd HH:mm:ss"/>' user="<c:out value='${user.name}'/>"><c:forEach var="book" items="${ResourceBean.bookmarks}">
<post 
   href="<c:out value='${book.url}' />" 
   description="<c:out value='${book.title}'/>" 
   <c:if test="${!empty book.extended}">
     extended="<c:out value='${book.extended}'/>" 
   </c:if>
   hash="<c:out value='${book.hash}' />"
   user="<c:out value='${book.user}' />"
   tag="<c:forEach var="tag" items='${book.tags}'><c:out value='${tag} ' /> </c:forEach>"
   time="<fmt:formatDate type='both' pattern="yyyy-MM-dd'T'HH:mm:ssZ" value='${book.date}'/>" />
</c:forEach></posts>
