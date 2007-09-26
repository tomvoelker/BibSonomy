<%@include file="include_jsp_head.jsp" %>

<c:if test="${empty user.name}">
  <jsp:forward page="login"/>
</c:if>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="edit metadata" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a href="#" rel="path_menu">edit metadata&nbsp;<img src="/resources/image/box_arrow.png"></a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<div id="general">

<%--h2>Variante 1</h2>

<form action="/ExtendedFieldsHandler?group=<c:out value='${param.group}'/>" method="POST">
<c:set var="counter" value="0"/>

<table>

        <tr>
          <c:forEach items="${extended_fields_map}" var="efm">
            <th><c:out value="${efm.key}"/></th>
          </c:forEach>
        </tr>

        <tr>      
          <c:forEach items="${extended_fields_map}" var="efm">
            <td><c:out value="${efm.description}"/></td>
          </c:forEach>
        </tr>


<c:forEach items="${resources}" var="bib">
  <tr>
    <td colspan="<%=((SortedSet) session.getAttribute("extended_fields_map")).size() %>">
      <hr>
      <%@include file="/boxes/bibtex_desc.jsp" %>
      <input type="hidden" name="${bib.hash}" value="${counter}"/>
      
    </td>
</tr><tr>
    <c:forEach items="${extended_fields_map}" var="efm">
      <td>
      <textarea wrap="soft" cols="15" rows="3" name="${counter}_${efm.order}"><c:out value='${bib.extended_fields[efm.key]}'/></textarea>  
      </td>
    </c:forEach>

  </tr>
  <c:set var="counter" value="${counter + 1}"/>
</c:forEach>
</table>
<input type="submit"/>
</form>

<hr>
<h2>Variante 2</h2 --%>

<h2>Edit metadata for group <c:out value="${param.group}"/></h2>

<p>${status}</p>

<form action="/ExtendedFieldsHandler?group=<c:out value='${param.group}'/>" method="POST">
<input type="submit" value="save metadata"/>
<hr>
<c:set var="counter" value="0"/>
<ul class="bblist">
<c:forEach items="${resources}" var="resource">
  <li class="bm">
    <div style="background: #fafafa;"><%@include file="/boxes/bibtex_desc.jsp" %></div>
    <input type="hidden" name="${resource.hash}" value="${counter}"/>
    <table>
    <c:forEach items="${extended_fields_map}" var="efm">
      <tr>
        <td><c:out value="${efm.key}"/>:</td>
        <td><input type="text" size="40" name="${counter}_${efm.order}" value="<c:out value='${resource.extended_fields[efm.key]}'/>"/></td>
        <td><c:out value="${efm.description}"/></td>
      </tr>
    </c:forEach>
    </table>
    <hr>
  </li>
  <c:set var="counter" value="${counter + 1}"/>
</c:forEach>
</ul>
<input type="submit" value="save metadata"/>
</form>

<h2>Disregarded entries</h2>

<p>The following entries from your basket are not owned by you. Therefore you can not add metadata to them.</p>

<c:set var="basePath" value="/"/>
<ul class="bblist"><c:forEach items="${nonown}" var="bib">
  <li class="bm">
    <%@include file="/boxes/bibtex_desc.jsp" %>
    by <a href="/user/${resource.user}">${resource.user}</a>
    <%@ include file="/boxes/bibtex_action.jsp" %>
  </li>
</c:forEach></ul>



</div>

<%@ include file="footer.jsp" %>