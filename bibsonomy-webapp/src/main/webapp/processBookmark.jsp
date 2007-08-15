<%@include file="include_jsp_head.jsp" %>

<jsp:useBean id="bookmarkHandlerBean" class="beans.BookmarkHandlerBean" scope="request">
  <jsp:setProperty name="bookmarkHandlerBean" property="*"/>
</jsp:useBean>

<%-- test if tagstring contains comma and handle it --%>
<c:if test="${empty param.acceptComma}">
	<%
		String tags = request.getParameter("tags");
		if (tags != null && (tags.indexOf(",") != -1 || tags.indexOf(";") != -1)) {
	%>
		<jsp:forward page="/edit_bookmark">
			<jsp:param name="testComma" value="true" />
		</jsp:forward>
	<%
		} 
	%>
</c:if>


<%
   if (bookmarkHandlerBean.isValid()) {
%>
<jsp:forward page="/bookmarkHandler" />
<%
   } else {
%>
<jsp:forward page="/edit_bookmark"/>
<%
   }
%>
