<%@include file="include_jsp_head.jsp" %>

<c:if test="${empty user.name || not(user.name == 'jaeschke' || user.name == 'hotho' || user.name == 'schmitz' || user.name == 'stumme' || user.name == 'grahl' || user.name == 'beate' || user.name == 'natasha' || user.name == 'abhita')}">
   <jsp:forward page="/login"/>
</c:if>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="CKC2007 statistics" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: <a href="/ckc-statistics.jsp">CKC2007 statistics</a></h1> 

<c:set var="group_name" value="ckc2007"/>
<%--c:set var="group_name" value="nepomuk"/--%>

<%@include file="/boxes/navi.jsp" %> 
<%-- count rows --%>
<%-- ubookmarks --%><sql:query var="ubookmarks"    dataSource="${dataSource}">SELECT count(distinct book_url_hash) AS ctr FROM bookmark JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}'</sql:query>
<%-- ibibtex    --%><sql:query var="ubibtex"       dataSource="${dataSource}">SELECT count(distinct simhash1) AS ctr FROM bibtex        JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}'</sql:query>

<%-- posts       --%><sql:query var="posts"        dataSource="${dataSource}">SELECT count(DISTINCT content_id) AS ctr FROM tas JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}'</sql:query>
<%-- bibtex      --%><sql:query var="bibtex"       dataSource="${dataSource}">SELECT count(*) AS ctr FROM bibtex                JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}'</sql:query>
<%-- bookmarks   --%><sql:query var="bookmarks"    dataSource="${dataSource}">SELECT count(*) AS ctr FROM bookmark              JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}'</sql:query>

<%-- posts       --%><sql:query var="posts24"      dataSource="${dataSource}">SELECT count(DISTINCT content_id) AS ctr FROM tas JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}' AND DATE_SUB(CURDATE(), INTERVAL 24 HOUR) <= date</sql:query>
<%-- bibtex      --%><sql:query var="bibtex24"     dataSource="${dataSource}">SELECT count(*) AS ctr FROM bibtex                JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}' AND DATE_SUB(CURDATE(), INTERVAL 24 HOUR) <= date</sql:query>
<%-- bookmarks   --%><sql:query var="bookmarks24"  dataSource="${dataSource}">SELECT count(*) AS ctr FROM bookmark              JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}' AND DATE_SUB(CURDATE(), INTERVAL 24 HOUR) <= date</sql:query>

<%-- tas         --%><sql:query var="tas"          dataSource="${dataSource}">SELECT count(*) AS ctr FROM tas JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}'        </sql:query>
<%-- tasbib      --%><sql:query var="tasbib"       dataSource="${dataSource}">SELECT count(*) AS ctr FROM tas JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}' AND content_type = 2  </sql:query>
<%-- tasbook     --%><sql:query var="tasbook"      dataSource="${dataSource}">SELECT count(*) AS ctr FROM tas JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}' AND content_type = 1  </sql:query>

<%-- tas         --%><sql:query var="tas24"        dataSource="${dataSource}">SELECT count(*) AS ctr FROM tas JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}' AND DATE_SUB(CURDATE(), INTERVAL 24 HOUR) <= date</sql:query>
<%-- tasbib      --%><sql:query var="tasbib24"     dataSource="${dataSource}">SELECT count(*) AS ctr FROM tas JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}' AND DATE_SUB(CURDATE(), INTERVAL 24 HOUR) <= date AND content_type = 2  </sql:query>
<%-- tasbook     --%><sql:query var="tasbook24"    dataSource="${dataSource}">SELECT count(*) AS ctr FROM tas JOIN groups USING (user_name) JOIN groupids ON groups.group=groupids.group WHERE group_name='${group_name}' AND DATE_SUB(CURDATE(), INTERVAL 24 HOUR) <= date AND content_type = 1  </sql:query>

