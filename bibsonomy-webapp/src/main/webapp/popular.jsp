<%@include file="include_jsp_head.jsp" %>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="popular" />
</jsp:include>
 
 <%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: <a href="/popular">popular</a></h1> 
 
 
<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>
<div id="outer">
<%@include file="/boxes/bookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
<%@include file="/boxes/bibtex.jsp"%>    <%-------------------------- BibTeX     -----------------------%>
</div>

<ul id="sidebar">
   <%@include file="/boxes/tags/busytags.jsp"%>
</ul>

<%@ include file="footer.jsp" %>