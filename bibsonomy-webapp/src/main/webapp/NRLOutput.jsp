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
<%@ taglib uri="/WEB-INF/taglibs/mytaglib.tld" prefix="mtl" %>

<rdf:RDF
 xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
 xmlns:nao="http://www.semanticdesktop.org/ontologies/2007/03/nao#"
 xmlns:nrl="http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#"
>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>
<jsp:useBean id="now" class="java.util.Date" />

<c:forEach var="book" items="${ResourceBean.bookmarks}">
  <rdf:Description rdf:about="<c:out value='${book.url}'/>">
    <c:forEach var="tag" items="${book.tags}">
    <nao:isRelated>
      <nao:Tag rdf:about="${projectHome}tag/<mtl:encode value='${tag}'/>">
        <nao:prefLabel><c:out value='${tag}'/></nao:prefLabel>
      </nao:Tag>
    </nao:isRelated>
    </c:forEach>
  </rdf:Description>
</c:forEach>
</rdf:RDF>