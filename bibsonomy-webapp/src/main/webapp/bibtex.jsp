<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="bibtex::${ResourceBean.title}" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path">
  <a href="/" rel="Start">${projectName}</a> :: <a href="#" rel="path_menu">bibtex&nbsp;<img src="/resources/image/box_arrow.png"></a> :: 
  <form class="smallform" method="get" action="/search">
    <input type="text" name="q" value="<mtl:bibclean value='${ResourceBean.title}'/>" size="30"/>
  </form>
</h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>

<div id="outer">
<%@include file="/boxes/bibtex.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
<%@include file="/boxes/itemcount.jsp" %>
</div>

<ul id="sidebar">
  <%@include file="/boxes/tags/bibtextags.jsp" %>
</ul>

<%@ include file="/footer.jsp" %>