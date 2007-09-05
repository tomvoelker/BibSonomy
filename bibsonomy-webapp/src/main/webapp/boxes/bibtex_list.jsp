  <ul class="bblist"><c:forEach var="resource" items="${ResourceBean.bibtex}">
      <%@ include file="/boxes/resource_own_entry_mark.jsp" %>
      <%@ include file="/boxes/bibtex_desc.jsp" %>
      <%@ include file="/boxes/bibtex_desc2.jsp" %>
      <%@ include file="/boxes/bibtex_action.jsp" %>
    </li>
  </c:forEach></ul>