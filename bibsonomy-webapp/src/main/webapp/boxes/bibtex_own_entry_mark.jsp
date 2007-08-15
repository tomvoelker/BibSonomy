    <c:choose>
     <c:when test="${user.name eq bib.user}">
       <li class="bm bmown">
     </c:when>
     <c:otherwise>
   <li class="bm">
      </c:otherwise>
    </c:choose>
    <c:if test='${not empty bib.docHash}'> 
      	<a href="/documents/${bib.docHash}"><img alt="PDF" src="/resources/image/document-txt-blue.png" style="float: left;"/></a>
   	</c:if>  
