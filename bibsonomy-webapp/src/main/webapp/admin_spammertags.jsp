<%@include file="include_jsp_head.jsp" %>

<%@include file="/boxes/admin/login.jsp"%>

<script type="text/javascript" src="/ajax/scriptaculous/lib/prototype.js"></script>
<script type="text/javascript" src="/ajax/scriptaculous/src/scriptaculous.js"></script> 
<script type="text/javascript" src="/resources/javascript/ajax-dynamic-content.js"></script>
<script type="text/javascript" src="/resources/javascript/ajax.js"></script>
<script type="text/javascript" src="/resources/javascript/ajax-tooltip.js"></script>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="admin" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start"> ${projectName} </a> :: <a rel="path_menu" href="admin.jsp">admin&nbsp;<img src="/resources/image/box_arrow.png"></a></h1>

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp"%>

<%-- ------------------------ change settings -------------------------- --%>
<jsp:useBean id="adminBean" class="beans.AdminBean">
	<jsp:setProperty name="adminBean" property="*"/>
</jsp:useBean>

<% adminBean.queryDB(); %>

<div id="fullscreen">

<%@include file="/boxes/admin/navi.jsp"%>

<%--------------------------infos/errors -----------------------%>
<p style="font-weight:bold; color:#ff0000;">
	<c:forEach var="info" items="${adminBean.infos}">Info: <c:out value="${info}"/></c:forEach>
	<c:forEach var="error" items="${adminBean.errors}">Error: <c:out value="${error}"/></c:forEach>
</p>

<%-------------------------- add / remove tags or spammers -----------------------%>
<table width="100%">
	<tr>
		<td style="padding-right: 30px">
			<h2>add/remove spammertags</h2>
			<form action="/admin_spammertags.jsp">
			<b>spammertag</b>:		
			<input type="text" name="tag" id="acl_spamtag1" />
			<input type="hidden" name="action" value="addtag"/>
			<input type="submit" value=" OK " />		
			</form>	
			<div id="autocomplete_spammertag" class="autocomplete"></div>
			<script type="text/javascript">
			  new Ajax.Autocompleter("acl_spamtag1","autocomplete_spammertag","admin_tag_suggest.jsp?type=1");
			</script>
		
			<form action="/admin_spammertags.jsp">
			<b style="text-decoration:line-through;">spammertag</b>: 		
			<input type="text" name="tag" id="acl_spamtag2" />
			<input type="hidden" name="action" value="rmvtag"/>
			<input type="submit" value=" OK " />	
			</form>
			<div id="autocomplete_spammertag2" class="autocomplete"></div>
			<script type="text/javascript">
			  new Ajax.Autocompleter("acl_spamtag2","autocomplete_spammertag2","admin_tag_suggest.jsp?type=2");
			</script>	
		</td>
		<td>
			<h2>flag/unflag a spammer</h2>	
			<%-- spammer form  --%>
			<form action="/admin_spammertags.jsp">
			  <b>spammer:</b> user name <input type="text" name="user" id="acl_spam" />
			  <input type="hidden" name="action" value="flag_spammer"/>
			  <input type="submit" value=" OK "/>
			</form>			
			<div id="autocomplete_spam" class="autocomplete"></div>
			<script type="text/javascript">
			  new Ajax.Autocompleter("acl_spam","autocomplete_spam","admin_suggest.jsp?type=0");
			</script>
			
			<form action="/admin_spammertags.jsp">
			  <b style="text-decoration:line-through;">spammer:</b> user name <input type="text" name="user" id="acl_unspam"/>
			  <input type="hidden" name="action" value="unflag_spammer"/>
			  <input type="submit" value=" OK "/>
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

<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/>

<%-------------------------- list of marked spammer tags (ordered by
							 count of usage in last 1000 posts) ----------%>	
<sql:query var="count" dataSource="${dataSource}">
	SELECT COUNT(*) AS anz FROM spammer_tags WHERE spammer = 1
</sql:query>

<sql:query var="rs" dataSource="${dataSource}">
	SELECT tas_tags.tag_name, COUNT(*) AS anz_tags FROM  
	(
	    SELECT tag_name FROM  
	    (
	        SELECT DISTINCT content_id FROM tas t 
	        ORDER BY date DESC
	        LIMIT 1000 
	    ) AS tas_ids
	    JOIN tas t USING (content_id)
	) AS tas_tags
	JOIN spammer_tags s ON (tas_tags.tag_name = s.tag_name AND s.spammer = 1)
	GROUP BY tas_tags.tag_name
	ORDER BY anz_tags DESC
	LIMIT 30
