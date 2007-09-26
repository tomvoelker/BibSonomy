<%@include file="include_jsp_head.jsp" %>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>
<jsp:useBean id="upBean" class="beans.UploadBean" scope="request"/>

<%-- HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="bibtex::${ResourceBean.title}" />
</jsp:include>


<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/">${projectName}</a> :: <a href="#" rel="path_menu">bibtex&nbsp;<img src="/resources/image/box_arrow.png"></a> ::
  <form class="smallform" method="get" action="/search">
    <input type="text" name="q" value="<mtl:bibclean value='${ResourceBean.title}'/>" size="30"/>
  </form>
</h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%------------- Navigation --------------%>  
<%@include file="/boxes/navi.jsp" %> 

   
<div id="outer"><%-- neccessary so that tags are boxed to the right --%>
<c:set var="noedittags" value="true"/>

<%-- ############################################ loop over all entries ############################################ --%>
<div style="margin: 5px 1% 2% 1%;"><c:forEach var="resource" items="${ResourceBean.bibtex}">

<div class="kiste">
  <span class="nextprev">
    <a href="/user/<mtl:encode value='${resource.user}'/>"><c:out value="${resource.user}"/></a>'s BibTeX entry:
  </span>
  &nbsp;
</div>
  
<h2 style="font-size: 120%; margin-bottom: 0px;"><a href="/bibtex/<%=Bibtex.INTER_HASH %>${resource.simhash}"><mtl:bibclean value="${resource.title}"/></a></h2>
<div class="bibextra" style="margin-bottom: 1em;">
<c:choose>
<c:when test='${!empty resource.journal}'><em><c:out value="${resource.journal}"/>, </em></c:when>
<c:when test="${!empty resource.booktitle}"><em><c:out value="${resource.booktitle}"/>, </em></c:when>
</c:choose>
<c:if test='${!empty resource.volume}'><c:out value="${resource.volume}"/></c:if>
<c:if test='${!empty resource.number}'>(<c:out value="${resource.number}"/>)</c:if>
<c:if test='${!empty resource.pages}'>: <c:out value="${resource.pages}"/>, </c:if>
<c:if test='${!empty resource.year}'><c:out value="${resource.year}"/>.</c:if>
</div>

<table>

<c:if test='${!empty resource.author}'><tr>
<td class="expl2">Authors:</td>
<td>
    <c:forEach var="author" items="${resource.authornamesseparated}" varStatus="loopStatus">          
       <c:out value="${author[0]} "/>
       <a href="/author/<mtl:encode value='${author[1]}'/>"><c:out value="${author[1]}" /></a>                  
       <c:if test="${not loopStatus.last}"> and </c:if>
    </c:forEach> 
</td>
</tr></c:if>
	
<c:if test='${!empty resource.editor}'><tr>
<td class="expl2">Editors:</td>
<td>
    <c:forEach var="editor" items="${resource.editornamesseparated}" varStatus="loopStatus">          
       <c:out value="${editor[0]} " />
       <a href="/author/<mtl:encode value='${editor[1]}'/>"><c:out value="${editor[1]}" /></a>                  
       <c:if test="${not loopStatus.last}"> and </c:if>          
    </c:forEach> 
</td>
</tr></c:if>
	
<c:if test='${!empty resource.url}'><tr>
<td class="expl2">URL:</td> 
<td><a href="<c:out value='${resource.cleanurl}'/>"/><c:out value="${resource.cleanurl}"/></a></td>
</tr></c:if>


<%-- ##################### storage of multiple URLs per entry ############################## --%>  
<jsp:useBean id="urlBean" class="beans.BibtexURLBean" scope="request">
  <jsp:setProperty name="urlBean" property="user" value="${resource.user}"/>
  <jsp:setProperty name="urlBean" property="currUser" value="${user.name}"/>
  <jsp:setProperty name="urlBean" property="hash" value="${resource.hash}"/>
  <jsp:setProperty name="urlBean" property="validCkey" value="${validckey}"/>
  <jsp:setProperty name="urlBean" property="*"/>
</jsp:useBean>      

<%urlBean.getActionResult();%>

<c:if test="${not urlBean.leer or user.name eq resource.user}">
<tr>
<td class="expl2">extra&nbsp;URLs:</td>
<td>
  <c:forEach items="${urlBean.bibtexURL}" var="url">
  <a href="<c:out value='${url.url}'/>"><c:out value="${url.text}"/></a>
    <c:if test="${user.name eq resource.user}">
      <a class="action" href="/<c:out value='${requPath}'/>?action=deleteURL&amp;url=<mtl:encode value='${url.url}'/>&amp;ckey=${ckey}">delete</a><br>
    </c:if>
  </c:forEach>

