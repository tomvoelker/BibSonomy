<%@include file="include_jsp_head.jsp" %>

<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="success" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/">${projectName}</a> :: <a href="#" rel="path_menu"><img src="/resources/image/box_arrow.png">&nbsp;success</a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<div id="general">

<h2>${success}</h2>

</div>
<%@ include file="footer.jsp" %>