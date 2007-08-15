<%@include file="include_jsp_head.jsp" %>

<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="success" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: success</h1> 


<%@include file="/boxes/navi.jsp" %> 

<div id="general">

<h2>${success}</h2>

</div>
<%@ include file="footer.jsp" %>