<%@include file="include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="batch edit" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a href="#" rel="path_menu"><img src="/resources/image/box_arrow.png">&nbsp;batch edit</a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<div id="general">
  <c:set var="disableActions" value="true"/>

  <h2>Below you can edit your posts</h2>

  <%@include file="/boxes/batchedit/bookmark.jsp" %>
  <%@include file="/boxes/batchedit/bibtex.jsp" %>

  <%@include file="/boxes/itemcount.jsp" %>
</div>


<script type="text/javascript">
var deleteAllChecked = false;

function deleteAll() {
  if (!deleteAllChecked) {
    if (!confirm("Do you really want to mark all posts to be deleted?")) {
      document.getElementById("deleteAllId").checked = false;
      return;
    }
  }

  deleteAllChecked = !deleteAllChecked;

  var inp = document.getElementById("batchedit").getElementsByTagName("input");
  for (var i = 0; i<inp.length; i++) {
    if (inp[i].type.toLowerCase() == "checkbox") {
      inp[i].checked = deleteAllChecked;
    }
  }
}
</script>

<script type="text/javascript">
maximizeById("general");
</script>


<%@ include file="footer.jsp" %>