//for comments, see CVS version 1.12

var server	= null;
var path	= null;
var port     = 0;
var mostInnerLi = true;
var logUsername = "";

var serverurl = "/logging";

function log_init () {
	log_register_events();
}

function log_setUsername(username) {
	logUsername = username;	
}

function log_getUsername() {
	return logUsername;
}

function log_register_events() {
	if (document.addEventListener) { // Mozilla, Safari,...
		try {
			document.addEventListener ("click", log_sendRequest, false);
		} catch (e) {
			// do nothing
		}
	} else if (document.attachEvent) { // IE
		try {
			document.attachEvent('onclick', log_sendRequest);
		} catch (e) {
			// do nothingwelement
		}
	} else {
		// other browsers
	}
}

function log_sendRequest(e) {

	if (document.addEventListener) { // Mozilla, Safari,...
		element = e.target;
	} else if (document.attachEvent) { // IE
		element = event.srcElement;
	} else {
		// other browsers
	}

	// init
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
			childNodesA = [];
			childNodesA = welement.childNodes;

			dom_acontent = "";
			for (var cc=0; cc< childNodesA.length; cc++)
			{
				var childNode = childNodesA[cc];

				if (childNode.nodeName=="#text")
				{
					dom_acontent += " " + childNode.nodeValue; // can be empty, if other elements are inside node. every whitespace before and after other elements result in a text node 
				}
				else if (childNode.nodeName=="IMG")
				{
					var attributelistforimages = [ "id", "title", "alt" ];
					for (element in attributelistforimages) {
						if ((childNode.getAttribute(attributelistforimages[element]) != null) && (childNode.getAttribute(attributelistforimages[element])!=""))
						{
							dom_acontent += " " + childNode.getAttribute(attributelistforimages[element]);
							break; // break out of for loop
						}

					}
				}

			}


			// trim(dom_acontent)
			if (dom_acontent) {
				dom_acontent = dom_acontent.replace (/^\s+/, '').replace (/\s+$/, '');
			}
			dom_ahref=welementattrs_ahref;
			numberofposts=welementattrs_title.split(" ")[0];
			a_node_present = true;	

			if ( welementattrs_id.substr(0,5) == "spam_")
			{
				welement.setAttribute("id", "no"+welement.getAttribute("id"));
				welement.setAttribute("title", getString("post.meta.unflag_as_spam.title"));
				welement.firstChild.nodeValue = getString("post.meta.unflag_as_spam");

			}
			else if ( welementattrs_id.substr(0,7) == "nospam_")
			{
				welement.setAttribute("id", welementattrs_id.substr(2));
				welement.setAttribute("title", getString("post.meta.flag_as_spam.title"));
				welement.firstChild.nodeValue = getString("post.meta.flag_as_spam");
			}
		}  

		dom_path += welement.nodeName;
		if (welementattrs_id != "") dom_path += "#"+welementattrs_id;
		dom_path += "/";

		dom_path2 += welement.nodeName;
		if (welementattrs_id != "") dom_path2 += "#"+welementattrs_id;
		if (welementattrs_class != "") dom_path2 += "."+welementattrs_class;
		dom_path2 += "/";

		sibling_count = 0;

		if (welement.nodeName == "LI" && mostInnerLi)
		{
			mostInnerLi = false;

			siblingnode = welement;
			sibling_count = 1;
			while (siblingnode.previousSibling)
			{
				siblingnode = siblingnode.previousSibling;

				if (siblingnode.nodeName == "LI") 
				{
					sibling_count += 1;

				}
			} 

		}
		welement = welement.parentNode;

	} while (welement.parentNode);


	http_request = false;

	// if user clicked a a-node, then post data to logging-server

	//  create http_request-object
	if (a_node_present) {	

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
			return false;
		}


		// collect data to send
		senddata = 	'dompath='+dom_path+
		'&dompath2='+dom_path2+
		'&pageurl='+document.location.href+
		'&numberofposts='+	numberofposts+
		'&acontent='+dom_acontent+
		'&ahref='+dom_ahref+
		'&windowsize='+Fensterweite()+" "+Fensterhoehe()+
		'&mousedocumentpos='+mouseposition(e)+
		'&mouseclientpos='+absolutemouseposition(e)+
		'&listpos='+sibling_count+
		'&referer='+document.referrer+
		'&username='+log_getUsername();

		// post data
		http_request.open('POST', serverurl, true);
		http_request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		http_request.send(senddata);

	}  

}
log_init();