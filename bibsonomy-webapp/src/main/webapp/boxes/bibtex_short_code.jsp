@<c:out value="${bib.entrytype}" />{<c:out value="${bib.bibtexKey}" />, 
   <c:choose><c:when test="${bib.validtitle}" >title  = {<c:out value="${bib.title}" />}</c:when><c:otherwise><strong>title  = {<c:out value="${bib.title}" />}</strong></c:otherwise></c:choose>,
   <c:choose><c:when test="${bib.validauthor}">author = {<c:out value="${bib.author}"/>}</c:when><c:otherwise><strong>author = {<c:out value="${bib.author}"/>}</strong></c:otherwise></c:choose>,
   <c:choose><c:when test="${bib.valideditor}">editor = {<c:out value="${bib.editor}"/>}</c:when><c:otherwise><strong>editor = {<c:out value="${bib.editor}"/>}</strong></c:otherwise></c:choose>,
   <c:choose><c:when test="${bib.validyear}"  >year   = {<c:out value="${bib.year}"  />}</c:when><c:otherwise><strong>year   = {<c:out value="${bib.year}"  />}</strong></c:otherwise></c:choose>}
