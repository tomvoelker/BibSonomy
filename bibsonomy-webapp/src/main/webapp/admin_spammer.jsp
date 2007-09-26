<%@include file="include_jsp_head.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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

<%-- ------------------------ change settings -------------------------- --%>
<jsp:useBean id="adminBean" class="beans.AdminBean" scope="request">
  <jsp:setProperty name="adminBean" property="*"/>
</jsp:useBean>

<% adminBean.queryDB(); %> <%-- write data to database (if neccessary) --%>


<p>
<c:forEach var="info" items="${adminBean.infos}">Info: ${info}<br></c:forEach>
<c:forEach var="error" items="${adminBean.errors}">Error: ${error}<br></c:forEach>
</p>

<%-- do SQL query to get new users --%>

<%--<h2> You are logged in as ${user.name}</h2>--%>

<c:choose>
<c:when test="${!empty param.next}">
	<c:set var="limit" value="${param.next+50}"/>
</c:when>
<c:otherwise>
	<c:set var="limit" value="1"/>
</c:otherwise>
</c:choose> 

<c:choose>
	<c:when test="${user.name == 'hotho'}">
		<c:set var="evaluation" value="evaluator1"/>
	</c:when>
	<c:when test="${user.name == 'beate'}">
		<c:set var="evaluation" value="evaluator2"/>
	</c:when>
	<c:when test="${user.name == 'schmitz'}">
		<c:set var="evaluation" value="evaluator3"/>
	</c:when>
	<c:otherwise>
		<c:set var="evaluation" value="evaluator1"/>
	</c:otherwise>
</c:choose> 

<b>Your evaluation contributes to <c:out value="${evaluation}"/></b>

<hr>

This offset: <b><c:out value="${limit}"/></b><br>
<table>
<tr>
<th>
<form action="/admin_spammer.jsp">
<input type="hidden" name="next" value="${limit}"/> 
<input value="Next 20" type="submit"/>
</form>
</th>
<th>
<form action="/admin_spammer.jsp">
<input type="hidden" name="next" value="${limit-100}"/> 
<input value="Back" type="submit"/>
</form>
</th>
<th>
<form action="/admin_spammer.jsp">
Change your offset 
<input type="text" name="next" value="${limit}"/> 
<input value="Change" type="submit"/>
</form>
</th>
</tr>
</table>
<br> 

<h2> List of new users </h2>

<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/>   
<sql:query var="rs" dataSource="${dataSource}">
  SELECT user.user_name, spammer, ip_address, ${evaluation} AS evaluator, user_realname, user_email, reg_date, count(tas.tag_name) AS num_tag, count(tas.content_id) AS num_url 
    FROM user
      LEFT JOIN tas USING (user_name)
      LEFT JOIN evaluation USING (user_name)
    GROUP BY tas.user_name
    ORDER BY reg_date DESC
    LIMIT 50 OFFSET ?
  <sql:param value="${limit}" />
</sql:query>
  
  <table>
    <tr><th>Is it a Spammer?</th><th>Username</th><th>Your evaluation</th><th>DB Decision</th><th>IP</th><th>Realname</th><th>E-Mail</th><th>Registration</th><th># Tags</th><th># Urls</th><th>5 most used tags</th></tr>
      <c:forEach var="row" items="${rs.rows}">

    <c:choose>
		<c:when test="${row.evaluator}">
          <tr style="background-color: #ffeeee;">
		</c:when>
        <c:otherwise>
        	<tr>
        </c:otherwise>
    </c:choose>

       <td>
         <a href="/admin_spammer.jsp?user=<mtl:encode value='${row.user_name}'/>&action=flag_spammer_evaluator&evaluator=<mtl:encode value='${evaluation}'/>">Yes</a>
         <a href="/admin_spammer.jsp?user=<mtl:encode value='${row.user_name}'/>&action=unflag_spammer_evaluator&evaluator=<mtl:encode value='${evaluation}'/>">No</a>
       </td>
       <c:set var="user" value='${row.user_name}'/>
       <td><a href="/user/<mtl:encode value='${row.user_name}'/>"><c:out value="${row.user_name}"/></a></td>
       <td><c:out value="${row.evaluator}"/></td>
       <td><c:out value="${row.spammer}"/></td>
       
       <c:set var="ip" value="${row.ip_address}"/>
       <td><c:out value="${fn:substringBefore(ip, ',')}"/></td>
       <td><c:out value="${row.user_realname}"/></td>
       <td><c:out value="${row.user_email}"/></td>
       <td><c:out value="${row.reg_date}"/></td>
       <td><c:out value="${row.num_tag}"/></td>
       <td><c:out value="${row.num_url}"/></td>
		<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/>   
		<sql:query var="rs" dataSource="${dataSource}">
  			select tag_name, count(tag_name) as num from tas where tas.user_name = ? group by tag_name order by num desc limit 5;
  		<sql:param value="${user}" />
  		</sql:query>
  	   <td>
  	   	<c:forEach var="row" items="${rs.rows}">
  	   	<c:out value="${row.tag_name}"/>
  	   	</c:forEach>
		</c:forEach>
       </td>	
      </tr>
  </table>
</div>


<%@ include file="footer.jsp"%>
