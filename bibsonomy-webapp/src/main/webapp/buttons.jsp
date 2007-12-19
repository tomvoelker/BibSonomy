<%@include file="include_jsp_head.jsp" %>

<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="buttons" />
</jsp:include>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/buttons">buttons&nbsp;<img src="/resources/image/box_arrow.png"></a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<div id="bookbox">

<h2>Bookmarklet buttons for ${projectName}</h2>


  <c:if test="${not empty user.name}">
    <p>By dragging the <%@include file="boxes/button_mybibsonomy.jsp" %>
      button to your links toolbar you can access ${projectName} by 
      just one click in your browser.
    </p>
   
    <p>If you have read enough of the help pages, you may jump directly to your 
      <a href="/user/<mtl:encode value='${user.name}'/>">personal home page</a>, which you can also reach 
      from every page with the <a href="/user/<mtl:encode value='${user.name}'/>">my${projectName}</a> 
      link in the upper left corner. 
    </p>
  </c:if>
   
  
  <h3>Shortcut for posting bookmarks</h3>
    <p>
     <c:choose>
       <c:when test="${not empty user.name}">
         <a name="button_postbookmark"></a>Drag the <%@include file="boxes/button_postbookmark.jsp" %> 
         button to the links toolbar of your browser once. If you are a friend of popup windows,
         you can also use the <%@include file="boxes/button_postbookmark_popup.jsp" %> button. (If you use Internet Explorer you 
         have to right-click on the button and select "add to favorites".)
       </c:when>
       <c:otherwise>
         If you were <a href="/login">logged in</a>, you could find here a button which you can drag to the links 
         toolbar of your browser.
       </c:otherwise>
     </c:choose>
       Whenever you are on an interesting webpage, you just click on this button. 
       You are asked to type in some tags, and the bookmark is automatically added to your library.     
       Then you can continue your navigation.
     </p>

  <h3>Shortcut for posting BibTeX entries</h3>
    <p>
     <c:if test="${not empty user.name}">
     <a name="button_postbibtex"></a>Drag the  <%@include file="boxes/button_postbibtex.jsp" %>
      button to the links toolbar of your browser once. If you are a friend of popup windows,
      you can also use the <%@include file="boxes/button_postbibtex_popup.jsp" %> button. (If you use Internet Explorer you 
      have to right-click on the button and select "add to favorites".)
      Then you just need to <em>select</em> (highlight) publication entries you find on web 
      pages and after clicking on that button the entries will be stored within ${projectName}. 
    </c:if>
    </p>

</div>


<%@ include file="footer.jsp" %>