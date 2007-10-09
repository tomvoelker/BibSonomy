<div id="footer"> 
  
  ${projectName} is offered by the <a href="http://www.kde.cs.uni-kassel.de/index_en.html">Knowledge and Data Engineering Group</a> 
  of the University of Kassel, Germany.
  
  
  Contact: <%@include file="/boxes/emailaddress.jsp" %> 
  
  <script type="text/javascript">init(${user.tagboxStyle}, ${user.tagboxSort}, ${user.tagboxMinfreq}, "<c:out value='${param.requUser}'/>", "<c:out value='${user.name}'/>", "${ckey}", "<c:out value='${projectName}'/>");</script>
  
</div> 
</body>
</html>