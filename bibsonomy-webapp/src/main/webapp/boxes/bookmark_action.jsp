<div class="bmfoot">

   <c:if test="${not empty user.name}">
      <span class="bmaction">
        
          <c:if test="${user.name eq resource.user}">
            
            <%-- EDIT link --%>
            <c:url var="url_edit" value="${projectHome}ShowBookmarkEntry">
		  	  <c:param name="url" value="${resource.url}"/>
			  <c:param name="user" value="${resource.user}"/>
 		  	  <c:param name="jump" value="no"/>			
  		    </c:url>
          
            <%-- DELETE link --%>
            <c:url var="url_delete" value="${projectHome}bookmarkHandler">
		  	  <c:param name="delete" value="${resource.hash}"/>
			  <c:param name="user" value="${resource.user}"/>
              <c:param name="ckey" value="${ckey}"/>
		    </c:url>
		    
 		    <span>
    		    <a onclick="editTags(this,'${ckey}');return false;"  tags='<c:out value="${resource.fullTagString}"/>' hashsum="${resource.hash}" user='<c:out value="${resource.user}"/>' href="<c:out value='${url_edit}' escapeXml='true'/> ">edit</a>
		    </span>
            |
		    <a href="<c:out value='${url_delete}' escapeXml='true'/>">delete</a>
          </c:if>
          
          <c:if test="${user.name ne resource.user}">
            <%-- COPY link --%>
            <a href="/ShowBookmarkEntry?c=b&amp;jump=no&amp;url=<mtl:encode value='${resource.url}'/>&amp;description=<mtl:encode value='${resource.title}'/>&amp;extended=<mtl:encode value='${resource.extended}'/>&amp;copytag=<mtl:encode value='${resource.tagString}'/>" title="copy this bookmark to your repository">copy</a>
          </c:if>       

      </span>
      &nbsp;
  </c:if>

  <%@include file="/boxes/resource_rating.jsp" %> 

</div>