<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="viewable::${param.requGroup}::${param.requTag}" />
</jsp:include>


<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a href="#" rel="path_menu">viewable&nbsp;<img src="/resources/image/box_arrow.png"></a> :: 
<a href="/viewable/<mtl:encode value='${param.requGroup}'/>"><c:out value="${param.requGroup}"/></a> ::
<form action="/viewable/<mtl:encode value='${param.requGroup}'/>" method="GET" class="smallform">
  <input type="text" size="20" name="tag" id="inpf" value="<c:out value='${param.requTag}'/>"/>
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
  <%@include file="/boxes/tags/relatedviewabletags.jsp"%>
  <%@include file="/boxes/tags/viewabletags.jsp" %>
</ul> 


<%@ include file="/footer.jsp" %>