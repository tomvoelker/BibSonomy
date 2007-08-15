<%@include file="include_jsp_head.jsp" %>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>
<jsp:useBean id="upBean" class="beans.UploadBean" scope="request"/>

<%-- HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="bibtex::${ResourceBean.title}" />
</jsp:include>


<%-------------------------- Heading -----------------------%>
<h1><a href="/">${projectName}</a> :: bibtex ::
  <form class="smallform" method="get" action="/search">
    <input type="text" name="q" value="<mtl:bibclean value='${ResourceBean.title}'/>" size="30"/>
  </form>
</h1> 


<%------------- Navigation --------------%>  
<%@include file="/boxes/navi.jsp" %> 

   
<div id="outer"><%-- neccessary so that tags are boxed to the right --%>
<c:set var="noedittags" value="true"/>

<%-- ############################################ loop over all entries ############################################ --%>
<div style="margin: 5px 1% 2% 1%;"><c:forEach var="bib" items="${ResourceBean.bibtex}">

<div class="kiste">
  <span class="nextprev">
    <a href="/user/<mtl:encode value='${bib.user}'/>"><c:out value="${bib.user}"/></a>'s BibTeX entry:
    <span style="font-size: 125%">
    <%@ include file="/boxes/bibtex_action.jsp" %>
    </span>
  </span>
  &nbsp;
</div>
  
<h2 style="font-size: 120%; margin-bottom: 0px;"><a href="/bibtex/<%=Bibtex.INTER_HASH %>${bib.simhash}"><mtl:bibclean value="${bib.title}"/></a></h2>

<div class="bibextra" style="margin-bottom: 1em;">
<c:choose>
<c:when test='${!empty bib.journal}'><em><c:out value="${bib.journal}"/>, </em></c:when>
<c:when test="${!empty bib.booktitle}"><em><c:out value="${bib.booktitle}"/>, </em></c:when>
</c:choose>
<c:if test='${!empty bib.volume}'><c:out value="${bib.volume}"/></c:if>
<c:if test='${!empty bib.number}'>(<c:out value="${bib.number}"/>)</c:if>
<c:if test='${!empty bib.pages}'>: <c:out value="${bib.pages}"/>, </c:if>
<c:if test='${!empty bib.year}'><c:out value="${bib.year}"/>.</c:if>
</div>

<table>

<c:if test='${!empty bib.author}'><tr>
<td class="expl2">Authors:</td>
<td>
    <c:forEach var="author" items="${bib.authornamesseparated}" varStatus="loopStatus">          
       <c:out value="${author[0]} "/>
       <a href="/author/<mtl:encode value='${author[1]}'/>"><c:out value="${author[1]}" /></a>                  
       <c:if test="${not loopStatus.last}"> and </c:if>
    </c:forEach> 
</td>
</tr></c:if>
	
<c:if test='${!empty bib.editor}'><tr>
<td class="expl2">Editors:</td>
<td>
    <c:forEach var="editor" items="${bib.editornamesseparated}" varStatus="loopStatus">          
       <c:out value="${editor[0]} " />
       <a href="/author/<mtl:encode value='${editor[1]}'/>"><c:out value="${editor[1]}" /></a>                  
       <c:if test="${not loopStatus.last}"> and </c:if>          
    </c:forEach> 
</td>
</tr></c:if>
	
<c:if test='${!empty bib.url}'><tr>
<td class="expl2">URL:</td> 
<td><a href="<c:out value='${bib.cleanurl}'/>"/><c:out value="${bib.cleanurl}"/></a></td>
</tr></c:if>


<%-- ##################### storage of multiple URLs per entry ############################## --%>  
<jsp:useBean id="urlBean" class="beans.BibtexURLBean" scope="request">
  <jsp:setProperty name="urlBean" property="user" value="${bib.user}"/>
  <jsp:setProperty name="urlBean" property="currUser" value="${user.name}"/>
  <jsp:setProperty name="urlBean" property="hash" value="${bib.hash}"/>
  <jsp:setProperty name="urlBean" property="validCkey" value="${validckey}"/>
  <jsp:setProperty name="urlBean" property="*"/>
</jsp:useBean>      

<%urlBean.getActionResult();%>

<c:if test="${not urlBean.leer or user.name eq bib.user}">
<tr>
<td class="expl2">extra&nbsp;URLs:</td>
<td>
  <c:forEach items="${urlBean.bibtexURL}" var="url">
  <a href="<c:out value='${url.url}'/>"><c:out value="${url.text}"/></a>
    <c:if test="${user.name eq bib.user}">
      <span class="bmaction"><a href="/<c:out value='${requPath}'/>?action=deleteURL&amp;url=<mtl:encode value='${url.url}'/>&amp;ckey=${ckey}">delete</a></span><br>
    </c:if>
  </c:forEach>

<c:if test="${user.name eq bib.user}">
  <form method="POST" action="/<c:out value='${requPath}'/>" id="f_addURL" style="margin-top: .5em; margin-bottom:0pt;">
  url: <input type="text" name="url"/> text: <input type="text" name="text"/>
       <input type="hidden" name="ckey" value="${ckey}"/>
       <input type="submit" value="addURL" name="action"/>
  </form>
  <span class="bmaction"><a href="" onclick="showUrlForm(); return false;" id="l_addURL" style="display:none">addURL</a></span>
<script type="text/javascript">
  var f = document.getElementById("f_addURL");
  var l = document.getElementById("l_addURL");
  var f_s = f.getAttribute("style");
  var l_s = l.getAttribute("style");
  f.setAttribute("style", l_s);
  l.setAttribute("style", f_s);
  
  function showUrlForm () {
    f.setAttribute("style", f_s);
    l.setAttribute("style", l_s);
  }
