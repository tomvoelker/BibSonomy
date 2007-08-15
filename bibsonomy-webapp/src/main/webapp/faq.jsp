<%@include file="include_jsp_head.jsp" %>

<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="faq" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: <a href="/faq">faq</a></h1> 


<%@include file="/boxes/navi.jsp" %> 

<%@include file="/documentation/faq/bookbox.jsp" %> 

<script type="text/javascript">
maximizeById("bookbox");
</script>

<%@ include file="footer.jsp" %>