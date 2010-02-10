<%--
This JSP shows the global relations of the chosen concept (supertag(s))
 --%>

<jsp:useBean id="RelationBean" class="beans.RelationBean" scope="request">
  <jsp:setProperty name="RelationBean" property="requSuperTag" value="${param.requTag}"/>
</jsp:useBean>  
 
<li><span class="sidebar_h">
	<a href="/relations" title="get an overview of all relations">relations</a>   
</span>
	
<%-- show relations --%>
<ul id="relations">  
  <%-- very similiar code exists in allRelations.jsp --%>
    <c:set var="lastupper" value="" />
    <c:forEach var="relation" items="${RelationBean.chosenRelations}">
      <c:if test="${relation.upper ne lastupper}">
        <c:if test="${!empty lastupper}">
      	    <%-- not the first supertag --> close list of former supertag --%>
             </ul>
           </li>
  	    </c:if>
	    <%-- new supertag --%>
	    <c:set var="lastupper" value="${relation.upper}" />
        <li class="box_upperconcept">
          <c:if test="${usersOwnRelations}">
  	        <a onclick="hideConcept(event);" href="/ajax/pickUnpickConcept?action=hide&amp;tag=<mtl:encode value='${relation.upper}'/>&amp;ckey=${ckey}" title="hide relation">&darr; </a>
  	      </c:if>
		  <a href="/concept/tag/<mtl:encode value='${relation.upper}'/>" title="show all posts which have <c:out value='${relation.upper}'/> or one of its subtags attached"><c:out value="${relation.upper}"/></a>
		  &larr;
  	  	  <ul id="<c:out value='${relation.upper}'/>" class="box_lowerconcept_elements">
      </c:if>
          <%-- subtags --%>
          <li class="box_lowerconcept"> 
    	    <a href="/tag/<mtl:encode value='${relation.lower}'/>"><c:out value="${relation.lower}"/></a>
          </li>
    </c:forEach>
        </ul>
        <c:if test="${!empty lastupper}">
            <%-- not the first supertag --> close list of former supertag --%>
             </ul>
           </li>
        </c:if>  
</li>