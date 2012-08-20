const {classes: Cc, interfaces: Ci, utils: Cu} = Components;
Cu.import("resource://gre/modules/Services.jsm");
Cu.import("resource://gre/modules/AddonManager.jsm");

const 	PROJECT = "$PROJECT_NAME$",
		PROJECT_PREFIX = PROJECT.toLowerCase()+"-bookmarklet",
		BOOKMARK_ID = PROJECT_PREFIX+"-bookmark",
		PUBLICATION_ID = PROJECT_PREFIX+"-publication", 
		HOME_ID = PROJECT_PREFIX+"-home",
		KEYSET_ID = PROJECT_PREFIX+"-keyset",
		VERSION = 1;

function getPrefsService(){
	return Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService).getBranch("extensions.bookmarklet.");
}
		
var windowListener = {
		onOpenWindow: function(aWindow) {
			let domWindow = aWindow.QueryInterface(Ci.nsIInterfaceRequestor).getInterface(Ci.nsIDOMWindow);
			domWindow.addEventListener("load", function() {
				domWindow.removeEventListener("load", arguments.callee, false);
				domWindow.addEventListener("aftercustomization", function(){setMargin(domWindow.document);}, false);
				
				if(getPrefsService().getBoolPref("buttons"))
					setUpButtons(domWindow);
				updateShortcuts(domWindow);
			}, false);
		},
		onWindowTitleChange: function(aWindow){},
		onCloseWindow: function(aWindow){
			let domWindow = aWindow.QueryInterface(Ci.nsIInterfaceRequestor).getInterface(Ci.nsIDOMWindow);
			domWindow.removeEventListener("aftercustomization", arguments.callee, false);
		}
};

var addon = {getResourceURI: function(filePath) ({spec: __SCRIPT_URI_SPEC__ + "/../" + filePath})};
Services.scriptloader.loadSubScript(addon.getResourceURI("l10n.js").spec, this);
Services.scriptloader.loadSubScript(addon.getResourceURI("shortcutHelper.js").spec, this);
l10n(addon, "bookmarklet.properties", getLocale());

function getWindows(){
	let windows=new Array();
	let enumerator=Cc["@mozilla.org/appshell/window-mediator;1"].getService(Components.interfaces.nsIWindowMediator).getEnumerator("navigator:browser");
	while(enumerator.hasMoreElements())windows.push(enumerator.getNext());
	return windows;
}

function getCurrentWindow(){
	return Cc["@mozilla.org/appshell/window-mediator;1"].getService(Components.interfaces.nsIWindowMediator).getMostRecentWindow("navigator:browser");
}

function getMediator(){
	return Cc["@mozilla.org/appshell/window-mediator;1"].getService(Components.interfaces.nsIWindowMediator);
}

Array.prototype.each = function(callback) {for(var i=0; i<this.length;) {callback(i, this[i++]);}};

function getLocale() {
	let locale = Cc["@mozilla.org/chrome/chrome-registry;1"]
    .getService(Ci.nsIXULChromeRegistry).getSelectedLocale("global");
	switch(locale) {
			case "de":
				return "de";
			default:
				return "en";
	}
}

function PrefListener(branch_name, callback) {  
	// Keeping a reference to the observed preference branch or it will get  
	// garbage collected.  
	var prefService = Components.classes["@mozilla.org/preferences-service;1"]  
	.getService(Components.interfaces.nsIPrefService);  
	this._branch = prefService.getBranch(branch_name);
	this._branch.QueryInterface(Components.interfaces.nsIPrefBranch2);  
	this._callback = callback;  
}  

PrefListener.prototype.observe = function(subject, topic, data) {  
	if (topic == 'nsPref:changed')  
		this._callback(this._branch, data);  
};  

PrefListener.prototype.register = function(trigger) {  
	this._branch.addObserver('', this, false);  
	if (trigger) {
		let that = this;  
		this._branch.getChildList('', {}).  
		forEach(function (pref_leaf_name)  
				{ that._callback(that._branch, pref_leaf_name); });  
	}  
};  

PrefListener.prototype.unregister = function() {  
	if (this._branch)  
		this._branch.removeObserver('', this);  
};

