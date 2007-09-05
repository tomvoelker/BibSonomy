<%--
<c:if test="${resource.rating > 0}">
  <span class="bmrating">

  <c:forEach var="rating_pos" begin="0" end="5">
    <c:choose>
      <c:when test="${rating_pos < resource.rating}">
        <a style="background-color: #006699; padding-left: 3px;" href="javascript:rate('${resource.hash}', ${rating_pos});">&nbsp;</a>
      </c:when>
      <c:otherwise>
        <a style="background-color: #eee; padding-left: 3px;" href="javascript:rate('${resource.hash}', ${rating_pos});">&nbsp;</a>
      </c:otherwise>
    </c:choose>
  </c:forEach>
  
    <!-- img height="12px"
    src="/resources/image/star_<c:out value='${resource.rating}'/>.gif"
    alt="Rating: <c:out value='${resource.rating}' />" /--> 
  
    
  </span>
</c:if>
--%>