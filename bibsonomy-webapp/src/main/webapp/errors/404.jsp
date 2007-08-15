<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="error code 404" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: error code 404</h1> 

<p id="general">
Sorry, this page does not exist on our server.
</p>

<%@ include file="/footer.jsp" %>
