<%@include file="include_jsp_head.jsp" %>

<jsp:useBean id="loginHandlerBean" class="beans.LoginHandlerBean" scope="request">
<jsp:setProperty name="loginHandlerBean" property="*"/>
</jsp:useBean>

<%
   if (loginHandlerBean.validate()) {
%>
<jsp:forward page="/LoginHandler"/>
<%
   }  else {
%>

<jsp:forward page="/login?${request.queryString}"/>

<%
   }
%>