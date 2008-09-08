<%@include file="include_jsp_head.jsp" %>

<jsp:useBean id="registrationHandlerBean" class="beans.RegistrationHandlerBean" scope="request">
<jsp:setProperty name="registrationHandlerBean" property="*"/>
</jsp:useBean>

<%
   if (registrationHandlerBean.validateLight()) {
%>
<jsp:forward page="/RegistrationHandler"/>
<%
   }  else {
%>
<jsp:forward page="/settings"/>
<%
   }
%>