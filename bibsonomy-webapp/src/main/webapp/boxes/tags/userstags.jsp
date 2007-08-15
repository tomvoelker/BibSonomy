<%-- 
  this shows the users tag box (on all but edit pages)
--%>

<li><span class="sidebar_h">tags</span>

<%-- initialize bean, etc. --%>
<%@include file="/boxes/tags/userstags_init.jsp" %>


<%-- ------------------------------------- iterate over all tags ------------------------------------------------%>

<%-- instead of <%@include file="/boxes/tagboxstyle.jsp" %> 
     we insert the code here directly, because we need to 
     give the tagbox an id, such that it can be accessed 
     separatedly on the edit_tags page.
 --%>
<c:choose>
  <c:when test="${user.tagboxStyle eq 0}">
    <ul class="tagcloud" id="tagbox">
  </c:when>
  <c:otherwise>
    <ul class="taglist" id="tagbox">
  </c:otherwise>
</c:choose>

<%-- actually show tags --%>
<%@include file="/boxes/tags/userstags_tags.jsp" %>


</ul>
</li>