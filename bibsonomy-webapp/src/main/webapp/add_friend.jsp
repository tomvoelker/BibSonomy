<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ talib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%--

	This is a hack for LWA 2010 to allow users to easily add friends using the
	Conferator application. Please remove as soon as possible!

 --%>
<html>
	<body>
	
		<form action="/SettingsHandler" id="addfriend" style="width: 180px;">
			Add <c:out value="${param.add_friend}"/> as friend? 
			<input name="add_friend" type="hidden" value="${fn:escapeXml(param.add_friend)}"/>
			<input name="ckey" type="hidden" value="${ckey}"/><br/>
			<a href="" onclick="document.getElementById('addfriend').submit(); window.close();" style="float:left;">yes</a>
			<a href="" onclick="window.close();" style="float:right;">no</a>
		</form>
		
	</body>
</html>