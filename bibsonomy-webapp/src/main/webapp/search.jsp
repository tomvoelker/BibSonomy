<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="search::${search}" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path">
  <a href="/" rel="Start">${projectName}</a> :: <a href="#" rel="path_menu"><img src="/resources/image/box_arrow.png">&nbsp;search</a> :: 
  <form class="smallform" method="get" action="/search">
    <input type="text" name="q" id="inpf" value="<c:out value='${search}'/>" size="30"/>
  </form>
</h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>
<div id="outer">

<c:if test="${warning}">
  <p class="errmsg">Sorry, you can search only within the entries of <strong>one</strong> user.</p>
</c:if>

<%@include file="/boxes/bookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
<%@include file="/boxes/bibtex.jsp"%>   <%-------------------------- BibTeX     -----------------------%>
<%@include file="/boxes/itemcount.jsp" %>
</div>

<%@ include file="/footer.jsp" %>