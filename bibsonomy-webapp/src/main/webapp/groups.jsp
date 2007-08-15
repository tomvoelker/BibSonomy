<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="groups" />
</jsp:include>


<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: <a href="/groups">groups</a> :: 
<form action="/group/" method="GET" class="smallform">
  <input type="text" size="20" name="group"/>
</form>
</h1> 

<%@include file="/boxes/navi.jsp" %> 

<%-- do SQL query to get groups --%>
<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/>   
<sql:query var="rs" dataSource="${dataSource}">
  SELECT i.group_name, u.user_realname, u.user_homepage FROM groupids i, user u WHERE i.group > 2 AND i.group_name = u.user_name ORDER BY i.group_name
  <%-- TODO: add comment, maybe order by date? --%>
</sql:query>

<div id="general">

  <ul id="groups">
    <c:forEach var="row" items="${rs.rows}">
       <li>

         <strong>
          group :: 
          <a href="/group/<mtl:encode value='${row.group_name}'/>"><c:out value="${row.group_name}"/></a> ::
         </strong>
         

         <%-- show Name of group  --%>
         <c:if test="${!empty row.user_realname}">
           <c:out value="${row.user_realname} "/>
         </c:if>

         <span class="bmaction">         
         <%-- show URL of group  --%>
         <c:if test="${!empty row.user_homepage}"><a href="<c:out value='${row.user_homepage}'/>"> URL </a></c:if>

         <%-- show form to join group --%>
         <c:if test="${not empty user.name}"><a href="/join_group?group=<mtl:encode value='${row.group_name}'/>">join</a></c:if>
         </span>

       </li>
    </c:forEach>
  </ul>
  
</div>


<%@ include file="/footer.jsp" %>