<c:forEach var="row" items="${posts.rows}">      <c:set var="vposts" value="${row.ctr}"/></c:forEach>
<c:forEach var="row" items="${bibtex.rows}">     <c:set var="vbibtex" value="${row.ctr}"/></c:forEach>
<c:forEach var="row" items="${bookmarks.rows}">  <c:set var="vbookmarks" value="${row.ctr}"/></c:forEach>
<c:forEach var="row" items="${posts24.rows}">    <c:set var="vposts24" value="${row.ctr}"/></c:forEach>
<c:forEach var="row" items="${bibtex24.rows}">   <c:set var="vbibtex24" value="${row.ctr}"/></c:forEach>
<c:forEach var="row" items="${bookmarks24.rows}"><c:set var="vbookmarks24" value="${row.ctr}"/></c:forEach>
<c:forEach var="row" items="${tas.rows}">        <c:set var="vtas" value="${row.ctr}"/></c:forEach>
<c:forEach var="row" items="${tasbib.rows}">     <c:set var="vtasbib" value="${row.ctr}"/></c:forEach>
<c:forEach var="row" items="${tasbook.rows}">    <c:set var="vtasbook" value="${row.ctr}"/></c:forEach>    
<c:forEach var="row" items="${tas24.rows}">      <c:set var="vtas24" value="${row.ctr}"/></c:forEach>
<c:forEach var="row" items="${tasbib24.rows}">   <c:set var="vtasbib24" value="${row.ctr}"/></c:forEach>
<c:forEach var="row" items="${tasbook24.rows}">  <c:set var="vtasbook24" value="${row.ctr}"/></c:forEach>


<div id="general">

<h2>Group: <c:out value="${group_name}"/></h2>

<table id="table1" class="thetable">
  <tr>
    <th>unique bookmarks</th>
    <td><c:forEach var="row" items="${ubookmarks.rows}">${row.ctr}</c:forEach></td>
  </tr>
  <tr>
    <th>unique bibtex</th>
    <td><c:forEach var="row" items="${ubibtex.rows}">${row.ctr}</c:forEach></td>
  </tr>
</table>

<table id="table2" class="thetable">
  <tr><th>           </th><th>sum        </th><th>bibtex      </th><th>bookmark       </th></tr>
  <tr><th>posts      </th><td>${vposts}  </td><td>${vbibtex}  </td><td>${vbookmarks}  </td></tr>
  <tr><th>posts (24h)</th><td>${vposts24}</td><td>${vbibtex24}</td><td>${vbookmarks24}</td></tr>
  <tr><th>tas        </th><td>${vtas}    </td><td>${vtasbib}  </td><td>${vtasbook}    </td></tr>
  <tr><th>tas (24h)  </th><td>${vtas24}  </td><td>${vtasbib24}</td><td>${vtasbook24}  </td></tr>
  <tr>
    <th>tas/posts     </th>
    <td><fmt:formatNumber maxFractionDigits="3" value="${vtas/vposts}"/></td>
    <td><fmt:formatNumber maxFractionDigits="3" value="${vtasbib/vbibtex}"/></td>
    <td><fmt:formatNumber maxFractionDigits="3" value="${vtasbook/vbookmarks}"/></td>
  </tr>
  <tr>
    <th>tas/posts (24h)</th>
    <td><fmt:formatNumber maxFractionDigits="3" value="${vtas24/vposts24}"/></td>
    <td><fmt:formatNumber maxFractionDigits="3" value="${vtasbib24/vbibtex24}"/></td>
    <td><fmt:formatNumber maxFractionDigits="3" value="${vtasbook24/vbookmarks24}"/></td>
  </tr>

</table>


<%-- print statistics for each user --%>
<sql:query var="group_users" dataSource="${dataSource}">
  SELECT user_name FROM groups JOIN groupids USING (`group`) WHERE group_name = '${group_name}' ORDER BY user_name
</sql:query>

<c:forEach var="user" items="${group_users.rows}">
<hr/>
<h2>User: <c:out value="${user.user_name}"/></h2>