function openUrl(option) {
	let currentBrowser = null;
	let currentWindow=getCurrentWindow();
	if(!currentWindow)
		return;
	currentBrowser=currentWindow.getBrowser();

	var url = 'http://www.biblicious.org/';
	var homePrefix = "myBibSonomy";
	if(option==1) {
		url+=homePrefix;
	} else {
		var selectionText=encodeURIComponent(currentBrowser.selectedBrowser.contentWindow.window.getSelection());
		var srcUrl = encodeURIComponent(currentBrowser.selectedBrowser.contentWindow.location.href);
		var descr = '&description='+encodeURIComponent(currentBrowser.contentWindow.document.title);
		if(option==2) 
			url+='ShowBookmarkEntry?url='+srcUrl+descr+'&extended='+selectionText+"&referer="+srcUrl;
		else
			url+='BibtexHandler?requTask=upload&url='+srcUrl+descr+'&selection='+selectionText+"&referer="+srcUrl;
	}
	if(getPrefsService().getBoolPref("openNewTab")) {
		currentBrowser.selectedTab = currentBrowser.addTab(url);
		currentBrowser.selectedTab.focus();	
		return;
	}
	currentBrowser.selectedBrowser.contentWindow.location.href = url;
}

function saveToolbarLayout(doc, toolbar){
	if(toolbar==undefined)
		return;
	toolbar.setAttribute("currentset", toolbar.currentSet);
	doc.persist(toolbar.id, "currentset");
}

function createButton(doc, msg) {
	let button=doc.createElement("toolbarbutton");	
	button.setAttribute("class", "toolbarbutton-1 chromeclass-toolbar-additional");
	button.setAttribute("tooltiptext", msg);
	button.setAttribute("label", msg);
	return button;
}

function createButtons(doc) {
	
	var buttons = new Array();
	let buttonBookmark=createButton(doc, _("bookmarkLabel"));
	let buttonPublication=createButton(doc, _("publicationLabel"));
	let buttonHome=createButton(doc, _("homeLabel"));
	
	buttonBookmark.setAttribute("id", BOOKMARK_ID+"-button");
	buttonBookmark.setAttribute("image", __SCRIPT_URI_SPEC__ + "/../images/bookmark.png");
	buttonBookmark.addEventListener("click", function(){openUrl(2);}, true);

	buttonPublication.setAttribute("id", PUBLICATION_ID+"-button");
	buttonPublication.setAttribute("image", __SCRIPT_URI_SPEC__ + "/../images/publication.png");
	buttonPublication.addEventListener("click", function(){openUrl(3);}, true);
	
	buttonHome.setAttribute("id", HOME_ID+"-button");
	buttonHome.setAttribute("image", __SCRIPT_URI_SPEC__ + "/../images/home.png");
	buttonHome.addEventListener("click", function(){openUrl(1);}, true);

	buttons.push(buttonHome);
	buttons.push(buttonBookmark);
	buttons.push(buttonPublication);
	return buttons;
}

function addButtons(domWindow) {
	if (!domWindow) {return;}
	let doc = domWindow.document; 
	let toolbar=doc.getElementById("nav-bar");
	if (!toolbar) return;
	let buttons=createButtons(doc);
	for(let i=0;i < buttons.length; i++) {
		doc.getElementById("navigator-toolbox").palette.appendChild(buttons[i]);
		toolbar.insertItem(buttons[i].id, null);
	}
	setMargin(doc);
	saveToolbarLayout(doc, toolbar);
}

function removeButtons(buttons, doc, removeFromPalette) {
	var toolbars = doc.querySelectorAll("toolbar");
	if(removeFromPalette) {
		let navElements=doc.getElementById("navigator-toolbox").palette.children;
		let candidates = new Array();
		for(var i=0; i < navElements.length; i++) {
			for(var k=0;k<buttons.length;k++) {
				if(navElements[i].id==buttons[k]){
					candidates.push(navElements[i]);
					break;
				}
			}
		}
		for(let i=0; i < candidates.length;i++)
			candidates[i].parentNode.removeChild(candidates[i]);
	}
	let candidates = new Array();
	for(let i=0;i<toolbars.length;i++) {
			for(let k=0;k<toolbars[i].children.length;k++) {
						for(let m=0; m < (buttons.length); m++) { 
									if(toolbars[i].children[k].id==buttons[m]){
										candidates.push(toolbars[i].children[k]);
										break;
									}
						}
			}
	}
	for(let i=0; i < candidates.length;i++) {
			candidates[i].parentNode.removeChild(candidates[i]);
	}
	if(removeFromPalette) saveToolbarLayout(doc, toolbars[i]);
}

