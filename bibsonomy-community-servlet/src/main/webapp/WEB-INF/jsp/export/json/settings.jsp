<?xml version="1.0" ?>
<jsp:root version="2.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
	xmlns:layout="urn:jsptagdir:/WEB-INF/tags/layout"
	xmlns:tags="urn:jsptagdir:/WEB-INF/tags/tags"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:mtl="urn:jsptld:/WEB-INF/taglibs/mytaglib.tld"
	xmlns:output="urn:jsptagdir:/WEB-INF/tags/export/bibtex"> 
	
<jsp:directive.page contentType="application/json; charset=UTF-8" language="java" pageEncoding="UTF-8" session="true" />

<c:if test="${not empty command.callback}">
   <c:out value="${command.callback}"/> (
</c:if>


{  
   "types" : {
      "Bookmark" : {
         "pluralLabel" : "Bookmarks"
      },
      "Publication" : {
         "pluralLabel" : "Publications"
      },
      "Tag" : {
         "pluralLabel" : "Tags"
      }
   },
   
   "properties" : {
      "count" : {
         "valueType" : "number"
      },
      "date" : {
         "valueType" : "date"
      },
      "url" : {
         "valueType" : "url"
      },
      "id" : {
         "valueType" : "url"
      },
      "tags" : {
         "valueType" : "item"
      },
      "user" : {
         "valueType" : "item"
      }      
   }
   
}

<c:if test="${not empty command.callback}">
   )
</c:if> 

 </jsp:root>