<c:if test ="${!empty bib.address}"><swrc:address><c:out value ='${bib.address}'/></swrc:address></c:if>
<%-- annote: NEW --%><c:if test ="${!empty bib.annote}"><swrc:annote><c:out value ='${bib.annote}'/></swrc:annote></c:if>
<%-- author: BELOW --%>
<c:if test ="${!empty bib.booktitle}"><swrc:booktitle><c:out value ='${bib.booktitle}'/></swrc:booktitle></c:if>
<c:if test ="${!empty bib.chapter}"><swrc:chapter><c:out value ='${bib.chapter}'/></swrc:chapter></c:if>
<%-- crossref: NEW --%><c:if test ="${!empty bib.crossref}"><swrc:crossref><c:out value ='${bib.crossref}'/></swrc:crossref></c:if>
<c:if test ="${!empty bib.edition}"><swrc:edition><c:out value ='${bib.edition}'/></swrc:edition></c:if>
<%-- editor: BELOW --%>
<c:if test ="${!empty bib.howpublished}"><swrc:howpublished><c:out value ='${bib.howpublished}'/></swrc:howpublished></c:if>
<c:if test ="${!empty bib.institution}"><swrc:institution><swrc:Organization swrc:name="<c:out value ='${bib.institution}'/>"/></swrc:institution></c:if>
<c:if test ="${!empty bib.journal}"><swrc:journal><c:out value ='${bib.journal}'/></swrc:journal></c:if>
<%-- key: NEW--%><c:if test ="${!empty bib.key}"><swrc:key><c:out value ='${bib.key}'/></swrc:key></c:if>
<c:if test ="${!empty bib.month}"><swrc:month><c:out value ='${bib.month}'/></swrc:month></c:if>
<c:if test ="${!empty bib.note}"><swrc:note><c:out value ='${bib.note}'/></swrc:note></c:if>
<c:if test ="${!empty bib.number}"><swrc:number><c:out value ='${bib.number}'/></swrc:number></c:if>
<c:if test ="${!empty bib.organization}"><swrc:organization><swrc:Organization swrc:name="<c:out value ='${bib.organization}'/>"/></swrc:organization></c:if>
<c:if test ="${!empty bib.pages}"><swrc:pages><c:out value ='${bib.pages}'/></swrc:pages></c:if>
<c:if test ="${!empty bib.publisher}"><swrc:publisher><swrc:Organization swrc:name="<c:out value ='${bib.publisher}'/>"/></swrc:publisher></c:if>
<c:if test ="${!empty bib.school}"><swrc:school><swrc:University swrc:name="<c:out value ='${bib.school}'/>"/></swrc:school></c:if>
<c:if test ="${!empty bib.series}"><swrc:series><c:out value ='${bib.series}'/></swrc:series></c:if>
<c:if test ="${!empty bib.title}"><swrc:title><c:out value ='${bib.title}'/></swrc:title></c:if>
<c:if test ="${!empty bib.type}"><swrc:type><c:out value ='${bib.type}'/></swrc:type></c:if>
<c:if test ="${!empty bib.volume}"><swrc:volume><c:out value ='${bib.volume}'/></swrc:volume></c:if>
<c:if test ="${!empty bib.year}"><swrc:year><c:out value ='${bib.year}'/></swrc:year></c:if>
<%-- non-bibtex --%>
<c:if test ="${!empty bib.tagString}"><swrc:keywords><c:out value ='${bib.tagString}'/></swrc:keywords></c:if>
<%-- day: NEW --%><c:if test ="${!empty bib.day}"><swrc:day><c:out value ='${bib.day}'/></swrc:day></c:if>
<c:if test ="${!empty bib.date}"><swrc:date><c:out value ='${bib.date}'/></swrc:date></c:if>
<c:if test ="${!empty bib.bibtexAbstract}"><swrc:abstract><c:out value ='${bib.bibtexAbstract}'/></swrc:abstract></c:if>

<%-- included "misc" fields --%>
<c:forEach var="misc" items="${bib.miscMap}">
  <swrc:hasExtraField>
    <swrc:Field swrc:key="<c:out value='${misc.key}'/>" swrc:value="<c:out value='${misc.value}'/>"/>
  </swrc:hasExtraField>
</c:forEach>

<swrc:author>
  <rdf:Seq>
  <c:forEach var="person" varStatus="status" items="${bib.authorlist}">
    <rdf:_${status.index + 1}><swrc:Person swrc:name="<c:out value ='${person}'/>" /></rdf:_${status.index + 1}>
  </c:forEach>
  </rdf:Seq>
</swrc:author>

<swrc:editor>
  <rdf:Seq>
  <c:forEach var="person" varStatus="status" items="${bib.editorlist}">
    <rdf:_${status.index + 1}><swrc:Person swrc:name="<c:out value ='${person}'/>" /></rdf:_${status.index + 1}>
  </c:forEach>
  </rdf:Seq>
</swrc:editor>