<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="bibtexkey::${ResourceBean.bibtex[0].bibtexKey}" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path">
  <a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="#">bibtexkey&nbsp;<img src="/resources/image/box_arrow.png"></a> :: <c:out value='${ResourceBean.bibtex[0].bibtexKey}'/>
</h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>

<div id="outer">
<%@include file="/boxes/bibtex.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
<%@include file="/boxes/itemcount.jsp" %>
</div>

<%@ include file="/footer.jsp" %>