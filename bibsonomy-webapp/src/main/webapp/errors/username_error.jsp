
<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="username error" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: username error</h1> 
<div id="welcomeTop">A blue social bookmark and publication sharing system.</div> 
</div>
</div>

<div id="error">
Sorry, this username does not exist.
</div>


<%@ include file="/footer.jsp" %>
