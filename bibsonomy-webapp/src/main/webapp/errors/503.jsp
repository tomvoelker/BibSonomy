<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="error code 503" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: error code 503</h1> 

<p id="general">
Sorry, the service is not available: ${message}.

</p>

<%@ include file="/footer.jsp" %>
