<div id="itembox">
  Showing ${user.itemcount} items per page. Show 
  <c:choose>
    <c:when test="${empty ckey}">
      <a href="?items=10">10</a>, 
      <a href="?items=25">25</a>, 
      <a href="?items=50">50</a>, 
      <a href="?items=100">100</a> 
    </c:when>
    <c:otherwise>
      <a href="?items=10&amp;ckey=${ckey}">10</a>, 
      <a href="?items=25&amp;ckey=${ckey}">25</a>, 
      <a href="?items=50&amp;ckey=${ckey}">50</a>, 
      <a href="?items=100&amp;ckey=${ckey}">100</a> 
    </c:otherwise>
  </c:choose>
  items per page.
</div>