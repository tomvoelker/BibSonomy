<c:forEach var="tag" items="${TagConceptBean.tags}">
  <%-- set font size of tag depending on the count --%>
  <c:choose><c:when test="${tag.count == 1}">
    <li class="tagone">
  </c:when><c:when test="${tag.count > 10}">
    <li class="tagten">
  </c:when><c:otherwise>
    <li>
  </c:otherwise></c:choose>
      

  <%-- this variable will be used to construct the url which points to the tag page --%>
  <c:set var="urlPrefix" value="/user"/>

  <%-- show/hide arrows --%>
  <%-- if this tag is a supertag and this are the users own tags --%>
  <c:if test="${tag.supertag}">
    <c:set var="urlPrefix" value="/concept/user"/>
    <%-- check, whether concept is shown or not --%>
    <c:choose><c:when test="${tag.shown}">
      <a onclick='showOrHideConcept(event,"hide");' href="/ajax/pickUnpickConcept?action=hide&amp;tag=<mtl:encode value='${tag.name}'/>&amp;ckey=${ckey}" title="hide relation">&darr;</a>
      <a onclick='showOrHideConcept(event,"show");' href="/ajax/pickUnpickConcept?action=show&amp;tag=<mtl:encode value='${tag.name}'/>&amp;ckey=${ckey}" title="show relation" style="display:none" >&uarr;</a>
    </c:when><c:otherwise>
      <a onclick='showOrHideConcept(event,"hide");' href="/ajax/pickUnpickConcept?action=hide&amp;tag=<mtl:encode value='${tag.name}'/>&amp;ckey=${ckey}" title="hide relation" style="display:none">&darr;</a>
      <a onclick='showOrHideConcept(event,"show");' href="/ajax/pickUnpickConcept?action=show&amp;tag=<mtl:encode value='${tag.name}'/>&amp;ckey=${ckey}" title="show relation">&uarr;</a>
    </c:otherwise></c:choose>
  </c:if>
    
  <%-- link to /user/USER/TAG or /concept/user/USER/TAG --%>
  <a 
    title="${tag.count} posts" 
    <c:if test="${user.tagboxTooltip == 1}">
      <%-- TODO: here ${tag.name} is not properly quoted --%>
      onmouseover="javascript:preDoTooltip(event)" onmouseout="javascript:hideTip()"
    </c:if>
    <%--
	<c:if test="${param.tagMaxFreq != null}">
		style="font-size:${100 + (tag.count / param.tagMaxFreq * 200)}%"
	</c:if>
	--%>    
    href="${urlPrefix}/<mtl:encode value='${requestedUserName}' />/<mtl:encode value='${tag.name}' />"
  ><c:out value="${tag.name}" /></a></li>
  
      
</c:forEach>