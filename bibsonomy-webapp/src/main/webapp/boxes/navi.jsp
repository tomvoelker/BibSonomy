<%-- ######### myBibSonomy * post bookmark * post bibtex ######### --%>

<div id="nice_tnav">
<ul>
	<li>
		<a id="nav_home" href="/">Home</a>
	</li>
	<c:choose>
		<c:when test="${not empty user.name}">
			<li>
				<c:url var="userHome" value="/user/${user.name}" />
				<c:url var="userRelations" value="/relations/${user.name}" />
				<c:url var="userPDF" value="/user/${user.name}">
					<c:param name="filter" value="JUST_PDF" />
				</c:url>
				<c:url var="userDuplicates" value="/user/${user.name}">
					<c:param name="filter" value="DUPLICATES" />
				</c:url>
				<c:url var="userBibTeX" value="/bib/user/${user.name}">
					<c:param name="items" value="1000" />
				</c:url>
				
		        <a id="nav_my" href="${userHome}" rel="my_menu"><img src="/resources/image/nice_box_arrow.png" alt="" />my${properties['project.name']}</a>
			      <!-- dropdown menu for myBibSonomy -->  
				  <div id="my_menu" class="nicedropmenudiv">
				    <a href="/friends">myFriends</a>
					<a href="${userRelations}">myRelations</a>
					<a href="/mySearch">mySearch</a>
			        <a href="${userPDF}">myPDF</a>
			        <a href="${userDuplicates}">myDuplicates</a>
			        <a href="${userBibTeX}">myBibTeX</a>
				  </div>
			</li>
			<li>
				<a id="nav_postBook" href="/postBookmark">post bookmark</a>
			</li>
			<li>
				<a id="nav_postPub" href="/postPublication">post publication</a>
			</li>
		</c:when>
	</c:choose>
     <%-- ######### tags * groups * relations * popular ######### --%>
	<li>
	  <a id="nav_tags" href="/tags">tags</a>
	</li>		
	<li>
		<a id="nav_authors" href="/authors">authors</a>
	</li>
	<li>
	  <a id="nav_relations" href="/relations">relations</a>
	</li>
	<li>
     <a id="nav_urls" href="/groups" rel="grp_menu"><c:if test="${not empty user.groups}"><img src="/resources/image/nice_box_arrow.png" alt="" /> </c:if>groups</a>
	</li>
	<li>
	  <%-- dropdown menu for groups --%>		
  	  <div id="grp_menu" class="nicedropmenudiv">
  	  	<c:if test="${not empty user.groups}">
	    	<c:forEach var="grp" items="${user.groups}">
	    		<c:set var="groupName" value="${grp.name}" />
	    		<c:url value="/group/${groupName}" var="groupUrl" />
		  		<a href="${groupUrl}"><c:out value="${groupName}" /></a>
			</c:forEach>
		</c:if>  	
	  </div>										
	</li>
	<li>
 	  <a id="nav_popular" href="/popular">popular</a>
	</li>
</ul>
</div>

<%-- 2008/12/18, fei: commented out conditional as otherwise no tab is brought
                      to front when user isn't logged in 
--%>
<%-- <c:if test="${not empty user.name}">  --%>
	<script type="text/javascript">
  		cssdropdown.startchrome("nice_tnav");
		if ("${selectedNaviTab}" != "" ) {
			document.getElementById("${selectedNaviTab}").className = "checked";
		} else {
			document.getElementById("nav_my").className = "checked";
		}
	</script>  
<%-- </c:if>  --%> 
	