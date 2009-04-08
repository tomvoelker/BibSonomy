<%@include file="include_jsp_head.jsp"%>


<%------------No admin restrictions -----------------------%>
<%@include file="/boxes/admin/login.jsp"%> 


<link rel="stylesheet" type="text/css" href="/resources/css/spammer.css">
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- include HTML header --%>

<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="Evaluation page" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/">${projectName}</a> :: <a rel="path_menu" href="/admin"><img src="/resources/image/box_arrow.png">&nbsp;admin</a></h1>

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

<div id="general">

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
	<c:set var="limit" value="0"/>
</c:otherwise>
</c:choose> 

<c:choose>
	<c:when test="${user.name == 'hotho'}">
		<c:set var="evaluation" value="evaluator1"/>
	</c:when>
	<c:when test="${user.name == 'beate'}">
		<c:set var="evaluation" value="evaluator2"/>
	</c:when>
	<c:when test="${user.name == 'jaeschke'}">
		<c:set var="evaluation" value="evaluator3"/>
	</c:when>
	<c:when test="${user.name == 'dbenz'}">
		<c:set var="evaluation" value="evaluator4"/>
	</c:when>
	<c:when test="${user.name == 'folke'}">
		<c:set var="evaluation" value="evaluator5"/>
	</c:when>
	<c:when test="${user.name == 'eval_extern_w1'}">
		<c:set var="evaluation" value="evaluator6"/>
	</c:when>
	<c:when test="${user.name == 'eval_extern_w2'}">
		<c:set var="evaluation" value="evaluator7"/>
	</c:when>
	<c:otherwise>
		<c:set var="evaluation" value="evaluator8"/>
	</c:otherwise>
</c:choose> 


<b>Your evaluation contributes to <c:out value="${evaluation}"/></b><br/><br/>

Start with user number: <b><c:out value="${limit}"/></b><br>
<table>
<tr>
<th>
<c:choose>
<c:when test='${limit-50 < 0}'>
<form action="/admin_spammer.jsp">
<input type="hidden" name="next" value="${limit-50}"/> 
<input value="Back" type="submit"/>
</form>
</c:when>
<c:otherwise>
<form action="/admin_spammer.jsp">
<input type="hidden" name="next" value="${limit-100}"/> 
<input value="Back" type="submit"/>
</form>
</c:otherwise>
</c:choose>
</th>

<th>
<form action="/admin_spammer.jsp">
<input type="hidden" name="next" value="${limit}"/> 
<input value="Next" type="submit"/>
</form>
</th>


</tr>
</table>
<br/> 

<h2> List of new users </h2>

	<script type="text/javascript" src="${resdir}/javascript/spammer.js">&amp;nbsp;</script>				
			<script type="text/javascript"
				src="${resdir}/javascript/ajax-dynamic-content.js">&amp;nbsp;</script>
			<script type="text/javascript" src="${resdir}/javascript/ajax.js">&amp;nbsp;</script>
			<script type="text/javascript"
				src="${resdir}/javascript/ajax-tooltip.js">&amp;nbsp;</script>
			<script type="text/javascript"
				src="${resdir}/javascript/marksame.js">;</script>


<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/>   
<sql:query var="rs" dataSource="${dataSource}">
 SELECT user.user_name, ip_address, ${evaluation} AS evalC, user_realname, user_email, reg_date FROM user
    LEFT JOIN evaluation USING (user_name)
    WHERE user_id < 2000
    GROUP BY evaluation.user_id
    ORDER BY evaluation.user_id asc
    LIMIT 50 OFFSET ${limit}
</sql:query>

