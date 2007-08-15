<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>
<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/>

<%-- include HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="author::${param.requAuthor}" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: author :: 
<form action="/author/" method="GET" class="smallform">
  <input id="inpf" type="text" size="20" name="author" value="<c:out value='${param.requAuthor}'/>"/>
</form>
</h1> 

<%@include file="/boxes/navi.jsp"%>     <%-------------------------- Navigation -----------------------%>
<div id="outer">
<%@include file="/boxes/bookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
<%@include file="/boxes/bibtex.jsp"%>   <%-------------------------- BibTeX     -----------------------%>
<%@include file="/boxes/itemcount.jsp" %>             
</div>

<ul id="sidebar">
  <%@include file="/boxes/tags/authortags.jsp"%>
</ul>


<%@ include file="/footer.jsp" %>