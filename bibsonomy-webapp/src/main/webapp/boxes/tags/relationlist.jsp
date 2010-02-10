 
<%--

shows a list of relations:

SUPERTAG1 <-- subtag11 subtag12 subtag13
SUPERTAG2 <-- subtag21 subtag22 subtag23
SUPERTAG3 <-- subtag31 subtag32 subtag33

--%>
  <%-- duplicate of this code (very similiar) exists in allRelations.jsp --%>
    <c:set var="lastupper" value="" />
    <c:forEach var="relation" items="${relations}">
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
  	        <a onclick="hideConcept(event, '${ckey}');" href="/ajax/pickUnpickConcept?action=hide&amp;tag=<mtl:encode value='${relation.upper}'/>&amp;ckey=${ckey}" title="hide relation">&darr; </a>
  	      </c:if>
		  <a href="/concept/user/<mtl:encode value='${RelationBean.requUser}'/>/<mtl:encode value='${relation.upper}'/>" title="show all posts which have <c:out value='${relation.upper}'/> or one of its subtags attached"><c:out value="${relation.upper}"/></a>
		  &larr;
  	  	  <ul id="<c:out value='${relation.upper}'/>" class="box_lowerconcept_elements">
      </c:if>
          <%-- subtags --%>
          <li class="box_lowerconcept"> 
    	    <a href="/user/<mtl:encode value='${RelationBean.requUser}'/>/<mtl:encode value='${relation.lower}'/>"><c:out value="${relation.lower}"/></a>
          </li>
    </c:forEach>
        </ul>
        <c:if test="${!empty lastupper}">
            <%-- not the first supertag --> close list of former supertag --%>
             </ul>
           </li>
        </c:if>