function setUpFirstStart(data) {
	let prefs = getPrefsService();
	let domWindow = getCurrentWindow();

	prefs.setBoolPref("buttons", true);
	prefs.setBoolPref("openNewTab", false);
	prefs.setBoolPref("shortcutHomeEnabled", false);
	prefs.setBoolPref("shortcutBookmarkEnabled", false);
	prefs.setBoolPref("shortcutPublicationEnabled", false);
	prefs.setCharPref("shortcutHome", "");
	prefs.setCharPref("shortcutBookmark", "");
	prefs.setCharPref("shortcutPublication", "");
	
	if(!domWindow) {
		let windowMediator = getMediator();
		let firstWindowListener = {
				onOpenWindow: 
					function(aWindow) {
						let domWindow = aWindow.QueryInterface(Ci.nsIInterfaceRequestor).getInterface(Ci.nsIDOMWindow);
						getMediator().removeListener(this);
						domWindow.addEventListener("load", function() {
							domWindow.removeEventListener("load", arguments.callee, false);
							addButtons(domWindow);
						}, false);
					}
		};
		windowMediator.addListener(firstWindowListener);
		return;
	}
	addButtons(domWindow);
}

function setUpButton(doc, toolbar, id) {
	let prevItem = null;
	if(!((prevItem=getPrevToolbarItem(id, toolbar))===null)
			&& prevItem==false) 
		return false;

	toolbar.insertItem(id, ((prevItem===null)?null:doc.getElementById(prevItem)));
	return true;
}

function setUpButtons(domWindow) {
	if(domWindow===null)
		return;
	let doc=domWindow.document;
	let buttons=createButtons(doc);

	let navBox = doc.getElementById("navigator-toolbox");
	if(doc.getElementById("navigator-toolbox")!=null) {
		let palette = navBox.palette;
		for(var i = 0; i < buttons.length; i++)
			if((palette.getElementsByAttribute("id", buttons[i].id))[0] == null)
				palette.appendChild(buttons[i]);
	}

	var toolbars = doc.querySelectorAll("toolbar");
	for(var l = 0; l < toolbars.length; l++) {
		for(let m = 0; m < buttons.length;m++)
			setUpButton(doc, toolbars[l], buttons[m].id); 
	}
	setMargin(doc);
}

function setMargin(doc) {
	let buttons = new Array();
	buttons.push(doc.getElementById(HOME_ID+"-button"));
	buttons.push(doc.getElementById(BOOKMARK_ID+"-button"));
	buttons.push(doc.getElementById(PUBLICATION_ID+"-button"));
	for(let i=0;i < buttons.length; i++) {
		if(buttons[i]===null||
				buttons[i].previousSibling===null)
			continue;
		var margin="";
		if(buttons[i].previousSibling.id.indexOf(PROJECT.toLowerCase()) > -1)
			margin = "-2px";
		buttons[i].style.marginLeft = margin;
	}
}

function install(data, reason) {
	if(reason == ADDON_INSTALL)
		setUpFirstStart(data);
}

function uninstall() {}

function startup(data, reason) {
	let windowMediator = Cc["@mozilla.org/appshell/window-mediator;1"].getService(Components.interfaces.nsIWindowMediator);
	let enumerator = windowMediator.getEnumerator("navigator:browser");
	windowMediator.addListener(windowListener);
}

function shutdown(data, reason) {
	let remove = false; 
	if(reason==ADDON_UNINSTALL || reason==ADDON_DISABLE) remove=true;
	l10n.unload();
	Services.obs.removeObserver(optionsObserver, "addon-options-displayed", false);
	this.disable(remove);
} 

function removeShortcuts(window) {
	let keyset = null;
	if((keyset=window.document.getElementById(KEYSET_ID))===null) 
			return;
	keyset.parentNode.removeChild(keyset);
}

