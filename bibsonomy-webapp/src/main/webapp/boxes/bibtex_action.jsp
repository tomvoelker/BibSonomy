
<div class="bmfoot">


<span class="bmaction">
  <c:if test="${not empty user.name}">
    
    <%-- PICK --%>
    <c:choose>
      <c:when test="${empty unpick}">
        <a onclick="pickUnpickPublication(event);" title="add this post to your basket" href="/ajax/pickUnpickPost?action=pick&amp;requestedResourceHash=${resource.hash}&amp;user=<mtl:encode value='${resource.user}'/>&amp;ckey=${ckey}">pick</a>
      </c:when>
      <c:otherwise>
        <a title="remove this post from your basket" href="/ajax/pickUnpickPost?action=unpick&amp;requestedResourceHash=${resource.hash}&amp;user=<mtl:encode value='${resource.user}'/>&amp;ckey=${ckey}">unpick</a>
      </c:otherwise>
    </c:choose>
    <%-- user.name == resource.User ? --%>
    <c:choose>
      <c:when test="${user.name ne resource.user}">
        <%-- different users -- COPY --%>
        <c:url var="copy_url" value="${projectHome}ShowBibtexEntry">
	      <c:param name="hash" value="${resource.hash}"/>
		  <c:param name="user" value="${resource.user}"/>
		  <c:param name="copytag" value="${resource.tagString}"/>
	    </c:url>
        | <a href="<c:out value='${copy_url}'/>" title="copy this post to your repository">copy</a><%-- 
  --%></c:when>
      <c:otherwise>
        <%-- same user -- EDIT, DELETE, ... --%>
        <%-- (FAST TAG) EDIT --%>
        <span><c:choose>
          <c:when test="${not empty noedittags}">
            | <a href="/ShowBibtexEntry?hash=${resource.hash}" title="edit this post">edit</a>
          </c:when>
          <c:otherwise>
            | <a onclick="editTags(this,'${ckey}');return false;" tags='<c:out value="${resource.fullTagString}"/>' hashsum="${resource.hash}" href="/ShowBibtexEntry?hash=${resource.hash}" title="edit this entry">edit</a>
          </c:otherwise>
        </c:choose></span>
        <%-- DELETE --%>          
        | <a href="/deletePost?resourceHash=${resource.hash}&amp;ckey=${ckey}" title="delete this post from your repository">delete</a><%-- 
  --%></c:otherwise>
    </c:choose>
</c:if>

  <%-- URL --%>
  <c:if test="${not empty resource.cleanurl}">
    | <a href="<c:out value='${resource.cleanurl}'/>" title="this post contains a URL which is linked here">URL</a>        
  </c:if>
  
  | <a href="/bib/bibtex/<%=Bibtex.INTRA_HASH %>${resource.hash}/<mtl:encode value='${resource.user}'/>" title="show this post in BibTeX format">BibTeX</a>

  <%-- OPENURL --%>
  <c:if test="${not empty user.openurl}">
    | <a href="<c:out value='${user.openurl}'/>?<c:out value='${resource.openurl}'/>">OpenURL</a>
  </c:if>
  
  <!-- UnAPI -->
  <abbr class="unapi-id" title="${resource.hash}/${f:escapeXml(resource.user)}"><c:out value=" "/></abbr>
  
  
</span>

  &nbsp;
</div>