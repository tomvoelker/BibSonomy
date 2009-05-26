<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="error code 503" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: error code 503</h1> 
<div id="welcomeTop">A blue social bookmark and publication sharing system.</div> 
</div>
</div>

<div id="error">
Sorry, the service is not available: ${message}.
</div>

<%@ include file="/footer.jsp" %>