function createShortcut(initKey, callback, id, doc) {
	var keys = initKey.split(" ");
	var modifier = "";
	var currentKey = "";
	for(var i = 0; i < keys.length; i++) {
		var keyCode=keys[i];
		currentKey = keyCode == 17 ? ("control"): keyCode == 18 ? ("alt")
							: keyCode == 16 ? ("shift")
									: keyCode == 8 ? ("backspace")
											: keyCode == 9 ? ("tab")
													: keyCode == 46 ? ("delete")
															: keyCode == 13 ? ("enter")
																	: keyCode == 32 ? ("space"):null;
		if(currentKey!=null)
			modifier+=(modifier.length==0?"":", ")+currentKey.toLowerCase();
		else
			currentKey=String.fromCharCode(keyCode);
	}
	let key = doc.createElement("key");
	key.setAttribute("id", id);
	key.setAttribute("key", currentKey);
	key.setAttribute("modifiers", modifier);
	key.setAttribute("oncommand", "void(0);");
	key.addEventListener("command", callback, true);
	return key;
}

function updateShortcuts(domWindow) {
	if(domWindow===undefined)
		return;
	let append=false;
	let doc = domWindow.document;
	let mainKeyset=doc.getElementById("mainKeyset");
	
	if(doc===undefined
			|| mainKeyset===null)
		return;

	var keyset=doc.getElementById(KEYSET_ID);
	if(!(keyset===null))
		keyset.parentNode.removeChild(keyset);
	keyset=doc.createElement("keyset");
	keyset.setAttribute("id", KEYSET_ID);
	if(getPrefsService().getBoolPref("shortcutHomeEnabled") 
		&& null===doc.getElementById(HOME_ID+"-key")) {
		keyset.appendChild(createShortcut(getPrefsService().getCharPref("shortcutHome"), 
		function(){openUrl(1)}, HOME_ID+"-key", doc));
	}
	if(getPrefsService().getBoolPref("shortcutBookmarkEnabled") 
		&& null===doc.getElementById(BOOKMARK_ID+"-key")) { 
		keyset.appendChild(createShortcut(getPrefsService().getCharPref("shortcutBookmark"),
		function(){openUrl(2);}, BOOKMARK_ID+"-key", doc));
	}
	if(getPrefsService().getBoolPref("shortcutPublicationEnabled") 
		&& null===doc.getElementById(PUBLICATION_ID+"-key")) {
		keyset.appendChild(createShortcut(getPrefsService().getCharPref("shortcutPublication"), 
		function(){openUrl(3);}, PUBLICATION_ID+"-key", doc));
	}
	mainKeyset.parentNode.appendChild(keyset);
}

function getPrevToolbarItem(buttonId, toolbar) {
	
	if(toolbar === null)
		return false;
	let currentset = toolbar.getAttribute("currentset").split(",");
	for(var i = 0; i < currentset.length; i++)
		if(buttonId == currentset[i]) {
			if(i+1 < currentset.length) 
				for(var k = i+1; k < currentset.length; k++)
					for(var s = 0; s < toolbar.children.length; s++) {
						if(toolbar.children[s].id == currentset[k]) {
							return currentset[k];
						}
					}
			return null;
		}
	return false;
}

var listener = new PrefListener("extensions.bookmarklet.",  
	function(branch, name) {
		switch (name) {
			case "buttons":
				if(!branch.getBoolPref(name))
					getWindows().each(
						function(i, win){
							removeButtons(new Array(HOME_ID+"-button", BOOKMARK_ID+"-button", PUBLICATION_ID+"-button"),
							win.document, true);
						}
					);
				else getWindows().each(function(i, win){setUpButtons(win);});
				break;
			case "shortcutHomeEnabled":
			case "shortcutBookmarkEnabled":  
			case "shortcutPublicationEnabled":
			case "shortcutHome":
			case "shortcutBookmark":  
			case "shortcutPublication":
				getWindows().each(function(i, win){updateShortcuts(win);});
				break;  
		}
	}
);

listener.register(true);

function disable(removeFromPalette) {
	let windows=getWindows();
	let buttons=new Array(BOOKMARK_ID+"-button",PUBLICATION_ID+"-button",HOME_ID+"-button");
	for(var i=0;i<windows.length;i++) {
		removeShortcuts(windows[i]);
		removeButtons(buttons, windows[i].document, removeFromPalette);
	}
	listener.unregister();
	listener = null;
	getMediator().removeListener(windowListener);
}

var optionsObserver = {  
  observe: function(subject, topic, data) {  
    if (topic == "addon-options-displayed" && data == "bookmarklet@$PROJECT_DOMAIN$") {  
		shortcutHelper(subject);
    }  
  }
};

Services.obs.addObserver(optionsObserver, "addon-options-displayed", false);
