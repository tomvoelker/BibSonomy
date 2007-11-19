<div class="bmtitle">

  <%-- PDF symbol --%>
  <c:if test='${not empty resource.docHash}'> 
    <a href="/documents/${resource.docHash}"><img alt="PDF" src="/resources/image/document-txt-blue.png" style="float: left;"/></a>
  </c:if>  


  <a href="${basePath}bibtex/<%=Bibtex.INTRA_HASH %>${resource.hash}/<mtl:encode value='${resource.user}'/>"><mtl:bibclean value="${resource.title}" /></a>
</div>
<%-- Autoren, Editoren, Journal, Booktitel, Jahr --%>
<div class="bmdesc">
  <span style="color:#555555;"> 
    <c:choose>
      <c:when test="${not empty resource.author}">
        <c:forEach var="author" items="${resource.authornamesseparated}" varStatus="loopStatus">        	 
        	 	<c:out value="${author[0]} " />
        	 	<a href="${basePath}author/<mtl:encode value='${author[1]}'/>"><c:out value="${author[1]}" /></a>         	     	 
        	 <c:if test="${not loopStatus.last}"> and </c:if>
        </c:forEach>       
      </c:when>
      <c:otherwise>
        <c:forEach var="editor" items="${resource.editornamesseparated}" varStatus="loopStatus">        	 
        	 	<c:out value="${editor[0]} " />
        	 	<a href="${basePath}author/<mtl:encode value='${editor[1]}'/>"><c:out value="${editor[1]}" /></a>         	     	 
        	 <c:if test="${not loopStatus.last}"> and </c:if>        	 
        </c:forEach> 
        (eds.).
      </c:otherwise>
    </c:choose> 
  </span> 
  <c:choose>
    <c:when test="${not empty resource.journal}">
      <em><mtl:bibclean value="${resource.journal}" /></em>
      <c:if test="${not empty resource.volume}">
        <b><c:out value="${resource.volume}"/></b>
      </c:if>
    </c:when>
    <c:otherwise>
      <em><mtl:bibclean value="${resource.booktitle}" /></em>
    </c:otherwise>
  </c:choose> 
  <c:if test="${not empty resource.pages}">
    <c:out value="${resource.pages}"/>
  </c:if>
  (<c:out value="${resource.year}"/>)
</div>
