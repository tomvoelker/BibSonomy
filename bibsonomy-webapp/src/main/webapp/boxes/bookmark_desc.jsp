      <div class="bmtitle">
        <a href="<c:out value='${resource.url}' />"><c:out value='${resource.title}' /></a>&nbsp;
      </div>
      
      <div class="bmdesc">
        <c:out value='${resource.shortExtended}'/>
        <c:if test="${resource.isLongExtended}">
          <span class="info">...<span><c:out value='${resource.extended}'/></span></span>
        </c:if>
      </div>
      
      <span class="bmmeta">
        <%@include file="/boxes/resource_meta.jsp" %> 

        <c:if test="${!empty resource.hash && resource.ctr > 2}">
              <c:set var="style"><mtl:xOtherPersonsStyle value="${resource.ctr}"/></c:set>
          and <a style="${style}" href="/url/${resource.hash}">${resource.ctr-1} other people</a>
        </c:if>
        <c:if test="${!empty resource.hash && resource.ctr == 2}">
          and <a style="background-color:rgb(97%,97%,97%);" href="/url/${resource.hash}">1 other person</a>
        </c:if>
      
        on <fmt:formatDate type="both" pattern="yyyy-MM-dd HH:mm:ss " value="${resource.date}"/>
      </span>