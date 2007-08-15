<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" %>
<%@ page import="java.lang.*,java.util.*" %>
<%@ page contentType="application/xml;charset=UTF-8" %> 
<%@ page pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/taglibs/mytaglib.tld" prefix="mtl" %>


<rdf:RDF
 xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
 xmlns="http://purl.org/rss/1.0/"
 xmlns:cc="http://web.resource.org/cc/"
 xmlns:taxo="http://purl.org/rss/1.0/modules/taxonomy/"
 xmlns:content="http://purl.org/rss/1.0/modules/content/"
 xmlns:dc="http://purl.org/dc/elements/1.1/"
 xmlns:syn="http://purl.org/rss/1.0/modules/syndication/"
 xmlns:admin="http://webns.net/mvcb/"
>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>
 

<channel rdf:about="${projectHome}<c:out value='${requPath}'/>">
  <title>${projectName} bookmarks for /<c:out value='${requPath}'/></title>
  <link>${projectHome}<c:out value="${requPath}"/></link>
  <description>${projectName} RSS Feed for /<c:out value='${requPath}'/></description>

  <%-------------------------- bookmarks -----------------------%>
  <items>
    <rdf:Seq>
      <c:forEach var="book" items="${ResourceBean.bookmarks}">
        <rdf:li rdf:resource="<c:out value='${book.url}' />"/>
      </c:forEach>
    </rdf:Seq>
  </items>
</channel>

<c:forEach var="book" items="${ResourceBean.bookmarks}">
  <item rdf:about="<c:out value='${book.url}' />">
    <title><c:out value="${book.title}"/></title>
    <description><c:out value="${book.extended}"/></description>
    <link><c:out value='${book.url}' /></link>
    <dc:creator><c:out value="${book.user}" /></dc:creator>
    <dc:date><mtl:formatDate value="${book.date}"/></dc:date>
    <dc:subject>
      <c:forEach var="tag" items="${book.tags}"><c:out value='${tag} '/> </c:forEach>  
    </dc:subject>
    <content:encoded><![CDATA[
    <link rel="stylesheet" href="${projectHome}resources/css/rss.css" type="text/css"/>
    <div class="block">
      <%@include file="/boxes/bookmark_desc.jsp" %>     
    </div>
    ]]>
    </content:encoded>
    <taxo:topics>
      <rdf:Bag>
        <c:forEach var="tag" items="${book.tags}">
          <rdf:li rdf:resource="${projectHome}tag/<c:out value='${tag}' />" />
        </c:forEach>  
      </rdf:Bag>
    </taxo:topics>
  </item>
</c:forEach>


</rdf:RDF>