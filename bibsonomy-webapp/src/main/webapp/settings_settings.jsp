	
	<%-- ############################################# settings tab ############################################# --%>
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
		        <td>default language</td>
		        <td>
		           <select name="lang">
		            <option <c:if test="${user.defaultLanguage == 'en'}">selected="true"</c:if> value="en">english</option>
		            <option <c:if test="${user.defaultLanguage == 'de'}">selected="true"</c:if> value="de">german</option>
		          </select>
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
		  
		  <h3>shared documents</h3> 
		  <form name="groupSharedDocuments" method="post" action="/settings">
		    shared documents are  
		    <select name="sharedDocuments">
		      <option value="0" <c:if test="${gsBean.sharedDocuments eq 0}">selected="true"</c:if>>disabled</option>
		      <option value="1" <c:if test="${gsBean.sharedDocuments eq 1}">selected="true"</c:if>>enabled</option>
		    </select>
		    <input type="hidden" value="update_shared_documents" name="action"/>
		    <input type="submit" value="change" />
		  </form>		  
		  
		</c:when>
		<c:otherwise>
		  
		<h3>add a group</h3>
		<p>
		  If you want a group added to the system, create a user account with the desired group name and write an E-Mail to 
		  <%@include file="/boxes/emailaddress.jsp" %>.
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
		
		<%-- ------------------------ API -------------------------- --%>
		<h2>API</h2>
		<form name="apiKey" method="post" action="/settings">
		<c:choose>
			<c:when test="${apiKey.rows[0].api_key eq null}">			
				<p>
					BibSonomy now provides <a href="/help/doc/api.html">API</a> access for all users.					
					Just click the button below to get access. Your generated API key will be displayed here.  					 
				</p>	
			</c:when>
			<c:otherwise>
				<p>
					Your API key is: <tt style="font-size: 14px;">${apiKey.rows[0].api_key}</tt><br/><br/>
					Refer to the <a href="/help/doc/api.html">API Documentation</a> or write an email to
					<%@include file="/boxes/emailaddress_api_support.jsp" %> if you have questions or
					comments. 
				</p>		
			</c:otherwise>
		</c:choose>
		<input type="hidden" name="ckey" value="${ckey}"/>
		<input type="hidden" name="apikey" value="true"/>
		<input type="submit" value="(re)generate API key"/> 
		</form>
		<hr/>
    
        <%-- ------------------------ log level -------------------------- --%>
        <h2>options</h2>
        <form name="options" method="post" action="/settings">
        <table>
          <tr>
            <td>log clicks to external pages</td>
            <td>
		          <select name="logLevel">
		            <option <c:if test="${user.logLevel == 0}">selected="true"</c:if> value="0">yes</option>
		            <option <c:if test="${user.logLevel == 1}">selected="true"</c:if> value="1">no</option>
		          </select>
            </td>
          </tr> 
          <tr>
            <td>ask for confirmation when deleting posts</td>
            <td>
              <select name="confirmDelete">
                <option <c:if test="${user.confirmDelete == 'true'}">selected="true"</c:if> value="true">yes</option>
                <option <c:if test="${user.confirmDelete == 'false'}">selected="true"</c:if> value="false">no</option>
              </select>
            </td>
          </tr> 
          <tr>
            <td>
              <input type="submit" value="update settings"/>
              <input type="hidden" name="ckey" value="${ckey}"/>
            </td>
          </tr>
        </table>
        </form>
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
		<form name="delete" method="post" action="/actions/goodBye">
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