</sql:query>

<div class="threecolbox">
<h2>list of spammertags (<c:out value="${count.rows[0].anz}"/>)</h2>
<br>
<% System.out.println("0"); %>

<table cellpadding="2" class="taglist" >	
	<tr>
		<th>tag</th>
		<th>count</th>		
		<th>action</th>
	</tr>
	<tbody id="spammertags">
	<c:forEach var="rows" items="${rs.rows}" varStatus="status">		
	<tr id="st<c:out value='${status.count}'/>">
		<td><a href="/tag/<mtl:encode value='${rows.tag_name}'/>"><c:out value="${rows.tag_name}"/></a></td>
		<td><c:out value="${rows.anz_tags}"/></td>		
		<td>
			<a href="javascript:removeSpammertag('<mtl:encode value="${rows.tag_name}"/>','spammertags','st<c:out value='${status.count}'/>')" title="remove from list"><img src="/resources/image/minus.png"/></a>
			<a onclick="javascript:ajax_showTooltip('admin_tag_suggest.jsp?type=3&tag=<mtl:encode value='${rows.tag_name}'/>',this);return false;" style="cursor:pointer" title="show related tags"><img src="/resources/image/rel.png"/></a>
		</td>
	</tr>
	</c:forEach>
	</tbody>	
</table>	
</div>

<%-------------------------- suggestions -----------------------%>

<%-------------------------- busy spammertags (tags of last 10000 tas which  
							 have a spammer group but not listed in spammer tags yet) -----%>
<div class="threecolbox">
<h2>tag suggestions</h2>

<sql:query var="busytags" dataSource="${dataSource}">
	SELECT tag_name, tag_anzahl
	FROM
	(
	   SELECT
	   t.tag_name, COUNT(t.tag_name) AS tag_anzahl
	   FROM
	   (
	      SELECT
	      t.tag_name
	      FROM tas t
	      LEFT JOIN spammer_tags s ON (t.tag_name = s.tag_name AND s.spammer = 1)
	      WHERE ISNULL(s.tag_name)
	      AND t.group = -2147483648
	      ORDER BY date DESC LIMIT 10000
	   )
	   AS t
	   GROUP BY t.tag_name
	   ORDER BY 2 DESC LIMIT 30
	)
	AS t1
	ORDER BY 1
</sql:query>

<table cellpadding="2" class="taglist">
	<tr>
		<th>tag</th>
		<th>count</th>		
		<th>action</th>
	</tr>
	<tbody id="busytaglist">
	<c:forEach var="rows" items="${busytags.rows}" varStatus="status">		
	<tr id="bt<c:out value='${status.count}'/>">
		<td><a href="/tag/<mtl:encode value='${rows.tag_name}'/>"><c:out value="${rows.tag_name}"/></a></td>
		<td><c:out value="${rows.tag_anzahl}"/></td>
		<td>
			<a href="javascript:addSpammertag('<c:out value="${rows.tag_name}"/>','busytaglist','bt<c:out value='${status.count}'/>')" title="mark as spammertag"><img src="/resources/image/plus.png"/></a>
			<a href="javascript:cleanTag('<c:out value="${rows.tag_name}"/>','busytaglist','bt<c:out value='${status.count}'/>')" title="remove tag from suggestion list"><img src="/resources/image/minus.png"/></a>
		</td>
	</tr>
	</c:forEach>	
	</tbody>
</table>	
</div>

<%-------------------------- list of users not marked as spammers but post spammertags -----------------------%>
<div class="threecolbox">

<h2>spammer suggestions</h2>

<sql:query var="spammers" dataSource="${dataSource}">
	SELECT t.user_name AS user, t.tag_name AS tag 
	FROM (
		SELECT user_name, MAX(t.date) AS date 
		FROM tas t 
			JOIN spammer_tags s USING (tag_name)
			JOIN user u USING (user_name)
		WHERE s.spammer = 1 AND u.spammer = 0
		AND u.spammer_suggest = 1
		GROUP BY u.user_name
		ORDER BY MAX(t.date) DESC 
		LIMIT 30) AS tas2
	JOIN tas t ON (t.user_name = tas2.user_name AND t.date = tas2.date)
	JOIN spammer_tags s ON (t.tag_name = s.tag_name AND s.spammer = 1)
	GROUP BY t.user_name
	ORDER BY MAX(t.date) DESC  
