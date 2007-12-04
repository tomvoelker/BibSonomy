<li><span class="sidebar_h">related tags</span>

<jsp:useBean id="tagCloudBean" class="beans.TagCloudBean" scope="request">
	<jsp:setProperty name="tagCloudBean" property="groupingName" value="${param.requGroup}"/>
	<jsp:setProperty name="tagCloudBean" property="requTags" value="${param.requTag}"/>	
	<c:if test="${user.name != null}">
		<jsp:setProperty name="tagCloudBean" property="username" value="${user.name}"/>
	</c:if>
</jsp:useBean>

<%@include file="/boxes/tagboxstyle.jsp" %> 
<c:forEach var="tag" items="${tagCloudBean.tags}">
	<li>
      <a title="<c:out value='${param.requTag}+${tag.name}'/>" href="/group/<mtl:encode value='${param.requGroup}' />/<mtl:encode value='${param.requTag}'/>+<mtl:encode value='${tag.name}'/>">+</a>
      <a href="/group/<mtl:encode value='${param.requGroup}'/>/<mtl:encode value='${tag.name}' />"> <c:out value="${tag.name}" /> </a>
    </li>
</c:forEach></ul> 
</li>