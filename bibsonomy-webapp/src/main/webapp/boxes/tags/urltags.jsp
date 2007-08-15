<%-- 
  this shows the tag cloud on the urls page
--%>


<li><span class="sidebar_h">tags</span>

 <sql:query var="rs" dataSource="${dataSource}">
   SELECT tag_name, lower(tag_name) as x, tag_anzahl, round(log(if(tag_anzahl>100, 100, tag_anzahl)))*50+80  class
    from ( 
    SELECT t.tag_name, count(t.tag_name) AS tag_anzahl
      FROM 
        (SELECT ta.tag_name 
          FROM tas ta, bookmark b
          WHERE ta.group = 0
          and ta.content_id=b.content_id
          and b.book_url_hash = '${ResourceBean.bookmarkHash}'
          ) AS t      
      GROUP BY t.tag_name
      ORDER BY 2 desc
      LIMIT 20
      ) as t1
      order by 2;
  </sql:query>

  <%@include file="/boxes/tagboxstyle.jsp" %> 
  <c:forEach var="row" items="${rs.rows}">
    <li><a style="font-size:${row.class}%" title="${row.tag_anzahl}" href="/tag/<mtl:encode value='${row.tag_name}'/>"><c:out value='${row.tag_name}'/></a></li>
  </c:forEach></ul>
  
</li>
  