</sql:query>

<table cellpadding="2" class="taglist">
	<tr>
		<th>user</th>
		<th>tagged</th>		
		<th>action</th>
	</tr>
	<tbody id="spammerlist">
	<c:forEach var="rows" items="${spammers.rows}" varStatus="status">		
		<tr id="sl<c:out value='${status.count}'/>">
			<td><a href="/user/<mtl:encode value='${rows.user}'/>"><c:out value="${rows.user}"/></a></td>
			<td><c:out value="${rows.tag}"/></td>
			<td>
				<a href="javascript:addSpammer('<mtl:encode value="${rows.user}"/>','spammerlist','sl<c:out value="${status.count}"/>')" title="flag this user as spammer">
    	 			<img src="/resources/image/plus.png"/>
    	 		</a>    	 		
    	 		<a href="javascript:removeUser('<mtl:encode value="${rows.user}"/>','spammerlist','sl<c:out value="${status.count}"/>')" title="remove user from suggestion list">
    	 			<img src="/resources/image/minus.png"/>
    			</a> 				
			</td>
		</tr>
	</c:forEach>
	</tbody>	
</table>	
</div>

</div> <!-- ende fullscreen -->


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
	
	function addSpammertag(name, parentId, rowId) {		
		/* remove item from old list */
		removeItem(rowId, parentId);	
			
		/* add spammertga in db via AJAX*/
		runAjax("tag=" + name, "addtag");
		
		/* add new node in spammertag list */
		var tr = document.createElement("TR");
		tr.id = name;
		
		var nameTD = document.createElement("TD");
		var linkName = document.createElement("A");
		linkName.href = "/tag/" + name;
		linkName.innerHTML = name;
		
		var countTD = document.createElement("TD");
		countTD.innerHTML = "-";
		
		var linkTD = document.createElement("TD");
		var remLink = document.createElement("A");
		remLink.href = "javascript:removeSpammertag('" + name + "','spammertags','" + name + "')";
		remLink.innerHTML = "<img src='/resources/image/minus.png'/>";
		
		var relLink = document.createElement("A");
		relLink.onclick = function() {ajax_showTooltip('admin_tag_suggest.jsp?type=3&tag=' + name ,this); return false;}		
		relLink.style.cursor = "pointer";
		relLink.innerHTML = "<img src='/resources/image/rel.png'/>";
		
		/* append new node */
		nameTD.appendChild(linkName);
		linkTD.appendChild(remLink);
		linkTD.appendChild(relLink);		
		tr.appendChild(nameTD);
		tr.appendChild(countTD);
		tr.appendChild(linkTD);		
		addItem(tr,"spammertags","first");
	}	
	
	function removeSpammertag(name, parentId, rowId) {		
		/* remove item from old list */
		removeItem(rowId, parentId);	
			
		/* remove spammertag from db via AJAX*/
		runAjax("tag=" + name, "rmvtag");
	}	
	
	function cleanTag(name, parentId, rowId) {		
		/* remove item from old list */
		removeItem(rowId, parentId);	
			
		/* remove spammertag from db via AJAX*/
		runAjax("tag=" + name, "cleantag");
	}
	
	function addSpammer(name, parentId, rowId) {		
		/* remove item from old list */
		removeItem(rowId, parentId);	
			
		/* remove spammertag from db via AJAX*/
		runAjax("user=" + name, "flag_spammer");
	}
	
	function removeUser(name, parentId, rowId) {		
		/* remove item from old list */
		removeItem(rowId, parentId);	
			
		/* remove spammertag from db via AJAX*/
		runAjax("user=" + name, "remove_user");
	}
	
	
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
	
	function ajax_updateLog(request) {   			
		return function() {			
			if (4 == request.readyState) {    	
		    	var division = document.getElementById("log");
		    	var li = document.createElement("LI");
		    	li.innerHTML = request.responseText;
		    	division.insertBefore(li,division.firstChild);		    	   
		    }
		}
	}
	
	function removeItem(item) {
		var item = document.getElementById(item);		
		item.style.display = "none";		
	}
	
	function addItem(item, parent, pos) {
		var parent = document.getElementById(parent);		
				
		if (pos == 'last') {
			parent.appendChild(item);	
		} else {			
			parent.insertBefore(item,parent.firstChild);
		}	
	}	
</script>

<%-------------------------- footer -----------------------%>
<%@ include file="footer.jsp" %>