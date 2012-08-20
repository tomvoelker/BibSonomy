function shortcutHelper(doc) {
	var currentBrowser=getCurrentWindow().getBrowser();
	var prefManager = Components.classes["@mozilla.org/preferences-service;1"]
	.getService(Components.interfaces.nsIPrefBranch);
	var locale = Components.classes["@mozilla.org/chrome/chrome-registry;1"]
	.getService(Ci.nsIXULChromeRegistry).getSelectedLocale("global");
	var keyStrings = new Array("de", "en");

	keyStrings['en'] = new Array("CTRL", "ALT","SHIFT","BACKSPACE","TAB","DEL","ENTER","SPACE");
	keyStrings['de'] = new Array("STRG", "ALT","UMSCHALT","LÖSCHEN","TAB","ENTFERNEN","ENTER","SPACE");
	
	if(!(locale=="en"||locale=="de"))
		locale = "en";
	var getKeyStack = null;
	var getSavedKeyStack = null;
	var getSelectedInput;
	{
		var stack = new Array();
		var stackSavedKeys = new Array();
		getKeyStack = function() {
			return stack;
		};
		getSavedKeyStack = function(value) {
			if (value != undefined)
				stackSavedKeys = value;
			return stackSavedKeys;
		};
		var input;
		getSelectedInput = function(value) {if(value=!undefined) input=value; return input;};
	}
	
	function clearInput (e) {
		e.target.value=e.target.value;
		getSavedKeyStack().length=getKeyStack().length=0;
		
	}
	
	function setCursorToEnd(e) {
		var value=e.target.value;
		e.target.value="";
		e.target.value=value;
	}

	function getKeyString(keyCode) {
		return keyCode == 17 ? keyStrings[locale][0]
				: keyCode == 18 ? keyStrings[locale][1]
						: keyCode == 16 ? keyStrings[locale][2]
								: keyCode == 8 ? keyStrings[locale][3]
										: keyCode == 9 ? keyStrings[locale][4]
												: keyCode == 46 ? keyStrings[locale][5]
														: keyCode == 13 ? keyStrings[locale][6]
																: keyCode == 32 ? keyStrings[locale][7] : 
																		String.fromCharCode(keyCode);
	}

	function keyupGrabber(e) {
		e.preventDefault();
		var index;
		var lastItem;
		uglyRemoveAltKey(e);
		if ((index = getKeyStack().indexOf(e.keyCode)) == -1) {
			if(getKeyStack().length == 1)
				getKeyStack().pop();
			return !1;
		}
		
		if(getSavedKeyStack().length==0) {
				prefManager.setCharPref("extensions.bookmarklet."+e.target.id, (e.target.value=""));
				prefManager.setCharPref("extensions.bookmarklet."+e.target.id.replace("Input", ""), "");
				getKeyStack().length=0;
				return !1;
		}

		getKeyStack().splice(index, 1);
		var lastItem = getSavedKeyStack()[getSavedKeyStack().length-1];
		if(32 > lastItem || lastItem > 128)
			getSavedKeyStack().length=0;
			
		if (getKeyStack().length == 0) {
			if(getSavedKeyStack().length <= 1) 
				e.target.value="";
			getSavedKeyStack().length=0;
		}
		return !1;
	}
	
	function uglyRemoveAltKey(e) {
		if(!e.altKey&&(altIndex=getKeyStack().indexOf(18))!=-1) {
			getKeyStack().splice(altIndex, 1);	
		}
	}
	
	function renderInputString() {
		var inputString="";
		for ( var i = 0; i < getSavedKeyStack().length; i++)
			inputString += getKeyString(getSavedKeyStack()[i])
			+ (i == getSavedKeyStack().length - 1 ? " " : " + ");
		return inputString;
	}

	function keydownGrabber(e) {
		e.preventDefault();
		uglyRemoveAltKey(e);
		
		if (getKeyStack().indexOf(e.keyCode) > -1)
			return !1;
			
		var keySettingsString = "";

		getSavedKeyStack().length = 0;

		if ((!getKeyStack().length && !(15 < e.keyCode && e.keyCode < 19))
				|| (getKeyStack().length > 0 && getKeyStack().indexOf(16)
						+ getKeyStack().indexOf(17) + getKeyStack().indexOf(18) == -3)) {
			getKeyStack().length=0;
			getSavedKeyStack().length=0;
			e.target.value = "";
			return !1;
		}
		
		e.target.value = "";
		
		if(getKeyStack().length > 0 
		&& (32 < getKeyStack()[getKeyStack().length-1] && getKeyStack()[getKeyStack().length-1] < 128)) {
			getKeyStack().pop();
			getSavedKeyStack().pop();
		}
			
		getKeyStack().push(parseInt(e.keyCode));
		
		for(var i = 0; i < getKeyStack().length; i++) {
			getSavedKeyStack()[i] = getKeyStack()[i];
			keySettingsString+=(i>0?" ":"")+getKeyStack()[i];
			e.target.value += getKeyString(getKeyStack()[i])
					+ (i == getKeyStack().length - 1 ? " " : " + ");			
		}
		
		if(32 < getKeyStack()[getKeyStack().length-1] && getKeyStack()[getKeyStack().length-1] < 128) {
			prefManager.setCharPref("extensions.bookmarklet."+e.target.id, e.target.value);
			prefManager.setCharPref("extensions.bookmarklet."+e.target.id.replace("Input", ""), keySettingsString);
		}

		return !1;
	}
	
	function keypressGrabber(e) {
		e.preventDefault();
	}

	var shortcuts = new Array("shortcutHomeInput","shortcutBookmarkInput","shortcutPublicationInput");	
	var element = null;
	for(var i = 0; i < shortcuts.length; i++) {
		element = doc.getElementById(shortcuts[i]);
		element.onkeydown = keydownGrabber;
		element.onkeypress = keypressGrabber;
		element.onkeyup = keyupGrabber;
		element.onfocus = clearInput;
		element.onclick = setCursorToEnd;
		element.style.textAlign = "center";
		element.style.color = "#069";
		element.style.fontSize = "8pt";
		element.style.fontColor = "#069";
	}
}