<table class="thetable">
  <tr><th></th><th>sum</th><th>bibtex</th><th>bookmark</th></tr>
  
  <tr>
    <th>posts</th>
    <td>
      <%-- sum of posts --%>
      <sql:query var="posts" dataSource="${dataSource}">SELECT count(DISTINCT content_id) AS ctr FROM tas WHERE user_name = ?
        <sql:param value="${user.user_name}"/>
      </sql:query>
      <c:forEach var="row" items="${posts.rows}">${row.ctr}<c:set var="vposts" value="${row.ctr}"/></c:forEach>
    </td>
    <td>
      <%-- bibtex posts --%>
      <sql:query var="posts" dataSource="${dataSource}">SELECT count(DISTINCT content_id) AS ctr FROM bibtex WHERE user_name = ?
        <sql:param value="${user.user_name}"/>
      </sql:query>
      <c:forEach var="row" items="${posts.rows}">${row.ctr}<c:set var="vbibtex" value="${row.ctr}"/></c:forEach>
    </td>
    <td>
      <%-- bookmark posts --%>
      <sql:query var="posts" dataSource="${dataSource}">SELECT count(DISTINCT content_id) AS ctr FROM bookmark WHERE user_name = ?
        <sql:param value="${user.user_name}"/>
      </sql:query>
      <c:forEach var="row" items="${posts.rows}">${row.ctr}<c:set var="vbookmarks" value="${row.ctr}"/></c:forEach>
    </td>
  </tr>

  <th>posts (24h)</th>
    <td>
      <%-- sum of posts --%>
      <sql:query var="posts" dataSource="${dataSource}">SELECT count(DISTINCT content_id) AS ctr FROM tas WHERE user_name = ? AND DATE_SUB(CURDATE(), INTERVAL 24 HOUR) <= date
        <sql:param value="${user.user_name}"/>
      </sql:query>
      <c:forEach var="row" items="${posts.rows}">${row.ctr}<c:set var="vposts24" value="${row.ctr}"/></c:forEach>
    </td>
    <td>
      <%-- bibtex posts --%>
      <sql:query var="posts" dataSource="${dataSource}">SELECT count(DISTINCT content_id) AS ctr FROM bibtex WHERE user_name = ? AND DATE_SUB(CURDATE(), INTERVAL 24 HOUR) <= date
        <sql:param value="${user.user_name}"/>
      </sql:query>
      <c:forEach var="row" items="${posts.rows}">${row.ctr}<c:set var="vbibtex24" value="${row.ctr}"/></c:forEach>
    </td>
    <td>
      <%-- bookmark posts --%>
      <sql:query var="posts" dataSource="${dataSource}">SELECT count(DISTINCT content_id) AS ctr FROM bookmark WHERE user_name = ? AND DATE_SUB(CURDATE(), INTERVAL 24 HOUR) <= date
        <sql:param value="${user.user_name}"/>
      </sql:query>
      <c:forEach var="row" items="${posts.rows}">${row.ctr}<c:set var="vbookmarks24" value="${row.ctr}"/></c:forEach>
    </td>
  </tr>
  
  <tr>
    <th>tas</th>
    <td>
      <%-- sum of tas --%>
      <sql:query var="posts" dataSource="${dataSource}">SELECT count(*) AS ctr FROM tas WHERE user_name = ?
        <sql:param value="${user.user_name}"/>
      </sql:query>
      <c:forEach var="row" items="${posts.rows}">${row.ctr}<c:set var="vtas" value="${row.ctr}"/></c:forEach>
    </td>
    <td>
      <%-- bibtex tas --%>
      <sql:query var="posts" dataSource="${dataSource}">SELECT count(*) AS ctr FROM tas WHERE user_name = ? AND content_type=2
        <sql:param value="${user.user_name}"/>
      </sql:query>
      <c:forEach var="row" items="${posts.rows}">${row.ctr}<c:set var="vtasbib" value="${row.ctr}"/></c:forEach>
    </td>
    <td>
      <%-- bookmark tas --%>
      <sql:query var="posts" dataSource="${dataSource}">SELECT count(*) AS ctr FROM tas WHERE user_name = ? AND content_type=1
        <sql:param value="${user.user_name}"/>
      </sql:query>
      <c:forEach var="row" items="${posts.rows}">${row.ctr}<c:set var="vtasbook" value="${row.ctr}"/></c:forEach>
    </td>
  </tr>

  <th>tas (24h)</th>
    <td>
      <%-- sum of tas --%>
      <sql:query var="posts" dataSource="${dataSource}">SELECT count(*) AS ctr FROM tas WHERE user_name = ? AND DATE_SUB(CURDATE(), INTERVAL 24 HOUR) <= date
        <sql:param value="${user.user_name}"/>
      </sql:query>
      <c:forEach var="row" items="${posts.rows}">${row.ctr}<c:set var="vtas24" value="${row.ctr}"/></c:forEach>
    </td>
    <td>
      <%-- bibtex tas --%>
      <sql:query var="posts" dataSource="${dataSource}">SELECT count(*) AS ctr FROM tas WHERE user_name = ? AND DATE_SUB(CURDATE(), INTERVAL 24 HOUR) <= date AND content_type=2
        <sql:param value="${user.user_name}"/>
      </sql:query>
      <c:forEach var="row" items="${posts.rows}">${row.ctr}<c:set var="vtas24bib" value="${row.ctr}"/></c:forEach>
    </td>
    <td>
      <%-- bookmark tas --%>
      <sql:query var="posts" dataSource="${dataSource}">SELECT count(*) AS ctr FROM tas WHERE user_name = ? AND DATE_SUB(CURDATE(), INTERVAL 24 HOUR) <= date AND content_type=1
        <sql:param value="${user.user_name}"/>
      </sql:query>
      <c:forEach var="row" items="${posts.rows}">${row.ctr}<c:set var="vtas24book" value="${row.ctr}"/></c:forEach>
    </td>
  </tr>
  
  <tr>
    <th>tas/posts     </th>
    <td><fmt:formatNumber maxFractionDigits="3" value="${vtas/vposts}"/></td>
    <td><fmt:formatNumber maxFractionDigits="3" value="${vtasbib/vbibtex}"/></td>
    <td><fmt:formatNumber maxFractionDigits="3" value="${vtasbook/vbookmarks}"/></td>
  </tr>
  
  <tr>
    <th>tas/posts (24h)</th>
    <td><fmt:formatNumber maxFractionDigits="3" value="${vtas24/vposts24}"/></td>
    <td><fmt:formatNumber maxFractionDigits="3" value="${vtasbib24/vbibtex24}"/></td>
    <td><fmt:formatNumber maxFractionDigits="3" value="${vtasbook24/vbookmarks24}"/></td>
  </tr>
  
</table>
</c:forEach>

</div>









<style>
 .odd{background-color: white;}
 .even{background-color: #eeeeee;}
 .thetable {
   margin: 1em;
 }
 tr, td, th {
   padding: 4px;
 }
 td {
   text-align: right;
 }
 th {
   text-align: left;
 }
</style>


<script type="text/javascript">
function alternate(id){
 if(document.getElementsByTagName){  
   var tables = document.getElementsByTagName("table");
   for (t = 0; t < tables.length; t++) {
     var table = tables[t];
     if (table.className == "thetable") {
       var rows = table.getElementsByTagName("tr");  
       for(i = 0; i < rows.length; i++){          
         //manipulate rows
         if(i % 2 == 0){
           rows[i].className = "even";
         } else {
           rows[i].className = "odd";
         }
       }
     }
   }
 }
}
alternate('table1');
alternate('table2');
</script>


<%-- ------------------------ right box -------------------------- --%>
<div id="rightbox">
</div>        

<%@ include file="footer.jsp" %>