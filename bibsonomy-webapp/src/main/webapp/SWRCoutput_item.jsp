<c:if test ="${!empty resource.address}"><swrc:address><c:out value ='${resource.address}'/></swrc:address></c:if>
<%-- annote: NEW --%><c:if test ="${!empty resource.annote}"><swrc:annote><c:out value ='${resource.annote}'/></swrc:annote></c:if>
<%-- author: BELOW --%>
<c:if test ="${!empty resource.booktitle}"><swrc:booktitle><c:out value ='${resource.booktitle}'/></swrc:booktitle></c:if>
<c:if test ="${!empty resource.chapter}"><swrc:chapter><c:out value ='${resource.chapter}'/></swrc:chapter></c:if>
<%-- crossref: NEW --%><c:if test ="${!empty resource.crossref}"><swrc:crossref><c:out value ='${resource.crossref}'/></swrc:crossref></c:if>
<c:if test ="${!empty resource.edition}"><swrc:edition><c:out value ='${resource.edition}'/></swrc:edition></c:if>
<%-- editor: BELOW --%>
<c:if test ="${!empty resource.howpublished}"><swrc:howpublished><c:out value ='${resource.howpublished}'/></swrc:howpublished></c:if>
<c:if test ="${!empty resource.institution}"><swrc:institution><swrc:Organization swrc:name="<c:out value ='${resource.institution}'/>"/></swrc:institution></c:if>
<c:if test ="${!empty resource.journal}"><swrc:journal><c:out value ='${resource.journal}'/></swrc:journal></c:if>
<%-- key: NEW--%><c:if test ="${!empty resource.key}"><swrc:key><c:out value ='${resource.key}'/></swrc:key></c:if>
<c:if test ="${!empty resource.month}"><swrc:month><c:out value ='${resource.month}'/></swrc:month></c:if>
<c:if test ="${!empty resource.note}"><swrc:note><c:out value ='${resource.note}'/></swrc:note></c:if>
<c:if test ="${!empty resource.number}"><swrc:number><c:out value ='${resource.number}'/></swrc:number></c:if>
<c:if test ="${!empty resource.organization}"><swrc:organization><swrc:Organization swrc:name="<c:out value ='${resource.organization}'/>"/></swrc:organization></c:if>
<c:if test ="${!empty resource.pages}"><swrc:pages><c:out value ='${resource.pages}'/></swrc:pages></c:if>
<c:if test ="${!empty resource.publisher}"><swrc:publisher><swrc:Organization swrc:name="<c:out value ='${resource.publisher}'/>"/></swrc:publisher></c:if>
<c:if test ="${!empty resource.school}"><swrc:school><swrc:University swrc:name="<c:out value ='${resource.school}'/>"/></swrc:school></c:if>
<c:if test ="${!empty resource.series}"><swrc:series><c:out value ='${resource.series}'/></swrc:series></c:if>
<c:if test ="${!empty resource.title}"><swrc:title><c:out value ='${resource.title}'/></swrc:title></c:if>
<c:if test ="${!empty resource.type}"><swrc:type><c:out value ='${resource.type}'/></swrc:type></c:if>
<c:if test ="${!empty resource.volume}"><swrc:volume><c:out value ='${resource.volume}'/></swrc:volume></c:if>
<c:if test ="${!empty resource.year}"><swrc:year><c:out value ='${resource.year}'/></swrc:year></c:if>
<%-- non-bibtex --%>
<c:if test ="${!empty resource.tagString}"><swrc:keywords><c:out value ='${resource.tagString}'/></swrc:keywords></c:if>
<%-- day: NEW --%><c:if test ="${!empty resource.day}"><swrc:day><c:out value ='${resource.day}'/></swrc:day></c:if>
<c:if test ="${!empty resource.date}"><swrc:date><c:out value ='${resource.date}'/></swrc:date></c:if>
<c:if test ="${!empty resource.bibtexAbstract}"><swrc:abstract><c:out value ='${resource.bibtexAbstract}'/></swrc:abstract></c:if>

<%-- included "misc" fields --%>
<c:forEach var="misc" items="${resource.miscMap}">
  <swrc:hasExtraField>
    <swrc:Field swrc:key="<c:out value='${misc.key}'/>" swrc:value="<c:out value='${misc.value}'/>"/>
  </swrc:hasExtraField>
</c:forEach>

<swrc:author>
  <rdf:Seq>
  <c:forEach var="person" varStatus="status" items="${resource.authorlist}">
    <rdf:_${status.index + 1}><swrc:Person swrc:name="<c:out value ='${person}'/>" /></rdf:_${status.index + 1}>
  </c:forEach>
  </rdf:Seq>
</swrc:author>

<swrc:editor>
  <rdf:Seq>
  <c:forEach var="person" varStatus="status" items="${resource.editorlist}">
    <rdf:_${status.index + 1}><swrc:Person swrc:name="<c:out value ='${person}'/>" /></rdf:_${status.index + 1}>
  </c:forEach>
  </rdf:Seq>
</swrc:editor>