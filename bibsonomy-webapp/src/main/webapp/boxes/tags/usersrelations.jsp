<%--
This JSP shows the concepts of a user above the tag cloud
 --%>

<jsp:useBean id="RelationBean" class="beans.RelationBean" scope="request">
  <jsp:setProperty name="RelationBean" property="requUser" value="${param.requUser}"/>
</jsp:useBean>  
 
<li><span class="sidebar_h"><a href="/relations/<mtl:encode value='${param.requUser}'/>" title="get an overview of the relations of this user">relations</a>
  <c:if test="${param.requUser eq user.name}">
    <%-- show buttons to hide/show all of the users (own!) relations --%>
    <a onclick='updateRelations(event,"all", "show");' href="/ajax/pickUnpickConcept?action=all&amp;tag=show&amp;ckey=${ckey}" title="show all relations">&uarr;</a>
    <a onclick='updateRelations(event,"all", "hide");' href="/ajax/pickUnpickConcept?action=all&amp;tag=hide&amp;ckey=${ckey}" title="hide all relations">&darr;</a>
      
   <%-- this are the users own relations, which has some implications, e.g.
        - show "hide" symbol left to each relation 
        - show only the picked relations (i.e., RelationBean.shownRelations instead of RelationBean.userRelations)
        --%>
   <c:set var="usersOwnRelations" value="true" />
  
  </c:if>
</span>


<%-- get all relations of the user which should be shown--%>
<c:choose>
  <c:when test="${usersOwnRelations}">
    <c:set var="relations" value="${RelationBean.shownRelations}"/>
  </c:when>
  <c:otherwise>
    <c:set var="relations" value="${RelationBean.userRelations}"/>
  </c:otherwise>
</c:choose>
	
<%-- show relations --%>
<ul id="relations">
  <%@include file="/boxes/tags/relationlist.jsp" %>
  
</li>