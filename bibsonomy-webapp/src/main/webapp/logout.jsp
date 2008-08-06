<%@page import="filters.SessionSettingsFilter"%>
<%@include file="include_jsp_head.jsp" %>

<%
    response.setHeader("Pragma","no-cache");
    response.setHeader("Cache-Control","no-cache");
    response.setDateHeader("Expires",-1);
    response.setDateHeader("Last-Modified",0);
	Cookie userCookie = new Cookie (SessionSettingsFilter.USER_COOKIE_NAME, "");
	userCookie.setPath("/");
	userCookie.setMaxAge(0);
	response.addCookie(userCookie);
	
	Cookie openIDCookie = new Cookie (SessionSettingsFilter.OPENID_COOKIE_NAME, "");
	openIDCookie.setPath("/");
	openIDCookie.setMaxAge(0);
	response.addCookie(openIDCookie);

    session.invalidate(); 
 
    
%>

<%   response.sendRedirect("/"); %>
