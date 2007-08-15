<% response.setHeader("Content-Disposition","inline; filename=bibsonomy.bib"); %><%@ page contentType="text/plain"%><%@ page pageEncoding="UTF-8" %><%@ page session="true" %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>
<c:choose>

  <c:when test="${param.duplicates == 'no'}">
    <c:forEach var="bib" items="${ResourceBean.bibtexSortedByYear}">
      ${bib.bibtex}<%="\n\n"%>
    </c:forEach>
  </c:when>

  <c:otherwise>
    <c:forEach var="bib" items="${ResourceBean.bibtex}">
      ${bib.bibtex}<%="\n\n"%>
    </c:forEach>
  </c:otherwise>
</c:choose>
