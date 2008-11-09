<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="group::${param.requGroup}::${param.requTag}" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/groups"><img src="/resources/image/box_arrow.png">&nbsp;group</a> :: 
<a href="/group/<mtl:encode value='${param.requGroup}'/>"><c:out value="${param.requGroup}"/></a> ::
<form action="/group/<mtl:encode value='${param.requGroup}'/>" method="GET" class="smallform">
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

<div id="sidebarroundcorner" >
<ul id="sidebar">
  <li><a href="/tag/<mtl:encode value='${param.requTag}'/>"><c:out value="${param.requTag}"/></a> as tag from all users</li>
  <li style="margin-bottom: 1ex;"><a href="/concept/tag/<mtl:encode value='${param.requTag}'/>"><c:out value="${param.requTag}"/></a> as concept from all users</li>
     	
  <%@include file="/boxes/tags/relatedgrouptags.jsp"%>
  <%@include file="/boxes/tags/grouptags.jsp" %>  
</ul>
</div>
<script type="text/javascript">
   $("#sidebarroundcorner").corner("round bottom 15px").corner("round tl 15px");
</script>

<%@ include file="/footer.jsp" %>
