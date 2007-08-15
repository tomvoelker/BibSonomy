

<li><span class="sidebar_h">you are a friend of</span>  

<%-- get users, which have user.name as friend --%>
<sql:query var="rs" dataSource="${dataSource}">
  SELECT user_name FROM friends WHERE f_user_name = ?
  <sql:param value="${user.name}"/>  
</sql:query>

<ul><c:forEach var="row" items="${rs.rows}">
  <li><a href="/friend/<mtl:encode value='${row.user_name}'/>"><c:out value="${row.user_name}"/></a></li>
</c:forEach></ul>

</li>

<li><span class="sidebar_h">your friends are</span>
  
<%-- get all friends of user --%>
<sql:query var="f" dataSource="${dataSource}">
  SELECT f_user_name FROM friends WHERE user_name = ?
  <sql:param>${user.name}</sql:param>
</sql:query>

<ul><c:forEach var="row" items="${f.rows}">
  <li>
    <a href="/user/<mtl:encode value='${row.f_user_name}'/>"><c:out value='${row.f_user_name}'/></a> 
    <a class="bmaction" href="/SettingsHandler?ckey=${ckey}&amp;del_friend=<mtl:encode value='${row.f_user_name}'/>" title="remove <c:out value='${row.f_user_name}'/> from your friend list">delete</a>
  </li>
</c:forEach></ul>

</li>

<%-- ------------------------ add a friend -------------------------- --%>
<li>
<span class="sidebar_h">add a friend</span>

<form name="friends" method="post" action="/SettingsHandler">
  <label for="laddfriend">user</label> 
  <input type="text" size="20" name="add_friend" id="laddfriend"/>
  <input type="hidden" value="${ckey}" name="ckey"/>
  <input type="submit" value="add" />
</form>
</li>