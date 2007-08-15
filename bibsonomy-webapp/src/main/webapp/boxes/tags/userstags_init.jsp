<%-- set user to user.name for tag query, if not user requested (used on post page) --%>
<c:choose>
  <c:when test="${empty param.requUser}">
    <c:set var="requestedUserName" value="${user.name}"/>
  </c:when>
  <c:otherwise>
    <c:set var="requestedUserName" value="${param.requUser}"/>   
  </c:otherwise>
</c:choose>

<jsp:useBean id="TagConceptBean" class="beans.TagConceptBean"           scope="request">
  <jsp:setProperty name="TagConceptBean" property="currUser"            value="${user.name}"/>
  <jsp:setProperty name="TagConceptBean" property="sortOrder"           value="${user.tagboxSort}"/>
  <jsp:setProperty name="TagConceptBean" property="withMarkedSupertags" value="${markSuperTags}"/>
  <jsp:setProperty name="TagConceptBean" property="requUser"            value="${requestedUserName}"/>
  <jsp:setProperty name="TagConceptBean" property="minfreq"             value="${user.tagboxMinfreq}" />
</jsp:useBean>