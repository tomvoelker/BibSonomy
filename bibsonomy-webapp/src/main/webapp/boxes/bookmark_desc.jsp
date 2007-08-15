      <div class="bmdesc">
        <a href="<c:out value='${book.url}' />"><c:out value='${book.title}' /></a>
      </div>
      <div class="bmext">
        <c:out value='${book.shortExtended}'/>
        <c:if test="${book.isLongExtended}">
          <span class="info">...<span><c:out value='${book.extended}'/></span></span>
        </c:if>
      </div>
      <span class="bmmeta">
        <%-- Tags --%>
        to
        <c:set var="tagString" value=""/>
  	    <c:forEach var="tag" items="${book.tags}">
  	      <c:set var="tagString" value="${tagString}${tag} "/>
          <a href='/user/<mtl:encode value="${book.user}" />/<mtl:encode value="${tag}" />'><c:out value='${tag}' /></a>
        </c:forEach>
        <%-- add "for:" tags --%>
        <c:if test="${user.name eq book.user}">
          <c:forEach var="user" items="${book.usersToPost}">
            <a href='/user/<mtl:encode value="${book.user}" />/for:<mtl:encode value="${user}" />'>for:<c:out value='${user}' /></a>
          </c:forEach>
        </c:if>

        <c:if test="${!empty book.group && book.group ne 'public'}">
          <c:choose>
            <c:when test="${book.group eq 'friends'}">
              as <a href="/friend/<mtl:encode value='${book.user}'/>">friends</a>
            </c:when>
            <c:otherwise>
              as <a href="/viewable/<mtl:encode value='${book.group}'/>"><c:out value="${book.group}" /></a>
            </c:otherwise>
          </c:choose>
        </c:if>
        <c:if test="${!isUserPage}">
          by <a href="/user/<mtl:encode value='${book.user}' />"><c:out value='${book.user}' /></a> 
        </c:if>
        <c:if test="${!empty book.hash && book.ctr > 2}">
          and <a <mtl:xotherpersons value="${book.ctr}"/> href="/url/${book.hash}">${book.ctr-1} other people</a>
        </c:if>
        <c:if test="${!empty book.hash && book.ctr == 2}">
          and <a style="background-color:rgb(97%,97%,97%);" href="/url/${book.hash}">1 other person</a>                
        </c:if>
      
        on <fmt:formatDate type="both" pattern="yyyy-MM-dd HH:mm:ss " value="${book.date}"/>
      </span>