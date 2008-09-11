/**
    Klicklogging. Logging Infos bei jedem Klick an einen Server senden.
    
	Diese Datei sendet bei jedem Klick einen Request an einen vordefinierten Server. (AJAX)
	Dieser Request enthält Daten, um die Klickposition auswerten zu können.
	So kann jeder Klick nachvollzogen werden.
	
	Es werden folgende Daten übermittelt:
    dompath: Position des Klicks im DOM-Baum
	pageurl: URL der aktuellen Seite
	acontent:  Inhlat des angeklickten Links, wenn verfügbar. Der klickbare Linktext.
	ahref:   Ziel-URL des angeklickten Links

	Es werden nur Klicks auf einen Link (A-Element) an den Loggingserver gesendet.

	Der Loggingserver kann die Requests auswerten und in einer Datenbank speichern, so dass eine spätere
	Auswertung möglich sein wird.

	Sven Stefani, 2008
	
*/

var server	= null;
var path	= null
var port     = 0;
var mostInnerLi = true;


//var serverurl = "http://p7.biblicious.org/logging"; // ohne slash
//var serverurl = "http://p7.biblicious.org/logging/"; // mit slash
var serverurl = "/logging"; // absolut, ohne serverangabe, ohne slash

function log_init () {
  log_register_events();
}

function log_register_events() {
	document.addEventListener ("click", log_sendRequest, false);
}

function log_sendRequest(e) {
	element = e.target;

	welement = element;
	dom_path = "";
	dom_path2 = "";
	dom_acontent = "";
	dom_ahref = ""; 
	a_node_present = false;	
	numberofposts = "";

	function Fensterweite () {
	  if (window.innerWidth) {
	    return window.innerWidth;
	  } else if (document.body && document.body.offsetWidth) {
	    return document.body.offsetWidth;
	  } else {
	    return 0;
	  }
	}
	
	function Fensterhoehe () {
	  if (window.innerHeight) {
	    return window.innerHeight;
	  } else if (document.body && document.body.offsetHeight) {
	    return document.body.offsetHeight;
	  } else {
	    return 0;
	  }
	}

	function mouseposition(e){
	
		// find out if ie runs in quirks mode
		//
		var docEl = (
		             typeof document.compatMode != "undefined" && 
		             document.compatMode        != "BackCompat"
		            )? "documentElement" : "body";
		

		// position where event happens
		//
		var xPos    =  e? e.pageX : window.event.x;
		var yPos    =  e? e.pageY : window.event.y;
		
		
		// for ie add scroll position
		//
		if (document.all && !document.captureEvents) {
			xPos    += document[docEl].scrollLeft;
		    yPos    += document[docEl].scrollTop;
		}
		   
		return xPos + " " + yPos;			
	
	}

	function absolutemouseposition(e){
	
		// position where event happens
		//
		var xPos    =  e? e.clientX : window.event.clientX;
		var yPos    =  e? e.clientY : window.event.clientY;
		
		   
		return xPos + " " + yPos;			
	
	}


	// schleife fängt mit dem innersten LI an
	mostInnerLi = true;

	do
	{
		
		welementattrs = welement.attributes;
		welementattrs_id	= "";
		welementattrs_class = "";
		welementattrs_ahref = "";
		welementattrs_title = "";
		
		try {
			welementattrs_id = welementattrs.getNamedItem('id').value;
		}
		catch(err)
		{
		}

		try	{
			welementattrs_class = welementattrs.getNamedItem('class').value;
		}
		catch(err)
		{
		}

		try	{
			welementattrs_ahref = welementattrs.getNamedItem('href').value;
		}
		catch(err)
		{
		}
		
		try	{
			welementattrs_title = welementattrs.getNamedItem('title').value;
		}
		catch(err)
		{
		}

		if (welement.nodeName == "A") {
			dom_acontent = welement.childNodes[0].textContent;
			// trim(dom_acontent)
			dom_acontent = dom_acontent.replace (/^\s+/, '').replace (/\s+$/, '');
			dom_ahref=welementattrs_ahref;
			numberofposts=welementattrs_title.split(" ")[0];
			a_node_present = true;	
		}  

		dom_path += welement.nodeName;
		if (welementattrs_id != "") dom_path += "#"+welementattrs_id;
		dom_path += "/"

		dom_path2 += welement.nodeName;
		if (welementattrs_id != "") dom_path2 += "#"+welementattrs_id;
		if (welementattrs_class != "") dom_path2 += "."+welementattrs_class;
		dom_path2 += "/"


		if (welement.nodeName == "LI" && mostInnerLi)
		{
			// damit die schleife nicht bei geschachtelten LIs erneut aufgerufen wird
			mostInnerLi = false;
		
		// zeige alle schwesterknoten
		
			siblingnode = welement;
			sibling_count = 1;
			while (siblingnode.previousSibling)
			{
				siblingnode = siblingnode.previousSibling;
				
				if (siblingnode.nodeName == "LI") 
				{
//					console.log(siblingnode.nodeName);
					sibling_count += 1;
					
				}
			} 
			
//			console.log("Stelle im DOM: " + sibling_count);
			
		}
		welement = welement.parentNode
		
	} while (welement.parentNode)

	if (a_node_present) {	
		http_request = false;

        if (window.XMLHttpRequest) { // Mozilla, Safari,...
            http_request = new XMLHttpRequest();
            if (http_request.overrideMimeType) {
                http_request.overrideMimeType('text/xml');
                // zu dieser Zeile siehe weiter unten
            }
        } else if (window.ActiveXObject) { // IE
            try {
                http_request = new ActiveXObject("Msxml2.XMLHTTP");
            } catch (e) {
                try {
                    http_request = new ActiveXObject("Microsoft.XMLHTTP");
                } catch (e) {}
            }
        }

        if (!http_request) {
//            alert('Ende :( Kann keine XMLHTTP-Instanz erzeugen');
            return false;
        }


		senddata = 	'dompath='+dom_path+
					'&dompath2='+dom_path2+
					'&pageurl='+document.location.href+
					'&numberofposts='+	numberofposts+
					'&acontent='+dom_acontent+
					'&ahref='+dom_ahref+
					'&windowsize='+Fensterweite()+" "+Fensterhoehe()+
					'&mousedocumentpos='+mouseposition(e)+
					'&mouseclientpos='+absolutemouseposition(e)+
					'&listpos='+sibling_count;
					
		http_request.open('POST', serverurl, true);
		http_request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		http_request.send(senddata);
		
	}  

	http_request.onreadystatechange = readystatechange; 

}

    function readystatechange() {

//		console.log('.. readyState:  '+http_request.readyState);
		if (http_request.readyState == 4) {
//			console.log('.. http-status:  '+http_request.status);
		}
//		console.log('.. http-response:  '+http_request.responseText);

    }



// load logger
log_init();