<%@ include file="include_jsp_head.jsp"%>

<%@include file="/boxes/admin/login.jsp"%>

<c:if test="${not empty param.q and param.type eq 0}">
	<sql:query var="name" dataSource="jdbc/bibsonomy">
		SELECT user_name FROM user
		WHERE user_name LIKE ?
		AND spammer = 0
		LIMIT 10
		<sql:param value="${param.q}%"/>
	</sql:query>
</c:if>

<c:if test="${not empty param.q and param.type eq 1}">
	<sql:query var="name" dataSource="jdbc/bibsonomy">
		SELECT user_name FROM user
		WHERE user_name LIKE ?
		AND spammer = 1
		LIMIT 10
		<sql:param value="${param.q}%"/>
	</sql:query>
</c:if>

<c:if test="${not empty param.q and param.type eq 2}">
	<sql:query var="name" dataSource="jdbc/bibsonomy">
		SELECT user_name FROM user
		WHERE user_name LIKE ?
		LIMIT 10
		<sql:param value="${param.q}%"/>
	</sql:query>
</c:if>

<c:forEach items="${name.rows}" var="n">
	${n.user_name}
</c:forEach>

