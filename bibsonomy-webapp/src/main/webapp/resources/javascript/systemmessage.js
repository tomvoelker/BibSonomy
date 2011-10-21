var message = "NEW: Discussion and Ratings! See our <a href='<a href='<a href='http://blog.bibsonomy.org/2011/07/feature-of-week-revies-and-discussion.html'>blog</a>, click on the discussion button or on the 5-Stars icon below each post.<br /> In September we will change the format of author names. <a href='http://blog.bibsonomy.org/2011/07/structure-of-authoreditor-names-in.html'>read more</a>.";
//var cookieName = "bibSystemMessageAuthorNames";
var cookieName = "bibSystemMessageDiscussion";

var cookieDays = 100;
var width = 400; // px

$(window).load(function() {
	// user is logged in and has no cookie
	if (document.getElementById("inboxctr")&&!hasCookie(cookieName)) {
		showMessage();
	}
});


function showMessage() {
	var box = document.createElement("div");
	box.style.position = "absolute";
	box.style.width = width + "px";
	box.style.left = Math.floor((getWindowWidth() - width) / 2) + "px";
	box.style.top  = "150px";
	box.style.background = "#eee";
	box.style.padding = "1em";
	box.style.border = "2px solid #006699";
	box.id = "systemmessage";
	
	var m = document.createElement("span");
	m.innerHTML = message;
	box.appendChild(m);

	var close = document.createElement("a");
	close.style.cssFloat = "right";
	close.style.cursor = "pointer";
	close.href = "javascript:void(0);";
	close.onclick = hideMessage;
	close.appendChild(document.createTextNode("close"));
	box.appendChild(close);

	document.getElementsByTagName("body")[0].appendChild(box);
}

function getWindowWidth() {
	if (window.innerWidth) return window.innerWidth;
	if (document.body.clientWidth) return document.body.clientWidth;
}

function hideMessage() {
	var box = document.getElementById("systemmessage");
	if (box) {
		document.getElementsByTagName("body")[0].removeChild(box);
	}
	setCookie();
}

function setCookie() {
	var e = new Date();
	var epp = e.getTime() + (cookieDays * 24 * 60 * 60 * 1000);
	e.setTime(epp);
	document.cookie = cookieName + "=x;expires=" + e.toGMTString();
}

function hasCookie(name) {
	var c = document.cookie.split("; ");
	for (var i=0;i<c.length;i++) {
		if (c[i].split("=")[0]==name) return true;
	}
	return false;
}