<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="viewable::${param.requGroup}::${param.requTag}" />
</jsp:include>


<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: viewable :: 
<a href="/viewable/<mtl:encode value='${param.requGroup}'/>"><c:out value="${param.requGroup}"/></a> ::
<form action="/viewable/<mtl:encode value='${param.requGroup}'/>" method="GET" class="smallform">
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
  <%@include file="/boxes/tags/relatedviewabletags.jsp"%>
  <%@include file="/boxes/tags/viewabletags.jsp" %>
</ul> 


<%@ include file="/footer.jsp" %>