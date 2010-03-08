<%@include file="/include_jsp_head.jsp" %>

<c:if test="${empty user.name}">
  <jsp:forward page="login"/>
</c:if>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="${param.requUser}" />
</jsp:include>

<h1 id="path"><a href="/">${projectName}</a> :: <a rel="path_menu" href="/edit_tags"><img src="/resources/image/box_arrow.png">&nbsp;edit tags</a></h1>

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>

<div id="general">

<%----------- Info Messages --------------%>
<c:if test="${!empty updatedResourcesCount || !empty updatedRelationsCount}">
  <h2>update successful</h2>
  <c:if test="${!empty updatedResourcesCount}">
    <p>updated ${updatedResourcesCount} resource(s)</p>
    <c:remove var="updatedResourcesCount"/>
  </c:if>
  <c:if test="${!empty updatedRelationsCount}">
    <p>updated ${updatedRelationsCount} relation(s)</p>
    <c:remove var="updatedRelationsCount"/>
  </c:if>
</c:if>
  

  <h2>rename/replace all your tags</h2>
  <p>
    In all posts which contain all of the tags in the first box these tags will be substituted by the tags in the second box.
  </p>
  <form action="/TagEditHandler?do=replace" method="post">
    <table>      
      <tr>
        <td>tag(s) to replace:</td>
        <td><input id="inpf" name="delTags" onClick="setActiveInputField(this.id)" onFocus="setActiveInputField(this.id)" autocomplete="off" on size="30" value="" tabindex="1" title="these tags will be removed from every posts which contains ALL of these tags"/></td>
        <td rowspan="2" style="padding-left:1em; border: 1px solid black; font-size:smaller;">
          This example shows the substitution of the tags 
          <a href="${projectHome}tag/folksonomy" onFocus="switchField('inpf','inpfNew')">folksonomy</a> and
          <a href="${projectHome}tag/project" onFocus="switchField('insert_up','insert_lo')">project</a>
          by the tags 
          <a href="${projectHome}tag/social">social</a>,
          <a href="${projectHome}tag/system">system</a> and
          <a href="${projectHome}tag/bookmarking">bookmarking</a>. This would be done in all posts which contain the tags     
          <a href="${projectHome}tag/folksonomy">folksonomy</a> <em>and</em> <a href="${projectHome}tag/project">project</a>. 
          <hr>
          <%@include file="/boxes/edit_tags_example.jsp" %>
        </td>
      </tr>
      <tr>
        <td>new tag(s):</td>
        <td><input id="inpfNew" name="addTags" onClick="setActiveInputField(this.id)" onFocus="setActiveInputField(this.id)" autocomplete="off" size="30" value="" tabindex="2" title="these tags will be added to every post which contains ALL of the above tags"/></td>
      </tr>
      <tr>
        <td>also update relations: </td>
        <td colspan="2">
          <input type="checkbox" name="updaterelations" value="yes" tabindex="3"/>
          NOTE: <em>This works only, when exactly one tag is substituted by another.</em>
        </td>
      </tr>
      <tr>
        <td style="width: 150px">
          <input type="hidden" name="ckey" value="${ckey}"/>
          <input type="submit" tabindex="4"/><input type="reset" tabindex="5"/>
        </td>
        <td></td>
      </tr>
   </table>
  </form>
 
<hr>
<%-- tag relations--%>

  <h2>insert relations</h2>
  <form action="/RelationsHandler?do=insert" method="post">
     relations to insert: 
     <input id="insert_up" onClick="setActiveInputField(this.id)" onFocus="setActiveInputField(this.id)" autocomplete="off" name="upper" size="15" value="" tabindex="7" title="enter the supertag(s) here"/> &larr;
     <input id="insert_lo" onClick="setActiveInputField(this.id)" onFocus="setActiveInputField(this.id)" autocomplete="off" name="lower" size="30" value="" tabindex="6" title="enter the subtag(s) here"/>
     <input type="submit" value="Insert relation" tabindex="8"/> 
     <input type="reset" value="Reset" tabindex="9"/>
     <input type="hidden" name="ckey" value="${ckey}"/>
  </form>
  
      
  <h2>delete relations</h2>
  <form action="/RelationsHandler?do=delete" method="post">
    relations to delete:
    <input id="delete_up" name="upper" size="15" value="" tabindex="11" title="enter the supertag(s) here"/>&larr;
    <input id="delete_lo" name="lower" size="30" value="" tabindex="10" title="enter the subtag(s) here"/>
    <input type="submit" value="Delete Relation" tabindex="12"/> 
    <input type="reset" value="Reset" tabindex="13"/>
    <input type="hidden" name="ckey" value="${ckey}"/>
  </form>

  <hr>
  
<%-- suggestions --%>

	<tr>
      <td height="40">
	    <ul id="suggTags">
	      <li>suggested</li>
	    </ul>
	  </td>
	  <td height="40">
        <ul id="suggested" class="suggtag">
        </ul>
        <div class="errmsg"></div>
  	  </td>
	</tr>  

</div>


<%-- tag cloud, including: list of ALL relations of the user and the tag filter box --%>
<div id="sidebarroundcorner" >
<ul id="sidebar">

  <jsp:useBean id="RelationBean" class="beans.RelationBean" scope="request">
    <jsp:setProperty name="RelationBean" property="requUser" value="${user.name}"/>
  </jsp:useBean>

  <%-- get ALL relations of the user --%>
  <c:set var="relations" value="${RelationBean.userRelations}"/>
   
  <%-- don't show "hide" symbol left to the relations (doesn't make sense, since we want to see ALL relations) --%>
  <c:set var="usersOwnRelations" value="false" />
   
  <%-- don't show show/hide symbol in tag cloud --%>
  <c:set var="markSuperTags" value="false"/>
   
  <%-- list of relations --%>	
  <li><span class="sidebar_h">relations</span>
	
    <ul id="relations">
      <%@include file="/boxes/tags/relationlist.jsp" %>
  </li>
  
  <%@include file="/boxes/tags/userstags.jsp"%>

</ul> 
</div>
<script type="text/javascript">
   $("#sidebarroundcorner").corner("round bottom 15px").corner("round tl 15px");
</script>

<script type="text/javascript">add_toggle_relations(); add_toggle();</script>

<%@ include file="/boxes/copytag.jsp" %>

<%@ include file="/footer.jsp" %>