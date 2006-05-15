<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %> 
<%@ page pageEncoding="UTF-8" %>

<jsp:useBean id="bean" class="org.bibsonomy.viewmodel.TestViewModel" scope="request"/>

<html>
	<head>
		<title>test</title>
	</head>
	<body>
		<h1>hallo</h1>
		<ul>
			<c:forEach items="${bean.items}" var="item">
			<li><c:out value="${item}"/></li>
			</c:forEach>
		</ul>
	</body>
</html>