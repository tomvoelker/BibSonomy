<%@include file="include_jsp_head.jsp" %>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="tags" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/tags">tags&nbsp;<img src="/resources/image/box_arrow.png"></a> ::
<form action="/tag/" method="GET" class="smallform">
  <input type="text" size="20" name="tag" id="inpf" value="<c:out value='${param.requTag}'/>"/>
</form>
</h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<c:set var="items" value="200" />

<div id="general" style="text-align: justify;">
  <%-- Query --%>
  <sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/>   
  <sql:query var="rs" dataSource="${dataSource}">
    SELECT * FROM 
      (
        SELECT tag_name, tag_ctr_public, round(log(tag_ctr_public/40))*25 class 
          FROM tags 
          ORDER BY tag_ctr_public 
          DESC LIMIT ${items}
      ) AS a 
      ORDER BY lower(tag_name)
  </sql:query>


  <%-- 1. part --%>
  <span class="tags_first">
  <c:forEach var="row" items="${rs.rows}" begin="0" end="${items}">
     <a style="font-size:${row.class}%" title="${row.tag_ctr_public} posts" href="/tag/${row.tag_name}" style="padding:0.3em">${row.tag_name}</a>
  </c:forEach>
  </span>
 
<%@ include file="footer.jsp" %>