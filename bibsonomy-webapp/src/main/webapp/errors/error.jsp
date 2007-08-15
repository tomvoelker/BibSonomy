<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="error" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: error</h1> 

<div id="general">
Sorry, we could not process your request because of the following error:
<pre>
${error}
</pre>
</div>

<%@ include file="/footer.jsp" %>