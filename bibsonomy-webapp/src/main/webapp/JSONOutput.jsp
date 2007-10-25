
<% response.setHeader("Content-Disposition","inline; filename=publ.js"); %>
<%@ page contentType="application/json"%>
<%@ page language="java"%>
<%@ page import="java.lang.*,java.util.*"%>
<%@ page import="resources.*"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page session="true"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f"%>
<%@ taglib uri="/WEB-INF/taglibs/mytaglib.tld" prefix="mtl"%>
<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean"
	scope="request" />
{ "items": [
<c:forEach var="bib" items="${ResourceBean.bibtexSortedByYear}">
      {
            "pub-type": "<mtl:bibclean value="${bib.entrytype}" />", 
            "label": "<mtl:bibclean value="${bib.title}" />",
            "id": "<mtl:bibclean value="${bib.title}" />",
        <c:choose>
		    <c:when test="${!empty bib.journal}">
            	"journal": "<mtl:bibclean value="${bib.journal}" />",
          	</c:when>
		    <c:when test="${!empty bib.booktitle}">
	            "booktitle": "<mtl:bibclean value="${bib.booktitle}" />",
          	</c:when>
		    <c:when test="${!empty bib.series}">
    	        "series": "<mtl:bibclean value="${bib.series}" />",
          	</c:when>
	    </c:choose>

	    <c:if test="${!empty bib.publisher}">
	         "publisher":"<mtl:bibclean value="${bib.publisher}" />",
		</c:if>
  		<c:if test="${!empty bib.address}">
  		    "address":"<mtl:bibclean value="${bib.address}" />",
		</c:if>
            "year": "<mtl:bibclean value="${bib.year}" />", 
            "url": "<mtl:bibclean value="${bib.url}" />", 
            "uri": "${projectHome}uri/bibtex/<%=Bibtex.INTER_HASH%>${bib.hash}", 
        <c:if test="${!empty bib.author}">
                   "author": [
                   <c:forEach var="author" items="${bib.authorlist}">
	                   "<mtl:bibclean value="${author}" />",
                   </c:forEach>
                   ],
         </c:if>
	     <c:if test="${!empty bib.editor}">
                   "editor": [
                   <c:forEach var="editor" items="${bib.editorlist}">
	                   "<mtl:bibclean value="${editor}" />",
                   </c:forEach>
                   ],
         </c:if>
         
         <c:if test="${!empty bib.description}">
            "description": "<mtl:bibclean value="${bib.description}" />", 
         </c:if>	
         
         <c:if test="${!empty bib.volume}">
         	"volume": "<mtl:bibclean value="${bib.volume}"/>",
         </c:if>	
         <c:if test="${!empty bib.number}">
         	"number": "<mtl:bibclean value="${bib.number}"/>",
         </c:if>
         <c:if test="${!empty bib.pages}">
         	"pages": "<mtl:bibclean value="${bib.pages}"/>",
         </c:if>

            "key": "<mtl:bibclean value="${bib.key}"/>",
            
            "keywords": [
            
            <c:forEach var="tag" items="${bib.tags}">
            	"<c:out value='${tag}'/>",
            </c:forEach>
            ],

         <c:if test="${!empty bib.note}">
 			"note": "<mtl:bibclean value="${bib.note}"/>",
         </c:if>
         <c:if test="${!empty bib.abstract}">
 			"abstract": "<mtl:bibclean value="${bib.abstract}"/>",
         </c:if>
           
           
            "type": "Publication"
        }, 
    </c:forEach>
] }
