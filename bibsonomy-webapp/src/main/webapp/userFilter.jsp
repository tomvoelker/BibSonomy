<%@include file="/include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="user::${param.requUser}" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: user :: 
<a href="/user/<mtl:encode value='${param.requUser}'/>"><c:out value='${param.requUser}'/></a> ::
<form action="/user/<mtl:encode value='${param.requUser}'/>" method="GET" class="smallform">
  <input type="text" size="20" name="tag" id="inpf"/>
</form>
</h1> 


<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>


<div id="outer">

<div id="bibbox"> 

    <c:choose>
      <c:when test="${param.filter eq 'myDuplicates'}">
        <p>All of your publication posts which you have at least twice.</p>
      </c:when>
      <c:otherwise>
        <p>All of your publication posts which have a document (PDF, PS or DJVU) attached.</p>
      </c:otherwise>
    </c:choose>

    <c:set var="basePath" value="/"/>
    <%@include file="/boxes/bibtex_list.jsp" %>  <%-------------------------- BibTeX     -----------------------%>
</div>

</div>


<ul id="sidebar">
  <%@include file="/boxes/tags/usersrelations.jsp" %>
   
  <c:set var="markSuperTags" value="true"/>
  <%@include file="/boxes/tags/userstags.jsp"%>
</ul>


<%@ include file="/footer.jsp" %>