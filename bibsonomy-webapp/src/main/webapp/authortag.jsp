<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>
<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/>

<%-- include HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="author::${param.requAuthor}::${param.requTag}" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a href="#" rel="path_menu">author&nbsp;<img src="/resources/image/box_arrow.png"></a> :: 
<a href="/author/<mtl:encode value='${param.requAuthor}'/>"/><c:out value='${param.requAuthor}'/></a> ::
<form action="/author/<mtl:encode value='${param.requAuthor}'/>" method="GET" class="smallform">
  <input type="text" size="20" name="tag" value="<c:out value='${param.requTag}'/>"/>
</form>
</h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

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