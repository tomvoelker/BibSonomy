<%@include file="include_jsp_head.jsp"%>

<jsp:useBean id="upBean" class="beans.UploadBean" scope="request" />
<jsp:useBean id="bibtexHandlerBean" class="beans.BibtexHandlerBean"
  scope="page" />

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="post bibtex" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<!-- 2008/12/18, fei: removed attribute 'rel="Start"' from link as it brakes chrome menus -->
<h1 id="path"><a href="/">${projectName}</a> :: <a rel="path_menu" href="/post_bibtex"><img src="/resources/image/box_arrow.png">&nbsp;post bibtex</a></h1>

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>

<%-- 2008/12/18, fei: passing parameter 'selectedNaviTab' due to bug 646 --%>
<c:set var="selectedNaviTab" value="nav_postPub"/>
<%@include file="/boxes/navi.jsp"%>

<div id="general">

  <c:if test="${not empty user.name}">
    <p>Posting could be so easy: just <strong>drag and drop</strong> our <a href="/buttons">buttons</a> to your bookmark toolbar.
    (IE users <a href="/help/basic/buttons#setup">look here</a>)
    </p>
  </c:if>

<hr style="margin: 2em 0px 2em 0px;" />

<%-- ---------------- MANUAL BIBTEX INPUT ---------------- --%>
<h2>Enter your publication data here:</h2>
<form method="POST" action="/postPublication" name="post_bibtex">
<table>
  <tr>
    <td class="expl">type*</td>
    <td><select name="post.resource.entrytype" class="reqinput">
      <c:forEach var="et" items="${bibtexHandlerBean.entrytypes}">
        <option value="${et}">${et}</option>
      </c:forEach>
    </select></td>
    <td rowspan="6" class="expl_s">
    Please enter title, year and author/editor on this form to check, if
    you've already bookmarked this entry. On the next page you can enter
    the missing publication metadata fields.</td>
  </tr>
  <tr>
    <td class="expl">title*</td>
    <td><input type="text" name="post.resource.title" size="60" class="reqinput"></td>
    <td></td>
  </tr>
  <tr>
    <td class="expl">authors*</td>
    <td><input type="text" name="post.resource.author" size="60" class="reqinput" onkeyup="toggle_required_author_editor()"></td>
    <td></td>
  </tr>
  <tr>
    <td class="expl">editors*</td>
    <td><input type="text" name="post.resource.editor" size="60" class="reqinput" onkeyup="toggle_required_author_editor()"></td>
    <td></td>
  </tr>
  <tr>
    <td class="expl">year*</td>
    <td><input type="text" name="post.resource.year" size="10" class="reqinput"></td>
    <td></td>
  </tr>
  <tr>
    <td></td>
    <td><input type="submit" name="submit" value="check"></td>
    <td></td>
  </tr>
</table>
</form>

<hr style="margin: 3em 0px 3em 0px;" />


<%-- ---------------- BIBTEX SNIPPET INPUT ---------------- --%>
<span style="float:right; padding-top:4px;" id="generatePublicationBlock"></span>
<h2 id="generatePublicationHeader">Insert your publication snippet here:</h2>

<form method="POST" action="/BibtexHandler">
<input type="hidden" name="requTask" value="upload" />
<table>
  <tr>
    <td class="expl">BibTeX snippet*</td>
    <td>
    	<textarea id="selection" name="selection" cols="60" rows="10"  class="reqinput" onkeyup="sz(this);"></textarea>
    </td>
    <td rowspan="3" class="expl_s">
    Just copy and paste any valid BibTeX snippet or publication reference into
    this box and enter a description. If your snippet contains only one
    BibTeX entry you can edit it on the next page.
    </td>
  </tr>
  <tr>
    <td class="expl">description</td>
    <td><input type="text" name="description" size="60"> </td>
    <td></td>
  </tr>
  <tr>
    <td class="expl">viewable for</td>
    <td><%@include file="/boxes/groupselection.jsp"%></td>
    <td></td>
  </tr>
  <tr>
    <td></td>
    <td><input type="submit" name="submit" value="post"></td>
    <td></td>
  </tr>
</table>
</form>


<hr style="margin: 3em 0px 3em 0px;" />


<%-- ---------------- BIBTEX UPLOAD INPUT ---------------- --%>
<h2>Upload your BibTeX or EndNote file here:</h2>

