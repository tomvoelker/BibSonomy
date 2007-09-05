<%@include file="/include_jsp_head.jsp" %>
<%
  // TODO: das ist nicht gerade schön, denn es wird in ResourceHandler schon gemacht -> evtl. dort
  // den Parameter ändern?
  // an sich überflüssig, denn wir müssen die ganzen related~ Sachen eh in ein Servlet auslagern,
  // da ansonsten Tag-Intersection-Anfragen schwer zu realisieren sind ...
  String requTag = request.getParameter("requTag").replace(' ','+'); 
  %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>


<%-- include HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="tag::${param.requTag}" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: <a href="/tags">tag</a> :: 
<form action="/tag/" method="GET" class="smallform">
  <input id="inpf" type="text" size="20" name="tag" value="<c:out value='${param.requTag}'/>"/>
  <span style="font-size: 65%">
  <c:choose>
	<c:when test="${not empty param.order && param.order eq 'folkrank'}">
		order by (<a href="/tag/<mtl:encode value='${param.requTag}'/>">date</a> | folkrank)
		<input type="hidden" name="order" value="folkrank"/>
	</c:when>
	<c:otherwise>
		order by (date | <a href="/tag/<mtl:encode value='${param.requTag}'/>?order=folkrank">folkrank</a>)
	</c:otherwise> 
  </c:choose>
  </span>
</form>
</h1> 

<%@include file="/boxes/navi.jsp"%>     <%-------------------------- Navigation -----------------------%>
<div id="outer">
<%@include file="/boxes/bookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
<%@include file="/boxes/bibtex.jsp"%>   <%-------------------------- BibTeX     -----------------------%>
<%@include file="/boxes/itemcount.jsp" %>             
</div>

<ul id="sidebar">
	<li><a href="/concept/tag/<mtl:encode value='${param.requTag}'/>"><c:out value="${param.requTag}"/></a> as concept from all users</li>
    <br/>
  <%@include file="/boxes/tags/relatedtags.jsp"%>
</ul>


<%@ include file="/footer.jsp" %>