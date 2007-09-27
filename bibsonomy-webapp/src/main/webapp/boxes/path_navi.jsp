<%--
	layer for subnavigation inside the path elements (headline)
	@author: ccl
 --%>

<%-- dropdown menu for BibSonomy Path --%>  
<div id="path_menu" class="dropmenudiv">
	<a onclick="naviSwitchSpecial('${projectName}','${param.requUser}','tag')" style="cursor:pointer">tag</a>
	<a onclick="naviSwitchSpecial('${projectName}','${param.requUser}','user')" style="cursor:pointer">user</a>
	<a onclick="naviSwitchSpecial('${projectName}','${param.requUser}','group')" style="cursor:pointer">group</a>
	<a onclick="naviSwitchSpecial('${projectName}','${param.requUser}','author')" style="cursor:pointer">author</a>
	<a onclick="naviSwitchSpecial('${projectName}','${param.requUser}','concept')" style="cursor:pointer">concept</a>
	<a onclick="naviSwitchSpecial('${projectName}','${param.requUser}','all')" style="cursor:pointer">search:all</a>
	<c:if test="${not empty param.requUser}">
		<a onclick="naviSwitchSpecial('${projectName}','${param.requUser}','explicit_user')" style="cursor:pointer">search:${param.requUser}</a>
		<c:set var="mode" value="requUser" />
	</c:if>
	<c:if test="${not empty user.name and empty param.requUser}">
		<a onclick="naviSwitchSpecial('${user.name}','${user.name}','explicit_user')" style="cursor:pointer">search:${user.name}</a>
		<c:set var="mode" value="requUser" />
	</c:if>
</div>

<div id="Start"></div>

<script type="text/javascript">
  cssdropdown.startchrome("path");
</script>