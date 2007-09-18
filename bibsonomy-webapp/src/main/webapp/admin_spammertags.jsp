<%@include file="include_jsp_head.jsp" %>

<%@include file="/boxes/admin/login.jsp"%>

<script type="text/javascript" src="/ajax/scriptaculous/lib/prototype.js"></script>
<script type="text/javascript" src="/ajax/scriptaculous/src/scriptaculous.js"></script> 

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="admin" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start"> ${projectName} </a> :: <a href="admin.jsp">admin</a></h1>

<%@include file="/boxes/navi.jsp"%>

<%-- ------------------------ change settings -------------------------- --%>
<jsp:useBean id="adminBean" class="beans.AdminBean">
	<jsp:setProperty name="adminBean" property="*"/>
</jsp:useBean>

<% adminBean.queryDB(); %>

<div id="general">

<%@include file="/boxes/admin/navi.jsp"%>

<%--------------------------infos/errors -----------------------%>
<p style="font-weight:bold; color:#ff0000;">
	<c:forEach var="info" items="${adminBean.infos}">Info: <c:out value="${info}"/></c:forEach>
	<c:forEach var="error" items="${adminBean.errors}">Error: <c:out value="${error}"/></c:forEach>
</p>
<%-------------------------- add / remove tags -----------------------%>
<h2>add/remove spammertags</h2>
<table>
	<tr>
		<form action="/admin_spammertags.jsp">
		<td align="right">add <b>spammertag</b>:</td> 
		<td>
			<input type="text" name="tag" id="acl_spam" />
			<input type="hidden" name="action" value="addtag"/>
			<input type="submit" value=" OK " />
		</td>
		</form>
		<div id="autocomplete_spam" class="autocomplete"></div>
		<script type="text/javascript">
		  new Ajax.Autocompleter("acl_spam","autocomplete_spam","admin_tag_suggest.jsp?type=1");
		</script>
	</tr>
	<tr>
		<form action="/admin_spammertags.jsp">
		<td align="right">remove <b>spammertag</b>:</td> 
		<td>
			<input type="text" name="tag" id="acl_spam2" />
			<input type="hidden" name="action" value="rmvtag"/>
			<input type="submit" value=" OK " />
		</td>
		</form>
		<div id="autocomplete_spam2" class="autocomplete"></div>
		<script type="text/javascript">
		  new Ajax.Autocompleter("acl_spam2","autocomplete_spam2","admin_tag_suggest.jsp?type=2");
		</script>
	</tr>
</table>

<hr>

<%-------------------------- list of marked spammer tags -----------------------%>	
<sql:setDataSource dataSource="jdbc/bibsonomy" var="dataSource"/>	

<%-------------------------- spammertags count -----------------------%>
<c:if test="${empty tagcount || not empty param.tagcount}">
	<c:choose>
		<c:when test="${empty param.tagcount}">
			<c:set var="tagcount" value="50" scope="session"/>
		</c:when>
		<c:otherwise>
			<c:set var="tagcount" value="${param.tagcount}" scope="session"/>
		</c:otherwise>
	</c:choose>
</c:if>

<div id="bookbox">

<sql:query var="count" dataSource="${dataSource}">
	SELECT COUNT(*) AS anz FROM spammer_tags WHERE spammer = 1
</sql:query>
<h2>list of spammertags (<c:out value="${count.rows[0].anz}"/>)</h2>

<div style="font-size:70%;"> 
	(show 
	<a href="?tagcount=25">25</a>,
	<a href="?tagcount=50">50</a>,
	<a href="?tagcount=100">100</a>, 
	<a href="?tagcount=1000">1000</a> or 
	<a href="?tagcount=10000">all</a> spammertags)
</div>
<br>
<% System.out.println("0"); %>

<sql:query var="rs" dataSource="${dataSource}">
	SELECT tag_name
      FROM spammer_tags
      WHERE spammer = 1
      ORDER BY LOWER(tag_name) ASC
      LIMIT 25
