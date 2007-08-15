<%@ include file="include_jsp_head.jsp"%>

<%@include file="/boxes/admin/login.jsp"%>

<%-- spammer tag suggestions --%>
<c:if test="${param.tag != null and param.type == 1}">
	<sql:query var="tag" dataSource="jdbc/bibsonomy">
		SELECT DISTINCT tag_name 
		FROM tas 
		WHERE tas.group = -2147483648 AND 
		tas.tag_name LIKE ? LIMIT 10
		<sql:param value="${param.tag}%"/>
	</sql:query>
	<ul>
	<c:forEach items="${tag.rows}" var="n">
		<li style="text-align:left;">${n.tag_name}</li>
	</c:forEach>
</ul>
</c:if>

<%-- spammer-tag list entries --%>
<c:if test="${param.tag != null and param.type == 2}">
	<sql:query var="tag" dataSource="jdbc/bibsonomy">
		SELECT DISTINCT tag_name 
		FROM spammer_tags 
		WHERE spammer = 1 AND
		tag_name LIKE ? LIMIT 10
		<sql:param value="${param.tag}%"/>
	</sql:query>
	<ul>
	<c:forEach items="${tag.rows}" var="n">
		<li style="text-align:left;">${n.tag_name}</li>
	</c:forEach>
</ul>
</c:if>

<%-- related (folkrank) tags to chosen spammertag --%>
<c:if test="${param.tag != null and param.type == 3}">
	<sql:query var="queryreltags" dataSource="jdbc/bibsonomy">
		SELECT w.item AS sug_tag, ROUND(w.weight,5) AS weight
		FROM rankings r 
			JOIN weights w USING (id)
			LEFT JOIN spammer_tags s ON s.tag_name = w.item 
		WHERE r.dim = 0 and r.item = ?
			AND w.dim = 0 AND w.item <> r.item
			AND ISNULL(s.tag_name)
		ORDER BY w.weight DESC
		LIMIT ?
		<sql:param value="${param.tag}"/>
		<sql:param value="${sugcount}"/>			
	</sql:query>	
	
	<c:choose>
	
		<c:when test="${queryreltags.rowCount eq 0}">
		<h3>related tags to chosen spammertag <c:out value="${param.tag}"/></h3>
		<br>
		- no related tags found for <c:out value="${param.tag}"/> - 		
		<c:set var="reltags" value="" scope="session"/>	
		<c:set var="reltag" value="${param.tag}" scope="session"/>	
		</c:when>
		
		<c:otherwise>
			<h3>related tags to chosen spammertag <c:out value="${param.tag}"/></h3>
			<br>
			<table class="taglist">
			<tr>
				<th>tag</th>
				<th>weight</th>
				<th colspan="2">action</th>
			</tr>
			<c:forEach var="tag" items="${queryreltags.rows}">
				<tr>
					<td><a href="/tag/<mtl:encode value='${tag.sug_tag}'/>"><c:out value="${tag.sug_tag}"/></a></td>
					<td align="center"><c:out value="${tag.weight}"/></td>
					<td align="center" style="background-color:#eeeeee">
						<a href="admin_spammertags.jsp?tag=<mtl:encode value='${tag.sug_tag}'/>&action=addtag">ADD</a>
					</td>
					<td align="center" style="background-color:#eeeeee">
						<a href="admin_spammertags.jsp?tag=<mtl:encode value='${tag.sug_tag}'/>&action=cleantag">REMOVE</a>
					</td>
				</tr>
			</c:forEach>
		</table> 		
		<c:set var="reltags" value="${queryreltags}" scope="session"/>	
		<c:set var="reltag" value="${param.tag}" scope="session"/>							
		</c:otherwise>	
		
	</c:choose>	
</c:if>