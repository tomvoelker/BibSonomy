      <div class="bmdesc">
        <a href="<c:out value='${book.resource.url}' />"><c:out value='${book.resource.title}' /></a>
      </div>
      <div class="bmext">
      </div>
      <span class="bmmeta">
        <%-- Tags --%>
        to
        <c:set var="tagString" value=""/>
  	    <c:forEach var="tag" items="${book.tags}">
  	      <c:set var="tagString" value="${tagString}${tag.name} "/>
          <a href='/user/<mtl:encode value="${book.user.name}" />/<mtl:encode value="${tag.name}" />'><c:out value='${tag.name}' /></a>
        </c:forEach>
      
        on <fmt:formatDate type="both" pattern="yyyy-MM-dd HH:mm:ss " value="${book.date}"/>
      </span>