<div id="bookbox">    
 
  <c:set var="basePath" value="/"/>
  
  <h2 class="listh">bookmarks</h2> 

  <div class="listh">  
    <c:if test="${ResourceBean.bookmarkTotalCount != 0}">
      <span title="total: ${ResourceBean.bookmarkTotalCount} bookmarks">(${ResourceBean.bookmarkTotalCount})</span>
    </c:if>
    <%-- Buttons to show content of page in different formats --%>
    <a href="${basePath}rss/<c:out value='${requPath}'/>" ><img alt="RSS" src="/resources/image/rss.png"/></a>
    <a href="${basePath}xml/<c:out value='${requPath}'/>" ><img alt="XML" src="/resources/image/xml.png"/></a>
  </div>
  
  
  <%@include file="/boxes/nextprevbook.jsp" %>     
  <ul class="bblist"><c:forEach var="book" items="${ResourceBean.bookmarks}">

   <c:choose>
     <c:when test="${user.name eq book.user}">
       <li class="bm bmown">
     </c:when>
     <c:otherwise>
   <li class="bm">
      </c:otherwise>
    </c:choose>
      
      
      <%@include file="/boxes/bookmark_desc.jsp" %>     
      <%@include file="/boxes/bookmark_action.jsp" %>     
    </li>
  </c:forEach></ul>
  <%@include file="/boxes/nextprevbook.jsp" %>      
</div>
