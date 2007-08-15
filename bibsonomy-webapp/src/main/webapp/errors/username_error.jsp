
<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="username error" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: username error</h1> 

<p id="general">
Sorry, this username does not exist.
</p>


<%@ include file="/footer.jsp" %>
