<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="login error" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: login error</h1> 
<div id="welcomeTop">A blue social bookmark and publication sharing system.</div> 
</div>
</div>

<div id="error">
Sorry, you don't have the right to access this site.
<br /><br />
Please <a href="/login">login</a>.
</div>

<%@ include file="/footer.jsp" %>
