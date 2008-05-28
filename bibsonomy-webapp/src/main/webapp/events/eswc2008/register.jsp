<%@include file="/include_jsp_head.jsp" %>

<jsp:useBean id="registrationHandlerBean" class="beans.RegistrationHandlerBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="register" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/">${projectName}</a> :: <a rel="path_menu" href="/register">register&nbsp;<img src="/resources/image/box_arrow.png"></a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 
<div id="general">

<h2>Get a ${projectName} account!</h2>

<div class="errmsg">${registrationHandlerBean.errors.general}</div>

<form method=post action="/registration_process" >
<table>
   <tr>
    <td><label for="inpf">username</label></td>
    <td>
     <input type="text" size="30" id="inpf" name="userName" value="<c:out value='${registrationHandlerBean.userName}'/>" maxlength="30">
     <div class="errmsg">${registrationHandlerBean.errors.userName}</div>
   </td>
    <td>(required :: without spaces)</td>
    </tr>
   <tr>
    <td><label for="lreal">realname</label></td>
    <td>
     <input type="text" size="30" id="lreal" name="realName" value="<c:out value='${registrationHandlerBean.realName}'/>" maxlength="255">
     <div class="errmsg">${registrationHandlerBean.errors.realName}</div>
   </td>
    <td>(optional)</td>
    </tr>
   <tr>
    <td><label for="lhome">homepage</label></td>
    <td>
     <input type="text" size="30" id="lhome" name="homepage" value="<c:out value='${registrationHandlerBean.homepage}'/>" maxlength="255">
     <div class="errmsg">${registrationHandlerBean.errors.homepage}</div>
   </td>
    <td>(optional :: only http)</td>
    </tr>
  <tr>
    <td><label for="lemail">email</td>
    <td>
     <input type="text" size="30" id="lemail" name="email" value="<c:out value='${registrationHandlerBean.email}'/>"><br>
     <div class="errmsg">${registrationHandlerBean.errors.email}</div>
   </td>
      <td>(required :: valid email address)</td>
  </tr>
  <tr>
    <td>password</td>
    <td>
      <input type="password" size="30" name="password1" maxlength="30">
      <div class="errmsg">${registrationHandlerBean.errors.password1}</div>
    </td>
    <td>(required :: without spaces)</td>
  </tr>
    </tr>
  <tr>
    <td>password2</td>
    <td>
     <input type="password" size="30" name="password2">
     <div class="errmsg">${registrationHandlerBean.errors.password2}</div>
    </td>
    <td>(please confirm password)</td>
  </tr>
	<tr>
    	<td></td>
    	<td colspan="2">
      
        <%-- ReCaptcha to fight spammers --%>
        <%@ include file="/boxes/captcha.jsp" %>
        <div class="errmsg">${registrationHandlerBean.errors.captcha}</div>
        </td>
	</tr>
  <tr>
    <td>
      <input type="submit" value='register'>
      <input type="hidden" name="event" value="eswc2008"/>
    </td>
    <td></td><td></td>
  </tr>
 </table>
</form>

After registration you can remember the interesting papers of the conference by copying them:
<br/>
<img src="/resources/image/copy.png" alt="copy publications to your repository"/>

</div>

<%@include file="/footer.jsp" %>