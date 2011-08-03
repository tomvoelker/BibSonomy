<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="error" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${properties['project.name']}</a> :: error</h1>
<div id="welcomeTop">A blue social bookmark and publication sharing system.</div> 
</div>
</div>

<div id="error">
	Sorry, we could not process your request because of the following error:
	<pre class="error">
	${error}
	</pre>
</div>

<%@ include file="/footer.jsp" %>