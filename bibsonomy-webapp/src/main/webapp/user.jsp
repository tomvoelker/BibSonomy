<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="user::${param.requUser}" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start" >${projectName}</a> :: <a rel="path_menu" href="#">user&nbsp;<img src="/resources/image/box_arrow.png"></a> :: 
<a href="/user/<mtl:encode value='${param.requUser}'/>"><c:out value='${param.requUser}'/></a> ::
<form action="/user/<mtl:encode value='${param.requUser}'/>" method="GET" class="smallform">
  <input type="text" size="20" name="tag" id="inpf"/>
</form>
</h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %>


<div id="outer">
  <c:choose>
    <%-------------------------- show help -----------------------%>
    <c:when test="${ResourceBean.bookmarkTotalCount eq 0 &&  ResourceBean.bibtexTotalCount eq 0 && user.name eq param.requUser}">
      <div id="bookbox">
        <h2>bookmarks</h2>
        <p style="padding: 4em;">- no personal bookmarks at the moment -</p>
        <p>To post a bookmark, type in a URL here
              <form style="margin: 3% 10px 3% 5px;" method="POST" action="/ShowBookmarkEntry">
                <input type="text" name="url" size=50 />
                <input type="submit" name="submit" value="post" />
              </form>
        </p>
        <p style="margin-top: 2em;">
           It's easier to use the <%@include file="/boxes/button_postbookmark.jsp" %> button in your toolbar, when you're on the
           respective page. <a href="/help/basic/buttons">More infos here</a>.
        </p>
        <p style="margin-top: 2em;">
           Importing your bookmarks from del.icio.us is easily possible on the <a href="/settings">settings page</a>.
        </p>

      </div>
      <div id="bibbox">
        <h2>publications</h2>
        <p style="padding: 4em;">- no personal publication posts at the moment -</p>
        <p>To add a publication <a href="/post_bibtex">go here</a>.</p>
      </div>  
    </c:when>
    <%-------------------------- show data -----------------------%>
    <c:otherwise>
      <%@include file="/boxes/bookmark.jsp"%> <%-------------------------- Bookmarks  -----------------------%>
      <%@include file="/boxes/bibtex.jsp"%>   <%-------------------------- BibTeX     -----------------------%>
      <%@include file="/boxes/itemcount.jsp" %>
    </c:otherwise>
  </c:choose>
</div>


<ul id="sidebar">
  <%@include file="/boxes/tags/usersrelations.jsp" %>
   
  <c:set var="markSuperTags" value="true"/>
  <%@include file="/boxes/tags/userstags.jsp"%>
</ul>


<%@ include file="/footer.jsp" %>