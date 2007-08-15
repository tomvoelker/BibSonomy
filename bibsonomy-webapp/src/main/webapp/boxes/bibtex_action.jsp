<span class="bmaction">
  <c:if test="${not empty user.name}">
    <%-- PICK --%>
    <a onclick="pickUnpickPublication(event);" title="add this entry to your basket" href="/Collector?pick=${bib.hash}&amp;user=<mtl:encode value='${bib.user}'/>&amp;ckey=${ckey}">pick</a> 
 
    <%-- user.name == bib.User ? --%>
    <c:choose>
      <c:when test="${user.name ne bib.user}">
        <%-- different users -- COPY --%>
        <c:url var="copy_url" value="${projectHome}ShowBibtexEntry">
	      <c:param name="hash" value="${bib.hash}"/>
		  <c:param name="user" value="${bib.user}"/>
		  <c:param name="copytag" value="${bib.tagString}"/>
	    </c:url>
        <a href="<c:out value='${copy_url}'/>" title="copy this entry to your repository">copy</a><%-- 
  --%></c:when>
      <c:otherwise>
        <%-- same user -- EDIT, DELETE, ... --%>
        <%-- (FAST TAG) EDIT --%>
        <span><c:choose>
          <c:when test="${not empty noedittags}">
            <a href="/ShowBibtexEntry?hash=${bib.hash}" title="edit this entry">edit</a>
          </c:when>
          <c:otherwise>
            <a onclick="editTags(this,'${ckey}');return false;" tags='<c:out value="${bib.fullTagString}"/>' hashsum="${bib.hash}" href="/ShowBibtexEntry?hash=${bib.hash}" title="edit this entry">edit</a>
          </c:otherwise>
        </c:choose></span>
        <%-- DELETE --%>          
        <a href="/BibtexHandler?hash=${bib.hash}&amp;requTask=delete&amp;ckey=${ckey}" title="delete this entry from your repository">delete</a><%-- 
  --%></c:otherwise>
    </c:choose>
</c:if>
</span>
      
<span class="bmmeta">

  <%-- URL --%>
  <c:if test="${not empty bib.cleanurl}">
    <a href="<c:out value='${bib.cleanurl}'/>" title="this entry contains an URL which is linked here">URL</a>        
  </c:if>
  
  <a href="/bib/bibtex/<%=Bibtex.INTRA_HASH %>${bib.hash}/<mtl:encode value='${bib.user}'/>" title="show this entry in BibTeX format">BibTeX</a>

  <%-- OPENURL --%>
  <c:if test="${!empty user.openurl}">
    <a href="<c:out value='${user.openurl}'/>?<c:out value='${bib.openurl}'/>">OpenURL</a>
  </c:if>
  
</span>