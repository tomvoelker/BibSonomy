<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="concept::user::${param.requUser}::${param.requTag}" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: concept :: user :: 
<a href="/user/<mtl:encode value='${param.requUser}'/>"><c:out value='${param.requUser}'/></a> ::
<form action="/concept/user/<mtl:encode value='${param.requUser}'/>" method="GET" class="smallform">
  <input type="text" id="inpf" size="20" name="tag" value="<c:out value='${param.requTag}'/>"/>
</form>
</h1> 

<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>
<div id="outer">
  <%@include file="/boxes/bookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
  <%@include file="/boxes/bibtex.jsp"%>   <%-------------------------- BibTeX     -----------------------%>
  <%@include file="/boxes/itemcount.jsp" %>
</div>

<ul id="sidebar">
    <li><a href="/tag/<mtl:encode value='${param.requTag}'/>"><c:out value="${param.requTag}"/></a> from all users</li>
    <li><a href="/user/<mtl:encode value='${param.requUser}'/>/<mtl:encode value='${param.requTag}'/>"><c:out value="${param.requTag}"/></a> as tag</li>

    <%-- @include file="/boxes/relatedusertags.jsp" --%>

    <%@include file="/boxes/tags/usersrelations.jsp" %>
   
    <c:set var="markSuperTags" value="true"/>
    <%@include file="/boxes/tags/userstags.jsp"%>
</ul>

<%@ include file="/footer.jsp" %>