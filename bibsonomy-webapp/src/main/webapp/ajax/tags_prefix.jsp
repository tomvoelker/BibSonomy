<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="application/xml;charset=UTF-8" %> 
<%@ page pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>

<%-- returns the top most tags which have param.prefix as their prefix --%>

<%@include file="/include_jsp_user.jsp" %>
<sql:setDataSource dataSource="jdbc/del" var="dataSource"/>
  <sql:query var="rs" dataSource="${dataSource}">
    SELECT tag_name, tag_ctr 
      FROM tags  
      WHERE LOWER(LEFT(tag_name,?)) LIKE LOWER(?)
        AND tag_ctr > 0  
      ORDER BY tag_ctr DESC
      LIMIT 10;
    <sql:param value="${f:length(param.prefix)}"/>  
    <sql:param value="${param.prefix}"/>
  </sql:query>

<response>
<method>niceOutput</method>

<tags>
<c:forEach var="row" items="${rs.rows}">
<tag count="${row.tag_ctr}"><c:out value='${row.tag_name}'/></tag>
</c:forEach>
</tags>
</response>