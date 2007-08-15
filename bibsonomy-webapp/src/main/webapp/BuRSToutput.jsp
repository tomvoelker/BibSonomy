<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" %>
<%@ page import="java.lang.*,java.util.*,java.text.*" %>
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

 <!ENTITY swrc 'http://swrc.ontoware.org/ontology#'>
 <!ENTITY xsd 'http://www.w3.org/2001/XMLSchema#'>
]>

<rdf:RDF
 xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
 xmlns="http://purl.org/rss/1.0/"
 xmlns:cc="http://web.resource.org/cc/"
 xmlns:taxo="http://purl.org/rss/1.0/modules/taxonomy/"
 xmlns:dc="http://purl.org/dc/elements/1.1/"
 xmlns:syn="http://purl.org/rss/1.0/modules/syndication/"
 xmlns:content="http://purl.org/rss/1.0/modules/content/"
 xmlns:admin="http://webns.net/mvcb/"
 xmlns:burst="http://xmlns.com/burst/0.1/"
 
 xmlns:rdfs="&rdfs;"
 xmlns:swrc="&swrc;"
 xmlns:xsd="&xsd;"
>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>
<jsp:useBean id="now" class="java.util.Date" />
<c:set var="basePath" value="${projectHome}"/> 

<channel rdf:about="${projectHome}<c:out value='${requPath}'/>">
  <title>${projectName} publications for /<c:out value='${requPath}'/></title>
  <link>${projectHome}burst/<c:out value="${requPath}"/></link>
  <description>${projectName} BuRST Feed for /<c:out value='${requPath}'/></description>
  <dc:date><mtl:formatDate value="${now}"/></dc:date>

  <%-------------------------- bookmarks -----------------------%>
  <items>
    <rdf:Seq>
      <c:forEach var="bib" items="${ResourceBean.bibtex}">
        <rdf:li rdf:resource="${projectHome}bibtex/<%=Bibtex.INTRA_HASH %><c:out value='${bib.hash}'/>/<mtl:encode value='${bib.user}'/>"/>
      </c:forEach>
    </rdf:Seq>
  </items>
</channel>

<c:forEach var="bib" items="${ResourceBean.bibtex}">
  <item rdf:about="${projectHome}bibtex/<%=Bibtex.INTRA_HASH %><c:out value='${bib.hash}'/>/<mtl:encode value='${bib.user}'/>">
    <title><mtl:bibclean value="${bib.title}"/></title>
    <c:if test ="${!empty bib.description}"><description><c:out value ='${bib.description}'/></description></c:if>
    <link>${projectHome}bibtex/<%=Bibtex.INTRA_HASH %><c:out value='${bib.hash}'/>/<mtl:encode value='${bib.user}'/></link>
    <dc:creator><c:out value="${bib.user}" /></dc:creator>
    <dc:date><mtl:formatDate value="${bib.date}"/></dc:date>
    <dc:subject><c:forEach var="tag" items="${bib.tags}"><c:out value='${tag} '/> </c:forEach></dc:subject>
    <content:encoded>
	    <![CDATA[
        <c:set var="noStyleAttribute" value="true"/>
	    <div class="block">
	      <%@ include file="/boxes/bibtex_desc.jsp" %>
	      <%@ include file="/boxes/bibtex_desc2.jsp" %>
	    </div>
	    ]]>
    </content:encoded>
    <taxo:topics>
      <rdf:Bag>
        <c:forEach var="tag" items="${bib.tags}">
          <rdf:li rdf:resource="${projectHome}tag/<c:out value='${tag}' />" />
        </c:forEach>  
      </rdf:Bag>
    </taxo:topics>
    <burst:publication>
      <swrc:<mtl:entrytype value='${bib.entrytype}'/>>
        <%@include file="/SWRCoutput_item.jsp" %>
      </swrc:<mtl:entrytype value='${bib.entrytype}'/>>  
    </burst:publication>
  </item>
</c:forEach>


</rdf:RDF>