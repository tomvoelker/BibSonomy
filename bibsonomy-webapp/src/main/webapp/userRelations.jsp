<%@include file="include_jsp_head.jsp" %>
<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="${param.requUser}" />
</jsp:include>

<jsp:useBean id="RelationBean" class="beans.RelationBean" scope="request">
  <jsp:setProperty name="RelationBean" property="*"/>
  <jsp:setProperty name="RelationBean" property="items" value="${user.itemcount}"/>
</jsp:useBean>

<%-- heading --%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="#"><img src="/resources/image/box_arrow.png">&nbsp;relations</a> :: 
<a href="/relations/<mtl:encode value='${param.requUser}'/>"><c:out value='${param.requUser}'/></a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<%-- show relations of the requested user --%>
<div id="outer">
<div id="general">    

  <%-- get relations from bean --%>
  <c:set var="relations" value="${RelationBean.limitedRelations}"/>
	
  <%-- don't show "hide" symbol left to each relation --%>
  <c:set var="usersOwnRelations" value="false" />


  <h2>relations
    <%-- show number of relations the user has --%>
    <c:if test="${RelationBean.total != 0}">
      <span class="count" title="total: ${RelationBean.total} relations">(${RelationBean.total})</span>
    </c:if>
  </h2>
 
	
  <%-- show the list of relations --%>
  <%@include file="/boxes/nextprevrel.jsp" %>
  <ul style="font-size:140%;">
    <%@include file="/boxes/tags/relationlist.jsp" %>
  <%@include file="/boxes/nextprevrel.jsp" %>
	
</div>
<%@include file="/boxes/itemcount.jsp" %>
</div>

<div id="sidebarroundcorner" >
<ul id="sidebar">
  <%--
   can't be shown because I programmed RelationBean in a stupid way. :-(
   @include file="/boxes/tags/usersrelations.jsp" 
   
   --%>
  <c:set var="markSuperTags" value="true"/>
  <%@include file="/boxes/tags/userstags.jsp"%>
</ul>
</div>
<script type="text/javascript">
   $("#sidebarroundcorner").corner("round bottom 15px").corner("round tl 15px");
</script>




<%@ include file="footer.jsp" %>