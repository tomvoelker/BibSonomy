<%-- the tags of a resource --%>
        to
        <span class="bmtags">
        <c:forEach var="tag" items="${resource.tags}">
          <a href="${basePath}user/${resource.user}/<mtl:encode value='${tag}'/>"><c:out value="${tag}"/></a>
        </c:forEach>
        <%-- add "for:" tags --%>
        <c:if test="${user.name eq resource.user}">
          <c:forEach var="foruser" items="${resource.usersToPost}">
            <a href='${basePath}user/<mtl:encode value="${resource.user}" />/for:<mtl:encode value="${foruser}" />'>for:<c:out value='${foruser}' /></a>
          </c:forEach>
        </c:if>
        </span>
        
<%-- group of a resource --%>
        <c:if test="${!empty resource.group && resource.group ne 'public'}">
          <c:choose>
            <c:when test="${resource.group eq 'friends'}">
              as <a href="${basePath}friend/<mtl:encode value='${resource.user}'/>">friends</a>
            </c:when>
            <c:otherwise>
              as <a href="${basePath}viewable/<mtl:encode value='${resource.group}'/>"><c:out value="${resource.group}" /></a>
            </c:otherwise>
          </c:choose>
        </c:if>

<%-- user name of a resource --%>        
        <c:if test="${!isUserPage}">
          by <a href="${basePath}user/${resource.user}">${resource.user}</a> 
        </c:if>