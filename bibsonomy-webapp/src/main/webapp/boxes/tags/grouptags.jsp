
<li><span class="sidebar_h">tags</span>


<%--
  TODO: if a user is not in the group it could nevertheless see the groups tags
        (maybe this is ok, as we said, that tags are public anyway?!)
        The implementation below nethertheless does NOT show the tags of the group,
        because ${group} is empty (is set in ResourceHandler only, if user.name is
        in group) and so the query asks for "t.group = null" --> if there is a group
        "null", everybody could see it's tags.
        This behaviour is not nice and depends on the implementation of JSTL, which
        inserts "null" instead of throwing an exception
--%>

<c:choose>
  <c:when test="${user.tagboxSort eq 1}">
    <sql:query var="rst" dataSource="${dataSource}">
      SELECT tag_name, count(tag_name) AS tag_anzahl
        FROM tas t, groups g 
        WHERE g.group = ?
          AND t.user_name = g.user_name
        GROUP BY t.tag_name 
        ORDER BY tag_anzahl DESC, t.tag_name COLLATE utf8_unicode_ci
      <sql:param value="${group}"/>  
    </sql:query>
  </c:when>

  <c:otherwise>
    <sql:query var="rst" dataSource="${dataSource}">
      SELECT tag_name, count(tag_name) AS tag_anzahl
        FROM tas t, groups g 
        WHERE g.group = ?
          AND g.user_name = t.user_name
        GROUP BY t.tag_name 
        ORDER BY tag_name COLLATE utf8_unicode_ci
      <sql:param value="${group}"/>  
    </sql:query>
  </c:otherwise>
</c:choose>

    <%@include file="/boxes/tagboxstyle.jsp" %> 
    <c:forEach var="row" items="${rst.rows}">
      <c:choose>
        <c:when test="${row.tag_anzahl == 1}">
          <li class="tagone">
        </c:when>        
        <c:when test="${row.tag_anzahl > 10}">
          <li class="tagten">
        </c:when>
        <c:otherwise>
          <li>
        </c:otherwise>
      </c:choose>
      <a title="${row.tag_anzahl} posts" href="/group/<mtl:encode value='${param.requGroup}' />/<mtl:encode value='${row.tag_name}' />"><c:out value="${row.tag_name}" /></a></li>
    </c:forEach>
    </ul>

</li>