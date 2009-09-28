	<%-- ############################################# import tab ############################################# --%>
		<%-- ------------------------ firefox import -------------------------- --%>
		<h2>import your bookmarks from Firefox:</h2>
		<form enctype="multipart/form-data" method="POST" action="/import/firefox?ckey=${ckey}">
		  <table> 
		   <tr>
		   <td>select your file:</td>
		   <td><input name="file" type="file"  size="20"/></td></tr>
		   <tr>
		      <td>viewable for</td>
		      <td><select name="group"><c:forEach var="group" items="${user.allGroups}">
		         <option value="${group}">${group}</option>
		      </c:forEach></select></td>
		    </tr>
		   <tr><td></td><td><input type="checkbox" name="overwrite" value="yes">Firefox bookmarks overwrite ${projectName} bookmarks</td></tr>
		   <tr><td>
              <input type="submit" value="import bookmarks">
           </td><td></td></tr>
		  </table> 
		</form>
		
		<hr/>
		
		<%-- ------------------------ delicious import -------------------------- --%>
		<h2>import your Delicious data:</h2>
		<form method="POST" action="/import/delicious">
	  	<table>
	    	<tr><td>username</td><td><input type="text" name="userName" size="30"/></td></tr>
	    	<tr><td>password</td><td><input type="password" name="passWord" size="30"/></td></tr>
	    	<tr>
	      		<td colspan="2">group settings will be transferred from Delicious</td>
	    	</tr>
                    <tr><!-- posts or bundles? -->
                      <td>
                      import only 
                      </td>
                      <td>
                        <input type="radio" name="importData" value="posts" selected="true"/>
                        bookmarks
  
                        <input type="radio" name="importData" value="bundles"/>
                        bundles
                      </td>
                    </tr>        
	    	<tr><td></td><td><input type="checkbox" name="overwrite" value="yes">Delicious bookmarks overwrite ${projectName} bookmarks</td></tr>
	    	<tr><td>
	      		<input type="hidden" name="ckey" value="${ckey}"/>
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
		<a href="http://jabref.sourceforge.net/help/CustomExports.php">the JabRef page</a>.
		You (and only you!) can use these layouts to show ${projectName}'s publication lists
		in a format of your choice. After uploading the correct file(s) have a look at
		<a href="/layout/custom/user/<c:out value='${user.name}'/>">my${projectName}</a> to see 
		how it looks.
		</p>
		
		<form enctype="multipart/form-data" method="post" action="/import/jabref?action=create&amp;ckey=${ckey}" style="display:inline;">
		  <table>
		    <tr> 
		      <td>begin.layout</td>
		      <td><c:choose>
		        <c:when test="${empty layoutBean.beginName}"><input name="fileBegin" type="file" size="20"/></c:when>
		        <c:otherwise><c:out value="${layoutBean.beginName} "/><a class="action" href="/import/jabref?action=delete&amp;ckey=${ckey}&amp;hash=${layoutBean.beginHash}">delete</a></c:otherwise>
		      </c:choose></td>
		      <td>(appears at the beginning of the output, optional)</td>
		    </tr><tr>
		      <td>item.layout</td>
		      <td><c:choose>
		        <c:when test="${empty layoutBean.itemName}"><input name="fileItem" type="file" size="20"/></c:when>
		        <c:otherwise><c:out value="${layoutBean.itemName} "/><a class="action" href="/import/jabref?action=delete&amp;ckey=${ckey}&amp;hash=${layoutBean.itemHash}">delete</a></c:otherwise>
		      </c:choose></td>
		      <td>(used to render each publication item)</td>
		    </tr><tr>
		      <td>end.layout</td>
		      <td><c:choose>
		        <c:when test="${empty layoutBean.endName}"><input name="fileEnd" type="file" size="20"/></c:when>
		        <c:otherwise><c:out value="${layoutBean.endName} "/><a class="action" href="/import/jabref?action=delete&amp;ckey=${ckey}&amp;hash=${layoutBean.endHash}">delete</a></c:otherwise>
		      </c:choose></td>
		      <td>(appears at the end of the output, optional)</td>
		    </tr><tr>
		      <td><input type="submit" value="upload" /><div class="errmsg"><c:out value="${layoutBean.error}"/></div></td>
		      <td></td>
		    </tr>
		  </table>
		</form>	
