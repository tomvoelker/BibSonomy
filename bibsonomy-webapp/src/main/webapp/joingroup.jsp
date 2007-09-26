<%@include file="include_jsp_head.jsp" %>

<jsp:useBean id="registrationHandlerBean" class="beans.RegistrationHandlerBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="join group" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/">${projectName}</a> :: <a href="#" rel="path_menu">join group&nbsp;<img src="/resources/image/box_arrow.png"></a> :: <a href="/group/<mtl:encode value='${param.group}'/>"><c:out value='${param.group}'/></a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<div id="general">

<div class="errmsg">${error}</div>

<form method=post action="/JoinGroupHandler">
<table>
   <tr>
	  <td><label for="group">group</label></td>
	  <td><input type="text" size="30" id="group" name="group" value="<c:out value='${param.group}'/>" maxlength="30"></td>
      <td></td>
  </tr>
   <tr>
    <td><label for="reason">reason</label></td>
    <td><textarea cols="30" rows="3" id="reason" name="reason" maxlength="30"></textarea></td>
    <td>Justify, why you want to join this group. (first 200 characters are accepted)</td>
  </tr>
  <tr>
      <td>code</td>
      <td><img src="Captcha.jpg"></td>
      <td></td>
  </tr>
  <tr>
	<td></td>
	<td><input type="text" size="10" maxlength="5" name="captcha"></td>
    <td>Please confirm the 5 digit security code.</td>
  </tr>
  <tr>
    <td></td>
	<td><input type=submit value='join group'></td>
    <td></td>
  </tr>
</table>
</form>
</div>


<%@ include file="footer.jsp" %>