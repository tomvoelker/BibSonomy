<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="friend" />
</jsp:include>


<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/friend">friend&nbsp;<img src="/resources/image/box_arrow.png"></a> :: 
<a href="/friend/<mtl:encode value='${param.requUser}'/>"><c:out value="${param.requUser}"/></a> ::
<form action="/friend/<mtl:encode value='${param.requUser}'/>" method="GET" class="smallform">
  <input type="text" size="20" id="inpf" name="tag" value="<c:out value='${param.requTag}'/>"/>
</form>
</h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%> 
<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>
<div id="outer">
<%@include file="/boxes/bookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
<%@include file="/boxes/bibtex.jsp"%>   <%-------------------------- BibTeX     -----------------------%>
<%@include file="/boxes/itemcount.jsp" %>
</div>

<ul id="sidebar">
  <%@include file="/boxes/tags/friendtags.jsp" %>  
</ul>

<%@ include file="/footer.jsp" %>