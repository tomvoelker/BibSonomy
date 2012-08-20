var bgPage = chrome.extension.getBackgroundPage();
var openHome = bgPage.openHome;
var savePost = bgPage.savePost;
var getCheckboxes = null;
function setOption(e) {localStorage[e.target.name] = e.target.checked;}

document.addEventListener('DOMContentLoaded', 
	function () {
		var optionsArea = document.getElementById("optionsArea");
		var cogwheel = document.getElementById("cogwheel");
		var homeButton = document.getElementById("homeButton");
		var saveButton = document.getElementById("saveButton");
		var bookmarkButton = document.getElementById("bookmarkButton")
		var publicationButton = document.getElementById("publicationButton");
		var optionsButton =	document.getElementById("optionsButton");
		var controlArea = document.getElementById("controlArea");
		var locales = document.getElementsByTagName("localestring");
		var checkboxes = document.querySelectorAll(".checkBox");
		var shortcutInputFields = new Array("shortcutHome","shortcutBookmark","shortcutPublication");
		var element;
		
		getCheckboxes = function(){return checkboxes;}
		reflectStoredSettings();
		
		for(var i = 0; locales.length > i; i++) 
			locales[i].innerHTML=chrome.i18n.getMessage(locales[i].innerHTML);
		
		optionsArea.style.display = "none";
		cogwheel.style.display = "none";

		homeButton.title = chrome.i18n.getMessage("homeButtonTitle");
		publicationButton.title = chrome.i18n.getMessage("publicationButtonTitle");
		bookmarkButton.title = chrome.i18n.getMessage("bookmarkButtonTitle");
		saveButton.value = chrome.i18n.getMessage("saveButtonLabel");
		optionsButton.title = chrome.i18n.getMessage("showOptionsLabel");

		for(var i = 0; i < shortcutInputFields.length; i++) {
			element = document.getElementById(shortcutInputFields[i]);
			element.onkeypress = keypressGrabber;
			element.onkeydown = keydownGrabber;
			element.onkeyup = keyupGrabber;
			element.value = localStorage[shortcutInputFields[i]]!=undefined?localStorage[shortcutInputFields[i]]:"";
		}
		
		homeButton.addEventListener('click', openHome);
		saveButton.addEventListener('click', saveOptions);
		bookmarkButton.addEventListener('click', function(){savePost(true);});
		publicationButton.addEventListener('click', function(){savePost(false);});
		optionsButton.addEventListener('click', 
				function(e){
					var visibility = "none";
					var indicator = "cogwheel";
					var optionsButtonClass = "spacer";
					optionsButton.title = chrome.i18n.getMessage("showOptionsLabel");
					
					if(optionsArea.style.display=="none") {
						visibility="";
						indicator="contract";
						optionsButtonClass="";
						reflectStoredSettings();
						optionsButton.title = chrome.i18n.getMessage("hideOptionsLabel");
					}
					controlArea.style.display=optionsArea.style.display;
					optionsArea.style.display=visibility;
					cogwheel.style.display=visibility;
					e.target.src="../images/"+indicator+".png";
					e.target.className=optionsButtonClass;
				}
		);
	}
);

function reflectStoredSettings() {
	var checkboxes = getCheckboxes();
	for(var i = 0; checkboxes.length > i; i++) {
		checkboxes[i].checked=false;
		if(localStorage[checkboxes[i].name]=="true")
			checkboxes[i].checked=true;
	}
}

function saveOptions() {
	var checkboxes = getCheckboxes();
	for(var i = 0; checkboxes.length > i; i++) {
		localStorage[checkboxes[i].name]="false";
		if(checkboxes[i].checked)
			localStorage[checkboxes[i].name]="true";
	}
}
