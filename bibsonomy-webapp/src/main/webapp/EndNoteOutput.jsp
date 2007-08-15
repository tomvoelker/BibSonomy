<% response.setHeader("Content-Disposition","inline; filename=bibsonomy.endnote"); %>
<%@ page contentType="text/plain"%>
<%@ page pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>
<c:forEach var="bib" items="${ResourceBean.bibtex}">
%0 <c:choose>
    <c:when test="${bib.entrytype == 'article'}">Journal Article</c:when>
    <c:when test="${bib.entrytype == 'book'}">Book</c:when>
    <c:when test="${bib.entrytype == 'booklet'}">Book</c:when>
    <c:when test="${bib.entrytype == 'inbook'}">Book Section</c:when>
    <c:when test="${bib.entrytype == 'incollection'}">Book Section</c:when>
    <c:when test="${bib.entrytype == 'inproceedings'}">Conference Paper</c:when>
    <c:when test="${bib.entrytype == 'manual'}">Generic</c:when>
    <c:when test="${bib.entrytype == 'mastersthesis'}">Thesis</c:when>
    <c:when test="${bib.entrytype == 'misc'}">Generic</c:when>
    <c:when test="${bib.entrytype == 'phdthesis'}">Thesis</c:when>
    <c:when test="${bib.entrytype == 'proceedings'}">Conference Proceedings</c:when>
    <c:when test="${bib.entrytype == 'techreport'}">Report</c:when>
    <c:when test="${bib.entrytype == 'unpublished'}">Unpublished Work</c:when>
    <c:otherwise>%0 Generic</c:otherwise>
  </c:choose>
  <%="\n"%>
  <c:forEach var="author" items="${bib.authorlist}">%A ${author}<%="\n"%></c:forEach>
  <%-- booktitle --%><c:if test="${not empty bib.booktitle}">%B ${bib.booktitle}<%="\n"%></c:if>
  <%-- address   --%><c:if test="${not empty bib.address}">%C ${bib.address}<%="\n"%></c:if>
  <%-- year      --%><c:if test="${not empty bib.year}">%D ${bib.year}<%="\n"%></c:if>
  <c:forEach var="editor" items="${bib.editorlist}">%E ${editor}<%="\n"%></c:forEach>
  <%-- publisher --%><c:if test="${not empty bib.publisher}">%I ${bib.publisher}<%="\n"%></c:if>
  <%-- journal   --%><c:if test="${not empty bib.journal}">%J ${bib.journal}<%="\n"%></c:if>
  <%-- keywords  --%>%K ${bib.tagString}
  <%-- number    --%><c:if test="${not empty bib.number}">%N ${bib.number}<%="\n"%></c:if>
  <%-- pages     --%><c:if test="${not empty bib.pages}">%P ${bib.pages}<%="\n"%></c:if>
  <%-- title     --%><c:if test="${not empty bib.title}">%T ${bib.title}<%="\n"%></c:if>
  <%-- url       --%><c:if test="${not empty bib.url}">%U ${bib.url}<%="\n"%></c:if>
  <%-- volume    --%><c:if test="${not empty bib.volume}">%V ${bib.volume}<%="\n"%></c:if>
  <%-- abstract  --%><c:if test="${not empty bib.bibtexAbstract}">%X ${bib.bibtexAbstract}<%="\n"%></c:if>
  <%-- annote    --%><c:if test="${not empty bib.annote}">%Z ${bib.annote}<%="\n"%></c:if>
  <%-- edition   --%><c:if test="${not empty bib.edition}">%7 ${bib.edition}<%="\n"%></c:if>
  <%-- chapter   --%><c:if test="${not empty bib.chapter}">%& ${bib.chapter}<%="\n"%></c:if>
  <%="\n"%>
</c:forEach>