</sql:query>
<c:choose>
	<c:when test="${rs.rowCount eq 0}">
		- no spammertags listed yet -	
	</c:when>
	<c:otherwise>
		<table cellpadding="2" class="taglist">
			<tr>
				<th>tag</th>
				<th colspan="2">action</th>
			</tr>
			<c:forEach var="rows" items="${rs.rows}" >		
				<tr>
					<td> <a href="/tag/<mtl:encode value='${rows.tag_name}'/>"><c:out value="${rows.tag_name}"/></a></td>
					<td style="background-color:#eeeeee; text-align:center;"><a href="admin_spammertags.jsp?tag=<mtl:encode value='${rows.tag_name}'/>&action=rmvtag">REMOVE</a></td>
					<td style="background-color:#eeeeee; text-align:center;"><a href='javascript:updateRelatedTags("<mtl:encode value='${rows.tag_name}'/>")'>RELATED</a></td>
				</tr>
			</c:forEach>	
		</table>	
	</c:otherwise>
</c:choose>
</div>


<% System.out.println("1");%>
<%-------------------------- tag suggestions -----------------------%>

<%-------------------------- suggestions count -----------------------%>
<c:if test="${empty sugcount || not empty param.sugcount}">
	<c:choose>
		<c:when test="${empty param.sugcount}">
			<c:set var="sugcount" value="25" scope="session"/>
		</c:when>
		<c:otherwise>
			<c:set var="sugcount" value="${param.sugcount}" scope="session"/>
		</c:otherwise>
	</c:choose>
</c:if>

<div id="bibbox">
<h2>suggested tags</h2>

<div style="font-size:70%;"> 
	(show 
	<a href="?sugcount=25">25</a>,
	<a href="?sugcount=50">50</a>,
	<a href="?sugcount=100">100</a>, 
	<a href="?sugcount=1000">1000</a> or 
	<a href="?sugcount=10000">all</a> suggestions)
</div>

<div style="font-size:70%;margin-left:5px; margin-top:15px; margin-bottom:15px;">
	<a href="?recalc=true">update suggestion lists and tag cloud</a>
</div>

<%-------------------------- most popular tags -----------------------%>

<c:if test="${not empty param.recalc || empty poptags || not empty param.sugcount}">
<sql:query var="querypoptags" dataSource="${dataSource}">
	SELECT t.tag_name , COUNT(t.tag_name) AS tag_count
      FROM tas t
        LEFT JOIN spammer_tags s USING (tag_name)
      WHERE ISNULL(s.tag_name) 
        AND t.group = -2147483648 
      GROUP BY t.tag_name
      ORDER BY tag_count DESC
      LIMIT 25
</sql:query>
<c:set var="poptags" value="${querypoptags}" scope="session"/>
</c:if>

<h3>most popular tags used by spammers</h3>

<br>
<table class="taglist" cellpadding="2" cellspacing="2" border="0">	
	<tr>		
		<th>tag</th>
		<th>count</th>
		<th colspan="2">action</th>		
	</tr>
	<c:forEach var="tag" items="${poptags.rows}">
		<tr>			
			<td> <a href="/tag/<mtl:encode value='${tag.tag_name}'/>"><c:out value="${tag.tag_name}"/></a></td>
			<td align="center"><c:out value="${tag.tag_count}"/></td>
			<td align="center" style="background-color:#eeeeee">
				<a href="admin_spammertags.jsp?tag=<mtl:encode value='${tag.tag_name}'/>&action=addtag">ADD</a>
			</td>
			<td align="center" style="background-color:#eeeeee">
				<a href="admin_spammertags.jsp?tag=<mtl:encode value='${tag.tag_name}'/>&action=cleantag">REMOVE</a>
			</td>
		</tr>
	</c:forEach>
</table>
<br>

<% System.out.println("2"); %>
<%-------------------------- related tags to one special spammer tag -----------------------%>

<span id="related">
<h3>related tags to chosen spammertag <c:out value="${reltag}"/></h3>
<br/>
<c:if test="${empty reltags}">	
	<c:if test="${empty reltag}">
		- please choose a tag from your spammertag list -
	</c:if>
	<c:if test="${not empty reltag}">	
		- no related tags found for <c:out value="${reltag}"/> -
	</c:if>
</c:if>
<c:if test="${not empty reltags}">
	<table class="taglist">	
		<tr>
			<th>tag</th>
			<th>weight</th>
			<th colspan="2">action</th>
		</tr>
		<c:forEach var="tag" items="${reltags.rows}">
			<tr>
				<td><a href="/tag/<mtl:encode value='${tag.sug_tag}'/>"><c:out value="${tag.sug_tag}"/></a></td>
				<td align="center"><c:out value="${tag.weight}"/></td>
				<td align="center" style="background-color:#eeeeee">
					<a href="admin_spammertags.jsp?tag=<mtl:encode value='${tag.sug_tag}'/>&action=addtag">ADD</a>
				</td>
				<td align="center" style="background-color:#eeeeee">
					<a href="admin_spammertags.jsp?tag=<mtl:encode value='${tag.sug_tag}'/>&action=cleantag">REMOVE</a>
				</td>
			</tr>
		</c:forEach>
	</table> 
