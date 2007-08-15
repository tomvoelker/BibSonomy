<script type="text/javascript">
  var myurl = "";
  if (window.getSelection) {
    myurl  = "javascript:location.href='${projectHome}ShowBookmarkEntry?c=b&amp;jump=yes&amp;user=<mtl:encode value='${user.name}'/>&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;extended='+encodeURIComponent(window.getSelection())";
  } else if (document.getSelection) {
    myurl  = "javascript:location.href='${projectHome}ShowBookmarkEntry?c=b&amp;jump=yes&amp;user=<mtl:encode value='${user.name}'/>&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;extended='+encodeURIComponent(document.getSelection())";
  } else if (document.selection) {
    myurl  = "javascript:location.href='${projectHome}ShowBookmarkEntry?c=b&amp;jump=yes&amp;user=<mtl:encode value='${user.name}'/>&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;extended='+encodeURIComponent(document.selection.createRange().text)";
  }
  document.write("<a title=\"postBookmark\"href=\""+myurl+"\" onclick=\"return false\" class=\"bookmarklet2\"><img src=\"/resources/image/button_postBookmark.png\" alt=\"postBookmark\"/></a>");
</script>
<noscript>
  <%-- Firefox & Opera document.getSelection() --%>
  (you need JavaScript enabled: Firefox/Opera: 
  <a title="postBookmark" href="javascript:location.href='${projectHome}ShowBookmarkEntry?c=b&amp;jump=yes&amp;user=<mtl:encode value='${user.name}'/>&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;extended='+encodeURIComponent(document.getSelection())" onclick="return false" class="bookmarklet2"><img src="/resources/image/button_postBookmark.png" alt="postBookmark"/></a>
  <%-- Internet Explorer document.selection.createRange().text --%>    
  InternetExplorer: 
  <a title="postBookmark" href="javascript:location.href='${projectHome}ShowBookmarkEntry?c=b&amp;jump=yes&amp;user=<mtl:encode value='${user.name}'/>&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;extended='+encodeURIComponent(document.selection.createRange().text)" onclick="return false" class="bookmarklet2"><img src="/resources/image/button_postBookmark.png" alt="postBookmark"/></a>

</noscript>
