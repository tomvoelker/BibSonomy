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


	<layout:layout pageTitle="Simple test site for your online recommender implementation">
		<jsp:attribute name="heading">
			<fmt:message key="error"/>
		</jsp:attribute>	
    
		<jsp:attribute name="content">
              <style type="text/css">
            textarea, input[type=text] {
              width: 100%;
              min-width: 30em;
              margin-right: 1em;
            }
            label {
              padding-right: 1em;
              width: 20%;
            }
            textarea {
              height: 4em;
            }
            td {
              padding-right: 1em;
            }
            
            #frontpage {
            	margin-left: auto;
            	margin-right: auto;
            	width: 777px;
            	border: solid;
            	border-style: outset;
            	border-width: 1px;
            }
            
            #frontpage img {
            	margin: 0px;
            }
            
            #frontpage h1 {
            	margin-left: 0em;
            }
            
            #frontpage p {
            	margin-left: 2em;
            }
            
            #frontpage ul {
            	list-style: circle;
            	margin-left: 3em;
            }
          </style>
          
          <div id="general">

          	<div id="frontpage">
          		<a href="http://www.kde.cs.uni-kassel.de/ws/dc09"><img src="http://www.ecmlpkdd2009.net/wp-content/themes/mountain/img/head.png" alt="logo"/></a>
          		<br/><br/>
          		<h1>Simple test site for your online recommender implementation</h1><br/>
          		<p>
          			In order to test how your online remote recommender integrates into BibSonomy's
          			recommender framework, you can send us a link to your recommender webservice. 
          			Your recommender will then be queried on every recommendation request 
          			submitted by one of the following posting forms:
          		</p>
          		<ul>
          			<li><a href="postBookmark">Bookmark posting form</a></li>
          			<li><a href="postPublication">BibTex posting form</a></li>
          		</ul>
          		
          		<br/><br/> 
          	</div>  
          </div>
            
		</jsp:attribute>				
	</layout:layout>

</jsp:root>