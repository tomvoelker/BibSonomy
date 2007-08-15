      <span class="bmaction">
        <c:if test="${user.name eq book.user}">
            <c:url var="url_edit" value="${projectHome}ShowBookmarkEntry">
		  	  <c:param name="url" value="${book.url}"/>
			  <c:param name="user" value="${book.user}"/>
 		  	  <c:param name="jump" value="no"/>			
  		    </c:url>
            <c:url var="url_delete" value="${projectHome}bookmarkHandler">
		  	  <c:param name="delete" value="${book.hash}"/>
			  <c:param name="user" value="${book.user}"/>
              <c:param name="ckey" value="${ckey}"/>
		    </c:url>
		    
 		    <span>
    		    <a onclick="editTags(this,'${ckey}');return false;"  tags='<c:out value="${book.fullTagString}"/>' hashsum="${book.hash}" user='<c:out value="${book.user}"/>' href="<c:out value='${url_edit}' escapeXml='true'/> ">edit</a>
		    </span>

		    <a href="<c:out value='${url_delete}' escapeXml='true'/>">delete</a>
        </c:if>
        <c:if test="${user.name ne book.user}">
            <a href="/ShowBookmarkEntry?c=b&amp;jump=no&amp;url=<mtl:encode value='${book.url}'/>&amp;description=<mtl:encode value='${book.title}'/>&amp;extended=<mtl:encode value='${book.extended}'/>&amp;copytag=<mtl:encode value='${book.tagString}'/>" title="copy this bookmark to your repository">copy</a>
        </c:if>
      </span>