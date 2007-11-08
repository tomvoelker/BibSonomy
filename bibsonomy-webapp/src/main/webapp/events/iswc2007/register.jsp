<%@include file="standard_header.jsp" %>
  
<body>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: register</h1>

<%@include file="/boxes/navi.jsp" %>

<div id="full">

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
      <td>Please confirm the 5 digit security code:</td>
      <td><img src="/Captcha.jpg"></td>
  </tr>
  <tr>
      <td></td>
      <td>
     <input type="text" size="10" maxlength="5" name="captcha">
     <div class="errmsg">${registrationHandlerBean.errors.captcha}</div>
      </td>
  </tr>
  <tr>
    <td>
      <input type="submit" value='register'>
      <input type="hidden" name="event" value="iswc2007"/>
    </td>
    <td></td><td></td>
  </tr>
 </table>
</form>

After registration you can remember the interesting papers of the conference by copying them:
<br/>
<img src="/resources/image/copy.png" alt="copy publications to your repository"/>

</div>

<%@include file="footer.jsp" %>
