<div id="bibbox">

  <h3>busy User BibTeX based on 1000 tas</h3>  
  <sql:query var="rs" dataSource="${dataSource}">
    SELECT t.user_name, count(t.user_name) AS user_anzahl
      FROM 
        (SELECT user_name 
          FROM tas 
          WHERE tas.group = 0  and content_type=2
          ORDER BY date desc
          LIMIT 1000) AS t      
      GROUP BY t.user_name
      ORDER BY 2 desc
      LIMIT 20;
  </sql:query>

  <ul class="taglist"><c:forEach var="row" items="${rs.rows}">
    <li><a style="font-size:100%" href="/user/<mtl:encode value='${row.user_name}'/>"><c:out value='${row.user_name}'/></a>, <c:out value='${row.user_anzahl}'/></li>
  </c:forEach></ul>

  <h3>busy User BibTeX based on 1000 post</h3>  
  <sql:query var="rs" dataSource="${dataSource}">
    SELECT t.user_name, count(t.user_name) AS user_anzahl
      FROM 
        (SELECT user_name 
          FROM bibtex b
          WHERE b.group = 0
          ORDER BY date desc
          LIMIT 1000) AS t      
      GROUP BY t.user_name
      ORDER BY 2 desc
      LIMIT 20;
  </sql:query>

  <ul class="taglist"><c:forEach var="row" items="${rs.rows}">
    <li><a style="font-size:100%" href="/user/<mtl:encode value='${row.user_name}'/>"><c:out value='${row.user_name}'/></a>, <c:out value='${row.user_anzahl}'/></li>
  </c:forEach></ul>

  
  </div>