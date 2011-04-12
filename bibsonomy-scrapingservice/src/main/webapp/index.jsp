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
		<link href="/resources/css/style.css" type="text/css" rel="stylesheet"/>
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
          
            <p>
            This service allows you to extraxt bibliographic metadata from 
            <a href="http://www.bibsonomy.org/scraperinfo">numerous digital libraries</a>.
            The extracted data is represented in  
            <a href="http://en.wikipedia.org/wiki/BibTeX">BibTeX</a> format.
            
            </p>
             

			<div class="scrapebox">
				<form method="get" action="/service">
                  <fieldset>
                    <legend>enter URL here</legend>
                    <input name="url" type="text" style="width: 75%;" value="${fn:escapeXml(bean.url)}"/>
					<input type="submit" value="send"/>
                  </fieldset>

                  <div onclick="javascript:document.getElementById('selection').style.visibility = 'visible';">
                    <fieldset id="selection" style="visibility: hidden" >
                      <legend>optional: selected text</legend>
                      <textarea rows="3" name="selection" style="width: 100%;">
                         <c:out value="${bean.selection}"/><c:out value=" "/>
                      </textarea>
                    </fieldset>
                  </div>
				</form>
			</div>


<c:set var="projectHome" value="http://scraper.bibsonomy.org/service"/>

<p>You can also drag the 
<script type="text/javascript">
<![CDATA[
  var myurl = "";
  if (window.getSelection) {
    myurl  = "javascript:location.href='${projectHome}?url='+encodeURIComponent(location.href)+'&amp;selection='+encodeURIComponent(window.getSelection())";
  } else if (document.getSelection) {
    myurl  = "javascript:location.href='${projectHome}?url='+encodeURIComponent(location.href)+'&amp;selection='+encodeURIComponent(document.getSelection())";
  } else if (document.selection) {
    myurl  = "javascript:location.href='${projectHome}?url='+encodeURIComponent(location.href)+'&amp;selection='+encodeURIComponent(document.selection.createRange().text)";
  }
  document.write("<a title=\"scrapePublication\"href=\""+myurl+"\" onclick=\"return false\" class=\"bookmarklet2\"><img src=\"/resources/image/button_scrapePublication.png\" alt=\"scrapePublication\"/></a>");
]]>
</script>
<noscript>
  (you need JavaScript enabled: Firefox/Opera: <a title="scrapePublication" href="javascript:location.href='${projectHome}?url='+encodeURIComponent(location.href)+'&amp;selection='+encodeURIComponent(document.getSelection())" onclick="return false" class="bookmarklet2"><img src="/resources/image/button_scrapePublication.png" alt="scrapePublication"/></a>,
  InternetExplorer: <a title="postPublication" href="javascript:location.href='${projectHome}?url='+encodeURIComponent(location.href)+'&amp;selection='+encodeURIComponent(document.selection.createRange().text)" onclick="return false" class="bookmarklet2"><img src="/resources/image/button_scrapePublication.png" alt="scrapePublication"/></a>)
</noscript>
button to the links toolbar of your browser once and then use it to scrape publications from pages listed <a href="http://www.bibsonomy.org/scraperinfo">here</a> by pressing
the button on one of the listed pages.</p>

<p>The service accepts the following parameters:</p>
<ul>
<li><tt>url</tt>: The URL of the page which should be scraped.</li>
<li><tt>selection</tt>: Text selected on the web page. Used by some scrapers in addition to / instead of the URL.</li>
<li><tt>format</tt>: If equal to <tt>bibtex</tt>, plain BibTeX is returned instead of this HTML page. Experimental: <code>rdf+xml</code></li>
</ul>
 
      
            <c:if test="${not empty bean}">
              
              <hr/>
  
              <c:if test="${not empty bean.errorMessage}">
                  <h3>errors</h3>
  				  <p class="errmsg">
  					<c:out value="${bean.errorMessage}"/>
  				  </p>
              </c:if>
              
              <c:if test="${not empty bean.url}">
                  <h3>scraped URL</h3> 
                  <p>
  					<a href="${fn:escapeXml(bean.url)}"><c:out value="${bean.url}"/></a>
  				  </p>
              </c:if>

              <c:if test="${not empty bean.selection}">
                  <h3>scraped selection</h3> 
                  <p>
                   <c:out value="${bean.selection}"/>
                  </p>
              </c:if>

              <c:if test="${not empty bean.scraper}">
                  <h3>active scraper</h3> 
                  <p>
                    <em><c:out value="${bean.scraper.class.name}"/>: </em>
                    ${bean.scraper.info}
                  </p>
              </c:if>
              
              <c:if test="${not empty bean.bibtex}">
                  <hr/>
                  <h3>resulting BibTeX</h3>
                  
                  <c:url var="postBibTeXURL" value="http://www.bibsonomy.org/BibtexHandler">
                    <c:param name="requTask">upload</c:param>
                    <c:param name="selection">${bean.selection}</c:param>
                    <c:param name="url">${bean.url}</c:param>
                  </c:url>
                  <c:url var="plainBibTeXURL" value="${projectHome}">
                    <c:param name="format">bibtex</c:param>
                    <c:param name="selection">${bean.selection}</c:param>
                    <c:param name="url">${bean.url}</c:param>
                  </c:url>
                     
                  <p>
                     <a style="border: 1px solid #ccc; background-color: #eee; margin: .5em; padding: .5em;" 
                        href="${postBibTeXURL}">post to BibSonomy</a>
                     <a style="border: 1px solid #ccc; background-color: #eee; margin: .5em; padding: .5em;" 
                        href="${plainBibTeXURL}">get plain BibTeX</a>
                  </p>

   	              <p style="white-space:pre;">
                     <c:out value="${bean.bibtex}"/>
  				  </p>
              </c:if>
              
            </c:if>
        
        </div>
     	</div>

	</body>
  </html>
</jsp:root>