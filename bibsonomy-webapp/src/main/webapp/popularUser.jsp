<%@include file="include_jsp_head.jsp" %>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="popular" />
</jsp:include>
 
 <%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/">${projectName}</a> :: <a rel="path_menu" href="/popular"><img src="/resources/image/box_arrow.png">&nbsp;popular</a></h1> 
 
<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>
<div id="outer">
<%@include file="/boxes/popularUserBookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
<%@include file="/boxes/popularUserBibtex.jsp"%>    <%-------------------------- BibTeX     -----------------------%>
</div>

<div id="sidebarroundcorner" >
<ul id="sidebar">
  <%@include file="/boxes/tags/busytags.jsp"%>
</ul>
</div>
<script type="text/javascript">
   $("#sidebarroundcorner").corner("round bottom 15px").corner("round tl 15px");
</script>

<%@ include file="footer.jsp" %>