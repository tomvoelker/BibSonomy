<%@include file="include_jsp_head.jsp" %>

<jsp:useBean id="registrationHandlerBean" class="beans.RegistrationHandlerBean" scope="request"/>

<c:if test="${empty user.name}">
   <jsp:forward page="/register"/>
</c:if>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="success register" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a href="#" rel="path_menu">success register&nbsp;<img src="/resources/image/box_arrow.png"></a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 
	    
<div id="bookbox">
  <h2>The Registration was successful!</h2>
  
  <p>Welcome to <a href="/">${projectName}</a>! You have successfully signed in. </p>
  
  <h3>Next steps:</h3>

  <ol>
    <li>Drag these buttons to the links toolbar of your browser.
    <p><%@include file="/boxes/button_mybibsonomy.jsp" %>
       <%@include file="/boxes/button_postbookmark.jsp" %>
       <%@include file="/boxes/button_postbibtex.jsp" %>
    </p>
    <p style="font-size: 80%;">
       For Internet Explorer you have to right-click the button and select "add to favorites". 
       More infos <a href="/help">here</a>.
    </p>
   </li>
   <li style="padding-top:2em;">Surf to your favourite page and bookmark it by clicking on the newly added "post bookmark" button.<br>
       Afterwards a click on "my${projectName}" shows your first successful bookmark.
   </li>
  </ol>

  <hr style="margin: 2em 0em 2em 0em;">

  <p>A confirmation message has been sent to your email address.
    <table>
      <tr><td>username:</td><td><jsp:getProperty name="registrationHandlerBean" property="userName"/></td></tr>
      <tr><td>email:</td><td><jsp:getProperty name="registrationHandlerBean" property="email"/></td></tr>
    </table>  
  </p>

</div>
<div id="bibbox">
<img src="/resources/image/register3.png" style="float:left;" width="70%">
</div>


<%@ include file="footer.jsp" %>