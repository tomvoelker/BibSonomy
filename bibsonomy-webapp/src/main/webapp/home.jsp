<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%--HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="" />
</jsp:include>


<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> ::
<form id="specialsearch" method="get" action="/specialsearch">
  <select name="scope" size="1" id="scope">
    <option value="tag">tag</option>
    <option value="user">user</option>
    <option value="group">group</option>
    <option value="author">author</option> 
    <option value="concept">concept</option> 
    <option value="all">search:all</option> 
    <c:if test="${not empty user.name}">
    	<option value="user:<c:out value='${user.name}'/>">search:<c:out value="${user.name}"/></option> 
    </c:if>       
  </select>  ::
  <input type="text" id="inpf" name="q" size="30"/>  
</form>
</h1>



<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigatopm -----------------------%>
<div id="outer">

<c:if test="${empty user.name}">
<div id="general">
<p class="smalltext">
${projectName} is a system for sharing bookmarks and lists of literature. When 
discovering a bookmark or a publication on the web, you can store it on our 
server. You can add tags to your entry to retrieve it more easily. This is 
very similar to the bookmarks/favorites that you store within your browser. 
The advantage of ${projectName} is that you can access your data from  whereever 
you are. Furthermore, you can discover more bookmarks and publications from 
your friends and other people.
</p>
<p class="smalltext">
This page shows you the latest updates of ${projectName}. Why don't you just 
try it yourself? After a free <a href="/register">registration</a>, you can 
organise your own bookmarks and publications, and discover related entries.
</p>
</div>
</c:if>


<%@include file="/boxes/bookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
<%@include file="/boxes/bibtex.jsp"%>   <%-------------------------- BibTeX     -----------------------%> 
</div>

<%-------------------------- Busy Tags -----------------------%>
<ul id="sidebar">
  <%@include file="/boxes/tags/busytags.jsp"%>
</ul>

<%@ include file="/footer.jsp" %>