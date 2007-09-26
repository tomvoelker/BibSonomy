<%@include file="include_jsp_head.jsp" %>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="popular" />
</jsp:include>
 
 <%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/popular">popular&nbsp;<img src="/resources/image/box_arrow.png"></a></h1> 
 
<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>
<div id="outer">
<%@include file="/boxes/popularUserBookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
<%@include file="/boxes/popularUserBibtex.jsp"%>    <%-------------------------- BibTeX     -----------------------%>
</div>

<ul id="sidebar">
  <%@include file="/boxes/tags/busytags.jsp"%>
</ul>

<%@ include file="footer.jsp" %>