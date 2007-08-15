
<%-- 
  shows on the /relations/USER pages navigation bars to navigate through relations with next/prev links
  --%>
  
  <div class="kiste">
    <span class="nextprev">
      <%-- show "previous" link--%>
   	  <c:choose>
	    <c:when test="${param.startRel > 0}">
	      <a href="?startRel=<c:out value='${param.startRel - user.itemcount}'/>" title="go to the previous page" rel="Prev"> previous</a>
	    </c:when>
	    <c:otherwise>
	      previous
	    </c:otherwise>
	  </c:choose>
	  
	  <%-- show page numbers --%>
	  |
      <c:if test="${RelationBean.total != 0}">
        <c:if test="${param.startRel - 2 * user.itemcount >= 0}"> <a href="?startRel=<c:out value='${param.startRel - user.itemcount - user.itemcount}'/>"><fmt:formatNumber value="${param.startRel / user.itemcount - 1}" maxFractionDigits="0"/></a> </c:if>
        <c:if test="${param.startRel - user.itemcount     >= 0}"> <a href="?startRel=<c:out value='${param.startRel - user.itemcount}'/>"><fmt:formatNumber value="${param.startRel / user.itemcount }" maxFractionDigits="0"/></a> </c:if>
        <fmt:formatNumber value="${param.startRel / user.itemcount + 1}" maxFractionDigits="0"/>
        <c:if test="${param.startRel + user.itemcount     < RelationBean.total}"> <a href="?startRel=<c:out value='${param.startRel + user.itemcount}'/>"> <fmt:formatNumber value="${param.startRel / user.itemcount + 2}" maxFractionDigits="0"/></a> </c:if>
        <c:if test="${param.startRel + 2 * user.itemcount < RelationBean.total}"> <a href="?startRel=<c:out value='${param.startRel + user.itemcount + user.itemcount}'/>"><fmt:formatNumber value="${param.startRel / user.itemcount + 3}" maxFractionDigits="0"/></a> </c:if>
        |
      </c:if>
      
      <%-- show "next" link --%>
  	  <c:choose>
  	    <c:when test="${not RelationBean.allRelRows}">
	      <a href="?startRel=<c:out value="${param.startRel + user.itemcount}"/>" title="go to the next page" rel="Next"> next</a>
	    </c:when>
	    <c:otherwise>
	      next
	    </c:otherwise>
	  </c:choose>
    </span>
    
    <span class="actions">
        <a href="/edit_tags">edit</a>
    </span>
    
    &nbsp;
  
  </div>
