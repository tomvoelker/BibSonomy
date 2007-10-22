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

<%-- related tags to chosen spammertag --%>
<c:if test="${param.tag != null and param.type == 3}">
	<sql:query var="reltags" dataSource="jdbc/bibsonomy">
		SELECT t2 AS tag, ctr_public AS ctr, s.tag_name 
		FROM tagtag tt LEFT JOIN spammer_tags s ON (s.tag_name = tt.t2)
		WHERE t1 = ? AND ctr_public > 0 AND ISNULL(s.tag_name) 
		ORDER BY ctr_public DESC LIMIT 25
		<sql:param value="${param.tag}"/>					
	</sql:query>	
	
	<div style="width:100%; font-size:0.7em; text-align:right;"><a href="javascript:ajax_hideTooltip()">close</a></div>
	
	<c:choose>	
		<c:when test="${reltags.rowCount eq 0}">		
		- no related tags found for <c:out value="${param.tag}"/> - 		
		</c:when>
		
		<c:otherwise>			
			<table class="taglist">
			<tr>
				<th>tag</th>	
				<th>count</th>			
				<th colspan="2">action</th>
			</tr>
			<tbody id="relatedlist">
			<c:forEach var="row" items="${reltags.rows}" varStatus="status">
				<tr id="rt<c:out value='${status.count}'/>">
					<td><a href="/tag/<mtl:encode value='${row.tag}'/>"><c:out value="${row.tag}"/></a></td>
					<td align="center"><c:out value="${row.ctr}"/></td>					
					<td>
						<a href="javascript:addSpammertag('<c:out value="${row.tag}"/>','relatedlist','rt<c:out value='${status.count}'/>')" title="mark as spammertag"><img src="/resources/image/plus.png"/></a>
						<a href="javascript:cleanTag('<c:out value="${row.tag}"/>','relatedlist','rt<c:out value='${status.count}'/>')" title="remove tag from suggestion list"><img src="/resources/image/minus.png"/></a>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table> 								
		</c:otherwise>		
	</c:choose>	
</c:if>

<!-- handle request on spammertag page -->
<c:if test="${param.type == 0}">
	<jsp:useBean id="adminBean" class="beans.AdminBean">
		<jsp:setProperty name="adminBean" property="*"/>
	</jsp:useBean>

	<% adminBean.queryDB(); %>
	<c:forEach var="info" items="${adminBean.infos}">
		<li><c:out value="${info}"/></li>
	</c:forEach>
	<c:forEach var="error" items="${adminBean.errors}">
		<li><c:out value="${error}"/></li>
	</c:forEach>
</c:if>