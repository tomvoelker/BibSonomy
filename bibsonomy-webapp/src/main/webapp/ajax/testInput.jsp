<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <link rel="stylesheet" type="text/css" href="/test/style.css" />
<title>Insert title here</title>
</head>
<body>

<script type="text/javascript">
<!--
var req;
function loadXMLDoc(url) 
{
    // branch for native XMLHttpRequest object
    if (window.XMLHttpRequest) {
        req = new XMLHttpRequest();
        req.onreadystatechange = processReqChange;
        req.open("GET", url, true);
        req.send(null);
    // branch for IE/Windows ActiveX version
    } else if (window.ActiveXObject) {
        req = new ActiveXObject("Microsoft.XMLHTTP");
        if (req) {
            req.onreadystatechange = processReqChange;
            req.open("GET", url, true);
            req.send();
        }
    }
}

function processReqChange() 
{
    // only if req shows "complete"
    if (req.readyState == 4) {
        // only if "OK"
        if (req.status == 200) {
            // ...processing statements go here...
      response  = req.responseXML.documentElement;

      method    =  response.getElementsByTagName('method')[0].firstChild.data;
		
	   niceOutput(response.getElementsByTagName('tags')[0]);

      } else {
          alert("There was a problem retrieving the XML data:\n" + req.statusText);
      }
    }
}

function niceOutput(response)
{
    message   = document.getElementById('nameCheckFailed');
    removeAllChildren(ultag);
    var tags = response.getElementsByTagName("tag");
	for (i=0; i<tags.length; i++) {
    	  message.className = 'error';
	      var newLi = document.createElement("li");
	      ultag.appendChild(newLi);
	      newLi.appendChild(document.createTextNode(tags[i].childNodes[0].nodeValue + " " + tags[i].getAttribute("count")));
    }
}

function setMessageHidden(knoten) {
	knoten.className = 'hidden';
}

function removeAllChildren(knoten) {
    while (knoten.hasChildNodes()){
    	knoten.removeChild(knoten.firstChild);
    }
}

function checkName(input)
{
  if (input != ''){ 
    // Input mode
    url  = '/ajax/tags_prefix.jsp?prefix=' + input;
    loadXMLDoc(url);
  } else {
    // lösche output
    removeAllChildren(ultag);
    setMessageHidden(nameCheckFailed);
    }

}

-->
</script>

<h1><a href="/">BibSonomy</a> :: user :: 

<input id="username" name="username" type="text" 
  onkeyup="checkName(this.value)" />
<span class="hidden" id="nameCheckFailed">
  <ul id="ultag">
  </ul>
</span>


</body>
</html>