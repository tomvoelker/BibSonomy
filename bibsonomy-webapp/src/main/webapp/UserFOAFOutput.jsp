<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" %>
<%@ page import="java.lang.*,java.util.*" %>
<%@ page import="resources.*" %>
<%@ page contentType="application/xml;charset=UTF-8" %> 
<%@ page pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/taglibs/mytaglib.tld" prefix="mtl" %>


<c:if test="${empty user.name}">
	<jsp:forward page="/login"/>
</c:if>

<jsp:useBean id="foafBean" class="beans.SettingsBean" scope="request">
  <jsp:setProperty name="foafBean" property="name"     value="${param.requUser}"/>
  <jsp:setProperty name="foafBean" property="currUser" value="${user.name}"/>  
</jsp:useBean>

<% foafBean.queryDBFOAF(); %>

<rdf:RDF
      xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
      xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
      xmlns:foaf="http://xmlns.com/foaf/0.1/"
      xmlns:dc="http://purl.org/dc/elements/1.1/"
      xmlns:admin="http://webns.net/mvcb/">
      
	<foaf:Person>
		<foaf:name><c:out value="${foafBean.realname}"/></foaf:name>
		<foaf:nick><c:out value="${foafBean.name}"/></foaf:nick>		
		<foaf:gender><c:out value="${foafBean.genderLong}"/></foaf:gender>
		<foaf:birthday><c:out value="${foafBean.birthday}"/></foaf:birthday>		
		<foaf:mbox_sha1sum><c:out value="${foafBean.SHA1Email}"/></foaf:mbox_sha1sum>
		<foaf:homepage rdf:resource="<c:out value='${foafBean.homepage}'/>"/>
		<foaf:interest dc:title="<c:out value='${foafBean.interests}'/>"/>
        <foaf:based_near dc:title="<c:out value='${foafBean.place}'/>"/>

		
		<c:forEach var="friend" items="${foafBean.friends}">
			<foaf:knows>
				<foaf:Person rdf:about="${projectHome}foaf/user/<c:out value='${friend}'/>">
					<foaf:nick><c:out value="${friend}"/></foaf:nick>
				</foaf:Person>
			</foaf:knows>		
		</c:forEach>		
	</foaf:Person>
</rdf:RDF>