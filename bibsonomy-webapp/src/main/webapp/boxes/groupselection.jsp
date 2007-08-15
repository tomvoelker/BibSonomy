  	    <%-- this is the preferred way of doing that (just using input taglib, see jakarta.apache.org)
  	    input:select name="group" bean="bookmarkHandlerBean" options="<%= optionsMap %>" / --%> 
	    <select name="group" id="lgroup">
   	      <c:forEach var="group" items="${user.allGroups}">
   	        <option 
              value="<c:out value='${group}'/>" 
              <c:if test="${bookmarkHandlerBean.group eq group || bibtexHandlerBean.group eq group}">
                selected="true"
              </c:if>
              ><c:out value='${group}'/></option>
          </c:forEach>
          
	    </select>