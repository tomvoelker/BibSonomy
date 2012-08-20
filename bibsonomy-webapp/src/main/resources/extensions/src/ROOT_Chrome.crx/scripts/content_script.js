chrome.extension.onMessage.addListener(function(request, sender, sendResponse) {
	var response = {}
	if (request.message == "requestURLData")
		response = {
			url : window.location.href,
			selection : window.getSelection().toString(),
			title : window.document.title
		}
	sendResponse(response);
});

/*
 * TODO: use new api sometimes soon, when stable
 * 
 */
var getSavedKeysStack = null;
var shortcutHome = null;
var shortcutBookmark = null;
var shortcutPublication = null;
var options = new Array("Home", "Bookmark", "Publication");
var getKeys = null;

{
	var stackSavedKeys = new Array();
	var keyArray = new Array();
	getKeys = function() {return keyArray;};
	getSavedKeyStack = function(value) {if(value!=undefined) stackSavedKeys=value; return stackSavedKeys;};	

	for(var x = 0; x < options.length; x++) {
		chrome.extension.sendMessage({message:"getShortcut"+options[x]}, 
				function(response) {
					if(response.shortcut!=undefined) {
						getKeys().push(response.shortcut.replace(/ /g, "").split("+"));
					}
				});
	}
}

function getKeyString(keyCode) {
	return keyCode==17?chrome.i18n.getMessage("CTRL"):
		keyCode==18?chrome.i18n.getMessage("ALT"):
			keyCode==16?chrome.i18n.getMessage("SHIFT"):
				keyCode==8?chrome.i18n.getMessage("BACKSPACE"):
					keyCode==9?chrome.i18n.getMessage("TAB"):
						keyCode==46?chrome.i18n.getMessage("DEL"):
							keyCode==13?chrome.i18n.getMessage("ENTER"):
								keyCode==32?chrome.i18n.getMessage("SPACE"):
								String.fromCharCode(keyCode);
}

function keyupGrabber(e) {
	var index = -1;
	var key = getKeyString(e.keyCode);
	
	if ((index = getSavedKeyStack().indexOf(key))!=-1) 
		getSavedKeyStack().splice(index, 1);
	return true;
}
	
function keydownGrabber(e) {
	var key = getKeyString(e.keyCode);
	var action = null;
	
	if(getSavedKeyStack().indexOf(key)!=-1)
		return true;
	
	getSavedKeyStack().push(key);
	for(var x = 0; x < options.length; x++) {
		if(getKeys()[x]!=null && getKeys()[x].length == getSavedKeyStack().length) {
			for(var y = 0, similarKeys = 0; y < getKeys()[x].length; y++) {
				if(getKeys()[x][y]==getSavedKeyStack()[y]) similarKeys++;
			}
			if(similarKeys == getKeys()[x].length) {
				chrome.extension.sendMessage({message:"action"+options[x]}, function(){});break;
			}
		}
	}
	
	return true;
}

window.addEventListener('keydown', keydownGrabber, false);
window.addEventListener('keyup', keyupGrabber, false);