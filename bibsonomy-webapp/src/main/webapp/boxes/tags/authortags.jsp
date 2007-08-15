<%-- 
  this shows the authors tags box 
--%>

<li><span class="sidebar_h">tags</span>

<jsp:useBean id="TagConceptBean" class="beans.TagConceptBean"  scope="request">
  <jsp:setProperty name="TagConceptBean" property="requAuthor" value="${param.requAuthor}"/> 
  <jsp:setProperty name="TagConceptBean" property="sortOrder"  value="${optionBean.sort}"/>
</jsp:useBean>

<%-- ------------------------------------- iterate over all tags ------------------------------------------------%>
<%@include file="/boxes/tagboxstyle.jsp" %>
<c:forEach var="tag" items="${TagConceptBean.tags}">
  <%-- set font size of tag depending on the count --%>
  <c:choose><c:when test="${tag.count == 1}">
    <li class="tagone">
  </c:when><c:when test="${tag.count > 10}">
    <li class="tagten">
  </c:when><c:otherwise>
    <li>
  </c:otherwise></c:choose>
    
  <%-- link to /author/AUTHOR/TAG --%>  
  <a title="${tag.count} posts" href="/author/<mtl:encode value='${param.requAuthor}' />/<mtl:encode value='${tag.name}' />"><c:out value="${tag.name}" /></a></li>
  
</c:forEach></ul>

</li>