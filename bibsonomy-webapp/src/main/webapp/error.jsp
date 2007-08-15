<%@include file="include_jsp_head.jsp" %>

<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="error" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/">${projectName}</a> :: error</h1> 


<%@include file="/boxes/navi.jsp" %> 

<div id="general">

<h2>${error}</h2>

</div>
<%@ include file="footer.jsp" %>