<%@include file="include_jsp_head.jsp" %>

<c:if test="${empty user.name}">
   <jsp:forward page="/login"/>
</c:if>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="settings" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/settings">settings&nbsp;<img src="/resources/image/box_arrow.png"></a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 


<%-- SQL Queries for Groups, Groupusers and Friends --%>

<%-- check, if usergroup exists --%>
<sql:query var="gadmin" dataSource="${dataSource}">
  SELECT g.user_name FROM groups g, groupids i WHERE g.user_name = ? AND i.group_name = ? AND g.group = i.group
  <sql:param>${user.name}</sql:param>
  <sql:param>${user.name}</sql:param>
</sql:query>

<%-- get users, which have user.name as friend --%>
<sql:query var="fr1" dataSource="${dataSource}">
  SELECT user_name FROM friends WHERE f_user_name = ?
  <sql:param value="${user.name}"/>  
</sql:query>

<%-- get all friends of user --%>
<sql:query var="fr2" dataSource="${dataSource}">
  SELECT f_user_name FROM friends WHERE user_name = ?
  <sql:param>${user.name}</sql:param>
</sql:query>

<%-- ------------------------ get users for own group  -------------------------- --%>

<c:set var="hasOwnGroup" value="false"/>
<c:forEach var="row" items="${gadmin.rows}">
  <c:if test="${row.user_name eq user.name}">
    <%-- get all users of usergroup --%>
    <sql:query var="gu" dataSource="${dataSource}">
      SELECT g.user_name FROM groups g, groupids i WHERE i.group_name = ? AND g.user_name != ? AND i.group=g.group ORDER BY g.user_name
      <sql:param>${user.name}</sql:param>
      <sql:param>${user.name}</sql:param>
    </sql:query>
    <c:set var="hasOwnGroup" value="true"/>
  </c:if>
</c:forEach>

<div id="general">

<%-- ------------------------ tab header -------------------------- --%>
<c:if test="${empty seltab || not empty param.seltab}">
	<c:choose>
		<c:when test="${empty param.seltab}">	
			<c:set var="seltab" value="1" scope="session"/>
		</c:when>
		<c:otherwise>
			<c:set var="seltab" value="${param.seltab}" scope="session"/>
		</c:otherwise>
	</c:choose>
</c:if>

<ul id="tab">	
	<li <c:if test="${seltab eq 1}">id="selected"</c:if> ><a href="?seltab=1">my profile</a></li>
	<li <c:if test="${seltab eq 2}">id="selected"</c:if> ><a href="?seltab=2">settings</a></li>
	<li <c:if test="${seltab eq 3}">id="selected"</c:if> ><a href="?seltab=3">imports</a></li>			
</ul>

<%-- ------------------------ tab content -------------------------- --%>
<div id="tab_content">
<c:choose>	

	<%-- ############################################# profile tab ############################################# --%>	
	<c:when test="${seltab eq 1}">
          <%@include file="/settings_profile.jsp" %> 
	</c:when>
	
	<%-- ############################################# settings tab ############################################# --%>
	<c:when test="${seltab eq 2}">
          <%@include file="/settings_settings.jsp" %> 
	</c:when>
	
	<%-- ############################################# import tab ############################################# --%>
	<c:otherwise>	
          <%@include file="/settings_import.jsp" %> 
	</c:otherwise>

</c:choose>
</div>
</div>

<%-- ------------------------ right box -------------------------- --%>
<ul id="sidebar">
  
  <c:if test="${hasOwnGroup}">
    <li>
    <span class="sidebar_h">mygroup</span>
    <ul><c:forEach var="row" items="${gu.rows}">
      <li>
        <a href="/user/${row.user_name}">${row.user_name}</a>
        <a class="action" href="/SettingsHandler?del_group_user=<mtl:encode value='${row.user_name}'/>&ckey=${ckey}">del</a>
      </li>
    </c:forEach></ul>
    </li>
  </c:if>


  <li>    
    <span class="sidebar_h"><a href="/groups">groups</a></span>
    <ul><c:forEach var="group" items="${user.groups}">
      <li><a href="/group/<mtl:encode value='${group}'/>"><c:out value='${group}'/></a></li>
    </c:forEach></ul>
  </li>
 
</ul>

<%@ include file="footer.jsp" %>