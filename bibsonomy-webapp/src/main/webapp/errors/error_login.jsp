<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="login error" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: login error</h1> 


<p id="general">
Sorry, you don't have the right to access this site.
<br>
Please <a href="/login">login</a>.
</p>

<%@ include file="/footer.jsp" %>
