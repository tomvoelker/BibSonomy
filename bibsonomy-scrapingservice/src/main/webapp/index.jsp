<%@ page import="org.bibsonomy.scrapingservice.beans.ScrapingResultBean" %>
<%
	ScrapingResultBean bean = (ScrapingResultBean)request.getAttribute("result");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta content="text/html; charset=UTF-8" http-equiv="content-Type"/>
		<link href="/bibsonomy-scrapingservice/style.css" type="text/css" rel="stylesheet"/>
		<link href="/bibsonomy-scrapingservice/faq.css" type="text/css" rel="stylesheet"/>
		<title>BibSonomy :: Scraping Service</title>
	</head>
	<body>
		<div style="float:left" id="heading">
			<h1>
				<a href="http://www.bibsonomy.org">BibSonomy</a> :: <a href="index.jsp">Scraping Service</a>
			</h1>
			<div id="welcomeTop">Welcome to the Scraping Service from <a href="http://www.bibsonomy.org">BibSonomy</a>.</div>
		</div>
		<div>
			<table id="tnav">
			  <tr>
			    <td id="upper_menu" class="tleft">
			  	  <br/>
			    </td>
			  </tr>
			  <tr>
			    <td id="lower_menu" class="tleft">
				    <br/>
			    </td>
			  </tr>
			</table>
		</div>
		<div id="outer">
			<div class="scrapebox">
				<h2 class="listh">Enter URL here:</h2><br/><br/>
				<form method="get" action="ScrapingServlet">
					<input name="scrapingUrl" type="text" size="50"/><br/>
					<input type="submit" value="Send"/>
				</form>
			</div>
			<%
				if(bean != null && bean.getErrorMessage() != null){
			%>
				<div class="errmsg">
					<%=bean.getErrorMessage()%><br/><br/>
				</div>
			<%
				}
				if(bean != null && bean.getUrl() != null){
			%>
				<div>
					<h3>Scarped URL:</h3> <%=bean.getUrl()%><br/><br/>
				</div>
			<%
				}
				if(bean != null && bean.getBibtex() != null){
			%>
				<div>
					<h3>Scarped Bibtex:</h3><p sytle="white-space:pre"><%=bean.getBibtex()%></p><br/><br/>
				</div>
			<%
				}
			%>
    	</div>
    	<ul id="sidebar">
    		<li>
    			<span class="sidebar_h">
				    <br/>
    			</span>
			</li>
		</ul>
	</body>
</html>