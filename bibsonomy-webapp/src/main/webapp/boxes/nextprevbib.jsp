<c:if test="${!empty haveBibRows}">
  <div class="kiste">
    
    <span class="nextprev">
  	  <c:choose>
        <c:when test="${startBib > 0}">
          <a href="?startBook=<c:out value="${startBook}"/>&amp;startBib=<c:out value="${startBib - user.itemcount}"/>" title="go to the previous page" rel="Prev"> previous</a>
        </c:when>
        <c:otherwise>
          previous
        </c:otherwise>
      </c:choose>
      |
      <c:if test="${ResourceBean.bibtexTotalCount != 0}">
        <c:if test="${startBib - 2 * user.itemcount >= 0}"> <a href="?startBook=<c:out value='${startBook}'/>&amp;startBib=<c:out value='${startBib - user.itemcount - user.itemcount}'/>"><fmt:formatNumber value="${startBib / user.itemcount - 1}" maxFractionDigits="0"/></a> </c:if>
        <c:if test="${startBib - user.itemcount     >= 0}"> <a href="?startBook=<c:out value='${startBook}'/>&amp;startBib=<c:out value='${startBib - user.itemcount}'/>"><fmt:formatNumber value="${startBib / user.itemcount }" maxFractionDigits="0"/></a> </c:if>
        <fmt:formatNumber value="${startBib / user.itemcount + 1}" maxFractionDigits="0"/>
        <c:if test="${startBib + user.itemcount     < ResourceBean.bibtexTotalCount}"> <a href="?startBook=<c:out value='${startBook}'/>&amp;startBib=<c:out value='${startBib + user.itemcount}'/>"> <fmt:formatNumber value="${startBib / user.itemcount + 2}" maxFractionDigits="0"/></a> </c:if>
        <c:if test="${startBib + 2 * user.itemcount < ResourceBean.bibtexTotalCount}"> <a href="?startBook=<c:out value='${startBook}'/>&amp;startBib=<c:out value='${startBib + user.itemcount + user.itemcount}'/>"><fmt:formatNumber value="${startBib / user.itemcount + 3}" maxFractionDigits="0"/></a> </c:if>
        |
      </c:if>
      <c:choose>
        <c:when test="${empty allBibRows}">
          <a href="?startBook=<c:out value="${startBook}"/>&amp;startBib=<c:out value="${startBib + user.itemcount}"/>" title="go to the next page" rel="Next"> next</a>
        </c:when>
        <c:otherwise>
          next
        </c:otherwise>
      </c:choose>
    </span>
    
    <c:if test="${not empty user.name and empty disableActions}">
      <span class="actions">
        <a href="/beditbib/<c:out value='${requPath}'/>" title="edit the tags of your own publication entries on this page">edit</a> |
        <a href="?startBook=<c:out value='${startBook}'/>&amp;startBib=<c:out value='${startBib}'/>&amp;action=pick" title="add all publication entries from this page to your basket">pick</a> |
        <a href="?startBook=<c:out value='${startBook}'/>&amp;startBib=<c:out value='${startBib}'/>&amp;action=unpick" title="remove all publication entries from this page from your basket">unpick</a>
      </span>
    </c:if>
    
    &nbsp;
  </div>
</c:if>