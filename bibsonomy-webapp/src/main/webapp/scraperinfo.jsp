<%@include file="include_jsp_head.jsp" %>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="scraperinfo" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/">${projectName}</a> :: <a rel="path_menu" href="/scraperinfo">scraperinfo&nbsp;<img src="/resources/image/box_arrow.png"></a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<div id="general">
  <h2>Scrapers used within ${projectName}</h2>
  
  <jsp:useBean id="factory" class="org.bibsonomy.scraper.KDEScraperFactory" scope="request"/>

  <dl><c:forEach var="scraper" items="${factory.scraper.scraper}">
    <dt style="padding-top: 1em;">${scraper.class.name}:</dt> 
    <dd>${scraper.info}</dd>
  </c:forEach></dl>

  </div>

<%@ include file="footer.jsp" %>