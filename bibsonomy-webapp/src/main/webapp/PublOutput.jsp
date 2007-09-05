<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" %>
<%@ page import="java.lang.*,java.util.*" %>
<%@ page import="resources.*" %>
<%@ page contentType="text/html;charset=UTF-8" %> 
<%@ page pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ taglib uri="/WEB-INF/taglibs/mytaglib.tld" prefix="mtl" %>


<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-------------------------- bibtex -----------------------%>
<html>
  <head>
    <title>${projectName}</title>
    <link rel="stylesheet" type="text/css" href="/resources/css/prolearn.css" />
  </head>
  <body>

    <h1>Publications</h1>
  
    <c:forEach var="bib" items="${ResourceBean.bibtexSortedByYear}">
      <p class="entry">
        <!-- author --><c:if test="${!empty bib.author}"><span class="entry_author"><mtl:bibclean value="${bib.author}"/>. </span></c:if>
        <!-- title  --><span class="entry_title"><a href="${projectHome}bibtex/<%=Bibtex.INTRA_HASH %>${bib.hash}/<mtl:encode value='${bib.user}'/>"><mtl:bibclean value="${bib.title}"/></a>. </span>
        <!-- editor --><c:if test="${!empty bib.editor}"><span class="entry_editor">In <mtl:bibclean value="${bib.editor}"/>, editor(s), </span></c:if>
        <!-- journal, booktitle, series  -->
        <c:choose>
          <c:when test="${!empty bib.journal}">
            <span class="entry_journal"><mtl:bibclean value="${bib.journal}"/>, </span>
          </c:when>
          <c:when test="${!empty bib.booktitle}">
            <span class="entry_booktitle"><mtl:bibclean value="${bib.booktitle}"/>, </span>
          </c:when>        
          <c:when test="${!empty bib.series}">
            <span class="entry_series"><mtl:bibclean value="${bib.series}"/>, </span>
          </c:when>
        </c:choose>
        <!-- volume,number,pages -->
        <span class="entry_vonupa">
         <c:if test="${!empty bib.volume}">(<mtl:bibclean value="${bib.volume}"/>)<c:if test="${!empty bib.pages && empty bib.number}">:</c:if></c:if><c:if test="${!empty bib.number}"><mtl:bibclean value="${bib.number}"/><c:if test="${!empty bib.pages}">:</c:if></c:if><c:if test="${!empty bib.pages}"><mtl:bibclean value="${bib.pages}"/>,</c:if>
        </span>
        <!-- publisher --><c:if test="${!empty bib.publisher}"><span class="entry_publisher"><mtl:bibclean value="${bib.publisher}"/>,</span></c:if>
        <!-- address   --><c:if test="${!empty bib.address}"><span class="entry_address"><mtl:bibclean value="${bib.address}"/>,</span></c:if>
        <!-- year      --><c:if test="${!empty bib.year}"><span class="entry_year"><mtl:bibclean value="${bib.year}"/>.</span></c:if>
        <c:if test="${empty param.notags}">
          [<a href="${projectHome}">${projectName}</a>:<c:forEach var="tag" items="${bib.tags}"><%=" "%><a href="${projectHome}user/<mtl:encode value='${bib.user}'/>/<c:out value='${tag}'/>"><c:out value='${tag}'/></a></c:forEach>]
          <%-- download URL --%>
          <c:if test="${!empty bib.cleanurl}">
            <a href="<c:out value='${bib.cleanurl}'/>">URL</a>        
          </c:if>
        </c:if>
  
      </p>
    </c:forEach>
  
  </body>
</html>
