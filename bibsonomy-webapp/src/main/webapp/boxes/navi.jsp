<div id="welcomeTop">A blue social bookmark and publication sharing system.</div>

<table id="tnav">
  <tr>
    <td class="tleft" id="upper_menu">
      
      <%-- ######### tags * groups * relations * popular ######### --%>
	  <a href="/tags">tags</a> &middot;
	  <a href="/relations">relations</a> &middot;
      <a href="/groups" rel="grp_menu">groups</a> &middot;
	  <a href="/popular">popular</a>

	  <%-- dropdown menu for groups --%>		
  	  <div id="grp_menu" class="dropmenudiv">
	    <c:forEach var="grp" items="${user.groups}">
		  <a href="/group/<mtl:encode value='${grp}'/>"><c:out value='${grp}'/></a>
		</c:forEach>  	
	  </div>			
			
    </td>
		
	<td class="tright">

      <%-- ######### logged in as USER * help * blog * about ######### --%>
      <c:if test="${not empty user.name}">
        logged in as 
        <a href="/user/<c:out value="${user.name}" />"><c:out value="${user.name}" /></a>
        &middot;
      </c:if>
    
      <a href="/help" rel="Help">help</a> &middot;
      <a href="http://bibsonomy.blogspot.com/">blog</a> &middot;
      <a href="/help/about/">about</a>
    </td>
  </tr>

  <tr>
    <td class="tleft" id="lower_menu">
    
      <%-- ######### myBibSonomy * post bookmark * post bibtex ######### --%>
    
      <c:choose>
	    <c:when test="${not empty user.name}">
          <a href="/user/<mtl:encode value='${user.name}'/>" rel="my_menu">my${projectName}</a> &middot;
  		  <a href="/post_bookmark">post bookmark</a> &middot;
       	  <a href="/post_bibtex">post bibtex</a>
	    </c:when>
	    <c:otherwise>
	      <%@include file="/boxes/login.jsp"%>      
	    </c:otherwise>
	  </c:choose>
	  
      <%-- dropdown menu for myBibSonomy --%>  
	  <div id="my_menu" class="dropmenudiv">
	    <a href="/friends">myFriends</a>
		<a href="/relations/<mtl:encode value='${user.name}'/>">myRelations</a>
		<a href="/advanced_search">mySearch</a>
        <a href="/user/<mtl:encode value='${user.name}'/>?filter=myPDF">myPDF</a>
        <a href="/user/<mtl:encode value='${user.name}'/>?filter=myDuplicates">myDuplicates</a>
        <a href="/bib/user/<mtl:encode value='${user.name}'/>?items=1000">myBibTeX</a>
	  </div>
	    
	    
    </td>
    <td class="tright">
      <%-- ######### basket * edit tags * settings * logout ######### --%>
    
      <c:choose>
        <c:when test="${!empty user.name}">
          <span id="pickctr">${user.postsInBasket}</span> picked in <a href="/basket">basket</a> &middot;
          <a href="/edit_tags">edit tags</a> &middot;
          <a href="/settings">settings</a> &middot;
          <a href="/logout">logout</a>
        </c:when>
        <c:otherwise>
          <a href="/login">login</a> &middot;
          <a href="/register">register</a>
        </c:otherwise>
      </c:choose>
    </td>
  </tr>

</table>

<script type="text/javascript">
  cssdropdown.startchrome("upper_menu");
  cssdropdown.startchrome("lower_menu")
</script>