<c:if test="${user.name eq resource.user}">
  <form method="POST" action="/<c:out value='${requPath}'/>" id="f_addURL" style="margin-top: .5em; margin-bottom:0pt;">
  url: <input type="text" name="url"/> text: <input type="text" name="text"/>
       <input type="hidden" name="ckey" value="${ckey}"/>
       <input type="submit" value="addURL" name="action"/>
  </form>
  <a class="action" href="" onclick="showUrlForm(); return false;" id="l_addURL" style="display:none">addURL</a>
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

<c:if test='${!empty resource.description}'><tr>
<td class="expl2">Description:</td>
<td><c:out value="${resource.description}"/></td>
</tr></c:if>

<%-- ################################# Tags ######################################## --%>       
<tr>
<td class="expl2">Tags:</td>
<td>
  <c:forEach var="tag" items="${resource.tags}">             
  <a href="/user/<mtl:encode value='${resource.user}'/>/<mtl:encode value='${tag}'/>"><c:out value="${tag}"/></a>
  </c:forEach>
  <c:if test="${user.name eq resource.user}"><br/>
  <form action="/TagHandler" method="post" style="margin-top: .5em; margin-bottom: 0pt;">
  <input type="text" name="${resource.hash}" size="40" value='<c:out value="${resource.fullTagString}"/>'>
  <input type="hidden" name="0${resource.hash}" value="<c:out value='${resource.fullTagString}'/>"/>
  <input type="submit" value="update tags">
  <input type="hidden" name="referer" value="<c:out value='${header.referer}'/>"/>
  <input type="hidden" name="requTask" value="bibtex"/>
  <input type="hidden" name="ckey" value="${ckey}"/>
  </form>
  </c:if>
</td>
</tr>
		
<c:if test='${!empty resource.bibtexAbstract}'><tr>
<td class="expl2">Abstract:</td>
<td><c:out value="${resource.bibtexAbstract}"/></td></tr>
</c:if>

</table>
     
<%@ include file="/boxes/bibtex_action.jsp" %>



<%-- ############################### BibTeX Output ########################## ---%>

<div class="boxed">
  <span class="bibentry">@<c:out value="${resource.entrytype}"/>{<c:out value="${resource.bibtexKey}"/>,</span>
  <div class="bibentry">
    title = {<c:out value="${resource.title}"/>},<br>
    <%-- standard fields --%>
    <c:forEach var="entry" items="${resource.entries}">
      <c:if test="${!empty entry.value}"><c:out value="${entry.key }"/> = {<c:out value="${entry.value}"/>},<br></c:if>
    </c:forEach>
    <%-- extra fields --%>
    <c:if test='${!empty resource.description}'>description = {<c:out value='${resource.description}'/>},<br></c:if>
    <c:if test='${!empty resource.bibtexAbstract}'>abstract = {<c:out value='${resource.bibtexAbstract}'/>},<br></c:if>
    <c:if test='${!empty resource.misc}'><c:out value='${resource.misc}'/>,<br></c:if>
    keywords = {<c:out value='${resource.tagString}'/>}
  </div>
  <span class="bibentry">}</span>
</div>

<c:if test="${user.name eq resource.user}">
<%-- ############################### private PDF ########################## ---%>

<div class="boxed">
  <h2>PDF / PS</h2>
  Your private copy of the document:
  <c:choose>
    <c:when test='${empty resource.docHash}'> 
      <form enctype="multipart/form-data" method="post" action="/DocumentUploadHandler" style="display:inline;">
      <input type="hidden" name="hash" value="${resource.hash}" />
      <input name="file" type="file"  size="20"/>
      <input type="submit" value="upload" />    
      <div class="errmsg"><%=upBean.getErrorMsg("file")%></div>
      </form>
    </c:when>  
    <c:otherwise>      
      <a href="/documents/${resource.docHash}"><c:out value="${resource.docName}"/></a>
      &nbsp;(<a href="/documents/${resource.docHash}?action=delete">delete</a>)
    </c:otherwise>
  </c:choose>
</div>


<%-- ############################### private Note ########################## ---%>

<jsp:useBean id="privnoteBean" class="beans.PrivnoteBean" scope="request">
  <jsp:setProperty name="privnoteBean" property="username"    value="${user.name}"/>
  <jsp:setProperty name="privnoteBean" property="hash"        value="${resource.hash}"/>
  <jsp:setProperty name="privnoteBean" property="oldprivnote" value="${resource.privnote}"/>
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

<c:if test="${user.name ne resource.user and not empty resource.docHash and publicDocuments}">
<%-- ############################### public PDF ########################## ---%>

<div class="boxed">
  <h2>PDF / PS</h2>
  Local copy of the document:
  <a href="/documents/${resource.docHash}"><c:out value="${resource.docName}"/></a>
  &nbsp;(<a href="/documents/${resource.docHash}?action=delete">delete</a>)
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