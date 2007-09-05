<?xml version="1.0" encoding="UTF-8"?>
<%@ page import="beans.RelationBean,resources.TagRelation"%>
<%@ page language="java"%>
<%@ page import="servlets.listeners.*"%>
<%@ page import="helpers.*"%>
<%@ page contentType="text/xml"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page session="true"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="/WEB-INF/src/tags/mytaglib.tld" prefix="mtl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%-- 

returns an XML file of relations the user wants to be shown for updating shown relations in AJAX style


--%>


  <%-- 
  
    filling the bean ... the bean also gets the parameter show= (or hide=) and
    initiates the corresponding action
    ALWAYS uses ${currUser}, since we can only show/hide the users own relations ...
  
  --%>
<jsp:useBean id="RelationBean" class="beans.RelationBean" scope="request">
  <jsp:setProperty name="RelationBean" property="requUser" value="${user.name}" />
  <jsp:setProperty name="RelationBean" property="*" />
</jsp:useBean> 
  
  
<relations user="<c:out value='${user.name}'/>"> 

  <c:set var="lastupper" value="" />
  <c:forEach var="relation" items="${RelationBean.shownRelations}">
    <c:if test="${relation.upper ne lastupper}">
      <c:if test="${!empty lastupper}">
  	  <%-- not the first supertag --> close list of former supertag --%>
        </lowers>
      </relation>
  	  </c:if>
	  <%-- new supertag --%>
	  <c:set var="lastupper" value="${relation.upper}" />
      <relation>
		<upper><c:out value="${relation.upper}"/></upper>
        <lowers id="<c:out value='${relation.upper}'/>"> <%--class="box_lowerconcept_elements" --%>
    </c:if>
        <%-- subtags --%>
        <lower><c:out value="${relation.lower}"/></lower>
  </c:forEach>
      </lowers>
    </relation>

</relations>
