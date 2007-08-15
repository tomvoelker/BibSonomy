<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="friend" />
</jsp:include>


<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: <a href="/friend">friend</a> :: 
<form action="/friends/" method="GET" class="smallform">
  <input type="text" size="20" name="user"/>
</form>
</h1> 

 
<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>
<div id="outer">
<%@include file="/boxes/bookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
<%@include file="/boxes/bibtex.jsp"%>   <%-------------------------- BibTeX     -----------------------%>
<%@include file="/boxes/itemcount.jsp" %>
</div>

<ul id="sidebar">
  <%@include file="/boxes/friends.jsp" %>  
</ul>


<%@ include file="/footer.jsp" %>