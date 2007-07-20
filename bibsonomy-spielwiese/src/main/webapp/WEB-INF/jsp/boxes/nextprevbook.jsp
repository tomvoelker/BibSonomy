<c:if test="${!empty haveBookRows}">
  <div class="kiste">
    
    <span class="nextprev">
   	  <c:choose>
	    <c:when test="${startBook > 0}">
	      <a href="?startBook=<c:out value='${startBook - user.itemcount}'/>&amp;startBib=<c:out value='${param.startBib}'/>" title="go to the previous page" rel="Prev"> previous</a>
	    </c:when>
	    <c:otherwise>
	      previous
	    </c:otherwise>
	  </c:choose>
	  |
      <c:if test="${ResourceBean.bookmarkTotalCount != 0}">
        <c:if test="${startBook - 2 * user.itemcount >= 0}"> <a href="?startBook=<c:out value='${startBook - user.itemcount - user.itemcount}'/>&amp;startBib=<c:out value='${param.startBib}'/>"><fmt:formatNumber value="${startBook / user.itemcount - 1}" maxFractionDigits="0"/></a> </c:if>
        <c:if test="${startBook - user.itemcount     >= 0}"> <a href="?startBook=<c:out value='${startBook - user.itemcount}'/>&amp;startBib=<c:out value='${param.startBib}'/>"><fmt:formatNumber value="${startBook / user.itemcount }" maxFractionDigits="0"/></a> </c:if>
          <fmt:formatNumber value="${startBook / user.itemcount + 1}" maxFractionDigits="0"/>          
        <c:if test="${startBook + user.itemcount     < ResourceBean.bookmarkTotalCount}"> <a href="?startBook=<c:out value='${startBook + user.itemcount}'/>&amp;startBib=<c:out value='${param.startBib}'/>"> <fmt:formatNumber value="${startBook / user.itemcount + 2}" maxFractionDigits="0"/></a> </c:if>
        <c:if test="${startBook + 2 * user.itemcount < ResourceBean.bookmarkTotalCount}"> <a href="?startBook=<c:out value='${startBook + user.itemcount + user.itemcount}'/>&amp;startBib=<c:out value='${param.startBib}'/>"><fmt:formatNumber value="${startBook / user.itemcount + 3}" maxFractionDigits="0"/></a> </c:if>
        |
      </c:if>
  	  <c:choose>
  	    <c:when test="${empty allBookRows}">
	      <a href="?startBook=<c:out value="${startBook + user.itemcount}"/>&amp;startBib=<c:out value="${param.startBib}"/>" title="go to the next page" rel="Next"> next</a>
	    </c:when>
	    <c:otherwise>
	      next
	    </c:otherwise>
	  </c:choose>
    </span>

    <c:if test="${not empty user.name and empty disableActions}">
      <span class="actions">
        <a href="/bediturl/<c:out value='${requPath}'/>" title="edit the tags of your own bookmarks on this page">edit</a>
      </span>
    </c:if>
    
    &nbsp;
  
  </div>
</c:if>