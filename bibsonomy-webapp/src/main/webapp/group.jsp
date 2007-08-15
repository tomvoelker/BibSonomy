<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="group::${param.requGroup}" />
</jsp:include>


<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: <a href="/groups">group</a> :: 
<a href="/group/<mtl:encode value='${param.requGroup}'/>"><c:out value="${param.requGroup}"/></a> ::
<form action="/group/<mtl:encode value='${param.requGroup}'/>" method="GET" class="smallform">
  <input type="text" size="20" name="tag" id="inpf" value="<c:out value='${param.requTag}'/>"/>
</form>
</h1> 

 
<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>
<div id="outer">
<%@include file="/boxes/bookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
<%@include file="/boxes/bibtex.jsp"%>   <%-------------------------- BibTeX     -----------------------%>
<%@include file="/boxes/itemcount.jsp" %>
</div>

<ul id="sidebar">
  <%@include file="/boxes/groupmembers.jsp" %>  
  <%@include file="/boxes/tags/grouptags.jsp" %>  
</ul>

<%@ include file="/footer.jsp" %>