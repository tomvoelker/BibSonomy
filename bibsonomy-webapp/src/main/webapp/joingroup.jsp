<%@include file="include_jsp_head.jsp" %>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="join group" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/">${projectName}</a> :: <a href="#" rel="path_menu"><img src="/resources/image/box_arrow.png">&nbsp;join group</a> :: <a href="/group/<mtl:encode value='${param.group}'/>"><c:out value='${param.group}'/></a></h1> 

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
      <td>Captcha</td>
      <td colspan="2">
      
        <%-- ReCaptcha to fight spammers --%>
        <%@ include file="/boxes/captcha.jsp" %>
        <div class="errmsg">${error}</div>
      </td>
      
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