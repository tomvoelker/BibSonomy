<%@include file="include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<c:if test="${empty user.name}">
   <jsp:forward page="/login"/>
</c:if>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="basket" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/basket"><img src="/resources/image/box_arrow.png">&nbsp;basket</a></h1> 


<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<%----------------------collected bibtex entries - extra meta handling without pick button--------------------%>
<div id="bibbox">
  
  <p>Here you can manage publication posts you picked with the "pick" button. For details see <a href="/help/basic/firststeps#basket">here</a>.</p>

  <%@ include file="/boxes/basket_actions.jsp" %>
  
  <%-------------------- Publication entries -----------------------%>
  <c:set var="basePath" value="/"/>
  <c:set var="unpick" value="yes"/>  
  
  <%@include file="/boxes/bibtex_list.jsp" %>  
  
  <%@ include file="/boxes/basket_actions.jsp" %>

</div>

<script type="text/javascript">
maximizeById("bibbox");
</script>


<%@ include file="footer.jsp" %>