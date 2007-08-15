<jsp:useBean id="gmBean" class="beans.GroupMembersBean" scope="request">
  <jsp:setProperty name="gmBean" property="username" value="${user.name}"/>
  <jsp:setProperty name="gmBean" property="group" value="${group}"/>
</jsp:useBean>

<c:if test="${gmBean.count > 0}">

<li>

  <span class="sidebar_h">members   <%-- show form to join group --%>
  <c:if test="${not empty user.name}"><a href="/join_group?group=<mtl:encode value='${param.requGroup}'/>">join <c:out value='${param.requGroup}'/></a></c:if>
  </span>

  <ul id="groupmembers"><c:forEach var="member" items="${gmBean.members}">
    <li><a href="/user/<mtl:encode value='${member}'/>"><c:out value="${member}"/></a></li>
  </c:forEach></ul>

</li>

</c:if>