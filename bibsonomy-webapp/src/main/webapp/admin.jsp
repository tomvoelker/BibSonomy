
<%@include file="/include_jsp_head.jsp"%>


<%@include file="/boxes/admin/login.jsp"%>

<script type="text/javascript" src="/ajax/scriptaculous/lib/prototype.js"></script>
<script type="text/javascript" src="/ajax/scriptaculous/src/scriptaculous.js"></script>
<link rel="stylesheet" type="text/css" href="/resources/css/spammer.css">

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="admin" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/?filter=no" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/admin.jsp">admin&nbsp;<img src="/resources/image/box_arrow.png"></a></h1>

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp"%>

<%-- ------------------------ change settings -------------------------- --%>
<jsp:useBean id="adminBean" class="beans.AdminBean" scope="request">
  <jsp:setProperty name="adminBean" property="*"/>
  <jsp:setProperty name="adminBean" property="currUser" value="${user.name}"/>
</jsp:useBean>

<% adminBean.queryDB(); %> <%-- write data to database (if neccessary) --%>

<%-------------------------- Content -----------------------%>
<div id="general">
<%@include file="/boxes/admin/navi.jsp"%>
<p style="font-weight: bold; color: #ff0000; ">
  <c:forEach var="info" items="${adminBean.infos}">Info: ${info}<br></c:forEach>
  <c:forEach var="error" items="${adminBean.errors}">Error: ${error}<br></c:forEach>
</p>

<table width="100%">
	<tr>
		<td style="padding-right: 30px">
			<h2>flag/unflag a spammer</h2>
			<%-- spammer form  --%>
			<form action="/admin.jsp">
			  <b>Spammer:</b> user name <input type="text" name="user" id="acl_spam" />
			  <input type="hidden" name="action" value="flag_spammer"/>
			  <input type="button" value=" OK " onclick="addSpammer(document.getElementsByName('user')[0].value,null);clearFields();"/>
			</form>
			
			<div id="autocomplete_spam" class="autocomplete"></div>
			<script type="text/javascript">
			  new Ajax.Autocompleter("acl_spam","autocomplete_spam","admin_suggest.jsp?type=0");
			</script>
			
			<form action="/admin.jsp">
			  <b style="text-decoration:line-through;">Spammer:</b> user name <input type="text" name="user" id="acl_unspam"/>
			  <input type="hidden" name="action" value="unflag_spammer"/>
			  <input type="button" value=" OK " onclick="unflagSpammer(document.getElementsByName('user')[1].value,null);clearFields();"/>
			</form>
			
			<div id="autocomplete_unspam" class="autocomplete"></div>
			<script type="text/javascript">
			  new Ajax.Autocompleter("acl_unspam","autocomplete_unspam","admin_suggest.jsp?type=1");
			</script>
		</td>
		<td>			
			<div class="logbox">			
				<ul id="log">
					<li>no log messages ...</li>
				</ul>
			</div>
		</td>		
	</tr>
</table>

<hr>

<h2> User Info </h2>

<form name="userinfo" action="/admin.jsp">
  user name <input type="text" name="userinfo" id="acl_userinfo"/>
  <input type="submit"/>
</form>

<div id="autocomplete_userinfo" class="autocomplete"></div>
<script type="text/javascript">
  new Ajax.Autocompleter("acl_userinfo","autocomplete_userinfo","admin_suggest.jsp");
</script>

<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/>   


<c:if test="${param.userinfo != null }">
	<%-- do SQL query to get new users --%>
	<sql:query var="rsInfo" dataSource="${dataSource}">
	  SELECT user_name, spammer, trim(trailing ', 141.51.167.67' from trim(trailing ', 141.51.167.67' from ip_address)) as ip_address, 
	  	user_realname, user_email, date_format(reg_date,'%d.%c.%y_%H:%i') as reg_date2, updated_by, updated_at 
	  FROM user WHERE user_name= ?
	  <sql:param value="${param.userinfo}" />
	</sql:query> 
</c:if>

<hr>

<c:set var="usercount" value="40"/>
<c:if test="${not empty param.usercount}">
  <c:set var="usercount" value="${param.usercount}"/>
</c:if>


<c:choose>
  <c:when test="${not empty param.frequent}">
    <%-- show most frequent IPs  --%>
    <sql:query var="rs" dataSource="${dataSource}">
      SELECT a.user_name, a.spammer, trim(trailing ', 141.51.167.67, 141.51.167.67' from trim(trailing ', 141.51.167.67' from ip_address)) as ip_address, 
      	a.user_realname, a.user_email, date_format(a.reg_date, '%d.%c.%y_%H:%i') as reg_date1 ,updated_by, updated_at 
      FROM (
      	SELECT ips.ip_address,count(ip_address) AS ipctr 
      	FROM (
      		SELECT u.ip_address,u.user_name FROM user u JOIN user u2 USING (ip_address) WHERE u.spammer = 0
      	) AS ips 
      group by ips.ip_address having ipctr > 2) AS u 
      JOIN user a USING(ip_address) ORDER BY ipctr, ip_address, reg_date DESC ;
    </sql:query>
  </c:when>
  <c:otherwise>
    <%-- do SQL query to get new users --%>
    <sql:query var="rs" dataSource="${dataSource}">
      SELECT user_name, spammer, TRIM(trailing ', 141.51.167.67, 141.51.167.67' FROM TRIM(trailing ', 141.51.167.67' from ip_address)) AS ip_address, 
      	user_realname, user_email, date_format(reg_date,'%d.%c.%y_%H:%i') AS reg_date1, updated_by, updated_at
      FROM user ORDER BY reg_date DESC LIMIT 100
    </sql:query>
  </c:otherwise>
