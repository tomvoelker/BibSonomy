	<%-- ############################################# profile tab ############################################# --%>	
		<%-- ------------------------ change settings -------------------------- --%>
		<jsp:useBean id="settingsBean" class="beans.SettingsBean" scope="request">
		  <jsp:setProperty name="settingsBean" property="*"/>
		  <jsp:setProperty name="settingsBean" property="name" value="${user.name}"/>
		  <jsp:setProperty name="settingsBean" property="validCkey" value="${validckey}"/>
		</jsp:useBean>
		
		<% settingsBean.queryDB(); %> <%-- write data to database (if neccessary) --%>		

        <p>The information you enter here can be accessed via <a href="/foaf/user/<mtl:encode value='${user.name}'/>">/foaf/user/<c:out value="${user.name}"/></a>.
           This feature is experimental and will be extended in the future. 
           With "profile viewable for" you can adjust who can see this information. </p>
		
		<form name="account" method="post" action="/settings">		
	
        <h2>general information</h2>		
    
		<table>
		  <tr>
		    <td width="180px">user name</td>
		    <td><c:out value="${settingsBean.name}"/></td>
		  </tr><tr>
		    <td>real name</td>
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
		    	<span class="errmsg">${settingsBean.errors.birthday}</span>format: YYYY-MM-DD</td>
		  </tr><tr>
		    <td>place</td>
		    <td><input type="text" size="30" name="place" value="${settingsBean.place}"/></td>
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
                mandatory; not included in your public profile
		        <span class="errmsg">${settingsBean.errors.email}</span></td>
		  </tr><tr>
		    <td>homepage</td>
		    <td><input type="text" size="30" name="homepage" value="${settingsBean.homepage}"/>
		        <span class="errmsg">${settingsBean.errors.homepage}</span></td>
		  </tr><tr>
		    <td>openURL</td>
		    <td><input type="text" size="30" name="openurl" value="${settingsBean.openurl}"/> 
                a URL to your local <a href="http://www.exlibrisgroup.com/sfx_openurl.htm">openURL</a> resolver
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
		 				<li><a href="/user/<mtl:encode value='${myfr1.user_name}'/>"><c:out value="${myfr1.user_name}"/></a></li>		 		
					</c:forEach>
				</ul>
			</td>
		</tr>		
		<tr>
			<td>my <a href="/friends">friends</a> are</td>
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
