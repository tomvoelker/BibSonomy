<%@include file="include_jsp_head.jsp" %>

<c:if test="${empty user.name}">
   <jsp:forward page="/login"/>
</c:if>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="settings" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: <a href="/settings">settings</a></h1> 

 
<%@include file="/boxes/navi.jsp" %> 


<%-- SQL Queries for Groups, Groupusers and Friends --%>

<%-- get all groups of user --%>
<sql:query var="g" dataSource="${dataSource}">
  (SELECT i.group_name,i.group FROM groups g, groupids i WHERE g.user_name = ? AND g.group=i.group) 
   UNION 
  (SELECT i.group_name,i.group FROM groupids i WHERE i.group < 3 AND i.group >= 0) 
   ORDER BY `group`;
  <sql:param>${user.name}</sql:param>
</sql:query>

<%-- check, if usergroup exists --%>
<sql:query var="gadmin" dataSource="${dataSource}">
  SELECT g.user_name FROM groups g, groupids i WHERE g.user_name = ? AND i.group_name = ? AND g.group = i.group
  <sql:param>${user.name}</sql:param>
  <sql:param>${user.name}</sql:param>
</sql:query>

<%-- get users, which have user.name as friend --%>
<sql:query var="fr1" dataSource="${dataSource}">
  SELECT user_name FROM friends WHERE f_user_name = ?
  <sql:param value="${user.name}"/>  
</sql:query>

<%-- get all friends of user --%>
<sql:query var="fr2" dataSource="${dataSource}">
  SELECT f_user_name FROM friends WHERE user_name = ?
  <sql:param>${user.name}</sql:param>
</sql:query>

<%-- ------------------------ get users for own group  -------------------------- --%>

<c:set var="hasOwnGroup" value="false"/>
<c:forEach var="row" items="${gadmin.rows}">
  <c:if test="${row.user_name eq user.name}">
    <%-- get all users of usergroup --%>
    <sql:query var="gu" dataSource="${dataSource}">
      SELECT g.user_name FROM groups g, groupids i WHERE i.group_name = ? AND g.user_name != ? AND i.group=g.group ORDER BY g.user_name
      <sql:param>${user.name}</sql:param>
      <sql:param>${user.name}</sql:param>
    </sql:query>
    <c:set var="hasOwnGroup" value="true"/>
  </c:if>
</c:forEach>

<div id="general">

<%-- ------------------------ tab header -------------------------- --%>
<c:if test="${empty seltab || not empty param.seltab}">
	<c:choose>
		<c:when test="${empty param.seltab}">	
			<c:set var="seltab" value="1" scope="session"/>
		</c:when>
		<c:otherwise>
			<c:set var="seltab" value="${param.seltab}" scope="session"/>
		</c:otherwise>
	</c:choose>
</c:if>

<ul id="tab">	
	<li <c:if test="${seltab eq 1}">id="selected"</c:if> ><a href="?seltab=1">my profile</a></li>
	<li <c:if test="${seltab eq 2}">id="selected"</c:if> ><a href="?seltab=2">settings</a></li>
	<li <c:if test="${seltab eq 3}">id="selected"</c:if> ><a href="?seltab=3">imports</a></li>			
</ul>

