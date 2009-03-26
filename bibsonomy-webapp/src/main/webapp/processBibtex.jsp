<%@include file="include_jsp_head.jsp" %>

<%@ page import="beans.SetPropertyEditor" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.beans.PropertyEditorManager" %>

<%
  /*
   register property editor for collections
   ***************************************************************************
   FIXME: what an ugly hack to read in collections in the old system ...
   ***************************************************************************
  */
  
   PropertyEditorManager.registerEditor(Set.class, SetPropertyEditor.class);

%>

<jsp:useBean id="bibtexHandlerBean" class="beans.BibtexHandlerBean" scope="request">
  <jsp:setProperty name="bibtexHandlerBean" property="*"/>
  <jsp:setProperty name="bibtexHandlerBean" property="relevantForParam" value="${paramValues.relevantFor}"/>  
</jsp:useBean>



<%-- test if tagstring contains comma and handle it --%>
<c:if test="${empty param.acceptComma}">
  <%
    String tags = request.getParameter("tags");
    if (tags != null && (tags.indexOf(",") != -1 || tags.indexOf(";") != -1)) {
  %>
    <jsp:forward page="/edit_bibtex">
      <jsp:param name="testComma" value="true" />
    </jsp:forward>
  <%
    } 
  %>
</c:if>

<%
   if (bibtexHandlerBean.isValid()) {
%>
<jsp:forward page="/BibtexHandler"/>
<%
   }  else {
%>
<jsp:forward page="/edit_bibtex"/>
<%
   }
%>
