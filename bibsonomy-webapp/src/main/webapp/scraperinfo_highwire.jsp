<%@include file="/include_jsp_head.jsp" %>

<%--HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="HighWire Scraper" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/">${projectName}</a> :: <a rel="path_menu" href="/scraperinfo"><img src="/resources/image/box_arrow.png">&nbsp;scraperinfo</a> :: <a href="/scraperinfo_highwire">highwire</a>
</h1>

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigatopm -----------------------%>


<%-- get the highwire linklist from the db --%>
<sql:query var="listInfo" dataSource="${dataSource}">
	SELECT list from highwirelist; 
</sql:query> 

<div id="general">

  <h2>Pages supported by the HighWire scraper</h2>

  <%-- TODO: wird da der HTML-Code 1:1 eingebunden? GEFAHR! --%>
  <ul>
  <c:forEach var="rows" items="${listInfo.rows}">
    ${rows.list}
  </c:forEach>
  </ul>
</div>

<%@ include file="/footer.jsp" %>