<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" %>
<%@ page import="java.lang.*,java.util.*" %>
<%@ page import="servlets.listeners.*" %>
<%@ page import="resources.*" %>
<%@ page contentType="application/xml;charset=UTF-8" %> 
<%@ page pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/taglibs/mytaglib.tld" prefix="mtl" %>

<!DOCTYPE rdf:RDF [
 <!ENTITY rdf 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'>
 <!ENTITY rdfs 'http://www.w3.org/2000/01/rdf-schema#'>
 <!ENTITY owl 'http://www.w3.org/2002/07/owl#'>
 <!ENTITY swrc 'http://swrc.ontoware.org/ontology#'>
 <!ENTITY xsd 'http://www.w3.org/2001/XMLSchema#'>
]>



<rdf:RDF
xml:base="${projectHome}<c:out value='${requPath}'/>"
 xmlns:rdf="&rdf;"
 xmlns:rdfs="&rdfs;"
 xmlns:owl="&owl;"
 xmlns:swrc="&swrc;"
 xmlns:xsd="&xsd;"
 >

 

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<owl:Ontology rdf:about="">
  <rdfs:comment>${projectName} publications for/<c:out value='${requPath}'/></rdfs:comment>
  <owl:imports rdf:resource="http://swrc.ontoware.org/ontology/portal"/>
</owl:Ontology>
  <%-------------------------- bookmarks -----------------------%>
 


<c:forEach var="bib" items="${ResourceBean.bibtex}" >
  <rdf:Description rdf:about="${projectHome}bibtex/<%=Bibtex.INTRA_HASH %><c:out value='${bib.hash}'/>/<mtl:encode value='${bib.user}'/>">
    <rdf:type rdf:resource="&swrc;<mtl:entrytype value='${bib.entrytype}'/>"/>
    <%@include file="SWRCoutput_item.jsp" %>
  </rdf:Description>
</c:forEach>
</rdf:RDF>