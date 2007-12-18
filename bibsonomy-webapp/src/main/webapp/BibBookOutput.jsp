<% response.setHeader("Content-Disposition","inline; filename=bookmarks.bib"); %><%@ page contentType="text/plain"%><%@ page pageEncoding="UTF-8" %><%@ page session="true" %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>
<c:forEach var="book" items="${ResourceBean.bookmarks}">
@misc{${book.urlHost},
   title    = {${book.title}}, 
   url      = {\url{${book.url}}},
   biburl   = {${projectHome}url/${book.hash}},
   keywords = {${book.tagString}}
}

</c:forEach>
