var message = "On Wednesday, January 19th, we will release a new version of BibSonomy. You will have to re-login. <a href='http://bibsonomy.blogspot.com/2010/12/what-happened-with-next-release.html/'>read more</a>."; 
var cookieName = "bibSystemMessage";
var cookieDays = 3; // cookie expires in
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
	box.style.left = Math.floor((window.innerWidth - width) / 2) + "px";
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