<%-- ------------------------ tab content -------------------------- --%>
<div id="tab_content">
<c:choose>	

	<%-- ############################################# profile tab ############################################# --%>	
	<c:when test="${seltab eq 1}">
		<%-- ------------------------ change settings -------------------------- --%>
		<jsp:useBean id="settingsBean" class="beans.SettingsBean" scope="request">
		  <jsp:setProperty name="settingsBean" property="*"/>
		  <jsp:setProperty name="settingsBean" property="username" value="${user.name}"/>
		  <jsp:setProperty name="settingsBean" property="validCkey" value="${validckey}"/>
		</jsp:useBean>
		
		<% settingsBean.queryDB(); %> <%-- write data to database (if neccessary) --%>		
		
		<form name="account" method="post" action="/settings">		
		<h2>general information</h2>		
		<table>
		  <tr>
		    <td width="180px">username</td>
		    <td><c:out value="${settingsBean.username}"/></td>
		  </tr><tr>
		    <td>realname</td>
		    <td><input type="text" size="30" name="realname" value="${settingsBean.realname}"/></td>
		  </tr><tr>
		    <td>gender</td>
		    <td><select name="gender">
		    		<option value="m" <c:if test="${settingsBean.gender eq 'm'}">selected</c:if> >male</option>
		    		<option value="f" <c:if test="${settingsBean.gender eq 'f'}">selected</c:if>>female</option>
		    </select></td>
		  </tr><tr>
		    <td>birthday</td>
		    <td><input type="text" size="10" name="birthday" value="${settingsBean.birthday}"/>
		    	<span class="errmsg">${settingsBean.errors.birthday}</span></td>
		  </tr><tr>
		    <td>home country</td>
		    <td><input type="text" size="30" name="country" value="${settingsBean.country}"/></td>
		  </tr><tr>
		    <td>profile viewable for</td>
		    <td><select name="profileGroup">
		    	<option value="0" <c:if test="${settingsBean.profileGroup eq 0}">selected</c:if> >public</option>
		    	<option value="1" <c:if test="${settingsBean.profileGroup eq 1}">selected</c:if> >private</option>
		    	<option value="2" <c:if test="${settingsBean.profileGroup eq 2}">selected</c:if> >friends</option>
		    </select>
			</td>
		  </tr>
		</table>
		<hr/>
		
		<h2>contact</h2>		
		<table>
		  <tr>
		    <td width="180px">email</td>
		    <td><input type="text" size="30" name="email" value="${settingsBean.email}"/>
		        <span class="errmsg">${settingsBean.errors.email}</span></td>
		  </tr><tr>
		    <td>homepage</td>
		    <td><input type="text" size="30" name="homepage" value="${settingsBean.homepage}"/>
		        <span class="errmsg">${settingsBean.errors.homepage}</span></td>
		  </tr><tr>
		    <td>openURL</td>
		    <td><input type="text" size="30" name="openurl" value="${settingsBean.openurl}"/>
		        <span class="errmsg">${settingsBean.errors.openurl}</span></td>
		  </tr>
		</table>
		
		<hr/>
		<h2>about me</h2>	
		<table>
		  <tr>
		    <td width="180px">profession</td>
		    <td><input type="text" size="30" name="profession" value="${settingsBean.profession}"/></td>		        
		  </tr><tr>
		    <td>scientific interests</td>
		    <td><textarea cols="30" rows="4" name="interests"><c:out value="${settingsBean.interests}"/></textarea></td>
		  </tr><tr>
		    <td>hobbies</td>
		    <td><textarea cols="30" rows="4" name="hobbies"><c:out value="${settingsBean.hobbies}"/></textarea></td>
		  </tr>
		</table>
		
		<hr/>
		<h2>my friends</h2>			  
		<table>		
		<tr>
			<td width="180px">I'm a friend of</td>
			<td>
				<c:if test="${fr1.rowCount eq 0}">no friends found</c:if>
				<ul>
					<c:forEach var="myfr1" items="${fr1.rows}">			
		 				<li><a href="/user/<mtl:encode value='myfr1.user_name'/>"><c:out value="${myfr1.user_name}"/></a></li>		 		
					</c:forEach>
				</ul>
			</td>
		</tr>		
		<tr>
			<td>my friends are</td>
			<td>
				<c:if test="${fr2.rowCount eq 0}">no friends found (<a href="/friends">add</a> friends now)</c:if>
				<ul>
					<c:forEach var="myfr2" items="${fr2.rows}">			
		 				<li><a href="/user/<mtl:encode value='${myfr2.f_user_name}'/>"><c:out value="${myfr2.f_user_name}"/></a></li>		 		
					</c:forEach>
				</ul>
			</td>
		</tr>
		<tr>
			<td><br/>
				<input type="hidden" name="action" value="update"/>
		        <input type="hidden" name="ckey" value="${ckey}"/>
		        <input type="submit" value="save changes">
		    </td>		   
		</tr>
	</table>
	</form>	
	</c:when>
	
	<%-- ############################################# settings tab ############################################# --%>
	<c:when test="${seltab eq 2}">
		<%-- ------------------------ style settings -------------------------- --%>		
		<h2>adapt the layout of your tag box and post lists</h2>
		  <form method="POST" action="/settings">
		  <table>
		    <tr>
		      <td>show tags as </td>
		      <td>
		        <select name="style">
		          <option <c:if test="${user.tagboxStyle == 0}">selected="true"</c:if> >cloud</option>
		          <option <c:if test="${user.tagboxStyle == 1}">selected="true"</c:if> >list</option>
		        </select>
		      </td>
		    </tr>
		    <tr>
		        <td>sort tags by </td>
		        <td>
		          <select name="sort">
		            <option <c:if test="${user.tagboxSort == 0}">selected="true"</c:if> value="alph">alphabet</option>
		            <option <c:if test="${user.tagboxSort == 1}">selected="true"</c:if> value="freq">frequency</option>
		          </select>
		        </td>
		      </tr>
		      <tr>
		        <td>show only tags with minimal frequency</td>
		        <td>
		          <input type="text" name="minfreq" size="3" value="<c:out value='${user.tagboxMinfreq}'/>" />
		        </td>
		      </tr>
		      <tr>
		        <td>show tooltips for tags</td>
		        <td>
		          <select name="tooltip">
		            <option <c:if test="${user.tagboxTooltip == 0}">selected="true"</c:if> >no</option>
		            <option <c:if test="${user.tagboxTooltip == 1}">selected="true"</c:if> >yes</option>
		          </select>
		        </td>
		      </tr> 
		
		      <tr>
		        <td>show that many items per page</td>
		        <td>
		           <input type="text" name="items" size="3" value="<c:out value='${user.itemcount}'/>" />
		        </td>
		      </tr> 
		      <tr>
		        <td>
		          <input type="submit" name="tagbox_style" value="set layout"/>
		          <input type="hidden" name="ckey" value="${ckey}"/>
		        </td>
		    </tr>       
		  </table>
		</form>
		
		<hr/>
		
		<%-- ------------------------ add a group -------------------------- --%>
		<h2>group</h2>		
		<c:choose>
		  <c:when test="${hasOwnGroup}">
		
		  <%-- ------------------------ GROUP HANDLING -------------------------- --%>
		  <jsp:useBean id="gsBean" class="beans.GroupSettingsBean" scope="request">
		    <jsp:setProperty name="gsBean" property="*"/>
		    <jsp:setProperty name="gsBean" property="username" value="${user.name}"/>
		  </jsp:useBean>
		  <% gsBean.queryDB(); %> <%-- write data to database (if neccessary) --%>
		
		
		<c:choose><%-- ####################### TODO: unify this red box handling, put it into style.css #######################--%>
		
		<c:when test="${not empty param.user and not empty param.group and param.group eq user.name}">
		  <%-- User clicked link in email and wants to add (or cancel) user --%>
		  <div id="groups" style="border: .2em solid red; padding: .2em;">
		  <%-- cancel request only if right params existing --%>
		  <h3>cancel join request for user <a href="/user/<mtl:encode value='${param.user}'/>"><c:out value="${param.user}"/></a></h3>
		  <form name="groupcanceladduser" method="post" action="/JoinGroupHandler">
		    <input type="hidden" name="user" value="<c:out value='${param.user}'/>">
		    <input type="hidden" name="cancel" value="<c:out value='true'/>">
		    <label for="inputreason">reason</label> 
		    <input type="text" size="30" name="reason" id="inputreason"/>
		    <input type="submit" value="cancel"/>
		  </form>
		</c:when>
		
		<c:otherwise>
		  <div id="groups">
		</c:otherwise>
		</c:choose>
		
		  <h3>add user to my group</h3>
		  <form name="groupusers" method="post" action="/SettingsHandler">
		    <label for="laddgu">username</label> <input type="text" size="30" name="add_group_user" id="laddgu" value="<c:out value='${param.user}'/>" />
		    <input type="hidden" name="ckey" value="${ckey}"/>
		    <input type="submit" value="add user" />
		  </form>
		
		  </div>
		 
		  <h3>privacy level</h3> 
		  <form name="groupPrivLevel" method="post" action="/settings">
		    member list is 
		    <select name="privlevel">
		      <option value="0" <c:if test="${gsBean.privlevel eq 0}">selected="true"</c:if>>public</option>
		      <option value="1" <c:if test="${gsBean.privlevel eq 1}">selected="true"</c:if>>hidden</option>
		      <option value="2" <c:if test="${gsBean.privlevel eq 2}">selected="true"</c:if>>public for members</option>
		    </select>
		    <input type="hidden" value="update_privlevel" name="action"/>
		    <input type="submit" value="change" />
		  </form>
		  
		</c:when>
		<c:otherwise>
		  
		<h3>add a group</h3>
		<p>
		  If you want a group added to the system, create an user account with the desired group name and write an E-Mail to 
		  <%@include file="/boxes/emailaddress.jsp" %>.
		  During the first test phase, group accounts are only available to selected projects. 
		  We will relax this constraint later.
		</p>
		
		</c:otherwise>
		</c:choose>		
		
		<%-- user is logged in with wrong account --%>
		<%-- ####################### TODO: unify this red box handling, put it into style.css #######################--%>
		<c:if test="${not (empty param.user or empty param.group or param.group eq user.name)}">
		  <div id="groups" style="border: .2em solid red; padding: .2em;">
		  Sorry, you're not logged in with the corresponding group account. Please <a href="/login">login</a> as user <c:out value="${param.group}"/>.
		  </div>
		</c:if>
		
		<hr/>			
		
		<%-- ------------------------ password change -------------------------- --%>
		<jsp:useBean id="registrationHandlerBean" class="beans.RegistrationHandlerBean" scope="request"/>
		
		<h2>change password</h2>
		<form name="password" method="post" action="/passwordchange_process">
		  <table>
		    <tr>
			  <td>current password</td>
			  <td>
			    <input type="password" size="30" name="currPass" value="" maxlength="30">
			    <div class="errmsg">${registrationHandlerBean.errors.currPass}</div>
			  </td>
			  <td></td>
			</tr>
		    <tr>
			  <td>new password</td>
			  <td>
			    <input type="password" size="30" name="password1" value="" maxlength="30">
			    <div class="errmsg">${registrationHandlerBean.errors.password1}</div>
			  </td>
			  <td>(required :: without spaces)</td>
			</tr>
			<tr>
			  <td>new password</td>
			  <td>
				 <input type="password" size="30" name="password2" value="">
				 <div class="errmsg">${registrationHandlerBean.errors.password2}</div>
			  </td>
			  <td>(please confirm password)</td>
			</tr>
			<tr>
			  <td><input type="submit" value="change password"></td>
			  <td></td>
			</tr>
		  </table>
		</form>
		
		<hr/>
		
		<%-- ------------------------ delete account -------------------------- --%>
		<h2>delete my account</h2>		
		<form name="delete" method="post" action="/SettingsHandler">
		  <table>
		     <tr>
		       <td>really?</td>
		       <td><input type="text" size="30" name="delete" value="no"/></td>
		       <td>(type <em>yes</em> to delete your account)</td>
		     </tr>
		     <tr>
		       <td><input type="submit" value="delete account">
		           <input type="hidden" name="ckey" value="${ckey}"/>
		       </td>
		       <td></td>
		       <td></td>
		     </tr>
		  </table>		
		</form>		
	</c:when>
	
	<%-- ############################################# import tab ############################################# --%>
	<c:otherwise>	
		<%-- ------------------------ firefox import -------------------------- --%>
		<h2>import your bookmarks from Firefox:</h2>
		<form enctype="multipart/form-data" method="POST" action="/bookmarkHandler?import=firefox">
		  <table> 
		   <tr>
		   <td>select your file:</td>
		   <td><input name="file" type="file"  size="20"/></td></tr>
		   <tr>
		      <td>viewable for</td>
		      <td><select name="group"><c:forEach var="group" items="${g.rows}">
		         <option value="${group.group_name}">${group.group_name}</option>
		      </c:forEach></select></td>
		    </tr>
		   <tr><td></td><td><input type="checkbox" name="overwrite" value="yes">Firefox bookmarks overwrite ${projectName} bookmarks</td></tr>
		   <tr><td><input type="submit" value="import bookmarks"></td><td></td></tr>
		  </table> 
		</form>
		
		<hr/>
		
		<%-- ------------------------ delicious import -------------------------- --%>
		<h2>import your del.icio.us data:</h2>
		<form method="POST" action="/bookmarkHandler">
	  	<table>
	    	<tr><td>username</td><td><input type="text" name="username" size="30"/></td></tr>
	    	<tr><td>password</td><td><input type="password" name="password" size="30"/></td></tr>
	    	<tr>
	      		<td>viewable for</td>
	      		<td><select name="group"><c:forEach var="group" items="${g.rows}">
	         	<option value="${group.group_name}">${group.group_name}</option>
	      		</c:forEach></select></td>
	    	</tr>
	    	<tr><td></td><td><input type="checkbox" name="overwrite" value="yes">del.icio.us bookmarks overwrite ${projectName} bookmarks</td></tr>
	    	<tr><td>
	      		<input type="hidden" name="ckey" value="${ckey}"/>
	      		<input type="hidden" name="import" value="delicious"/>
	      		<input type="submit" value="import bookmarks"/>
	    	</td><td></td></tr>
	  	</table>
	  	</form>
	  
		<hr/>

		<%-- ------------------------ handle layout file ------------------------ ---%>
		<jsp:useBean id="layoutBean" class="beans.LayoutBean" scope="request">
		  <jsp:setProperty name="layoutBean" property="username" value="${user.name}"/>
		</jsp:useBean>
		
		<a name="layout"></a>
		
		<h2>JabRef layout file</h2>
		
		<p>
		For an explanation on how to write such export filters, have a look at 
		<a href="http://jabref.sourceforge.net/help/CustomExports.html">the JabRef page</a>.
		You (and only you!) can use these layouts to show ${projectName}'s publication lists
		in a format of your choice. After uploading the correct file(s) have a look at
		<a href="/layout/custom/user/<c:out value='${user.name}'/>">my${projectName}</a> to see 
		how it looks.
		</p>
		
		<form enctype="multipart/form-data" method="post" action="/LayoutHandler" style="display:inline;">
		  <table>
		    <tr> 
		      <td>begin.layout</td>
		      <td><c:choose>
		        <c:when test="${empty layoutBean.beginName}"><input name="file.begin" type="file" size="20"/></c:when>
		        <c:otherwise><c:out value="${layoutBean.beginName} "/><a class="bmaction" href="/LayoutHandler?hash=${layoutBean.beginHash}&amp;action=delete">delete</a></c:otherwise>
		      </c:choose></td>
		      <td>(appears at the beginning of the output, optional)</td>
		    </tr><tr>
		      <td>item.layout</td>
		      <td><c:choose>
		        <c:when test="${empty layoutBean.itemName}"><input name="file.item" type="file" size="20"/></c:when>
		        <c:otherwise><c:out value="${layoutBean.itemName} "/><a class="bmaction" href="/LayoutHandler?hash=${layoutBean.itemHash}&amp;action=delete">delete</a></c:otherwise>
		      </c:choose></td>
		      <td>(used to render each publication item)</td>
		    </tr><tr>
		      <td>end.layout</td>
		      <td><c:choose>
		        <c:when test="${empty layoutBean.endName}"><input name="file.end" type="file" size="20"/></c:when>
		        <c:otherwise><c:out value="${layoutBean.endName} "/><a class="bmaction" href="/LayoutHandler?hash=${layoutBean.endHash}&amp;action=delete">delete</a></c:otherwise>
		      </c:choose></td>
		      <td>(appears at the end of the output, optional)</td>
		    </tr><tr>
		      <td><input type="submit" value="upload" /><div class="errmsg"><c:out value="${layoutBean.error}"/></div></td>
		      <td></td>
		    </tr>
		  </table>
		</form>	
	</c:otherwise>
</c:choose>
</div>
</div>

<%-- ------------------------ right box -------------------------- --%>
<ul id="sidebar">
  
  <c:if test="${hasOwnGroup}">
    <li>
    <span class="sidebar_h">mygroup</span>
    <ul><c:forEach var="row" items="${gu.rows}">
      <li>
        <a href="/user/${row.user_name}">${row.user_name}</a>
        <a class="bmaction" href="/SettingsHandler?del_group_user=<mtl:encode value='${row.user_name}'/>&ckey=${ckey}">del</a>
      </li>
    </c:forEach></ul>
    </li>
  </c:if>

  <li>    
  <span class="sidebar_h"><a href="/groups">groups</a></span>
  <ul><c:forEach var="row" items="${g.rows}" begin="3">
    <li><a href="/group/${row.group_name}">${row.group_name}</a></li>
  </c:forEach></ul>
  </li>
 
</ul>

<%@ include file="footer.jsp" %>