</c:if>
</span> 

</div>
</div>
<% System.out.println("3"); %>
<%-------------------------- tagcloud of busy spammertags -----------------------%>
<ul id="sidebar">

<li>

<span class="sidebar_h">busy spammertags</span>

<c:if test="${not empty param.recalc || empty busytags}">
<sql:query var="querybusytags" dataSource="${dataSource}">
	SELECT tag_name, LOWER(tag_name) AS x, tag_anzahl, ROUND(LOG(IF(tag_anzahl>100, 100, tag_anzahl+6)/6))*60+40  AS class
	FROM ( 
	  SELECT t.tag_name, COUNT(t.tag_name) AS tag_anzahl
	  FROM 
	  	(SELECT t.tag_name 
	     FROM tas t
	     LEFT JOIN spammer_tags s ON (t.tag_name = s.tag_name)
	     WHERE ISNULL(s.tag_name) AND 
				t.group = -2147483648
	      ORDER BY date desc
	      LIMIT 10000) AS t      
	      GROUP BY t.tag_name
	      ORDER BY 2 DESC
	      LIMIT 100
	    ) AS t1
	ORDER BY 2;
</sql:query>
<c:set var="busytags" value="${querybusytags}" scope="session"/>
</c:if>

<ul class="tagcloud">
	<c:forEach var="row" items="${busytags.rows}">
    	<li><a style="font-size:${row.class}%" title="${row.tag_anzahl}" href="admin_spammertags.jsp?tag=<mtl:encode value='${row.tag_name}'/>&action=addtag">
    	<c:out value='${row.tag_name}'/>
    	</a></li>
  	</c:forEach>
</ul>
</li>
<br>

<%-------------------------- list of users not marked as spammers but post spammertags -----------------------%>
<li>
<span class="sidebar_h">spammer suggestions</span>

<c:if test="${not empty param.recalc || empty spammers}">
<sql:query var="queryspammers" dataSource="${dataSource}">
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
		LIMIT 50) AS tas2
	JOIN tas t ON (t.user_name = tas2.user_name AND t.date = tas2.date)
	JOIN spammer_tags s ON (t.tag_name = s.tag_name AND s.spammer = 1)
	GROUP BY t.user_name
	ORDER BY MAX(t.date) DESC  
</sql:query>
<c:set var="spammers" value="${queryspammers}" scope="session"/>
</c:if>



<ul>
	<c:forEach var="row" items="${spammers.rows}">
    	<li>
    		<a style="font-size:100%" title="flag this user as spammer" href="/user/<mtl:encode value='${row.user}'/>">
    			<c:out value='${row.user}'/>
    		</a> 
    		<span style="font-size:70%">tagged <c:out value="${row.tag}"/> 
    		<a style="font-size:100%" style="background-color:#eeeeee" title="flag this user as spammer" href="admin_spammertags.jsp?user=<mtl:encode value='${row.user}'/>&action=flag_spammer">
    	 		ADD
    		</a> 
    		<a style="font-size:100%" style="background-color:#eeeeee" title="remove user from suggestion list" href="admin_spammertags.jsp?user=<mtl:encode value='${row.user}'/>&action=remove_user">
    	 		REMOVE
    		</a> 
    		</span>
    	</li>
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
	
	function updateRelatedTags(tag) {  			
		var request = initRequest(); 		   
	   	if (request) {    		
	   		var url = "/admin_tag_suggest.jsp?type=3&tag=" + tag;
	   		request.open('GET',url,true);	
	   		var handle = ajax_updateRelated(request); 	   		
	   		request.onreadystatechange = handle;
	   		request.send(null);		   		
	   	}    	
	}
	        
	function ajax_updateRelated(request) {   			
		return function() {			
			if (4 == request.readyState) {    	
		    	var division = document.getElementById("related");
		    	division.innerHTML = request.responseText;
		   
		    }
		}
	}
</script>

<%-------------------------- footer -----------------------%>
<%@ include file="footer.jsp" %>