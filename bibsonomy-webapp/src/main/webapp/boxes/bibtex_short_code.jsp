@<c:out value="${bib.entrytype}" />{<c:out value="${bib.bibtexKey}" />, 
   <c:choose><c:when test="${bib.validtitle}" >title  = {<c:out value="${bib.title}" />}</c:when><c:otherwise><strong>title  = {<c:out value="${bib.title}" />}</strong></c:otherwise></c:choose>,
   author = {<c:out value="${bib.author}"/>},
   editor = {<c:out value="${bib.editor}"/>},
   <c:choose><c:when test="${bib.validyear}"  >year   = {<c:out value="${bib.year}"  />}</c:when><c:otherwise><strong>year   = {<c:out value="${bib.year}"  />}</strong></c:otherwise></c:choose>}
