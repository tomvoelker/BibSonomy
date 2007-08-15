<%@include file="include_jsp_head.jsp" %>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="relations" />
</jsp:include>

<jsp:useBean id="RelationBean" class="beans.RelationBean" scope="request"/>

<h1><a href="/" rel="Start">${projectName}</a> :: <a href="/relations">relations</a></h1>

<%@include file="/boxes/navi.jsp" %> 

<div id="outer">
<div id="general">
<%--

shows the list of relations:

SUPERTAG1 <-- subtag11 subtag12 subtag13
SUPERTAG2 <-- subtag21 subtag22 subtag23
SUPERTAG3 <-- subtag31 subtag32 subtag33

--%>
 
  <%-- duplicated and only slightly changed code from /boxes/relationlist.jsp --%>
  <ul style="font-size:140%;">
    <c:set var="lastupper" value="" />
    <c:forEach var="relation" items="${RelationBean.allRelations}">
      <c:if test="${relation.upper ne lastupper}">
        <c:if test="${!empty lastupper}">
  	    <%-- not the first supertag --> close list of former supertag --%>
          </ul>
        </li>
  	    </c:if>
	    <%-- new supertag --%>
	    <c:set var="lastupper" value="${relation.upper}" />
        <li class="box_upperconcept">
		  <a style="font-size:${(relation.count * 10) + 80}%" title="${relation.count} users"  href="/tag/<mtl:encode value='${relation.upper}'/>"><c:out value="${relation.upper}"/></a>
		  &larr;
  	  	  <ul id="<c:out value='${relation.upper}'/>" class="box_lowerconcept_elements">
      </c:if>
          <%-- subtags --%>
          <li class="box_lowerconcept"> 
    	    <a href="/tag/<mtl:encode value='${relation.lower}'/>"><c:out value="${relation.lower}"/></a>
          </li>
    </c:forEach>
        </ul>
      </li>
    </ul>

</div>
</div>


<%@ include file="footer.jsp" %>