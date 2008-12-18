<%@include file="include_jsp_head.jsp"%>

<jsp:include page="html_header.jsp">
	<jsp:param name="title" value="buttons" />
</jsp:include>
<h1 id="path"><a href="/">${projectName}</a> :: <a
	rel="path_menu" href="/buttons"><img
	src="/resources/image/box_arrow.png">&nbsp;buttons</a></h1>

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp"%>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp"%>

<div id="bookbox">

<h2>Bookmarklet buttons for ${projectName}</h2>


<c:if test="${not empty user.name}">
	<p>By dragging the <%@include file="boxes/button_mybibsonomy.jsp"%>
	button to your links toolbar you can access ${projectName} by just one
	click in your browser.</p>

	<p>If you have read enough of the help pages, you may jump directly
	to your <a href="/user/<mtl:encode value='${user.name}'/>">personal
	home page</a>, which you can also reach from every page with the <a
		href="/user/<mtl:encode value='${user.name}'/>">my${projectName}</a>
	link in the upper left corner.</p>
</c:if>


<h3>Shortcut for posting bookmarks</h3>
<p><c:choose>
	<c:when test="${not empty user.name}">
		<a name="button_postbookmark"></a>Drag the <%@include
			file="boxes/button_postbookmark.jsp"%> 
         button to the links toolbar of your browser once. If you are a friend of popup windows,
         you can also use the <%@include
			file="boxes/button_postbookmark_popup.jsp"%> button. (If you use Internet Explorer you 
         have to right-click on the button and select "add to favorites".)
       </c:when>
	<c:otherwise>
         If you were <a href="/login">logged in</a>, you could find here a button which you can drag to the links 
         toolbar of your browser.
       </c:otherwise>
</c:choose> Whenever you are on an interesting webpage, you just click on this
button. You are asked to type in some tags, and the bookmark is
automatically added to your library. Then you can continue your
navigation.</p>

<h3>Shortcut for posting BibTeX entries</h3>
<p><c:if test="${not empty user.name}">
	<a name="button_postbibtex"></a>Drag the  <%@include
		file="boxes/button_postbibtex.jsp"%>
      button to the links toolbar of your browser once. If you are a friend of popup windows,
      you can also use the <%@include
		file="boxes/button_postbibtex_popup.jsp"%> button. (If you use Internet Explorer you 
      have to right-click on the button and select "add to favorites".)
      Then you just need to <em>select</em> (highlight) publication entries you find on web 
      pages and after clicking on that button the entries will be stored within ${projectName}. 
    </c:if></p>

<hr>

<h2>Javascript Code Snippets</h2>

<p>The code below is meant to be incorporated into existing websites
in order to ease posting bookmarks and publications to ${projectName}.
It will display a link which leads directly to the appropriate (bookmark
or publication) posting site in ${projectName}.</p>

<form name="select_all">
<table width="100%" border="0" cellspacing="0" cellpadding="5">
	<tr>
		<td>
		<div align="center"><strong>Javascript Code:</strong></div>
		</td>

		<td>
		<div align="center"><strong>will appear as: </strong></div>
		</td>
	</tr>

	<tr>
		<td width="51%"><textarea name="text_area1" rows="10" cols="40">
&lt;!-- post bookmark to ${projectName} link code --&gt;
&lt;script type=&quot;text/JavaScript&quot;&gt;
&lt;!--
var url=encodeURIComponent(document.location.href);
var title=encodeURIComponent(document.title);
document.write(&quot;&lt;a href=\&quot;${projectHome}ShowBookmarkEntry?c=b&amp;jump=yes&amp;url=&quot;+url+ &quot;&amp;description=&quot;+title +&quot;\&quot; title=\&quot;Bookmark this page to ${projectName}.\&quot;&gt;Bookmark to ${projectHome}!&lt;/a&gt;");
//--&gt;
&lt;/script&gt;
&lt;!-- end post bookmark to ${projectName} link code --&gt;
</textarea> <input type="button" value="Highlight Text"
			onClick="javascript:this.form.text_area1.focus();this.form.text_area1.select();">

		</td>
		<td width="49%">
		<div align="center"><!-- post bookmark to ${projectName} link code -->
		<script type="text/JavaScript">
<!--
var url=encodeURIComponent(document.location.href);
var title=encodeURIComponent(document.title);
document.write("<a href=\"${projectHome}ShowBookmarkEntry?c=b&jump=yes&url="+url+ "&description="+title +"\" title=\"Bookmark this page to ${projectName}.\">Bookmark to ${projectName}!</a>");
//-->
</script> <!-- end post bookmark to ${projectName} link code --></div>
		</td>

	</tr>

	<tr>
		<td>
		<div align="center"><strong>Javascript Code with Image:</strong></div>
		</td>
	
		<td>
		<div align="center"><strong>will appear as: </strong></div>
		</td>
	</tr>

	<tr>
		<td width="51%"><textarea name="text_area2" rows="10" cols="40">
&lt;!-- post bookmark to ${projectName} link code --&gt;
&lt;script type=&quot;text/JavaScript&quot;&gt;
&lt;!--
var url=encodeURIComponent(document.location.href);
var title=encodeURIComponent(document.title);
document.write(&quot;&lt;a href=\&quot;${projectHome}ShowBookmarkEntry?c=b&amp;jump=yes&amp;url=&quot;+url+ &quot;&amp;description=&quot;+title +&quot;\&quot; title=\&quot;Bookmark this page to ${projectName}!\&quot;&gt;&lt;img src=\&quot;${projectHome}resources/image/bookmarkIt.png\&quot;&gt;&lt;/a&gt;");
//--&gt;
&lt;/script&gt;
&lt;!-- end post bookmark to ${projectName} link code --&gt;
</textarea> <input
			type="button" value="Highlight Text"
			onClick="javascript:this.form.text_area2.focus();this.form.text_area2.select();">
		</td>
		<td width="49%">
		<div align="center"><!-- BibSonomy link code --> <script
			type="text/JavaScript">
	<!--
	var url=encodeURIComponent(document.location.href);
	var title=encodeURIComponent(document.title);
	document.write("<a href=\"${projectHome}ShowBookmarkEntry?c=b&jump=yes&url="+url+ "&description="+title +"\" title=\"Bookmark this page to ${projectName}.\"><img src=\"${projectHome}resources/image/bookmarkIt.png\"></a>");
	//-->
	</script> 
<!-- end post bookmark to ${projectName} link code -->

      </table>
      </form>

</div>


<%@ include file="footer.jsp" %>
