		<%--
			layer for subnavigation inside the path elements (headline)
			@author: ccl
		 --%>
		
		<%-- dropdown menu for BibSonomy Path --%>  
		<div id="path_menu" class="dropmenudiv">
			<a onclick="naviSwitchSpecial('tag')" style="cursor:pointer">tag</a>
			<a onclick="naviSwitchSpecial('user')" style="cursor:pointer">user</a>
			<a onclick="naviSwitchSpecial('group')" style="cursor:pointer">group</a>
			<a onclick="naviSwitchSpecial('author')" style="cursor:pointer">author</a>
			<a onclick="naviSwitchSpecial('concept')" style="cursor:pointer">concept</a>
			<a onclick="naviSwitchSpecial('bibtexkey')" style="cursor:pointer">BibTeX key</a>
			<a onclick="naviSwitchSpecial('all')" style="cursor:pointer">search:all</a>
			<c:if test="${not empty param.requUser}">
				<a onclick="naviSwitchSpecial('explicit_user')" style="cursor:pointer">search:<c:out value='${param.requUser}'/></a>
				<c:set var="mode" value="requUser" />
			</c:if>
			<c:if test="${not empty user.name and empty param.requUser}">
				<a onclick="naviSwitchSpecial('explicit_user')" style="cursor:pointer">search:<c:out value='${user.name}'/></a>
				<c:set var="mode" value="requUser" />
			</c:if>
		</div>
		<script type="text/javascript">
		  cssdropdown.startchrome("path");
		</script>
	</div>	
	
	<div id="mainHeaderRightBox">
		<c:choose>
			<c:when test="${!empty user.name}">
				<span id="pickctr">${user.postsInBasket}</span> picked in <a href="/basket">basket</a> &middot;
				<a href="/edit_tags">edit tags</a> 
			</c:when>
		</c:choose>
	</div>

	<div id="welcomeTop">A blue social bookmark and publication sharing system.</div>
</div>