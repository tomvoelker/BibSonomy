<%@include file="include_jsp_head.jsp" %>
<%@ page import="helpers.URLUtil" %>
<jsp:useBean id="loginHandlerBean" class="beans.LoginHandlerBean" scope="request"/>

<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="login" />
</jsp:include>
  
<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: <a href="/login">login</a></h1> 

<%@include file="/boxes/navi.jsp" %> 
                      
<div id="general">

<h2>Login</h2>
<form method="post" action="/login_process">
 
<%-- #######################################################
     remember page user is coming from to forward back to it
     ####################################################### --%>
<c:choose>
  <c:when test="${empty param.referer}">
    <input type="hidden" name="referer" value="${header.referer}">
  </c:when>
  <c:otherwise>
    <%-- user is coming from one of our servlets to login and 
         should be redirected back to the servlet after successful login --%>
    <jsp:useBean id="url" class="helpers.URLUtil" scope="request">
      <jsp:setProperty name="url" property="queryString" value="${param.referer}"/>
    </jsp:useBean>

    <div style="border:1px solid #006699; padding: 1em; margin-bottom: 1em;">
      Welcome to <a style="font-weight: bold;" href="/">${projectName}</a>, a system for managing and sharing web bookmarks and publication entries.
      <br/><br/>
      You tried 
      <c:choose>
        <c:when test="${url.pathName eq '/ShowBookmarkEntry'}">
          to bookmark the URL <a style="font-weight:bold; padding: 1em; display:block;" href="<c:out value='${url.query.url}'/>"><c:out value='${url.query.url}'/></a>
        </c:when>
        <c:when test="${url.pathName eq '/BibtexHandler'}">
          to save the publication entry 
          <c:if test="${not empty url.query.selection}">
            <pre style="background: #eee; padding: .5em;"><c:out value="${mtl:trimWhiteSpace(url.query.selection)}"/></pre>
          </c:if>
          from the URL <a style="font-weight:bold; padding: 1em; display:block;" href="<c:out value='${url.query.url}'/>"><c:out value='${url.query.url}'/></a>
        </c:when>
        <c:otherwise>
          to access a function which is only available for logged in users.
        </c:otherwise>
     </c:choose>
      To accomplish this, please <b>login</b> or <a style="font-weight:bold;" href="/register">create a new account</a>.
    </div>
      
    <input type="hidden" name="referer" value="${param.referer}">
  </c:otherwise>
</c:choose>

<%-- ###############
     show login form
     ############### --%>
<table>
  <tr>
    <td>username</td>
    <td> 
      <input type="text" size="30" name="userName" id="inpf" value="<%=loginHandlerBean.getUserName()%>"> 			  
      <div class="errmsg"><%=loginHandlerBean.getErrorMsg("userName")%></div>
    </td>
  </tr>
  <tr>
    <td>password</td>
    <td>
      <input type="password" size="30" name="loginPassword" value="">
      <div class="errmsg"><%=loginHandlerBean.getErrorMsg("loginPassword")%></div>
	</td>		
  </tr>
  <tr> 
    <td>
      <input type="submit" value="log in">
    </td>
  </tr>
</table>
</form>


<%-- ##########################
     registration/lost password 
     ########################## --%>
<h2>Registration</h2>
<p>I am not registered, but want to do this <a href="/register">now</a>.</p>

<h2>Lost Password</h2>
<p>I've lost my password. Please <a href="/reminder">send</a> me a new one.</p>

</div>

<%@ include file="footer.jsp" %>