<form name="upload" method="post" enctype="multipart/form-data" action='<%=response.encodeURL("/BibtexHandler?requTask=upload")%>'>
<table>
  <tr>
    <td class="expl">your file*</td>
    <td><input type="file" name="file" value="<c:out value='${upBean.file}'/>" size="50"  class="reqinput">
    <div class="errmsg"><%=upBean.getErrorMsg("file")%></div>
    </td>
    <td rowspan="3" class="expl_s">
    Here you can upload your existing BibTeX file. Be
    sure to select the correct character encoding. If the file contains just
    a few entries you can tag them on the next page.
    </td>
  </tr>
  <tr>
    <td class="expl">description</td>
    <td><input type="text" name="description" value="${upBean.description}" size="60">
    <div class="errmsg"><%=upBean.getErrorMsg("description")%></div>
    </td>
    <td></td>
  </tr>
  
  <tr>
    <td></td>
    <td>
      <input type="submit" name="submit" value="upload">
    </td>
    <td></td>
  </tr>
  
  <tr>
    <td colspan="3" style="padding:0.5em; border-top: 1px dashed #aaa; border-left: 1px dashed #aaa; border-right: 1px dashed #aaa;">
      options
    </td>
  </tr>
  
  <tr>
    <td class="expl" style="border-left: 1px dashed #aaa;" >character encoding</td>
    <%-- TODO: probably we should only list (ideally automatically) character sets supported #
               by Java --%>
    <td><select name="encoding" id="lencoding">
      <option value="UTF-8">UTF-8</option>
      <option value="ISO-8859-1">ISO-8859-1</option>
      <option value="ISO-8859-2">ISO-8859-2</option>
      <option value="ISO-8859-3">ISO-8859-3</option>
      <option value="ISO-8859-4">ISO-8859-4</option>
      <option value="ISO-8859-5">ISO-8859-5</option>
      <option value="ISO-8859-6">ISO-8859-6</option>
      <option value="ISO-8859-7">ISO-8859-7</option>
      <option value="ISO-8859-8">ISO-8859-8</option>
      <option value="ISO-8859-9">ISO-8859-9</option>
      <option value="ISO-8859-10">ISO-8859-10</option>
      <option value="ISO-8859-13">ISO-8859-13</option>
      <option value="ISO-8859-14">ISO-8859-14</option>
      <option value="ISO-8859-15">ISO-8859-15</option>
      <option value="ISO-8859-16">ISO-8859-16</option>
      <option value="US-ASCII">US-ASCII</option>
      <option value="UTF-16">UTF-16</option>
      <option value="UTF-16BE">UTF-16BE</option>
      <option value="UTF-16LE">UTF-16LE</option>
    </select></td>
    <td class="expl_s" style="border-right: 1px dashed #aaa;" >Specify the character encoding of your file.</td>
  </tr>
  <tr>
    <td class="expl" style="border-left: 1px dashed #aaa;" >viewable for</td>
    <td><%@include file="/boxes/groupselection.jsp"%></td>
    <td style="border-right: 1px dashed #aaa;" ></td>
  </tr>
  <tr>
    <td class="expl" style="border-left: 1px dashed #aaa;">tag delimiter</td>
    <td>
      <select name="delimiter" onchange="updateSubstitute(this);">
        <option value=" ">&nbsp;&nbsp;(whitespace)</option>
        <option value=",">, (comma)</option>
        <option value=";">; (semicolon)</option>
      </select>
    </td>
    <td class="expl_s" style="border-right: 1px dashed #aaa;">
      If your file contains a field "tags" (or "keywords") you can specify here 
      how tags are separated. The default is whitespace (space or tab). 
    </td>
  </tr>
  <tr>
    <td class="expl"  style="border-bottom: 1px dashed #aaa; border-left: 1px dashed #aaa;">whitespace substitute</td>
    <td style="border-bottom: 1px dashed #aaa;" >
      <input type="text" value="_" name="whitespace" size="2" maxlength="1" disabled="disabled"/>
    </td>
    <td class="expl_s" style="border-bottom: 1px dashed #aaa; border-right: 1px dashed #aaa;">
      If your tags are not delimited by whitespace, enter here a symbol to substitute whitespace.
    </td>
  </tr>

</table>
</form>

<script type="text/javascript">
showGenerateBibtexBlock();
maximizeById("general");

function updateSubstitute(select) {
  var subs = document.upload.whitespace;
  if (select.options[select.options.selectedIndex].value == " ") {
    subs.disabled = true;;
  } else {
    subs.disabled = false;
  }
}

function showGenerateBibtexBlock() {
	var selectionValue = document.getElementById("selection").value;
	if(!selectionValue.startsWith("http://")) {
		var target = document.createElement("a");
		target.style.cursor = "pointer";
		target.onclick = toggleGenerateURLOnClick;
		target.appendChild(document.createTextNode("generate 'post Publication' URL"));
		document.getElementById("generatePublicationBlock").appendChild(target);
	}
}

function toggleGenerateURLOnClick() {
	document.getElementById('selection').value='http://'+window.location.hostname+'/BibtexHandler?requTask=upload&selection='+escape(document.getElementById('selection').value);removeGenerateBlock();
}

function removeGenerateBlock() {
	document.getElementById("generatePublicationBlock").style.visibility = "hidden";
	document.getElementById("generatePublicationHeader").firstChild.data="Your generated 'post Publication' URL:";
}

</script>

</div>

<%@ include file="footer.jsp"%>