</c:choose>


<h2> List of new users </h2> 

(show 
<a href="?usercount=100">100</a>
<a href="?usercount=500">500</a>
<a href="?usercount=1000">1000</a>
<a href="?usercount=100000">all</a>, show <a href="?frequent=true">frequent IPs</a>)

  <table class="spammertable">
    <tr style="font-size:120%"><th>Is it a Spammer?</th><th>Username</th><th>Spammer?</th><th>IP</th><th>Realname</th><th>E-Mail</th><th>Date</th><th>updated by</th><th>updated at</th></tr>
    
    <!-- specified user infos -->
    <c:if test="${param.userinfo != null }">
    	<c:forEach var="rows" items="${rsInfo.rows}" varStatus="status">	
	      <c:choose>
	        <c:when test="${rows.spammer}">
	          <tr id="ul<c:out value='${status.count}'/>" class="spammer">
	        </c:when>
	        <c:otherwise>
	          <tr id="ul<c:out value='${status.count}'/>">
	        </c:otherwise>
	      </c:choose>
	
	       <td style="border-bottom: 1px solid black">
	         <a href="javascript:addSpammer('<mtl:encode value="${rows.user_name}"/>','ul<c:out value="${status.count}"/>')" title="flag this user as spammer">YES</a>
	     	 <a href="javascript:unflagSpammer('<mtl:encode value="${rows.user_name}"/>','ul<c:out value="${status.count}"/>')" title="unflag this user as spammer">NO</a>
           </td>
	       <td style="border-bottom: 1px solid black"><a href="/user/<mtl:encode value='${rows.user_name}'/>"><c:out value="${rows.user_name}"/></a></td>
	       <td style="border-bottom: 1px solid black"><c:out value="${rows.spammer}"/></td>
	       <td style="border-bottom: 1px solid black"><c:out value="${rows.ip_address}"/></td>
	       <td style="border-bottom: 1px solid black"><c:out value="${rows.user_realname}"/></td>
	       <td style="border-bottom: 1px solid black"><c:out value="${rows.user_email}"/></td>
	       <td style="border-bottom: 1px solid black"><c:out value="${rows.reg_date2}"/></td>	       
	       <td style="border-bottom: 1px solid black"><c:out value="${rows.updated_by}"/></td>
	       <td style="border-bottom: 1px solid black"><c:out value="${rows.updated_at}"/></td>		       
	       </tr>
	    </c:forEach>
	  </c:if>    
    
    <!-- last users registered in system -->
    <c:forEach var="row" items="${rs.rows}" varStatus="status">
      <c:choose>
        <c:when test="${row.spammer}">
          <tr id="sl<c:out value='${status.count}'/>" class="spammer">
        </c:when>
        <c:otherwise>
          <tr id="sl<c:out value='${status.count}'/>">
        </c:otherwise>
      </c:choose>

       <td>
	     <a href="javascript:addSpammer('<mtl:encode value="${row.user_name}"/>','sl<c:out value="${status.count}"/>')" title="flag this user as spammer">YES</a>
	     <a href="javascript:unflagSpammer('<mtl:encode value="${row.user_name}"/>','sl<c:out value="${status.count}"/>')" title="unflag this user as spammer">NO</a>
       </td>
       <td><a href="/user/<mtl:encode value='${row.user_name}'/>"><c:out value="${row.user_name}"/></a></td>
       <td><c:out value="${row.spammer}"/></td>
       <td><c:out value="${row.ip_address}"/></td>
       <td><c:out value="${row.user_realname}"/></td>
       <td><c:out value="${row.user_email}"/></td>
	   <td><c:out value="${row.reg_date1}"/></td>
	   <td><c:out value="${row.updated_by}"/></td>
	   <td><c:out value="${row.updated_at}"/></td>
       </tr>
    </c:forEach>
  </table>
</div>


<%-- ------------------------ right box -------------------------- --%>
<ul id="sidebar">

<li>

<%-- scraper action box --%>
<hr/>

<span class="sidebar_h">Scraper</span>

