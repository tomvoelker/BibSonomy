<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="concept::${param.requTag}" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<!-- 2008/12/18, fei: removed attribute 'rel="Start"' from link as it brakes chrome menus -->
<h1 id="path"><a href="/">${projectName}</a> :: <a href="#" rel="path_menu"><img src="/resources/image/box_arrow.png">&nbsp;concept</a> ::
<form action="/concept/tag/" method="GET" class="smallform">
  <input type="text" id="inpf" size="20" name="tag" value="<c:out value='${param.requTag}'/>"/>
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
    <c:if test="${not empty user.name}">
    	<li><a href="/concept/user/<mtl:encode value='${user.name}'/>/<mtl:encode value='${param.requTag}'/>"><c:out value="${param.requTag}"/></a> as concept from <c:out value="${user.name}"/></li>
   		<li><a href="/user/<c:out value="${user.name}"/>/<mtl:encode value='${param.requTag}'/>"><c:out value="${param.requTag}"/></a> as tag from <c:out value="${user.name}"/></li>    
    </c:if>
    <li style="margin-bottom: 1ex;"><a href="/tag/<mtl:encode value='${param.requTag}'/>"><c:out value="${param.requTag}"/></a> as tag from all users</li>
    
    <%@include file="/boxes/tags/conceptrelations.jsp" %>
   
    <c:set var="markSuperTags" value="true"/>    
</ul>
</div>
<script type="text/javascript">
   $("#sidebarroundcorner").corner("round bottom 15px").corner("round tl 15px");
</script>

<%@ include file="/footer.jsp" %>