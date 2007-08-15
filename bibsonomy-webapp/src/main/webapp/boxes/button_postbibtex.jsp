<script type="text/javascript">
  var myurl = "";
  if (window.getSelection) {
    myurl  = "javascript:location.href='${projectHome}BibtexHandler?requTask=upload&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;selection='+encodeURIComponent(window.getSelection())";
  } else if (document.getSelection) {
    myurl  = "javascript:location.href='${projectHome}BibtexHandler?requTask=upload&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;selection='+encodeURIComponent(document.getSelection())";
  } else if (document.selection) {
    myurl  = "javascript:location.href='${projectHome}BibtexHandler?requTask=upload&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;selection='+encodeURIComponent(document.selection.createRange().text)";
  }
  document.write("<a title=\"postPublication\"href=\""+myurl+"\" onclick=\"return false\" class=\"bookmarklet2\"><img src=\"/resources/image/button_postPublication.png\" alt=\"postPublication\"/></a>");
</script>
<noscript>
  <%-- Firefox & Opera document.getSelection() --%>
  (you need JavaScript enabled: Firefox/Opera: <a title="postPublication" href="javascript:location.href='${projectHome}BibtexHandler?requTask=upload&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;selection='+encodeURIComponent(document.getSelection())" onclick="return false" class="bookmarklet2"><img src="/resources/image/button_postPublication.png" alt="postPublication"/></a>,
  <%-- Internet Explorer document.selection.createRange().text --%>    
  InternetExplorer: <a title="postPublication" href="javascript:location.href='${projectHome}BibtexHandler?requTask=upload&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;selection='+encodeURIComponent(document.selection.createRange().text)" onclick="return false" class="bookmarklet2"><img src="/resources/image/button_postPublication.png" alt="postPublication"/></a>)
</noscript>