</script>
</c:if>
</td>
</tr>
</c:if>

<c:if test='${!empty bib.description}'><tr>
<td class="expl2">Description:</td>
<td><c:out value="${bib.description}"/></td>
</tr></c:if>

<%-- ################################# Tags ######################################## --%>       
<tr>
<td class="expl2">Tags:</td>
<td>
  <c:forEach var="tag" items="${bib.tags}">             
  <a href="/user/<mtl:encode value='${bib.user}'/>/<mtl:encode value='${tag}'/>"><c:out value="${tag}"/></a>
  </c:forEach>
  <c:if test="${user.name eq bib.user}"><br/>
  <form action="/TagHandler" method="post" style="margin-top: .5em; margin-bottom: 0pt;">
  <input type="text" name="${bib.hash}" size="40" value='<c:out value="${bib.fullTagString}"/>'>
  <input type="hidden" name="0${bib.hash}" value="<c:out value='${bib.fullTagString}'/>"/>
  <input type="submit" value="update tags">
  <input type="hidden" name="referer" value="<c:out value='${header.referer}'/>"/>
  <input type="hidden" name="requTask" value="bibtex"/>
  <input type="hidden" name="ckey" value="${ckey}"/>
  </form>
  </c:if>
</td>
</tr>
		
<c:if test='${!empty bib.bibtexAbstract}'><tr>
<td class="expl2">Abstract:</td>
<td><c:out value="${bib.bibtexAbstract}"/></td></tr>
</c:if>

</table>
     
<div class="kiste"><span class="nextprev"><span style="font-size: 125%"><%@ include file="/boxes/bibtex_action.jsp" %></span></span>&nbsp;</div>



<%-- ############################### BibTeX Output ########################## ---%>

<div class="boxed">
  <span class="bibentry">@<c:out value="${bib.entrytype}"/>{<c:out value="${bib.bibtexKey}"/>,</span>
  <div class="bibentry">
    title = {<c:out value="${bib.title}"/>},<br>
    <%-- standard fields --%>
    <c:forEach var="entry" items="${bib.entries}">
      <c:if test="${!empty entry.value}"><c:out value="${entry.key }"/> = {<c:out value="${entry.value}"/>},<br></c:if>
    </c:forEach>
    <%-- extra fields --%>
    <c:if test='${!empty bib.description}'>description = {<c:out value='${bib.description}'/>},<br></c:if>
    <c:if test='${!empty bib.bibtexAbstract}'>abstract = {<c:out value='${bib.bibtexAbstract}'/>},<br></c:if>
    <c:if test='${!empty bib.misc}'><c:out value='${bib.misc}'/>,<br></c:if>
    keywords = {<c:out value='${bib.tagString}'/>}
  </div>
  <span class="bibentry">}</span>
</div>

<c:if test="${user.name eq bib.user}">
<%-- ############################### private PDF ########################## ---%>

<div class="boxed">
  <h2>PDF / PS</h2>
  Your private copy of the document:
  <c:choose>
    <c:when test='${empty bib.docHash}'> 
      <form enctype="multipart/form-data" method="post" action="/DocumentUploadHandler" style="display:inline;">
      <input type="hidden" name="hash" value="${bib.hash}" />
      <input name="file" type="file"  size="20"/>
      <input type="submit" value="upload" />    
      <div class="errmsg"><%=upBean.getErrorMsg("file")%></div>
      </form>
    </c:when>  
    <c:otherwise>      
      <a href="/documents/${bib.docHash}"><c:out value="${bib.docName}"/></a>
      &nbsp;(<a href="/documents/${bib.docHash}?action=delete">delete</a>)
    </c:otherwise>
  </c:choose>
</div>


<%-- ############################### private Note ########################## ---%>

<jsp:useBean id="privnoteBean" class="beans.PrivnoteBean" scope="request">
  <jsp:setProperty name="privnoteBean" property="username"    value="${user.name}"/>
  <jsp:setProperty name="privnoteBean" property="hash"        value="${bib.hash}"/>
  <jsp:setProperty name="privnoteBean" property="oldprivnote" value="${bib.privnote}"/>
  <jsp:setProperty name="privnoteBean" property="*"/>
</jsp:useBean>
<% privnoteBean.queryDB(); %>

<div class="boxed">
  <h2>Private Note</h2>
  <form method="post" action="/<c:out value='${requPath}'/>" id="note">
  <textarea name="privnote" cols="60" rows="10" id="privnote"><c:out value="${privnoteBean.privnote}"/></textarea>
  <input type="submit" value="update" id="makeP"/>    
  </form>
</div>
 
</c:if>

<c:if test="${user.name ne bib.user and not empty bib.docHash and publicDocuments}">
<%-- ############################### public PDF ########################## ---%>

<div class="boxed">
  <h2>PDF / PS</h2>
  Local copy of the document:
  <a href="/documents/${bib.docHash}"><c:out value="${bib.docName}"/></a>
  &nbsp;(<a href="/documents/${bib.docHash}?action=delete">delete</a>)
</div>
</c:if>
    
</c:forEach></div>
</div>

<ul id="sidebar">
  <%@include file="/boxes/tags/usersrelations.jsp" %>
  
  <c:set var="markSuperTags" value="true"/>
  <%@include file="/boxes/tags/userstags.jsp"%>
</ul>

<%@ include file="/footer.jsp" %>