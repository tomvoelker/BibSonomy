<?xml version="1.0" ?>
<jsp:root version="2.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
	xmlns:layout="urn:jsptagdir:/WEB-INF/tags"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:tags="urn:jsptagdir:/WEB-INF/tags/tags"
>
	
	<jsp:directive.page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" session="true" />


	<layout:layout pageTitle="An error occured!">
		<jsp:attribute name="heading">
			<fmt:message key="error"/>
		</jsp:attribute>	
    
		<jsp:attribute name="content">
    
          <div id="general">

            success!
  
          </div>
            
		</jsp:attribute>				
	</layout:layout>

</jsp:root>