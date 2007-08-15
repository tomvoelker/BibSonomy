<%--

shows the related tags of a resource

(NOT if: requested Tag is "dblp" or the tag contains a space " ")

TODO: remove the DBLP testing from the code (it's just included, because the database is too stupid
to select the correct JOIN order when selecting two tags. 

--%>

<c:if test="${param.requTag != 'dblp' && !f:contains(param.requTag, ' ') && not(param.order eq 'folkrank')}">

<li><span class="sidebar_h">related tags</span>

  <%-- Query --%>   
  <sql:query var="rst" dataSource="${dataSource}">
    SELECT t2, ctr_public FROM tagtag WHERE t1 = ? AND ctr_public > 0 ORDER BY ctr_public DESC LIMIT 50
    <sql:param value="${param.requTag}"/>
  </sql:query>
  
  <%@include file="/boxes/tagboxstyle.jsp" %> 
  <c:forEach var="row" items="${rst.rows}">
    <li>
      <a title="${param.requTag}+${row.t2}" href="/tag/<mtl:encode value='${param.requTag}' />+<mtl:encode value='${row.t2}' />">+</a>
      <a title="${row.ctr_public} posts" href="/tag/<mtl:encode value='${row.t2}' />"><c:out value="${row.t2}" /></a>
    </li>
  </c:forEach></ul> 

</li>
</c:if>

<%------------------------------------------- FOLKRANK ------------------------------------------------------------------- --%>
<c:if test="${param.order eq 'folkrank'}">
	
	<%-- ------------------------- related tags ------------------------ --%>
	<li><span class="sidebar_h">related tags</span>
	
	<jsp:useBean id="FolkrankBean" class="beans.FolkrankBean" scope="request">
		<jsp:setProperty name="FolkrankBean" property="requTag" value="${param.requTag}"/>
	</jsp:useBean>
	
	<%@include file="/boxes/tagboxstyle.jsp" %> 
	<c:forEach var="row" items="${FolkrankBean.tags}">
		<li>
			<a title="<c:out value='${param.requTag}'/>+<c:out value='${row.name}'/>" href="/tag/<mtl:encode value='${param.requTag}'/>+<mtl:encode value='${row.name}'/>?order=folkrank">+</a>
			<a title="weight: ${row.weight}" href="/tag/<mtl:encode value='${row.name}' />?order=folkrank"><c:out value="${row.name}" /></a>	
		</li>
	</c:forEach></ul>	
	
	<%-- ------------------------- related users ------------------------ --%>
	<li><span class="sidebar_h">related users</span>
	
	<%@include file="/boxes/tagboxstyle.jsp" %> 
	<c:forEach var="row" items="${FolkrankBean.users}">
		<li>
			<a title="weight: ${row.weight}" href="/user/<mtl:encode value="${row.name}"/>"><c:out value="${row.name}"/></a>
		</li>
	</c:forEach>
	</ul>
	<%@include file="/boxes/relatedusersdiagram.jsp" %> 
	</li>
</c:if>