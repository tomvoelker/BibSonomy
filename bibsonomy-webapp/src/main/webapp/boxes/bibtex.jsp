<div id="bibbox">

  <c:set var="basePath" value="/"/>
  
  <h2 class="listh">publications</h2>
  
  <div class="listh">
    <c:if test="${ResourceBean.bibtexTotalCount != 0}">
      <span title="total: ${ResourceBean.bibtexTotalCount} publications">(${ResourceBean.bibtexTotalCount})</span>
    </c:if>
    <%-- Buttons to show content of page in different formats --%>
    <a href="${basePath}publrss/<c:out value='${requPath}'/>"><img alt="RSS" src="/resources/image/rss.png"/></a>
    <a href="${basePath}bib/<c:out value='${requPath}'/>"><img alt="BibTeX" src="/resources/image/bibtex.png"/></a>
    <a href="${basePath}burst/<c:out value='${requPath}'/>"><img alt="RDF" src="/resources/image/rdf.png"/></a>
    <a href="${basePath}export/<c:out value='${requPath}'/>"><img alt="other export options" src="/resources/image/more.png"/></a>
  </div>
  
  
  <%@include file="/boxes/nextprevbib.jsp" %>  
  <%@include file="/boxes/bibtex_list.jsp" %>  
  <%@include file="/boxes/nextprevbib.jsp" %>  
</div>