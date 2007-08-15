<c:choose>
  <c:when test="${user.tagboxStyle eq 0}">
    <ul class="tagcloud">
  </c:when>
  <c:otherwise>
    <ul class="taglist">
  </c:otherwise>
</c:choose>