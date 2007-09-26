<%@include file="include_jsp_head.jsp"%>

<%@include file="/boxes/admin/login.jsp"%>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="admin groups" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/admin">admin&nbsp;<img src="/resources/image/box_arrow.png"></a></h1>

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp"%>


<div id="general">

  <%@include file="/boxes/admin/navi.jsp"%>

  <p>
  <form action="/AdminHandler" method="POST">
    <input type="hidden" name="ckey" value="${ckey}"/>
    Aktion: <input type="text" name="action" value="delete dblp duplicates"/> 
    <input type="submit"/>
  </form>
  </p>
  
  Results:
  <ul>
    <li>deletedDuplicatesCtr: <c:out value="${deletedDuplicatesCtr}"/>
  </li>


</div>


<%@ include file="footer.jsp"%>
