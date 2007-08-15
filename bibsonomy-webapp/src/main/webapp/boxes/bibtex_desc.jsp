<div class="bmdesc">
  <a href="${basePath}bibtex/<%=Bibtex.INTRA_HASH %>${bib.hash}/<mtl:encode value='${bib.user}'/>"><mtl:bibclean value="${bib.title}" /></a>
</div>
<%-- Autoren, Editoren, Journal, Booktitel, Jahr --%>
<div class="bmext">
  <span style="color:#555555;"> 
    <c:choose>
      <c:when test="${not empty bib.author}">
        <c:forEach var="author" items="${bib.authornamesseparated}" varStatus="loopStatus">        	 
        	 	<c:out value="${author[0]} " />
        	 	<a href="${basePath}author/<mtl:encode value='${author[1]}'/>"><c:out value="${author[1]}" /></a>         	     	 
        	 <c:if test="${not loopStatus.last}"> and </c:if>
        </c:forEach>       
      </c:when>
      <c:otherwise>
        <c:forEach var="editor" items="${bib.editornamesseparated}" varStatus="loopStatus">        	 
        	 	<c:out value="${editor[0]} " />
        	 	<a href="${basePath}author/<mtl:encode value='${editor[1]}'/>"><c:out value="${editor[1]}" /></a>         	     	 
        	 <c:if test="${not loopStatus.last}"> and </c:if>        	 
        </c:forEach> 
        (eds.).
      </c:otherwise>
    </c:choose> 
  </span> 
  <c:choose>
    <c:when test="${not empty bib.journal}">
      <em><c:out value="${bib.journal}" /></em>
      <c:if test="${not empty bib.volume}">
        <b><c:out value="${bib.volume}"/></b>
      </c:if>
    </c:when>
    <c:otherwise>
      <em><c:out value="${bib.booktitle}" /></em>
    </c:otherwise>
  </c:choose> 
  <c:if test="${not empty bib.pages}">
    <c:out value="${bib.pages}"/>
  </c:if>
  (<c:out value="${bib.year}"/>)
</div>
