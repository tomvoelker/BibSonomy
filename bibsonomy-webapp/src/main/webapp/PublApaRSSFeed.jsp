<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" %>
<%@ page import="java.lang.*,java.util.*" %>
<%@ page import="resources.*" %>
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
 xmlns:dc="http://purl.org/dc/elements/1.1/"
 xmlns:syn="http://purl.org/rss/1.0/modules/syndication/"
 xmlns:content="http://purl.org/rss/1.0/modules/content/"
 xmlns:admin="http://webns.net/mvcb/"
>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>
<c:set var="basePath" value="${projectHome}"/> 

<channel rdf:about="${projectHome}<c:out value='${requPath}'/>">
  <title>${projectName} publications for /<c:out value='${requPath}'/></title>
  <link>${projectHome}<c:out value="${requPath}"/></link>
  <description>${projectName} RSS Feed for /<c:out value='${requPath}'/></description>

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
    <description><c:out value="${bib.description}"/></description>
    <link>${projectHome}bibtex/<%=Bibtex.INTRA_HASH %><c:out value='${bib.hash}'/>/<mtl:encode value='${bib.user}'/></link>
    <dc:creator><c:out value="${bib.user}" /></dc:creator>
    <dc:date><mtl:formatDate value="${bib.date}"/></dc:date>
    <dc:subject>
      <c:forEach var="tag" items="${bib.tags}"><c:out value='${tag} '/> </c:forEach>  
    </dc:subject>    
    <content:encoded><![CDATA[
    <c:if test="${not param.cleaned}">
      <link rel="stylesheet" href="${projectHome}resource/css/rss.css" type="text/css"/>
    </c:if>
    <c:if test="${param.cleaned}">
      <c:set var="noStyleAttribute" value="true"/>
    </c:if>       
       
    <font style="font-variant:small-caps;">
    <!-- authors -->
    <c:choose>
    <c:when test="${not empty bib.author}">      
        <c:forEach var="author" items="${bib.authornamesseparated}" varStatus="loopStatus">         	 	
       		<a href="${basePath}author/<mtl:encode value='${author[1]}'/>"><c:out value="${author[1]}" /></a>,         	     	 
        	<c:out value="${author[0]}" /><c:if test="${not loopStatus.last}"> &amp; </c:if>
        </c:forEach>
    </c:when>
    <c:otherwise> 
        <c:forEach var="editor" items="${bib.editornamesseparated}" varStatus="loopStatus">        	 
        	<a href="${basePath}author/<mtl:encode value='${editor[1]}'/>"><c:out value="${editor[1]}" /></a>,         	     	 
        	<c:out value="${editor[0]}" /><c:if test="${not loopStatus.last}"> &amp; </c:if>
    	</c:forEach>
    	(eds.)
    </c:otherwise>       
    </c:choose>
    
    <!-- year -->
    (<mtl:bibclean value="${bib.year}"/>).
    </font>
    
    <!-- title -->    
    <a href="${projectHome}bibtex/<%=Bibtex.INTRA_HASH %><c:out value='${bib.hash}'/>/<mtl:encode value='${bib.user}'/>"><i><mtl:bibclean value="${bib.title}"/>.</i></a>

	<!-- editor(s) -->
	<c:if test="${not empty bib.editor}">
    	In 
	    <c:if test="${not empty bib.editor}">
		    <c:forEach var="editor" items="${bib.editornamesseparated}" varStatus="loopStatus">        	 
		       	<a href="${basePath}author/<mtl:encode value='${editor[1]}'/>"><c:out value="${editor[1]}" /></a>,         	     	 
		       	<c:out value="${editor[0]}" /><c:if test="${not loopStatus.last}"> &amp; </c:if>           	     	 
		    </c:forEach>
		    	(eds.),
	    </c:if>
  	</c:if>
  	
  	<!-- inbook / incollection data -->
  	<c:choose>
    	<c:when test="${not empty bib.journal}">
      		<em><mtl:bibclean value="${bib.journal}"/></em>:
      		<c:if test="${not empty bib.volume}">
        		<b><mtl:bibclean value="${bib.volume}"/></b>.
      		</c:if>
      		<c:if test="${not empty bib.series}">
    			<mtl:bibclean value="${bib.series}"/>.
    		</c:if>
    	</c:when>
    	<c:otherwise>
      		<em><mtl:bibclean value="${bib.booktitle}"/></em>
    	</c:otherwise>
  	</c:choose> 
  	<c:if test="${not empty bib.pages}">
    	(pp. <mtl:bibclean value="${bib.pages}"/>).
  	</c:if>
	  
    <!-- address -->
    <c:if test="${not empty bib.address}"> 
    <mtl:bibclean value="${bib.address}"/>:    
   	</c:if>
    
    <!-- publisher -->
    <c:if test="${not empty bib.publisher}"> 
    <mtl:bibclean value="${bib.publisher}"/>.    
    </c:if>
    
    <!-- tags & URL-->
    <c:if test="${empty param.notags}">
       [<a href="${projectHome}">${projectName}</a>:<c:forEach var="tag" items="${bib.tags}"><%=" "%><a href="${projectHome}user/<mtl:encode value='${bib.user}'/>/<c:out value='${tag}'/>"><c:out value='${tag}'/></a></c:forEach>]
       <%-- download URL --%>
       <c:if test="${!empty bib.cleanurl}">
         <a href="<c:out value='${bib.cleanurl}'/>">URL</a>        
       </c:if>
     </c:if>    
    ]]>
    </content:encoded>
    <taxo:topics>
      <rdf:Bag>
        <c:forEach var="tag" items="${bib.tags}">
          <rdf:li rdf:resource="${projectHome}tag/<c:out value='${tag}' />" />
        </c:forEach>  
      </rdf:Bag>
    </taxo:topics>
  </item>
</c:forEach>
</rdf:RDF>