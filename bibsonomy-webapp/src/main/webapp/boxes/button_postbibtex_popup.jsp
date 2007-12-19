<script type="text/javascript">
  var myurl = "";
  if (window.getSelection) {
    myurl  = "javascript: var post=window.open('${projectHome}BibtexHandler?requTask=upload&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;selection='+encodeURIComponent(window.getSelection()), 'BibSonomy', 'width=1000,height=600,scrollbars=1,resizable=1'); void(window.setTimeout('post.focus()',250));";
  } else if (document.getSelection) {
    myurl  = "javascript: var post=window.open('${projectHome}BibtexHandler?requTask=upload&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;selection='+encodeURIComponent(document.getSelection()), 'BibSonomy', 'width=1000,height=600,scrollbars=1,resizable=1'); void(window.setTimeout('post.focus()',250));";
  } else if (document.selection) {
    myurl  = "javascript: var post=window.open('${projectHome}BibtexHandler?requTask=upload&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;selection='+encodeURIComponent(document.selection.createRange().text), 'BibSonomy', 'width=1000,height=600,scrollbars=1,resizable=1'); void(window.setTimeout('post.focus()',250));";
  }
  document.write("<a title=\"postPublication popup\"href=\""+myurl+"\" onclick=\"return false\" class=\"bookmarklet2\"><img src=\"/resources/image/button_postPublication_popup.png\" alt=\"postPublication popup\"/></a>");
</script>
<noscript>
  <%-- Firefox & Opera document.getSelection() --%>
  (you need JavaScript enabled: Firefox/Opera: <a title="postPublication popup" href="javascript:location.href='${projectHome}BibtexHandler?requTask=upload&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;selection='+encodeURIComponent(document.getSelection())" onclick="return false" class="bookmarklet2"><img src="/resources/image/button_postPublication.png" alt="postPublication"/></a>,
  <%-- Internet Explorer document.selection.createRange().text --%>    
  InternetExplorer: <a title="postPublication popup" href="javascript:location.href='${projectHome}BibtexHandler?requTask=upload&amp;url='+encodeURIComponent(location.href)+'&amp;description='+encodeURIComponent(document.title)+'&amp;selection='+encodeURIComponent(document.selection.createRange().text)" onclick="return false" class="bookmarklet2"><img src="/resources/image/button_postPublication.png" alt="postPublication"/></a>)
</noscript>
