  
  
<li><span class="sidebar_h">related tags</span>

  <%-- standard database query --%>
  <%-- Query --%>   
  <sql:query var="rst" dataSource="${dataSource}">
    SELECT tt.tag_name,count(tt.tag_name) AS tag_anzahl
      FROM tas t, tas tt
      WHERE tt.content_id=t.content_id
        AND t.group = ?
        AND t.tag_name = ?
        AND tt.tag_name != ?
      GROUP BY tt.tag_name
      ORDER BY tag_anzahl DESC 
      LIMIT 10
    <sql:param value="${group}"/>  
    <sql:param value="${param.requTag}"/>
    <sql:param value="${param.requTag}"/>
  </sql:query>
  
  <%@include file="/boxes/tagboxstyle.jsp" %> 
  <c:forEach var="row" items="${rst.rows}">
    <li>
      <a title="${param.requTag}+${row.tag_name}" href="/viewable/<mtl:encode value='${param.requGroup}' />/<mtl:encode value='${param.requTag}' />+<mtl:encode value='${row.tag_name}' />">+</a>
      <a title="${row.tag_anzahl} posts" href="/viewable/<mtl:encode value='${param.requGroup}' />/<mtl:encode value='${row.tag_name}' />"><c:out value="${row.tag_name}" /></a>
    </li>
  </c:forEach></ul>

</li>