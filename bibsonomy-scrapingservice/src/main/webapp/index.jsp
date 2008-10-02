<?xml version="1.0" ?>
<jsp:root version="2.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">

  <jsp:directive.page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" session="true" />

  <jsp:useBean id="bean" class="org.bibsonomy.scrapingservice.beans.ScrapingResultBean" scope="request"/>

  <jsp:output doctype-root-element="html" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

  <html>
	<head>
		<meta content="text/html; charset=UTF-8" http-equiv="content-Type"/>
		<link href="/style.css" type="text/css" rel="stylesheet"/>
		<link href="/faq.css" type="text/css" rel="stylesheet"/>
		<title>BibSonomy :: scraping service</title>
	</head>
	<body>
  
		<div style="float:left" id="heading">
			<h1><a href="http://www.bibsonomy.org/">BibSonomy</a> :: <a href="/service">scraping service</a></h1>
			<div id="welcomeTop">Welcome to the scraping service of <a href="http://www.bibsonomy.org/">BibSonomy</a>.</div>
		</div>
		<div>
			<table id="tnav">
			  <tr><td id="upper_menu" class="tleft"><br/></td></tr>
			  <tr><td id="lower_menu" class="tleft"><br/></td></tr>
			</table>
		</div>
		<div id="outer">
          <div id="general">

			<div class="scrapebox">
				<form method="get" action="/service">
                    <span>enter URL here:</span>
                    <input name="url" type="text" size="50"/>
					<input type="submit" value="send"/>
				</form>
			</div>
      
            <hr/>

            <c:if test="${not empty bean and not empty bean.errorMessage}">
                <h3>errors</h3>
				<p class="errmsg">
					<c:out value="${bean.errorMessage}"/>
				</p>
            </c:if>
            
            <c:if test="${not empty bean and not empty bean.url}">
                <h3>scraped URL</h3> 
				<p>
					<c:out value="${bean.url}"/>
				</p>
            </c:if>
            
            <c:if test="${not empty bean and not empty bean.bibtex}">
                <h3>scraped BibTeX</h3>
				<p style="white-space:pre;">
                   <c:out value="${bean.bibtex}"/>
				</p>
            </c:if>
        
        </div>
     	</div>

	</body>
  </html>
</jsp:root>