<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="error code 500" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: error code 500</h1> 

<p id="general">
Sorry, an internal error has occured.
</p>

<%@ include file="/footer.jsp" %>
