<%@ page language="java"%>
<%@ page import="java.lang.*,java.util.*"%>
<%@ page contentType="text/comma-separated-values;charset=UTF-8"%>
<%-- @ page contentType="text/plain;charset=UTF-8"--%>
<%response.setHeader("Content-Disposition","inline; filename=bibsonomy.txt"); %>
<%@ page pageEncoding="UTF-8"%>
<%@ page session="true"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="/WEB-INF/src/tags/mytaglib.tld" prefix="mtl"%>
<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request" />
<jsp:useBean id="bean" class="beans.ExtendedFieldsBean" scope="request"/>
<c:set var="delimiter" value=";"/>
PostDate${delimiter}Status${delimiter}Date(s)${delimiter}Type${delimiter}Type of audience${delimiter}Countries addressed${delimiter}Size of audience${delimiter}Partner responsible${delimiter}Workpackage${delimiter}Contact to main author (email)${delimiter}Comments${delimiter}Publication Details<c:forEach var="bib" items="${ResourceBean.bibtexSortedByYear}">
  <jsp:setProperty name="bean" property="currUser" value="${user.name}"/><jsp:setProperty name="bean" property="hash" value="${bib.hash}"/>
"${bib.date}"${delimiter}actual${delimiter}<c:forEach var="field" items="${bean.fields}">"${field}"${delimiter}</c:forEach>"<c:choose>
    <c:when test="${not empty bib.author}">
      <mtl:bibcleancsv value="${bib.author}" />
    </c:when>
    <c:otherwise>
      <mtl:bibcleancsv value="${bib.editor}" /> (editor(s))<%-- --%>
    </c:otherwise>
  </c:choose>: <mtl:bibcleancsv value="${bib.title}"/>.<c:choose> 
    <c:when test="${!empty bib.journal}">
      <mtl:bibcleancsv value="${bib.journal}"/>
    </c:when>
    <c:when test="${!empty bib.booktitle}">
      <mtl:bibcleancsv value="${bib.booktitle}"/>
    </c:when>
    <c:when test="${!empty bib.series}">
      <mtl:bibcleancsv value="${bib.series}"/>
    </c:when>
  </c:choose>
  <c:if test="${!empty bib.volume}"> (<mtl:bibcleancsv value="${bib.volume}"/>)<c:if test="${!empty bib.pages && empty bib.number}">:</c:if></c:if>
  <c:if test="${!empty bib.number}"><mtl:bibcleancsv value="${bib.number}"/><c:if test="${!empty bib.pages}">:</c:if></c:if>
  <c:if test="${!empty bib.pages}"><%=" "%><mtl:bibcleancsv value="${bib.pages}"/></c:if>,<%-- --%>
  <c:if test="${!empty bib.publisher}"><mtl:bibcleancsv value="${bib.publisher}"/>, </c:if>
  <c:if test="${!empty bib.address}"><mtl:bibcleancsv value="${bib.address}"/>, </c:if>
  <c:if test="${!empty bib.year}"><mtl:bibcleancsv value="${bib.year}"/>.</c:if>"<%-- --%>
</c:forEach>