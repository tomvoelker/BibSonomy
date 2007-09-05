<span class="bmmeta">
  
  <%-- tags, group, user --%>
  <%@include file="/boxes/resource_meta.jsp" %> 
  
        <c:if test="${empty noStyleAttribute}">
          <c:if test="${!empty resource.simhash && resource.ctr > 2}">
            and <a <mtl:xotherpersons value="${resource.ctr}"/> href="${basePath}bibtex/<%=Bibtex.INTER_HASH %>${resource.simhash}">${resource.ctr-1} other people</a>
          </c:if>
          <c:if test="${!empty resource.simhash && resource.ctr == 2}">
            and <a style="background-color:rgb(97%,97%,97%);" href="${basePath}bibtex/<%=Bibtex.INTER_HASH %>${resource.simhash}">1 other person</a>
          </c:if>
        </c:if>
        
        on <fmt:formatDate type="both" pattern="yyyy-MM-dd HH:mm:ss " value="${resource.date}"/> 

</span>