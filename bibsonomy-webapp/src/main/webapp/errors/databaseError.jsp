<%@include file="/include_jsp_head.jsp" %>

<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="database error" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${properties['project.name']}</a> :: database error</h1> 
<div id="welcomeTop">A blue social bookmark and publication sharing system.</div> 
</div>
</div>

<div id="error">
Sorry, database error.
</div>

<%@ include file="/footer.jsp" %>
