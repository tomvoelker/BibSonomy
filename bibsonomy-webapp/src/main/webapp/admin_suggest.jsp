<%@ include file="include_jsp_head.jsp"%>

<%@include file="/boxes/admin/login.jsp"%>

<c:if test="${param.userinfo != null }">
	<sql:query var="name" dataSource="jdbc/bibsonomy">
		SELECT user_name FROM user
		WHERE user_name LIKE ?
		LIMIT 10
		<sql:param value="${param.userinfo}%"/>
	</sql:query>
</c:if>

<c:if test="${param.user != null }">
	<sql:query var="name" dataSource="jdbc/bibsonomy">
		SELECT user_name FROM user
		WHERE user_name LIKE ?
		LIMIT 10
		<sql:param value="${param.user}%"/>
	</sql:query>
</c:if>

<ul>

	<c:forEach items="${name.rows}" var="n">
		<li>${n.user_name}</li>
	</c:forEach>

</ul>


