
<li><span class="sidebar_h">busy tags</span>

<%-- Diese Query dauert auf den delicious Daten 10min, evtl. hilft ein Index auf date, bis dahin: s.u.
  <sql:query var="rs" dataSource="${dataSource}">
    SELECT tag_name, count(tag_name) AS tag_anzahl 
      FROM tas 
      WHERE DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date
      GROUP BY tag_name 
      ORDER BY 2 DESC 
      LIMIT 10;
  </sql:query>
--%>
  <sql:query var="rs" dataSource="${dataSource}">
   SELECT tag_name, lower(tag_name) as x, tag_anzahl, round(log(if(tag_anzahl>100, 100, tag_anzahl+6)/6))*60+40  class
    from ( 
    SELECT t.tag_name, count(t.tag_name) AS tag_anzahl
      FROM 
        (SELECT tag_name 
          FROM tas 
          WHERE tas.group = 0
          ORDER BY date desc
          LIMIT 10000) AS t      
      GROUP BY t.tag_name
      ORDER BY 2 desc
      LIMIT 100
      ) as t1
      order by 2;
  </sql:query>

  <%@include file="/boxes/tagboxstyle.jsp" %> 
   <c:forEach var="row" items="${rs.rows}">
    <li><a style="font-size:${row.class}%" title="${row.tag_anzahl}" href="/tag/<mtl:encode value='${row.tag_name}'/>"><c:out value='${row.tag_name}'/></a></li>
  </c:forEach></ul>

</li>