<%-- do SQL query to get new users 
 SELECT user.user_name, spammer, ip_address, ${evaluation} AS evaluator, user_realname, user_email, reg_date, count(tas.tag_name) AS num_tag, count(tas.content_id) AS num_url 
    FROM user
      LEFT JOIN tas USING (user_name)
      LEFT JOIN evaluation USING (user_name)
    
    
 --%>
  
  <table class="spammertable">
    <tr><th>Spammer?</th><th>Username</th><th>IP</th><th>Realname</th><th>E-Mail</th><th>Registration</th><th># Tags</th><th>5 most used tags</th></tr>
     <c:set var="result" value="${rs.rows}" />
     <c:forEach var="row" items="${result}" varStatus="status">
     <c:set var="rIndex" value="${status.index}"/>
  
     <c:choose>
	        <c:when test="${row.evalC ==1}">
	          <tr id="ul<c:out value='${rIndex}'/>" class="spammer">
	        </c:when>
	        <c:otherwise>
	        <c:choose>
	        <c:when test="${row.evalC ==0}">
	          <tr id="ul<c:out value='${rIndex}'/>" class="nonspammer">
	        </c:when>
	        <c:otherwise><tr id="ul<c:out value='${rIndex}'/>"> </c:otherwise>
	          </c:choose>
	        </c:otherwise>
	 </c:choose>
	
	
	<%--     
    <c:choose>
		<c:when test="${row.evaluator}">
          <tr style="background-color: #ffeeee;">
		</c:when>
        <c:otherwise>
        	<tr>
        </c:otherwise>
    </c:choose>--%>

       <td>
       <c:set var="userIndex" value="${rIndex +1}"/>
       <c:set var="nextUser" value="${result[userIndex].user_name}"/> 
       <c:out value="${nextUser}"/>
       <a href="javascript:unflagSpammerEvaluator('${row.user_name}','ul${rIndex}', 'true', '${evaluation}' )" title="unflag this user as spammer"
       onClick="javascript:ajax_showTooltip('ajax?action=latest_posts&amp;userName=${nextUser}',document.getElementById('latest_posts_${status.index}'))">NO</a>
       <a href="javascript:flagSpammerEvaluator('${row.user_name}','ul${rIndex}', 'true', '${evaluation}')" title="flag this user as spammer"
        onClick="javascript:ajax_showTooltip('ajax?action=latest_posts&amp;userName=${nextUser}',document.getElementById('latest_posts_${status.index}'))">YES</a>
       
       <%--
         <a href="/admin_spammer.jsp?user=<mtl:encode value='${row.user_name}'/>&action=flag_spammer_evaluator&evaluator=<mtl:encode value='${evaluation}'/>">Yes</a>
         <a href="/admin_spammer.jsp?user=<mtl:encode value='${row.user_name}'/>&action=unflag_spammer_evaluator&evaluator=<mtl:encode value='${evaluation}'/>">No</a>
       --%>
       
       </td>
       <c:set var="user" value='${row.user_name}'/>
       <td>
       	<a id="latest_posts_${status.index}" href="/user/${fn:escapeXml(row.user_name)}"><c:out value="${row.user_name}" /></a>
		<a 
			onclick="javascript:ajax_showTooltip('ajax?action=latest_posts&amp;userName=${row.user_name}',this);return false;"
			style="cursor: pointer" title="show latest posts"><img
			src="${resdir}/image/info.png" />
		</a>
	   </td>
       
       <c:set var="ip" value="${row.ip_address}"/>
     
       <td><c:out value="${fn:substringBefore(ip, ',')}"/></td>
       <td><c:out value="${row.user_realname}"/></td>
       <td><c:out value="${row.user_email}"/></td>
       <td><c:out value="${row.reg_date}"/></td>
        
		<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/>   
		<sql:query var="rs" dataSource="${dataSource}">
  			select tag_name, count(tag_name) as num from tas where tas.user_name = ? group by tag_name order by num desc;
  		<sql:param value="${user}" />
  		</sql:query>
  		<td><c:out value="${fn:length(rs.rows)}"/></td>
  	   <td><c:out value="${rs.rows[0].tag_name} ${rs.rows[1].tag_name} ${rs.rows[2].tag_name} ${rs.rows[3].tag_name} ${rs.rows[4].tag_name}"/></td>

      </tr>
   </c:forEach>
  </table>
  
</div>

<%-- ------------------------ right box -------------------------- --%>
<div id="sidebarroundcorner" >


<%-- scraper action box --%>
<hr/>

<span class="sidebar">logging</span>	
<c:set var="initialValue" value="no log messages"/>
<div class="logbox">			
		<ul id="log">
			<li>
				<c:if test="${not empty initialValue}">
					<c:out value="${initialValue}"/>
				</c:if>			
			</li>
		</ul>
	</div>
<br />
</div>

<script type="text/javascript">
   $("#sidebarroundcorner").corner("round bottom 15px").corner("round tl 15px");
</script>
<div id="footer"/> 
</body>
</html>