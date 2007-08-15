<%-- ----------------- Header with buttons/links ------------  --%>

  <c:if test="${not empty haveBibRows}">
  <div class="kiste">

    <span class="nextprev">
      export to: <a href="/bib/basket">BibTeX</a> | <a href="/endnote/basket">EndNote</a> | <a href="/export/basket">more</a>
    </span>

    <span class="actions">
      <a href="/beditbib/<c:out value='${requPath}'/>" title="edit the tags of your own publication entries on this page">edit</a> |
      <a href="/Collector?requTask=unpickAll&amp;ckey=${ckey}" title="remove all publications from the basket">remove all from basket</a>

      <c:if test="${not empty user.groups}">
      | edit meta data for group:
        <c:forEach items="${user.groups}" var="group">
           <a href="/ExtendedFieldsHandler?group=<c:out value='${group}'/>"><c:out value="${group}"/></a>
        </c:forEach>
      </c:if>
    </span>
    
    &nbsp;
  </div>
  </c:if>