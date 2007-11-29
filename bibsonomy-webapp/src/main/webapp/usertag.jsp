<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="user::${param.requUser}::${param.requTag}" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a href="#" rel="path_menu">user&nbsp;<img src="/resources/image/box_arrow.png"></a> :: 
<a href="/user/<mtl:encode value='${param.requUser}'/>"><c:out value='${param.requUser}'/></a> ::
<form action="/user/<mtl:encode value='${param.requUser}'/>" method="GET" class="smallform">
  <input type="text" id="inpf" size="20" name="tag" value="<c:out value='${param.requTag}'/>"/>
</form>
</h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>
<div id="outer">
  <%@include file="/boxes/bookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
  <%@include file="/boxes/bibtex.jsp"%>   <%-------------------------- BibTeX     -----------------------%>
  <%@include file="/boxes/itemcount.jsp" %>
</div>

<ul id="sidebar">    
    <li><a href="/tag/<mtl:encode value='${param.requTag}'/>"><c:out value="${param.requTag}"/></a> as tag from all users</li>
    <c:if test="${not empty user.name}">
    	<li><a href="/concept/user/<mtl:encode value='${param.requUser}'/>/<mtl:encode value='${param.requTag}'/>"><c:out value="${param.requTag}"/></a> as concept from <c:out value="${user.name}"/></li>	
	</c:if>
	<li style="margin-bottom: 1ex;"><a href="/concept/tag/<mtl:encode value='${param.requTag}'/>"><c:out value="${param.requTag}"/></a> as concept from all users</li>
    
    <%@include file="/boxes/tags/relatedusertags.jsp"%>

    <%@include file="/boxes/tags/usersrelations.jsp" %>
  
    <c:set var="markSuperTags" value="true"/>
    <%@include file="/boxes/tags/userstags.jsp"%>
</ul>


<%@ include file="/footer.jsp" %>