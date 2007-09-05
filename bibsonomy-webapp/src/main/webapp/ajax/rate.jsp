<?xml version="1.0" encoding="UTF-8"?>
<%@ page import="beans.PickBean"%>
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

returns the number of currently picked publications, additionally
picks or unpicks a document

--%>

  <%-- 
  
    filling the bean ... the bean also gets the parameters and
    initiates the corresponding action
  
  --%>
<%--
  <jsp:useBean id="PickBean" class="beans.PickBean" scope="request">
    <jsp:setProperty name="PickBean" property="currUser" value="${user.name}" />
    <jsp:setProperty name="PickBean" property="*" />
  </jsp:useBean> 
  
  <result count="${PickBean.pickCount}" /> 
  --%>
  new rating: ${param.rating}