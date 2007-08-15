<%@include file="/include_jsp_head.jsp" %>

<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="database error" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: database error</h1> 


<p id="general">
Sorry, database error.
</p>

<%@ include file="/footer.jsp" %>
