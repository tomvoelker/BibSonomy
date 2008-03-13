<%@include file="include_jsp_head.jsp" %>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="export" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a href="/BibTeXHashExample.jsp">BibTeXHashExample.jsp</a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<div id="general"> 

<h2>Calculate ${projectName} BibTeX hashes</h2>

<p>Here you can enter some facts about a publication and ${projectName} 
calculates the resulting hashes for you.</p>


<jsp:useBean id="bibtex" class="org.bibsonomy.model.BibTex" scope="request">
  <jsp:setProperty name="bibtex" property="title"     value="${param.title}"/>
  <jsp:setProperty name="bibtex" property="author"    value="${param.author}"/>
  <jsp:setProperty name="bibtex" property="editor"    value="${param.editor}"/>
  <jsp:setProperty name="bibtex" property="year"      value="${param.year}"/>
  <jsp:setProperty name="bibtex" property="entrytype" value="${param.entrytype}"/>
  <jsp:setProperty name="bibtex" property="journal"   value="${param.journal}"/>
  <jsp:setProperty name="bibtex" property="booktitle" value="${param.booktitle}"/>
  <jsp:setProperty name="bibtex" property="volume"    value="${param.volume}"/>
  <jsp:setProperty name="bibtex" property="number"    value="${param.number}"/>
</jsp:useBean>



<form>
<table>
  <tr><th colspan="2">used in interhash and intrahash</th></tr>
  <tr><td>title:  </td><td><input type="text" size="100" name="title" value="${f:escapeXml(param.title)}"/></td></tr>
  <tr><td>author: </td><td><input type="text" size="100" name="author" value="${f:escapeXml(param.author)}"/></td></tr>
  <tr><td>editor: </td><td><input type="text" size="100" name="editor" value="${f:escapeXml(param.editor)}"/></td></tr>
  <tr><td>year:   </td><td><input type="text" size="100" name="year" value="${f:escapeXml(param.year)}"/></td></tr>
  <tr><th colspan="2">used only in intrahash</th></tr>
  <tr><td>entrytype:  </td><td><input type="text" size="100" name="entrytype" value="${f:escapeXml(param.entrytype)}"/></td></tr>
  <tr><td>journal:    </td><td><input type="text" size="100" name="journal" value="${f:escapeXml(param.journal)}"/></td></tr>
  <tr><td>booktitle:  </td><td><input type="text" size="100" name="booktitle" value="${f:escapeXml(param.booktitle)}"/></td></tr>
  <tr><td>volume:     </td><td><input type="text" size="100" name="volume" value="${f:escapeXml(param.volume)}"/></td></tr>
  <tr><td>number:     </td><td><input type="text" size="100" name="number" value="${f:escapeXml(param.number)}"/></td></tr>
  
  <% bibtex.recalculateHashes(); %>
  
  <tr><th colspan="2">resulting hashes</th></tr>
  <tr><td>interhash:</td><td>${bibtex.interHash} &rarr; <a href="/bibtex/<%=Bibtex.INTER_HASH%>${bibtex.interHash}">/bibtex/<b><%=Bibtex.INTER_HASH%></b>${bibtex.interHash}</a></td></tr>
  <tr><td>intrahash:</td><td>${bibtex.intraHash} &rarr; <a href="/bibtex/<%=Bibtex.INTRA_HASH%>${bibtex.intraHash}">/bibtex/<b><%=Bibtex.INTRA_HASH%></b>${bibtex.intraHash}</a></td></tr>
</table>
<input type="submit"/>
</form>





</div>

<%@ include file="footer.jsp" %>