<%@include file="include_jsp_head.jsp"%>

<c:if test="${empty tmpUser}">
	<c:set var="error" value="You're not authorised to view this page" scope="request"/>
	<jsp:forward page="/error.jsp" />
</c:if>

<jsp:useBean id="registrationHandlerBean" class="beans.RegistrationHandlerBean" scope="request">
	<jsp:setProperty name="registrationHandlerBean" property="*"/>
</jsp:useBean>

<% if (registrationHandlerBean.isPasswordChangeOnRemind()) { %>
  <jsp:forward page="/RegistrationHandler"/>
<%  } %>



<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="change password" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/change_password">change password&nbsp;<img src="/resources/image/box_arrow.png"></a></h1> 


<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp"%>

<div id="general">

	<h2>change password</h2>

	<form name="password" method="post" action="/change_password">
	  <table>
	    <tr>
		  <td>new password</td>
		  <td>
	      <input type="hidden" name="action" value="change"/>
		    <input type="password" size="30" name="password1" value="" maxlength="30">
		    <div class="errmsg">${registrationHandlerBean.errors.password1}</div>
		  </td>
		  <td>(required :: without spaces)</td>
		</tr>
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

</div>
<%@ include file="/footer.jsp" %>