<ul>
  
  <li><%-- -----HIGHWIRE LIST UPDATE ------- --%>
    <%-- get the date of lastupdate --%>
    <sql:query var="listInfo" dataSource="${dataSource}">
      SELECT lastupdate from highwirelist; 
    </sql:query> 
    Highwire Linklist:  
    <form method="POST" action="/AdminHandler" style="display:inline;">
      <input type="hidden" name="action" value="update highwire"/>
      <input type="hidden" name="ckey" value="${ckey}"/>
      <input type="submit" value="update" />
    </form>
    <br/>
    <c:forEach var="rows" items="${listInfo.rows}">
	  Last Update: <fmt:formatDate value="${rows.lastupdate}" pattern="dd.MM.yyyy HH:mm:ss" />
    </c:forEach>
  </li>
  
</ul>
<hr/>

<%-- API key management --%>
<span class="sidebar_h">API key</span><br>
<c:choose>	
	<c:when test="${param.userinfo != null}">
		user name: <c:out value="${rsInfo.rows[0].user_name}"/>
		<input type="button" value="Generate key" onclick="javascript:generateApiKey('<mtl:encode value="${rsInfo.rows[0].user_name}"/>')"/>	
	</c:when>
	<c:otherwise>
		please select a user
	</c:otherwise>
</c:choose> 	

<hr/>

<%-- group management  --%>
<span class="sidebar_h">add a group to the system</span>

<%--  group form  --%>
<form action="/admin.jsp">
  <table>
    <tr>
      <td>user name</td><td><input type="text" name="user" id="acl_usergroup" /></td></tr>
    <tr>
      <td>privacy</td>
      <td>
        <select name="privlevel">
          <option value="0">member list public</option>
          <option value="1">member list hidden</option>
          <option value="2">members can list members</option>
        </select>
      </td>
    </tr>
  
    <tr>
     <td></td>
     <td>
       <input type="hidden" name="action" value="add_group"/>
       <input type="submit"/>
     </td>
   </tr>
   </table>
</form>

<div id="autocomplete_usergroup" class="autocomplete"></div>
<script type="text/javascript">
  new Ajax.Autocompleter("acl_usergroup","autocomplete_usergroup","admin_suggest.jsp");
</script>
</li>

<li>

<%-- do SQL query to get groups --%> 
<sql:query var="rs" dataSource="${dataSource}">
  SELECT i.group_name, u.user_realname, u.user_homepage FROM groupids i, user u WHERE i.group > 2 AND i.group_name = u.user_name ORDER BY i.group_name
</sql:query>

<span class="sidebar_h">existing groups</span>
  <ul>
    <c:forEach var="row" items="${rs.rows}">
       <li><a href="/group/<c:out value='${row.group_name}'/>"><c:out value="${row.group_name}"/></a></li>
    </c:forEach>
  </ul>


</li>

</ul>

<!-- TODO: in functions.js Datei auslagern -->
<script type="text/javascript">  
	function initRequest(){
    	var req;
      	try{
        	if(window.XMLHttpRequest){
          		req = new XMLHttpRequest();
        	}else if(window.ActiveXObject){
          		req = new ActiveXObject("Microsoft.XMLHTTP");
        	}
        	if( req.overrideMimeType ) {
            	req.overrideMimeType("text/xml");
	        }        	
     	} catch(e){
     	   	return false;
     	}
     	return req;
    }	
		
	function addSpammer(name, rowId) {		
		if (name == null || name == "") {
			addLogMessage("please specify a user");
			return;
		}
		/* colorize */
		if (rowId != null) {			
			document.getElementById(rowId).className="spammer";
		}	
			
		/* add spammer to db via AJAX*/
		runAjax("user=" + name, "flag_spammer");
	}
	
	function unflagSpammer(name, rowId) {		
		if (name == null || name == "") {
			addLogMessage("please specify a user");
			return;
		}
		
		/* colorize row */
		if (rowId != null) {			
			document.getElementById(rowId).className="";
		}	
			
		/* remove spammer from db via AJAX*/
		runAjax("user=" + name, "unflag_spammer");
	}
	
	function generateApiKey(name) {
		if (name == null || name == "") {
			addLogMessage("please specify a user");
			return;
		}
		runAjax("user=" + name, "gen_api_key");
	}
		
	/* function interacts with server via ajax */
	function runAjax(parameter,action) {
		var request = initRequest(); 
		var url = "admin_tag_suggest.jsp?" + parameter;	   
	   	if (request) {    	   		
	   		request.open('GET',url + "&action=" + action + "&type=0",true);	
	   		var handle = ajax_updateLog(request); 	   		
	   		request.onreadystatechange = handle;
	   		request.send(null);		   		
	   	}    	
	}
	
	/* handler function */
	function ajax_updateLog(request) {   			
		return function() {			
			if (4 == request.readyState) {    	
		    	addLogMessage(request.responseText);		    			    	   
		    }
		}
	}
	
	/* add a message to log box */
	function addLogMessage(msg) {
		var division = document.getElementById("log");
		var li = document.createElement("LI");
		li.innerHTML = msg;
		division.insertBefore(li,division.firstChild);
	}	
	
	/* resets input fields */
	function clearFields() {		
		document.getElementsByName("user")[0].value = "";
		document.getElementsByName("user")[1].value = "";
	}
</script>

<%@ include file="footer.jsp"%>
