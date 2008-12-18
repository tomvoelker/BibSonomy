<%@include file="include_jsp_head.jsp" %>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="relations" />
</jsp:include>

<jsp:useBean id="RelationBean" class="beans.RelationBean" scope="request"/>

<%-------------------------- Heading -----------------------%>
<!-- 2008/12/18, fei: removed attribute 'rel="Start"' from link as it brakes chrome menus -->
<h1 id="path"><a href="/">${projectName}</a> :: <a rel="path_menu" href="/relations"><img src="/resources/image/box_arrow.png">&nbsp;relations</a>
:: <form action="/concept/tag/" method="GET" class="smallform">
  <input type="text" size="20" name="tag" id="inpf" value="<c:out value='${param.requTag}'/>"/>
</form>
</h1>

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<div id="general">

<p>
  Here you can see the most popular relations of our users.

  <c:if test="${not empty user.name}">
    You can access your own relations via 
    <a href="/relations/<mtl:encode value='${user.name}'/>">myRelations</a> and edit them on the
    <a href="/edit_tags">edit tags</a> page.
  </c:if>

</p>


<%--

shows the list of relations:

SUPERTAG1 <-- subtag11 subtag12 subtag13
SUPERTAG2 <-- subtag21 subtag22 subtag23
SUPERTAG3 <-- subtag31 subtag32 subtag33

--%>
 
  <%-- duplicated and only slightly changed code from /boxes/relationlist.jsp --%>
  <table>
    <c:set var="lastupper" value="" />
    <c:forEach var="relation" items="${RelationBean.allRelations}">
      <c:if test="${relation.upper ne lastupper}">
        <c:if test="${!empty lastupper}">
  	    <%-- not the first supertag --> close list of former supertag --%>
          </ul><%-- close subtag list --%>
        </td></tr><%-- close table row--%>
  	    </c:if>
	    <%-- new supertag --%>
	    <c:set var="lastupper" value="${relation.upper}" />
        <tr><td class="upperconcept">
		  <a style="font-size:${(relation.count * 10) + 80}%" title="${relation.count} users"  href="/concept/tag/<mtl:encode value='${relation.upper}'/>"><c:out value="${relation.upper}"/></a>
		  <td>&larr;</td>
  	  	  <td><ul id="<c:out value='${relation.upper}'/>" class="box_lowerconcept_elements">
      </c:if>
          <%-- subtags --%>
          <li class="box_lowerconcept"> 
    	    <a href="/tag/<mtl:encode value='${relation.lower}'/>"><c:out value="${relation.lower}"/></a>
          </li>
    </c:forEach>
    <%-- close last super-concept --%>
        </ul>
      </td></tr>
    </table>

</div>

<script type="text/javascript">
maximizeById("general");
</script>


<%@ include file="footer.jsp" %>