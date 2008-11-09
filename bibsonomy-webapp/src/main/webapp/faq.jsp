<%@include file="include_jsp_head.jsp" %>

<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="faq" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/faq"><img src="/resources/image/box_arrow.png">&nbsp;faq</a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<%@include file="/documentation/faq/bookbox.jsp" %> 

<script type="text/javascript">
maximizeById("bookbox");
</script>

<%@ include file="footer.jsp" %>