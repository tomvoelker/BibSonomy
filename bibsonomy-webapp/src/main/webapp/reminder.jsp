<%@include file="include_jsp_head.jsp" %>

<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="reminder" />
</jsp:include>

<jsp:useBean id="registrationHandlerBean" class="beans.RegistrationHandlerBean" scope="request">
	<jsp:setProperty name="registrationHandlerBean" property="*"/>
</jsp:useBean>

<%
   if (registrationHandlerBean.isValidReminder()) {
%>
<jsp:forward page="/RegistrationHandler"/>
<%   }  %>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/reminder">reminder&nbsp;<img src="/resources/image/box_arrow.png"></a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp"%>

<div id="general">
	
	<form method="post" action="/reminder">
        <div class="errmsg">${registrationHandlerBean.errors.general}</div>
		<table>
			<tr>
			  <td>username</td>
			  <td> 
		        <input type="hidden" name="action" value="reminder"/>
				<input type="text" size="30" name="userName" id="inpf"  value="<c:out value='${registrationHandlerBean.userName}'/>"> 			  
		        <div class="errmsg">${registrationHandlerBean.errors.userName}</div>				 
			  </td>
			</tr>
			<tr>
			  <td>email address</td>
			  <td>
				<input type="text" size="30" name="email" v value="<c:out value='${registrationHandlerBean.email}'/>">
 			    <div class="errmsg">${registrationHandlerBean.errors.email}</div>
	          </td>
			</tr>
			<tr>
    	      <td></td>
    	      <td>
      
                <%-- ReCaptcha to fight spammers --%>
                <%@ include file="/boxes/captcha.jsp" %>
                <div class="errmsg">${registrationHandlerBean.errors.captcha}</div>
              </td>
	        </tr>
			<tr> 
		    <td>
		      <input type="submit" value="remind me">
			  </td>
			</tr>
	  </table>
	</form>

</div>

<!------------------------------------------->
<!--             End of Content            -->
<!------------------------------------------->
<%@ include file="footer.jsp" %>