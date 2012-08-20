var PROJECT_URL="$PROJECT_HOME$";

function invokeAction(url, tab) {
	if(localStorage.openNewTab=="true")	chrome.tabs.create({'url':url});
	else chrome.tabs.update(tab.id, {'url':url});
}
function openHome(tab) {chrome.tabs.getSelected(null, function(tab) {invokeAction(PROJECT_URL+'/myBibSonomy', tab);});}
function savePost(saveBookmark) {
	chrome.tabs.getSelected(null, function(tab) {
		chrome.tabs.sendMessage(tab.id, {message: "requestURLData"}, function(response){
			invokeAction(PROJECT_URL+'/'+(saveBookmark?'ShowBookmarkEntry?':'BibtexHandler?requTask=upload&')+'selection='
			+encodeURIComponent(response.selection)
			+'&url='
			+encodeURIComponent(response.url)
			+'&referer='
			+encodeURIComponent(response.url)
			+'&description='
			+encodeURIComponent(response.title), 
			tab);
		  });
	});
}

chrome.extension.onMessage.addListener(function(request, sender, sendResponse) {
	switch(request.message) {
		case "actionHome":
			if(localStorage.shortcutHomeEnabled=="true") 
				openHome();
			break;
		case "actionBookmark":
			if(localStorage.shortcutBookmarkEnabled=="true")
				savePost(true);
			break;
		case "actionPublication":
			if(localStorage.shortcutPublicationEnabled=="true")
				savePost(false);
			break;
		case "shortcutHome":
			localStorage.shortcutHome=request.shortcut;
			break;
		case "shortcutBookmark":
			localStorage.shortcutBookmark=request.shortcut;
			break;
		case "shortcutPublication":
			localStorage.shortcutPublication=request.shortcut;
			break;
		case "getShortcutHome":
			sendResponse({shortcut:localStorage.shortcutHome});
			break;
		case "getShortcutBookmark":
			sendResponse({shortcut:localStorage.shortcutBookmark});
			break;
		case "getShortcutPublication":
			sendResponse({shortcut:localStorage.shortcutPublication});
			break;
	}
});
