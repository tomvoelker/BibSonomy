<script type="text/javascript">
  var myurl = "";
  if (window.getSelection) {
    myurl  = "javascript: var post=window.open('${projectHome}ShowBookmarkEntry?c=b&amp;jump=yes&amp;user=<mtl:encode value='${user.name}'/>&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;extended='+encodeURIComponent(window.getSelection()), 'BibSonomy', 'width=1000,height=600,scrollbars=1,resizable=1'); void(window.setTimeout('post.focus()',250));";
  } else if (document.getSelection) {
    myurl  = "javascript: var post=window.open('${projectHome}ShowBookmarkEntry?c=b&amp;jump=yes&amp;user=<mtl:encode value='${user.name}'/>&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;extended='+encodeURIComponent(document.getSelection()), 'BibSonomy', 'width=1000,height=600,scrollbars=1,resizable=1'); void(window.setTimeout('post.focus()',250));";
  } else if (document.selection) {
    myurl  = "javascript: var post=window.open('${projectHome}ShowBookmarkEntry?c=b&amp;jump=yes&amp;user=<mtl:encode value='${user.name}'/>&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;extended='+encodeURIComponent(document.selection.createRange().text), 'BibSonomy', 'width=1000,height=600,scrollbars=1,resizable=1'); void(window.setTimeout('post.focus()',250));";
  }
  document.write("<a title=\"postBookmark popup\"href=\""+myurl+"\" onclick=\"return false\" class=\"bookmarklet2\"><img src=\"/resources/image/button_postBookmark_popup.png\" alt=\"postBookmark popup\"/></a>");
</script>
<noscript>
  <%-- Firefox & Opera document.getSelection() --%>
  (you need JavaScript enabled: Firefox/Opera: 
  <a title="postBookmark popup" href="javascript:location.href='${projectHome}ShowBookmarkEntry?c=b&amp;jump=yes&amp;user=<mtl:encode value='${user.name}'/>&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;extended='+encodeURIComponent(document.getSelection())" onclick="return false" class="bookmarklet2"><img src="/resources/image/button_postBookmark.png" alt="postBookmark"/></a>
  <%-- Internet Explorer document.selection.createRange().text --%>    
  InternetExplorer: 
  <a title="postBookmark popup" href="javascript:location.href='${projectHome}ShowBookmarkEntry?c=b&amp;jump=yes&amp;user=<mtl:encode value='${user.name}'/>&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;extended='+encodeURIComponent(document.selection.createRange().text)" onclick="return false" class="bookmarklet2"><img src="/resources/image/button_postBookmark.png" alt="postBookmark"/></a>

</noscript>
