<jsp:include page="boxes/search.jsp" />
<div id="welcomeTop">A blue social bookmark and publication sharing system.</div>

<table id="tnav">
  <tr>
    <td class="tleft">

      <a href="/tags">tags</a> &middot;
      <a href="/groups">groups</a> &middot;
      <a href="/relations">relations</a> &middot;
      <a href="/popular">popular</a>
    </td>
    <td class="tright">
      <c:if test="${!empty user.name}"><%-- show user name, if available --%>
        logged in as 
        <a href="/user/<c:out value="${user.name}" />"><c:out value="${user.name}" /></a>
        &middot;
      </c:if>
    
      <a href="/help" rel="Help">help</a> &middot;
      <a href="http://bibsonomy.blogspot.com/">blog</a> &middot;
      <a href="/help/about/">about</a>
    </td>
  </tr>
  <tr>
    <td class="tleft">
      <c:choose>
        <c:when test="${!empty user.name}">
          <a href="/user/<c:out value='${user.name}'/>">my${projectName}</a> &middot;
          <a href="/post_bookmark">post bookmark</a> &middot;
          <a href="/post_bibtex">post bibtex</a> &middot; 
          <a href="/relations/<c:out value='${user.name}'/>">myRelations</a>
        </c:when>
        <c:otherwise>
          <%@include file="login.jsp"%>      
        </c:otherwise>
      </c:choose>
    </td>
    <td class="tright">
      <c:choose>
        <c:when test="${!empty user.name}">
          <span id="pickctr">${user.postsInBasket}</span> picked in <a href="/basket">basket</a> &middot;
          <a href="/edit_tags">edit tags</a> &middot;
          <a href="/friends">friends</a> &middot;        
          <a href="/settings">settings</a> &middot;
          <a href="/logout">logout</a>
        </c:when>
        <c:otherwise>
          <a href="/login">login</a> &middot;
          <a href="/register">register</a>
        </c:otherwise>
      </c:choose>
    </td>
  </tr>
</table>