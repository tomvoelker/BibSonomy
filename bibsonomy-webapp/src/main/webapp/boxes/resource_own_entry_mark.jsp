<%-- highlights a resource if it is owned by the user which is logged in --%>
<c:choose>
  <c:when test="${user.name eq resource.user}">
    <li class="bm bmown">
  </c:when>
  <c:otherwise>
    <li class="bm">
  </c:otherwise>
</c:choose>