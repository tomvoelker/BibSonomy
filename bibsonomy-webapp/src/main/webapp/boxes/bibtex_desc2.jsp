  <span class="bmmeta">
        to
  	    <c:forEach var="tag" items="${bib.tags}">
          <a href="${basePath}user/${bib.user}/<mtl:encode value='${tag}'/>"><c:out value="${tag}"/></a>
        </c:forEach>
        <%-- add "for:" tags --%>
        <c:if test="${user.name eq bib.user}">
          <c:forEach var="foruser" items="${bib.usersToPost}">
            <a href='${basePath}user/<mtl:encode value="${bib.user}" />/for:<mtl:encode value="${foruser}" />'>for:<c:out value='${foruser}' /></a>
          </c:forEach>
        </c:if>
         
        <c:if test="${!empty bib.group && bib.group ne 'public'}">
          <c:choose>
            <c:when test="${bib.group eq 'friends'}">
              as <a href="${basePath}friend/<mtl:encode value='${bib.user}'/>">friends</a>
            </c:when>
            <c:otherwise>
              as <a href="${basePath}viewable/<mtl:encode value='${bib.group}'/>"><c:out value="${bib.group}" /></a>
            </c:otherwise>
          </c:choose>
        </c:if>
        <c:if test="${!isUserPage}">
          by <a href="${basePath}user/${bib.user}">${bib.user}</a> 
        </c:if>
        
        <c:if test="${empty noStyleAttribute}">
          <c:if test="${!empty bib.simhash && bib.ctr > 2}">
            and <a <mtl:xotherpersons value="${bib.ctr}"/> href="${basePath}bibtex/<%=Bibtex.INTER_HASH %>${bib.simhash}">${bib.ctr-1} other people</a>
          </c:if>
          <c:if test="${!empty bib.simhash && bib.ctr == 2}">
            and <a style="background-color:rgb(97%,97%,97%);" href="${basePath}bibtex/<%=Bibtex.INTER_HASH %>${bib.simhash}">1 other person</a>
          </c:if>
        </c:if>
        
        on <fmt:formatDate type="both" pattern="yyyy-MM-dd HH:mm:ss " value="${bib.date}"/> 
      </span>