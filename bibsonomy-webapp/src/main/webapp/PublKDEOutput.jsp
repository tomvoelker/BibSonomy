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
  
    <h1>KDE Publications</h1>
  
    <c:forEach var="bib" items="${ResourceBean.bibtex}">
      <p class="entry">
        <%-- decide depending on entry type --%>
        <c:choose>
          <c:when test="${bib.entrytype eq 'article'}">
            ARTICLE
            <!--  article -->
            <!-- author --><span class="entry_author"><mtl:bibclean value="${bib.author}"/>.</span>
            <!-- title  --><span class="entry_title"><a href="${projectHome}bibtex/<%=Bibtex.INTRA_HASH%>${bib.hash}/<mtl:encode value='${bib.user}'/>"><mtl:bibclean value="${bib.title}"/></a>.</span>          
            <!-- journa --><span class="entry_journal"><mtl:bibclean value="${bib.journal}"/>, </span>
            <!-- vonupa -->
            <span class="entry_vonupa">
              <mtl:bibclean value="${bib.volume}"/><c:if test="${!empty bib.number}">(<mtl:bibclean value="${bib.number}"/>)</c:if><c:if test="${!empty bib.pages}"><c:if test="${! empty bib.volume or ! empty bib.pages}">:</c:if><mtl:bibclean value="${bib.pages}"/></c:if>,
            </span>
            <!-- year   --><span class="entry_year"><mtl:bibclean value="${bib.year}"/>.</span>
            <!-- note   --><c:if test="${!empty bib.note}"><span class="entry_note"><mtl:bibclean value="${bib.note}"/>.</span></c:if>
          </c:when>
          <c:when test="${bib.entrytype eq 'book'}">
            BOOK
            <!--  book  -->
            <!-- author --><span class="entry_author"><mtl:bibclean value="${bib.author}"/>.</span>
            <!-- title  --><span class="entry_title"><em><a href="${projectHome}bibtex/<%=Bibtex.INTRA_HASH%>${bib.hash}/<mtl:encode value='${bib.user}'/>"><mtl:bibclean value="${bib.title}"/></a></em></span><c:choose><c:when 
            test="${!empty bib.volume}">, volume <mtl:bibclean value="${bib.volume}"/><c:if test="${!empty bib.series}"> of <em><mtl:bibclean value="${bib.series}"/></em></c:if>.</c:when><c:otherwise><c:choose><c:when
            test="${!empty bib.number}">, number <mtl:bibclean value="${bib.number}"/><c:if test="${!empty bib.series}"> of <em><mtl:bibclean value="${bib.series}"/></em></c:if>.</c:when><c:otherwise><em><mtl:bibclean value="${bib.series}"/></em>.</c:otherwise></c:choose></c:otherwise></c:choose>
            <!-- vonupa -->
            <!-- publis --><c:if test="${!empty bib.publisher}"><span class="entry_publisher"><mtl:bibclean value="${bib.publisher}"/>,</span></c:if>
            <!-- adress --><c:if test="${!empty bib.address}"><span class="entry_address"><mtl:bibclean value="${bib.address}"/>,</span></c:if>
            <!-- editio --><c:if test="${!empty bib.edition}">edition <span class="entry_edition"><mtl:bibclean value="${bib.edition}"/>,</span></c:if>
            <!-- month  --><c:if test="${!empty bib.month}"><span class="entry_month"><mtl:bibclean value="${bib.month}"/></span></c:if>
            <!-- year   --><span class="entry_year"><mtl:bibclean value="${bib.year}"/>.</span>
            <!-- note   --><c:if test="${!empty bib.note}"><span class="entry_note"><mtl:bibclean value="${bib.note}"/>.</span></c:if>
          </c:when>
          
        
        
  
  
          <%-- all remaining types --%>    
          <c:otherwise>
            <c:choose>
              <c:when test="${!empty bib.author}">
                <!-- author --><c:if test="${!empty bib.author}"><span class="entry_author"><mtl:bibclean value="${bib.author}"/>.</span></c:if>
                <!-- title  --><span class="entry_title"><a href="${projectHome}bibtex/<%=Bibtex.INTRA_HASH%>${bib.hash}/<mtl:encode value='${bib.user}'/>"><mtl:bibclean value="${bib.title}"/></a>.</span>
              	<!-- editor --><c:if test="${!empty bib.editor}"><span class="entry_editor">In <mtl:bibclean value="${bib.editor}"/>, editor(s), </span></c:if>
              </c:when>
              <c:otherwise>
                <!-- editor --><c:if test="${!empty bib.editor}"><span class="entry_editor"><mtl:bibclean value="${bib.editor}"/>, editor(s). </span></c:if>
                <!-- title  --><span class="entry_title"><a href="${projectHome}bibtex/<%=Bibtex.INTRA_HASH%>${bib.hash}/<mtl:encode value='${bib.user}'/>"><mtl:bibclean value="${bib.title}"/></a>, </span>
              </c:otherwise>        
            </c:choose>
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
          </c:otherwise>
        </c:choose>
        
        
      </p>
    </c:forEach>
  
  </body>
</html>
