
<%@include file="/include_jsp_head.jsp"%>


<%@include file="/boxes/admin/login.jsp"%>

<script type="text/javascript" src="/resources/javascript/marksame.js"></script>
<link rel="stylesheet" type="text/css" href="/resources/css/spammer.css">

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="admin" />
</jsp:include>

<script type="text/javascript">
  $(document).ready(function(){
		$("#acl_usergroup").autocomplete("admin_suggest.jsp?type=2", {
			width: 260,
			selectFirst: true
		});
  });
</script>


<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/?filter=no" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/admin.jsp"><img src="/resources/image/box_arrow.png">&nbsp;admin</a></h1>

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp"%>

<%-- ------------------------ change settings -------------------------- --%>
<jsp:useBean id="adminBean" class="beans.AdminBean" scope="request">
  <jsp:setProperty name="adminBean" property="*"/>
  <jsp:setProperty name="adminBean" property="currUser" value="${user.name}"/>
</jsp:useBean>

<% adminBean.queryDB(); %> <%-- write data to database (if neccessary) --%>

<%-------------------------- Content -----------------------%>
<div id="general">
  <%@include file="/boxes/admin/navi.jsp"%>
  <p style="font-weight: bold; color: #ff0000; ">
    <c:forEach var="info" items="${adminBean.infos}">Info: ${info}<br></c:forEach>
    <c:forEach var="error" items="${adminBean.errors}">Error: ${error}<br></c:forEach>
  </p>


<hr/>

<%-- group management  --%>
<h2>add a group to the system</h2>

<%--  group form  --%>
<form action="/admin.jsp">
  <table>
    <tr>
      <td>user name</td><td><input type="text" name="user" id="acl_usergroup" class="ac_spam"/></td></tr>
    <tr>
      <td>privacy</td>
      <td>
        <select name="privlevel">
          <option value="0">member list public</option>
          <option value="1">member list hidden</option>
          <option value="2">members can list members</option>
        </select>
      </td>
    </tr>
  
    <tr>
     <td></td>
     <td>
       <input type="hidden" name="action" value="add_group"/>
       <input type="submit"/>
     </td>
   </tr>
   </table>
</form>

<hr/>

<%-- do SQL query to get groups --%> 
<sql:query var="rs" dataSource="${dataSource}">
  SELECT i.group_name, u.user_realname, u.user_homepage FROM groupids i, user u WHERE i.group > 2 AND i.group_name = u.user_name ORDER BY i.group_name
</sql:query>

<h2>existing groups</h2>
  <ul>
    <c:forEach var="row" items="${rs.rows}">
       <li><a href="/group/<c:out value='${row.group_name}'/>"><c:out value="${row.group_name}"/></a></li>
    </c:forEach>
  </ul>

</div>


<%@ include file="footer.jsp"%>