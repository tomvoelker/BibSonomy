<%@include file="include_jsp_head.jsp" %>

<jsp:useBean id="bookmarkHandlerBean" class="beans.BookmarkHandlerBean" scope="request"/>

<c:if test="${empty user.name}">
  <jsp:forward page="login"/>
</c:if>

<c:if test="${empty bookmarkHandlerBean.oldurl}">
  <jsp:forward page="/post_bookmark" />
</c:if>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="edit bookmark" />
</jsp:include>
 

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: edit bookmark</h1> 

<%@include file="/boxes/navi.jsp" %> 

<div id="outer">
<div id="general"> 

  <table class="expl_small">
  
  <c:choose>

    <c:when test="${not empty scraped}">
      <%-- -------------------------------- BibTeX edit form ------------------------- --%>
      <%-- if the bookmark is scrapable it will be shown here --%>
 
        <tr>
          <td colspan="3">
            <h2>Option 1: post as publication (recommended)</h2>
            The page you are trying to bookmark contains the following publication reference:
          </td>
        </tr>
	    <tr>
		  <td></td>
		  <td>
            <form method="POST" action="/BibtexHandler">
	  	      <textarea id="bib" name="selection" cols="60" rows="15" class="reqinput"><c:out value='${scraped}'/></textarea>
              <input type="hidden" name="requTask" value="upload" />
              <br/>edit and post as publication: <input id="button" type="submit" name="submit" value="post publication"/>
            </form>
		  </td>
		  <td></td>
	    </tr>

        <tr><td colspan="3"><hr/><h2>Option 2: post as bookmark</h2></td></tr>

    </c:when>
    
    <c:otherwise>
      <tr><td colspan="3"><h2>Feel free to edit your bookmark</h2></td></tr>
    </c:otherwise>
    
  </c:choose>
  
  <%-- -------------------------------- Bookmark edit form ------------------------- --%>
  <form method="POST" action="/bookmark_posting_process">
    <input type="hidden" name="oldurl" value="<c:out value='${bookmarkHandlerBean.oldurl}'/>">
    <input type="hidden" name="rating" value="<c:out value='${bookmarkHandlerBean.rating}'/>">
    <input type="hidden" name="jump" value="${bookmarkHandlerBean.jump}">
    <input type="hidden" value="${ckey}" name="ckey"/>
    
    <tr>
	  <td><label for="lurl">url*</label></td>
	  <td>
	    <input class="reqinput" tabindex="1" type="text" name="url" id="lurl" value="<c:out value='${bookmarkHandlerBean.url}' />" size="60" />
	    <div class="errmsg">${bookmarkHandlerBean.errors.url}</div>
	  </td>
	  <td></td>
    </tr>
	<tr>
  	  <td><label for="ldesc">title*</label></td>
	  <td>
	    <input class="reqinput" tabindex="2" type="text" name="description" id="ldesc" value="<c:out value='${bookmarkHandlerBean.description}' />" size="60" />
		<div class="errmsg">${bookmarkHandlerBean.errors.description}</div>
      </td>
      <td></td>
	</tr>
	<tr>
	  <td><label for="lext">description,<br>comment</label></td>
	  <td>
	    <textarea id="lext" tabindex="3" name="extended" cols="60" rows="3" onkeyup="sz(this);"><c:out value='${bookmarkHandlerBean.extended}' /></textarea>
		<div class="errmsg">${bookmarkHandlerBean.errors.extended}</div>
	  </td>
	  <td></td>
	</tr>
    <tr>
	  <td><label for="inpf">tags*</label></td>
  	  <td>
  	    <input class="reqinput" tabindex="4" type="text" name="tags" id="inpf" onClick="setActiveInputField(this.id); enableHandler();" onFocus="setActiveInputField(this.id); enableHandler()" onBlur="disableHandler()" value="<c:out value='${bookmarkHandlerBean.tagstring}' />" size="60" autocomplete="off" /> 

		<%-- tagging of tags --%>
		<c:if test="${!empty bookmarkHandlerBean.taggedTag}">
		  <select name="direction" 
		    title="Using this box you can tag the tag <c:out value='${bookmarkHandlerBean.taggedTag}'/> with other tags.">
            <option value="none" title="Don't add any relations.">none</option>
		    <option value="lower" title="For every tag add the relation tag&lt;-<c:out value='${bookmarkHandlerBean.taggedTag}'/> to my relations.">&lt;- <c:out value="${bookmarkHandlerBean.taggedTag}"/></option>
		    <option value="upper" title="For every tag add the relation tag-&gt;<c:out value='${bookmarkHandlerBean.taggedTag}'/> to my relations.">-&gt; <c:out value="${bookmarkHandlerBean.taggedTag}"/></option>
		  </select>
		</c:if>
		
       <%@include file="/boxes/comma_test.jsp" %> 
		
		<div class="errmsg">${bookmarkHandlerBean.errors.tags}</div>
  	  </td>
  	  <td>space separated</td>
	</tr>
	<c:if test="${not empty bookmarkHandlerBean.recommendedTags}" >
		<tr>
          <td height="40">
    	    <ul id="suggTags">
    	      <li>recommendation:</li>
    	    </ul>
    	  </td>
          <td height="40" id="recommender">
            <ul id="recommendtag">
                <c:forEach var="tag" items="${bookmarkHandlerBean.recommendedTags}">
                    <li class="recommended"><a onclick="toggle(event); return false;" title="<c:out value="${tag.score}"/> score"><c:out value="${tag.name}"/></a></li>
    			</c:forEach>
            </ul>
      	  </td>
    	</tr>		
	</c:if>
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
	
  	<tr>
 	  <td><label for="lgroup">viewable for</label></td>
	  <td>
        <%@include file="/boxes/groupselection.jsp" %>
		<input tabindex="5" type="submit" name="submit" value="save" onclick="clear_tags()"/>
	  </td>
	  <td></td>
	</tr>
  </table>
  </form>
</div><%-- general --%>

<%--  insert copy tags --%>
<c:if test="${bookmarkHandlerBean.copytag != null}">
  <div id="copytags"> 
    <h2>Tags of copied item: </h2>
    <ul id="copytag" >
      <c:forEach var="elem" items="${bookmarkHandlerBean.copytag}">
        <li ><c:out value='${elem}'/></li>
      </c:forEach>
    </ul>    
  </div>
</c:if>

</div><%-- outer --%>


<ul id="sidebar">
  <c:set var="markSuperTags" value="false"/>
  <%@include file="/boxes/tags/userstags.jsp"%>
</ul> 

<%@ include file="/boxes/copytag.jsp" %>

<%@ include file="footer.jsp" %>