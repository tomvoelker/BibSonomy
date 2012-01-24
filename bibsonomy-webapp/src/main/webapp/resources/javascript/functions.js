var activeField = null;
var tagbox      = null; // used in style.js!
var ckey        = null;
var currUser    = null;
var requUser	= null;
var projectName = null;
var pwd_id_postfix = "_form_copy";

/*
 * functions for cursor position
 */
var getPos = null;
var setPos = null;
var getSetPos = 0;


function init (tagbox_style, tagbox_sort, tagbox_minfreq, lrequUser, lcurrUser, lckey, lprojectName) {
	/*
	 * assign functions for cursor position
	 */
	setPos = function(p) {
		if (p == null)
			return getSetPos;
		return (getSetPos = p);
	};
	getPos = function() {
		return setPos(null);
	};

	/*
	 * adds hints for text input fields
	 */
	add_hints();

	// add a callback to every input that has the descriptiveLabel class 
	// so that hints get removed after focussing the input form
	$('.descriptiveLabel').each(function(){$(this).descrInputLabel({});});

	tagbox  = document.getElementById("tagbox");
	ckey = lckey;
	currUser = lcurrUser;

	if(lrequUser != "") {
		requUser = lrequUser;
	}

	projectName = lprojectName;

	if (tagbox) {
		init_tagbox(tagbox_style, tagbox_sort, tagbox_minfreq, lrequUser);
	}

	//FIXME: use some other condition, that does not depend on a location's name
	var pathname = location.pathname;
	if (!pathname.startsWith("/postPublication") && !pathname.startsWith("/postBookmark")){
		init_sidebar();
	}
}

function stopEvt () {
	return false;
}

function init_sidebar() {
	$("#sidebar li .sidebar_h").each(function(index){
		var span = $("<span class='toggler'><img src='/resources/image/icon_collapse.png'/></span>");
		span.click(hideNextList);
		$(this).prepend(span); 
	});
}

/*
 * hide/unhide the next list following the element which called the method
 */
function hideNextList() {
	var node = this.parentNode;

	while (node && node.nodeName.toUpperCase() != "UL") {
		node = node.nextSibling;
	}

	if (node) {
		/*
		 * decide, if the list must be hidden or shown
		 */
		var b = (node.style.display != "none");
		/*
		 * show/hide
		 */
		node.style.display = b ? "none" : "block";
		this.firstChild.src = "/resources/image/icon_" + (b ? "expand" : "collapse") + ".png";
	}
}

/**
 * Ask the user if he/she really wants to delete the post.
 * 
 * @return
 */
function confirmDelete() {
	// the post's list element
	var li = $(this).parents(".bm");
	// highlight post
	li.css("background", "#fdd");
	// get confirmation
	var del = confirm(getString("post.meta.delete.confirm"));
	// remove background color
	li.css("background", "transparent");
	return del;
}

/**
 * if window is small, maximizes the "general" div to 95%
 * 
 * @param id
 * @return
 */
function maximizeById(id) {
	if (window.innerWidth < 1200) {
		$("#" + id).css("width", "95%");
	}
}

/** 
 * 	prepare a text form which we'll use to switch between
 * 	password and text form to circumvent an issue caused
 * 	by IE's security policy
 * 
 * @param el 
 * 		password/text form element we'd like to switch 
 * @return 
 * 		password/text corresponding form element
 **/
function getFormTextCopy(el) {
	return $('#'+el.id+pwd_id_postfix).
	css('color','#aaa').
	width( $(el).width() ).
	click(function(){hideFormTextCopy({elementCopy:'#'+el.id+pwd_id_postfix, element:el});})[0];
}

//on blur of the user name input field set the password form in front of the fake password form
function hideFormTextCopy(map) {
	$(map.elementCopy).hide();
	$(map.element).removeClass('hiddenElement').focus();
}

//adds hints to input fields
function add_hints() {
	// for search input field
	var el = document.getElementById("se");
	if (validElement(el) && (el.value == "" || el.value == getString("navi.search.hint"))) {
		// add hint
		el.value		= getString("navi.search.hint");
		el.className 	= 'descriptiveLabel '+el.className;
	}
	// for username input field
	el = document.getElementById("un");
	if (validElement(el, 'input') && el.name == "username") {
		if(el.value == "" || el.value == getString("navi.username")) {
			el.value 		= getString("navi.username");
			el.className 	= 'descriptiveLabel '+el.className;
		}
		el.onblur = function(){hideFormTextCopy({elementCopy:'#pw'+pwd_id_postfix, element:'#pw'})};
	}
	// for password input field
	el = document.getElementById("pw");
	if (validElement(el, 'input') && el.name == "password") {
		el = getFormTextCopy(el);
		el.value = getString("navi.password");
	}
	// for username ldap input field
	el = document.getElementById("unldap");
	if (validElement(el, 'input') && el.name == "username") {
		if(el.value == "" || el.value == getString("navi.username.ldap")) {
			el.value = getString("navi.username.ldap");
			el.className = 'descriptiveLabel '+el.className;
		}
		el.onblur = function(){hideFormTextCopy({elementCopy:'#pwldap'+pwd_id_postfix, element:'#pwldap'})};
	}
	// for password ldap input field
	el = document.getElementById("pwldap");
	if (validElement(el, 'input') && el.name == "password" && (el.value == "" || el.value == getString("navi.password.ldap"))) {
		el = getFormTextCopy(el);
		el.value = getString("navi.password.ldap");
	}
	// for openid input field
	el = document.getElementById("openID");
	if (validElement(el, 'input') && el.name == "openID" && (el.value == "" || el.value == getString("navi.openid"))) {
		el.value = getString("navi.openid");
		el.className = 'descriptiveLabel '+el.className;
	}
	// for tag input field
	el = document.getElementById("inpf");
	if (validElement(el, 'input') && (el.name == "tag" || el.name == "tags") && (el.value == "" || el.value == getString("navi.tag.hint"))) {
		el.value = getString("navi.tag.hint");
		el.className = 'descriptiveLabel '+el.className;
	}
	// specialsearch (tag, user, group, author, relation)
	var scope = null;
	if (validElement(el, 'input') && el.name == 'search' && validElement((scope = document.getElementById("scope")), 'select')) {
		$(scope).bind("change", function(){setSearchInputLabel(this);}).trigger('change');
	}
}

/**
 * check if element's valid and optionally the 
 * element type for validity (if tagName is provided that is)
 * 
 * @param element
 *            the element to check
 * @param tagName
 *            tagName of the element e.g. 'input' or 'div'
 * @return true if element's valid
 */
function validElement(el, tagName) {
	return (el != null && (!tagName || el.tagName.toUpperCase() == tagName.toUpperCase()));
}


/**
 * Clears #inpf if it's value is equal to the tag hint.
 * 
 * @return
 */
function clear_tags() {
	var tag = $("#inpf");
	if (tag.val() == getString("navi.tag.hint")) {tag.val('');}
}

/**
 * toggle background color for required publication fields
 * 
 * @return
 */
function toggle_required_author_editor () {
	if (document.post_bibtex.author.value.search(/^\s*$/) == -1) {
		/* author field not empty */
		document.post_bibtex.editor.style.backgroundColor = "#ffffff";
		document.post_bibtex.author.style.backgroundColor = document.post_bibtex.title.style.backgroundColor;
	} else {
		/* author field empty */
		if (document.post_bibtex.editor.value.search(/^\s*$/) == -1) {
			/* editor not empty */
			document.post_bibtex.author.style.backgroundColor = "#ffffff";
			document.post_bibtex.editor.style.backgroundColor = document.post_bibtex.title.style.backgroundColor;
		} else {
			/* both empty */
			document.post_bibtex.author.style.backgroundColor = document.post_bibtex.title.style.backgroundColor;    
			document.post_bibtex.editor.style.backgroundColor = document.post_bibtex.title.style.backgroundColor;      
		}
	}
}

/**
 * Set the current input field to the given id and clear the suggestions.
 * 
 * @param id
 * @return
 */
function setActiveInputField(id) {
	activeField = id;
	$("#suggested").empty();
}


/**
 * Called on /edit_tags to make the tags from the sidebar clickable such that
 * on a click they are added to the active input field.  
 *
 * FIXME: click handler not bound :-(
 * observation during debugging: tags are sorted AFTER this method - so maybe 
 * it gets broken there? 
 * 
 * @return
 */
function add_tags_toggle() {
	$("#tagbox li a").each(function() {
		$(this).click(function() {
//			alert("t");
//			var v = xget_event(event).childNodes[0].nodeValue;
//			var v = this.childNodes[0].nodeValue;
//			alert("toggle " + v);

			clear_tags(); // remove getString("navi.tag.hint") 

//			toggleTag(
//			document.getElementById(activeField ? activeField : "inpf"), 
//			v
//			);
		})
		.removeAttr("href")
		.css("cursor", "pointer");
	});

}

/**
 * 
 * clickable relations for edit_tags
 * 
 * @return
 */
function add_toggle_relations() {
	/*
	 * add toggler for supertags
	 */
	$("#relations > li > a").each(function() {
		$(this).click(function() {
			var value = this.childNodes[0].nodeValue;
			$("#delete_up").val(value);
			$("#insert_up").val(value);
		})
		.css("cursor", "pointer")
		.attr("title", "add as supertag") // FIXME: I18N
		.removeAttr("href");
	});
	/*
	 * add toggler for subtags
	 */
	$("#relations > li ul li a").each(function() {
		$(this).click(function() {
			var delete_lo = $("#delete_lo");
			delete_lo.val(addIfNotContained(delete_lo.val(), this.childNodes[0].nodeValue.replace(/ /, "")));
			delete_lo.focus();
		})
		.css("cursor", "pointer")
		.attr("title", "add as subtag") // FIXME: I18N
		.removeAttr("href");
	});

}


/* returns the node of an event */
function xget_event (event) {
	if (!event) event = window.event;
	if (event.srcElement) {
		// Internet Explorer
		return event.srcElement;
	} else if (event.target) {
		// Netscape and Firefox
		return event.target;
	}
}
/*
 * FIXME: still necessary?
 */
function checkBrowser() {
	if (navigator.appName.indexOf("Opera") != -1)	{
		return "opera";
	} else if (navigator.appName.indexOf("Explorer") != -1)	{
		return "ie";
	} else if (navigator.appName.indexOf("Netscape") != -1)	{
		return "ns";
	} 
	return "undefined";
} 

var maxTagFreq = 0;                    // maximal tag frequency in tag cloud
var list = new Array();
var listElements = new Array();
var nodeList = new Array();
var copyListElements = new Array();
var copyList = new Array();
var savTag = "";
var activeTag = "";
var sortedCollection;
var collect;
var copyCollect;

//38,39,40 von 86

function enableHandler() {
	document.onkeydown = document.onkeypress = document.onkeyup = handler;
}

function disableHandler() {
	if (checkBrowser() == "ie" || checkBrowser() == "opera") {
		document.onkeydown = document.onkeypress = document.onkeyup;
	} else {
		document.onkeydown = document.onkeypress = document.onkeyup = disHandler;
	}
}

function Suggestion(tagname, wighting)	{
	this.tagname = tagname;
	this.wighting = wighting;
}

function disHandler(event) {	}

function handler(event) {
	var inputValue = document.getElementById(activeField ? activeField : "inpf").value;

	var e = (event || window.event || event.shiftkey);

	if (e.type == 'keyup') {
		switch (e.keyCode) {
		case 8: {	//backspace
			if(sortedCollection) {
				delete sortedCollection;
				sortedCollection = new Array();
			}

			if(inputValue == "") {
				savTag = "";
				activeTag = "";
			}
			else {
				getActiveTag(true);
				clearSuggestion();
				if(activeTag != "")	
					suggest();
			}
			break;
		}
		case 9:	{	//tab
			if(inputValue != "" && activeTag) {
				clearSuggestion();
				completeTag();
				activeTag = "";
			}
			if(sortedCollection) {
				delete sortedCollection;
				sortedCollection = new Array();
			}
			break;
		}
		case 38: // up
		case 40: {	break;	}	//down
		case 35: //end
		case 36: //home
		case 37: // left
		case 39: // right
		case 32: {	// space
			if(sortedCollection) {
				delete sortedCollection;
				sortedCollection = new Array();
			}
			clearSuggestion();
			break;
		}
		case 13: {	//enter
			getActiveTag(false);
			if(activeTag != "") {
				clearSuggestion();
				completeTag();
			}
			break;
		}
		default: {
			getActiveTag(false);
			clearSuggestion();
			if(activeTag != "")	
				suggest();
			break;
		}
		}
	}
	else if(e.type == "keypress") {
		switch(e.keyCode) {
		case 9: { 

			if(inputValue != "" && activeTag && e.preventDefault()) {
				e.preventDefault();
			}

			break;
		}
		case 8: {	//backspace
			clearSuggestion();
			savTag = getTags(inputValue);
			break;
		}
		case 38:
		case 40: {
			if(e.preventDefault && e.originalTarget)
				e.preventDefault();
			break;
		}
		default: {
			savTag = getTags(inputValue);
			break;
		}
		}
	}
	else if(e.type == "keydown") {
		switch(e.keyCode) {
		case 8: {	//backspace
			clearSuggestion();
			savTag = getTags(inputValue);
			break;
		}
		case 38: { // oben
			if(inputValue != "") {
				if(getPos() < sortedCollection.length - 1 && getPos() < 2)	
					setPos(getPos()+1);
				else
					setPos(0);

				clearSuggestion();
				addToggleChild(sortedCollection);
			}
			break;
		}
		case 40: { // unten
			if(inputValue != "") {
				if(getPos() > 0)
					setPos(getPos()-1);
				else {
					if(sortedCollection.length < 3)
						setPos(sortedCollection.length - 1);
					else
						setPos(2);
				}

				clearSuggestion();
				addToggleChild(sortedCollection);
			}
			break;
		}
		default: {
			savTag = getTags(inputValue);
			break;
		}
		}
	}
}

function switchField(source, target) {
	document.getElementById(activeTag == "" ? target  : source).focus();
}

function deleteCache() {
	clearSuggestion();
}

/**
 * Parses tag clound from json text into list of objects, each containing 
 * 'label' and 'count'. 
 * 
 * For using the old suggestion functions we update global variables:
 *    - array 'list' containing the tag names
 *    - "associative" array 'nodeList' containing objects O with attributes
 *      O.title = tagFrequency (as String), O.count = tagFrequency
 *   
 * @return result list
 */
function populateSuggestionsFromJSON(json) {
	// if there's no data we don't need to proceed
	if (json.items.length == 0)
		return;
	var tagCloud = json.items;

	// construct array from object such that sorting functions can be applied 
	var tagList = [];
	for( i in tagCloud ) {
		tagList.push(tagCloud[i]);
	}
	// now sort this array
	tagList.sort(tagCompare);

	// set global tag list, storing maximal tag frequency in maxTagFreq
	for( var i=0; i<tagList.length; i++ ) {
		var label = tagList[i].label;
		var count = tagList[i].count;
		if(count>maxTagFreq) 
			maxTagFreq = count;

		list.push(label);
		var node = new Object;
		node.title=count+" ";
		node.count=count;
		nodeList[label] = node;
	}

	// all done.
	return tagList;
}

/**
 * Append recommended tags to list of potential tag suggestions.
 *  
 * Input: list of objects with attributes:
 *    - title = tag weight
 *    - label = tagName
 *    - score, confidence
 * 
 * For using the old suggestion functions we update global variables:
 *    - array 'list' containing the tag names
 *    - "associative" array 'nodeList' containing objects O with attributes
 *      O.title = tagFrequency (as String), O.count = tagFrequency
 *   
 * @return result list
 */
function populateSuggestionsFromRecommendations(tagList) {
	// update global tag list
	for( var i=0; i<tagList.length; i++ ) {
		var label = tagList[i].label.replace(/^\s+|\s+$/g, '').replace(/ /g,"_");;
		list.push(label);

		var node = tagList[i];
		node.title = Math.ceil(node.score*(maxTagFreq/2)+(maxTagFreq/2))+ " ";
		nodeList[label] = tagList[i];
	}
	// sort the list
	list.sort(stringCompare);
}

/**
 * Compares two tag cloud entries.
 * 
 * @param a object containing properties 'label' and 'count'
 * @param b object containing properties 'label' and 'count'
 * @return -1 if a.label<b.label, 0 if a.label=b.label, 1 if a.label>b.label
 */
function tagCompare(a, b) {
	return stringCompare(a.label, b.label);
}

/**
 * Compares two strings, ignoring case.
 * 
 * @param a string
 * @param b string
 * @return -1 if a < label, 0 if a=b, 1 if a>b
 */
function stringCompare(a, b) {
	if (a.toLowerCase() < b.toLowerCase())
		return -1;
	else if (a.toLowerCase() == b.toLowerCase())
		return 0;
	else
		return 1;
}	

/**
 * Hier werden die Tags aus der Tagwolke, Copytags und Recommendations in Listen gepackt
 * 
 * @return
 */
function setOps() {

	var rows = document.getElementById("tagbox").getElementsByTagName("li");
	for (var i = 0; i < rows.length; ++i) {
		var a = rows[i].getElementsByTagName("a");
		var tmp = a[a.length < 2 ? 0 : 2];
		var tmpData = tmp.firstChild.data.trim();
		listElements[tmpData] = i;
		nodeList[tmpData] = tmp;
		list.push(tmpData);
	}

	var recommendedTag = document.getElementById("recommendtag");
	if (recommendedTag) {
		var recommRows = recommendedTag.getElementsByTagName("li");
		for (var i = 0; i < recommRows.length; ++i) {
			var tmp = recommRows[i].getElementsByTagName("a")[0];
			var tmpData = tmp.firstChild.data;
			listElements[tmpData] = rows.length + i;
			nodeList[tmpData] = tmp;
			list.push(tmpData.trim());
		}
	}


	var copyTag = document.getElementById("copytag");
	if (copyTag) {
		var copyRows = copyTag.getElementsByTagName("li");
		for (var i = 0; i < copyRows.length; ++i) {
			copyListElements[copyRows[i].firstChild.data] = i;
			copyList[copyRows[i].firstChild.data] = copyRows[i];
		}
	}

	list.sort(unicodeCollation);		
}

/*
 * Gibt eine Liste aus Tags zurück. Bei Relationen werden die Tags gesplittet.
 */

function getTags(s) {
	var tmpInput = s.split(" ");
	var input = new Array();

	if (s.match(/->/) || s.match(/<-/)) {
		for (i in tmpInput) {
			if(tmpInput[i].match(/->/)) {
				var parts = tmpInput[i].split("->");
				input.push(parts[0]);
				input.push(parts[1]);
			} else if(tmpInput[i].match(/<-/)) {
				var parts = tmpInput[i].split("<-");
				input.push(parts[0]);
				input.push(parts[1]);
			} else {
				input.push(tmpInput[i]);
			}
		}
	} else {
		input = tmpInput;
	}		

	return input;

}

/*
 *  Findet das Wort, welches gerade editiert wird
 */

function getActiveTag(backspace) {
	var input = getTags(document.getElementById(activeField ? activeField : "inpf").value);

	for (var n in input) {
		if (typeof savTag != "undefined") {
			if (input[n] > savTag[n] && !backspace) {
				activeTag = input[n];
				break;
			} else if(input[n] < savTag[n] && backspace && input[n] > "") {
				activeTag = input[n];
				break;
			} else {
				activeTag = "";
			}
		}
	}
	delete input;
}

/*
 * Vorschlagsfunktion
 */

function suggest() {
	delete collect;
	if(sortedCollection)
		delete sortedCollection;
	delete copyCollect;
	collect = new Array();
	sortedCollection = new Array();
	copyCollect = new Array();
	var searchString = activeTag.toLowerCase();
	var searchLength = searchString.length;
	var success = false;
	var counter = 0;
	var firstElement = 0;
	var lastElement = list.length - 1;
	var midElement = 0;

	/*
	 * if the following lines are activated, it's allowed to post duplicates in relations
	 */
	var tags = document.getElementById(activeField ? activeField : "inpf").value.toLowerCase().split(" ");

	/*
	 * if the following lines are activated, it's not allowed to post duplicates in relations.
	 * var tagString = activeField ? document.getElementById(activeField).value.toLowerCase()
	 *  					       : document.getElementById("inpf").value.toLowerCase();
	 * var tags = getTags(tagString);
	 */

	setPos(0);

	/*
	 * Bin?re Suche ?ber die Listenelemente, in denen die Tags stehen
	 */
	while(!success && firstElement <= lastElement) {
		midElement = Math.floor((firstElement + lastElement) / 2);
		if(list[midElement].substring(0,searchLength).toLowerCase() == searchString) {
			var i = midElement - 1;
			var j = midElement + 1;
			while(i >= 0 && list[i].substring(0,searchLength).toLowerCase() == searchString) {
				collect.push(new Suggestion(list[i], nodeList[list[i]].title.split(" ")[0]));
				i--;
			}
			while(j <= list.length - 1 && list[j].substring(0,searchLength).toLowerCase() == searchString) {
				collect.push(new Suggestion(list[j], nodeList[list[j]].title));
				j++;
			}
			collect.push(new Suggestion(list[midElement], nodeList[list[midElement]].title.split(" ")[0]));
			success == true;
			break;
		} 
		else if(list[midElement].substring(0,searchLength).toLowerCase() > searchString)
			lastElement = midElement - 1;
		else
			firstElement = midElement + 1;
	}
	collect.sort(byWight);

	if(document.getElementById("copytags") != null) {
		copyTag = document.getElementById("copytags");
		copyRows = copyTag.getElementsByTagName("a");
		for(var i = 0; i < copyRows.length; ++i) {
			copyListElements[copyRows[i].firstChild.data.replace(/^\s+/, '').replace(/\s+$/, '')] = i;
			copyList[copyRows[i].firstChild.data.replace(/^\s+/, '').replace(/\s+$/, '')] = copyRows[i];
		}
	}

	/*	collects suggested entrys inside copytag elemens	*/
	for(copyTag in copyListElements) {
		var duplicate = false;
		var tmpTag = "";
		valid = true;
		if(searchLength <= copyTag.length)
			tmpTag = copyTag.substring(0,searchLength).toLowerCase();
		else
			valid = false;

		if(tmpTag.match(activeTag) && valid) {
			for(var z in tags) {
				duplicate = false;

				if(copyTag.toLowerCase() == tags[z].toLowerCase()) {
					duplicate = true;
					break;
				}
			}
			if(!duplicate)
				copyCollect.push(copyTag);
		}
		i++;
	}

	for(var i in copyCollect)
		sortedCollection.push(copyCollect[i]);

	for(var i in collect) {
		for(var z in tags) {
			duplicate = false;
			if(collect[i].tagname.toLowerCase() == tags[z].toLowerCase()) {
				duplicate = true;
				break;
			}
		}
		var j = 0;
		while(j < sortedCollection.length && !duplicate) {
			if(collect[i].tagname.toLowerCase() == sortedCollection[j].toLowerCase()) {
				duplicate = true;
				break;
			}
			j++;
		}				
		if(!duplicate) {
			sortedCollection.push(collect[i].tagname);
		}
	}

	addToggleChild(sortedCollection);
}

/*
 * Sortiert 2 Tags nach Gewichtung
 */

function byWight(a, b) {
	if(b.wighting == a.wighting) {
		if(b.tagname < a.tagname)
			return -1;
		else if(b.tagname > a.tagname)
			return 1;
		else
			return 0;
	}
	else
		return b.wighting - a.wighting;
}

/*	adds list elements to the suggested list	*/
function addToggleChild(sortedCollection) {
	var sg = document.getElementById("suggested");

	for(var i in sortedCollection) {
		var newTag = document.createElement("a");
		sg.appendChild(document.createTextNode(" "));
		newTag.className = "tagone";
		newTag.appendChild(document.createTextNode(sortedCollection[i]));
		newTag.removeAttribute("href");
		newTag.style.cursor = "pointer";
		newTag.setAttribute('href','javascript:completeTag("'+sortedCollection[i].replace(/"/g,'\\"')+'")');

		if(i == getPos()) {
			newTag.style.color = "white";
			newTag.style.backgroundColor = "#006699";
		}

		if(i == 3)
			break;

		sg.appendChild(newTag);
	}
}

function clearSuggestion() {
	// remove selection
	$("#copytag a").css("color", "").css("backgroundColor", "");

	// remove all child nodes
	$("#suggested").empty();
}


function getRelations(input) {
	var relList = new Array();

	for (var i in input) {
		if (input[i].match(/->/)) {
			relList.push(1);
			relList.push(1);
		} else if (input[i].match(/<-/)) {
			relList.push(2);
			relList.push(2);
		} else
			relList.push(0);
	}

	return relList;
}

/*	completes the inquiry	
 *	tab -> sortedCollection[0]
 *	mouseclick -> parameter value (tag)
 */
function completeTag(tag) {
	var inpf = document.getElementById(activeField ? activeField : "inpf");
	var inpfValue = inpf.value;

	var tags = getTags(inpfValue);
	var val_tags = getTags(inpfValue.toLowerCase());
	var relList = getRelations(inpfValue.split(" "));
	var tmpTag = "";
	var mergedList = new Array();
	var counter = 0;
	var reset = false;
	var relation = false;



	for(var i in val_tags) {
		if(val_tags[i] == activeTag.toLowerCase()) {
			if(tag) {
				reset = true;
				tags[i] = tag + " ";
				break;
			}
			else if(sortedCollection) {
				if(sortedCollection[getPos()] != "") {
					// tag found in collection
					reset = true;
					var tag = sortedCollection[getPos()];
					tags[i] =  tag + " ";
					//
					// 2009/03/30, fei: treat tag completion as mouseclick
					//                  (that way we get this information via
					//                   clicklog)
					// FIXME: relations not tested!
					var target = lookupRecommendedTag(tag);
					// send clicklog-event
					if (target != null)
						simulateClick(target);
				}
				if(!sortedCollection[getPos()]) {
					reset = false;
					break;
				}
			}
		}
	}
	if(reset) {
		for(var i = 0; i < tags.length; i++) {
			relation = false;

			if(relList[i] == 1) {
				tmpTag = tags[i] + "->" + tags[i+1];
				i++;
			} else if(relList[i] == 2) {
				tmpTag = tags[i] + "<-" + tags[i+1];
				i++;
			} else {
				tmpTag = tags[i];
			}

			mergedList.push(tmpTag);
		}
		inpf.value = mergedList.join(" ");

		delete mergedList;
	}

	activeTag = "";
	clearSuggestion();

	inpf.focus();

	if(window.opera)
		inpf.select(); 

	inpf.value = inpf.value;

}

/**
 * @returns The link from the tag field, if the given tag name was recommended.
 */
function lookupRecommendedTag(tag) {
	var tagName = tag.replace(/^\s+|\s+$/g, '');

	var links = $("#tagField a").get();
	for (var i = 0; i < links.length; i++) {
		if (tagName == links[i].firstChild.nodeValue.replace(/^\s+|\s+$/g, '')) {
			return links[i];
		}
	}
	return null;
}

function simulateClick(target) {
	var evt;
	if (document.createEvent) {
		evt = document.createEvent("MouseEvents");
		if (evt.initMouseEvent) {
			evt.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
		} else {
			evt = false;
		}
	}
	(evt)? target.dispatchEvent(evt):(target.click && target.click());
} 

function unicodeCollation(ersterWert, zweiterWert){
	var result;
	if(isNaN(ersterWert) == false && isNaN(zweiterWert) == false){
		result = ersterWert - zweiterWert;// vergleich von 2 Zahlen
	}else if(isNaN(ersterWert) == false && isNaN(zweiterWert) == true){
		result = -1;// vergleich erster Wert ist eine Zahl und zweiter Wert ist ein String
	}else if(isNaN(zweiterWert) == false && isNaN(ersterWert) == true){
		result = 1;// vergleich zweiter Wert ist eine Zahl und erster Wert ist ein String
	}else if(ersterWert.toLowerCase() < zweiterWert.toLowerCase()){
		result = -1;// vergleich zweier Strings
	}else if(zweiterWert.toLowerCase() < ersterWert.toLowerCase()){
		result = 1;// vergleich zweier Strings
	}else if(zweiterWert.toLowerCase() == ersterWert.toLowerCase()){
		// vergleiche zwei gleiche Strings(im toLower Fall)
		if(ersterWert < zweiterWert){
			result = -1;
		}else if(zweiterWert < ersterWert){
			result = 1;
		}else{
			result = 0;
		}
	}else{
		result = 0;
	}
	return result;
}

// removes a relation from the list of shown relations
function hideConcept(evt) {
    var link = xget_event(evt);
	// get concept name
    var concept = link.parentNode.getElementsByTagName("a")[1].firstChild.nodeValue;
    // update relations list, hide concept
    updateRelations(evt, "hide", concept);
} 

/**
 * updates the user's relations in the sidebar
 * 
 * @param evt
 * @param action
 * @param concept
 * @return
 */
function updateRelations (evt, action, concept) {
	$.ajax({
		url : "/ajax/pickUnpickConcept?action=" + action + "&tag=" + encodeURIComponent(concept) + "&ckey=" + ckey,
		success : ajax_updateRelations,
		dataType : "xml"
	});
	breakEvent(evt);
} 


/*
 * updates the list of relations
 */
function ajax_updateRelations(data) {
	// remove all relations from list
	$("#relations").empty();

	// parse XML input
	var xml = data.documentElement; 

	if (xml) {
		// get all relations
		var requestrelations = xml.getElementsByTagName("relation");

		// iterate over the relations
		for(x=0; x<requestrelations.length; x++){       		    
			// one relation
			var rel = requestrelations[x];		    
			// the upper tag of the relation
			var upper = rel.getElementsByTagName("upper")[0].firstChild.nodeValue;

			// new list item for this super tag
			var rel_item = $(
					"<li class='box_upperconcept'>" + 
					"<a onclick='hideConcept(event)' href='/ajax/pickUnpickConcept?action=hide&tag=" + encodeURIComponent(upper) + "&ckey=" + ckey + "'>" + String.fromCharCode(8595) + "</a> " +
					"<a href='/concept/user/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(upper) + "'>" + upper + "</a>" +
					" " + String.fromCharCode(8592) + " " +
					"</li>"
			);

			// add subtags
			var lowerul = $("<ul id='" + upper + "' class='box_lowerconcept_elements'></ul>");

			// iterate over lower tags
			var lowers = rel.getElementsByTagName("lower");
			for (y = 0; y < lowers.length; y++) {
				var lower = lowers[y].firstChild.nodeValue;
				// add item
				lowerul.append("<li class='box_lowerconcept'><a href'/user/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(lower) + "'>" + lower + "</a> </li>");
			}

			// append list of lower tags to supertag item
			rel_item.append(lowerul);

			// insert relations_list for this supertag
			$("#relations").append(rel_item);
		}
	}
} 



function pickAll(evt) {
	pickUnpickAll(evt, "pickAll");
}

function unpickAll(evt) {
	pickUnpickAll(evt, "unpickAll");
}

function pickUnpickAll(evt, pickUnpick) {
	var param  = "";
	$("#bibtex li div.bmtitle a").each(function(index) {
		var href = $(this).attr("href");
		if (!href.match(/^.*\/documents[\/?].*/)){
			param += href.replace(/^.*bibtex./, "") + " ";
		}
	}
	);
	updateCollector("action=" + pickUnpick + "&hash=" + encodeURIComponent(param));

	breakEvent(evt);    	
}    

function breakEvent(evt) {
	// break link
	if (evt.stopPropagation) {
		evt.stopPropagation();
		evt.preventDefault();
	} else if (window.event){
		window.event.cancelBubble = true;
		window.event.returnValue = false;
	}
}

//this picks or unpicks a publication
function pickUnpickPublication(evt){
	/*
	 * pick/unpick publication
	 */
	updateCollector(xget_event(evt).getAttribute("href").replace(/^.*?\?/, ""));

	/*
	 * decide which page will be processed
	 * -> on the /basket page we have to remove the listitems
	 * -> on other we have to change the pick <-> unpick link (not yet implemented)
	 */
	if (location.pathname.startsWith("/basket")) {
		$(evt.currentTarget.parentNode.parentNode.parentNode).remove(); // XXX: !NEW_LAYOUT! depends on DOM tree

		document.getElementById("ttlctr").childNodes[0].nodeValue = "(" + document.getElementById("pickctr").childNodes[0].nodeValue + ")";
	}

	breakEvent(evt);
}



//picks/unpicks publications in AJAX style
function updateCollector (param) {
	$.ajax({
		type: 'POST',
		url: "/ajax/pickUnpickPost?ckey=" + ckey,
		data : param,
		dataType : "text",
		success: function(data) {
		/*
		 * update the number of basket items
		 */
		if (location.pathname.startsWith("/basket")) {
			// special case for the /basket page
			window.location.reload();
		} else {
			document.getElementById("pickctr").childNodes[0].nodeValue = data; 
		}

	}
	});
} 

/*
 * 
 */
function sendEditTags(obj, type, ckey, link) {
	var tags = obj.childNodes[0].value;
	var hash = obj.childNodes[0].name;
	var ckey = obj.childNodes[1].value;
	var targetChild = 0;

	$.ajax( {
		type :"POST",
		url :"/batchEdit?newTags['" + hash + "']=" + encodeURIComponent(tags.trim())
		+ "&ckey=" + ckey
		+ "&deleteCheckedPosts=true"
		+ "&resourcetype=" + type
		+ "&format=ajax",
		dataType :"html",
		global :"false",
		success : function(data) {
		var parent = obj.parentNode;
		$(obj).parent().children(".help").remove();

		if (data.trim().length > 0) {
			if (type == "bibtex") {
				$(obj).parent().children(':first').css({'float':'left'});
			}

			$(obj).before(data);
			return false;
		}

		if (type == "bibtex") {
			$(obj).parent().children(':first').css({'float':''});
			targetChild = 2;
			parent.removeChild(parent.childNodes[targetChild]);
			parent.removeChild(parent.childNodes[targetChild]);
		} else {
			parent.removeChild(parent.firstChild);
			parent.removeChild(parent.firstChild);
		}

		var edit = document.createElement("a");
		edit.setAttribute("onclick", "editTags(this, '" + ckey + "'); return false;");
		edit.setAttribute("tags", tags.trim());
		edit.setAttribute("href", link);
		edit.setAttribute("name", hash);
		edit.appendChild(document.createTextNode(getString("post.meta.edit")));

		parent.insertBefore(edit, parent.childNodes[targetChild]);
		parent = parent.parentNode.previousSibling.childNodes[1];

		while (parent.hasChildNodes()) {
			parent.removeChild(parent.firstChild);
		}

		var tagList = tags.split(" ");

		for (i in tagList) {
			var tag = document.createElement("a");
			tag.setAttribute("href", "/user/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(tagList[i]));
			tag.appendChild(document.createTextNode(tagList[i] + " "));
			parent.appendChild(tag);
		}
	}
	});

	return false;
}



/*
 * edit tags in place
 */	
function editTags(obj, ckey) {
	var tags = obj.getAttribute("tags");
	var link = obj.getAttribute("href");
	var hash = obj.getAttribute("name");
	var targetChild = 0;
	var parent = obj.parentNode;
	var type = "bookmark";

	if (link.search(/^\/editPublication/) != -1)	{
		type = "bibtex";
		targetChild = 2;
		parent.removeChild(parent.childNodes[targetChild]);
	}

	// remove the other childnodes
	// FIXME: this is a bad heuristic!
	parent.removeChild(parent.childNodes[targetChild]);

	// creates Form Element
	var form = document.createElement("form");
	form.className = "tagtextfield";
	form.setAttribute('onsubmit', 'sendEditTags(this, \'' 
			+ type + '\',  \'' + ckey + '\', '
			+ '\'' + link + '\'); return false;');

	// creates an input Field
	var input = document.createElement("input");
	input.setAttribute('name',hash);
	input.setAttribute('size','30');
	input.value = tags;

	var hidden = document.createElement("input");
	hidden.type="hidden";
	hidden.setAttribute("name", "ckey");
	hidden.value = ckey;

	// creates the link to detail-editing
	var details = document.createElement("a");
	details.setAttribute('href', link);

	details.appendChild(document.createTextNode(getString(type + ".actions.details")));
	details.title = getString(type + ".actions.details.title");

	// append all the created elements
	form.appendChild(input);
	form.appendChild(hidden);

	if (type == "bibtex") {
		parent.insertBefore(document.createTextNode(" | "), parent.childNodes[targetChild]);
		parent.insertBefore(details, parent.childNodes[targetChild]);
		parent.insertBefore(form, parent.childNodes[targetChild]);
	} else {
		parent.insertBefore(details, parent.firstChild);
		parent.insertBefore(form, parent.firstChild);
	}
}



/**
 * Provides localized messages for JavaScript functions. 
 * 
 * Always use this method to get your messages! If you add a new message,
 * call generate_localized_strings.pl afterwards.
 * 
 */
function getString( key ) {
	if ( typeof LocalizedStrings == "undefined" ) return "???"+key+"???"; 
	var s = LocalizedStrings[key];
	if( !s ) return "???"+key+"???";
	return s;
}


//----------------------------------------------------------------------------
//new functions for adding tags from tag-cloud to a field
//----------------------------------------------------------------------------

/**
 * Given the string of tags tagString, checks if tag is contained. 
 * If not, the tag is added to the string
 * 
 * @param tagString
 * @param tag
 * @return
 * 
 */
function addIfNotContained(tagString, tag) {
	var tags = tagString.split(" ");

	if (tags[0] == "") {
		tags.splice(0,1);
	}

	var drin = 0;
	var neuetags = new Array();

	for (var i = 0; i < tags.length; i++) {
		var eintrag = tags[i];
		if (eintrag == tag) {
			drin = 1;
		} else {
			neuetags.push(eintrag);
		}
	}

	if (!drin) {
		neuetags.push(tag);
	}

	return neuetags.join(" ");
}

 
function toggleTag(target, tagname) {
	clear_tags(); // remove getString("navi.tag.hint") 

	activeTag = "";

	if (sortedCollection) {
		sortedCollection[0] = "";
		clearSuggestion();
	}

	target.focus();
	target.value = addIfNotContained(target.value, tagname.replace(/^\s+|\s+$/g, '').replace(/ /g,"_"));
}

/**
 * add or remove tag to/from target field
 * 
 * @param target
 * @param tagname
 * @return
 */
function copytag(target, tagname) {
	var targetNode = document.getElementById(target);
	if (targetNode) {
		toggleTag(targetNode, tagname);
	}
}

function prepareErrorBoxes(className) {
	$("." + className).each(function () {
		if ($(this).html().length == 0) {
			return true;
		}

		$(this).mouseover(function() {
			$(this).fadeOut('slow');
		});

		var first = $(this).children(':first');
		if (typeof first != undefined && first.attr('id')) {
			var firstId = first.attr('id');
			var id = ("#" + firstId.substr(0, firstId.length - ".errors".length)).replace(/\./g, "\\.");
			var copy = $(this);
			var callback = function () {copy.fadeOut('slow');};
			$(id).keyup(callback).change(callback);
		}
		if(!$(this).hasClass('initiallyHidden'))
			$(this).fadeIn("slow");    
	});
	// this is a workaround because the tags input element's id is not 'tags.so-and-so' but 'inpf'
	$('#inpf').keyup(function() {$('#tags\\.errors').parent().fadeOut('slow');});  
}


/**
 * toggles the visibility of the content element encapsulated within the fieldset 
 * 
 * @param el
 *            the toggle element that has been clicked
 * @return 
 */
function toggleFieldsetVisibility(el) {
	var elp = $(el).parent();
	var content = elp.next("div");
	if (content == null) {
		return;
	}

	var elpp = elp.parent();
	var icon;
	var className = null;

	if (elpp.hasClass("fsHidden")) {
		$(content).hide();
		elpp.removeClass('fsHidden').addClass('fsVisible');
		icon = "collapse";
	} else {
		icon = "expand";
		className = "fsHidden";		
	}

	$(content).css('visibility', 'hidden').slideToggle(200, function() {
		el.src = "/resources/image/icon_" + icon + ".png";
		if (className) {
			elpp.removeClass('fsVisible').addClass(className);
		}	
		$(this).css('visibility', 'visible');
	});
}

/**
 * Adds an "export options" box to the "BibTeX" export link. 
 */
$(document).ready(function() {
	$(".exportbibtex").each(function(index, elm) {
		/*
		 * add and show export options when hovering over the link
		 */
		$(elm).mouseover(function() {
			/*
			 * the export options are always the next element after this link
			 */
			var next = $(this).next();

			/*
			 * add export options form if not already there
			 */
			if (!next.hasClass("exportoptions")) {
				// create form
				next = $(
						"<form class='exportoptions' method='get' action='" + $(elm).attr("href") + "' style='display:none'>" +
						"<input type='checkbox' name='generatedBibtexKeys'/>" + getString("post.resource.generateBibtexKey.export") + "<br/>" +
						"<input type='checkbox' name='firstLastNames'/>" + getString("post.resource.personnames.export") + "<br/>" +
						getString("posts") + ": " +
						"</form>"
				);
				
				// number of posts to be exported
				var items = new Array(5, 10, 20, 50, 100, 1000);
				for (var i = 0; i < items.length; i++) {
					next.append("<input type='radio' name='items' value='" + items[i] + "'/>" + items[i] + " ");
				}

				// submit button
				next.append("<br/><input type='submit' value='" + getString("export.bibtex.title") + "'/>");

				// insert form after export link
				$(elm).after(next);

				// close export box by leaving it
				next.mouseleave(function() {
					$(this).hide("fade", {}, 500);
				});
			}

			next.show("fade", {}, 500);
		});

	});
});




/**
 * 	removes the light-grey label of input form fields - if present - after focusing the input
 * 	(before allowing a form to submit we also check if the value equals the hint
 * 	or an empty string on every input field with a 'label') 
 **/

(function($) {
	$.fn.descrInputLabel = function(options) {
		$(this).each(
				function () {
					var self = $(this);
					var inputValue = ((typeof options.valueCallback == 'function') ? options.valueCallback : self.val());

					self.bind("focus", function() {
						if (self.hasClass('descriptiveLabel')) {
							self.val('') ;
							self.removeClass('descriptiveLabel');
						}
					});
					
					self.parents("form").submit(function() {
						if (self.hasClass('descriptiveLabel') || self.val() == '') {
							self.val('').removeClass( 'descriptiveLabel' ).trigger('focus');
							return false;
						}
					});
				}		
		);
	};
})(jQuery);

function overwriteLabel(el) {
	var value = el.val();
	return (!value.length 
			|| value == getString("navi.author.hint") 
			|| value == getString("navi.tag.hint") 
			|| value == getString("navi.user.hint") 
			|| value == getString("navi.group.hint") 
			|| value == getString("navi.concept.hint") 
			|| value == getString("navi.bibtexkey.hint")) 
			|| (el != null && value == getString("navi.search.hint"));
}

function setSearchInputLabel(scope) {
	var search = $('input[name=search]');
	if (!overwriteLabel(search)) return;

	var value = scope.value;
	var messageKey = "";
	if (value == "tag" || value == "user" || value == "group" || value == "author" || value == "bibtexkey") {
		messageKey = value;
	} else if (value == "concept/tag") {
		messageKey = "concept";
	} else if (value.indexOf("user") != -1 || value == "search") {
		messageKey = "search";
	}
	if (messageKey != "") {
		search.val(getString("navi." + messageKey + ".hint"));
	}

	search.addClass('descriptiveLabel');

	return search;
}


function appendToToolbar() {
	$("#toolbar").append(
			'<div id="post-toggle">' +
			'<a id="post-method-isbn" class="active">' + getString("post_bibtex.doi_isbn.isbn") + '</a>' +
			'<a id="post-method-manual">' + getString("post_bibtex.manual.title") + '</a>' +
			'<div style="clear:both; height:0;">&nbsp;</div>' + 
			'</div>'
	);
}

/**
 * create one-string representation of a list of strings
 * 
 * @param data
 *            array of strings
 * @param max_len
 *            return the representing string cut down to the size of
 *            max_len
 * @param delim
 * @return one string, containing concatenation of all strings,
 *         separated by either '\n' or the supplied delimeter
 */
function concatArray(data, max_len, delim) {
	var retVal = "";
	if (delim == null) {
		delim = "\n";
	}
	for (var entry in data) {
		retVal += data[entry] + ((entry < data.length-1) ? delim : "");
	}
	return ((max_len != null) && (retVal.length > max_len)) ? retVal.substr(0, max_len) + "..." : retVal;
}

/**
 * Creates the URL parameters for a title search query using the system tag "sys:title"
 * 
 * @param title
 * @return
 */
function createParameters(title) {
	var parts = title.trim().split(" ");
	var result = "";
	for (i = 0; i < parts.length; i++) {
		result += "sys:title:" + encodeURIComponent(parts[i]) + ((i+1 < parts.length) ? "+" : "*"); 
	}

	return result;
}

/*
 * shows a preview image for links having the class 'preview'
 * 
 * the URL to the images is generated by appending "?preview=LARGE" to the URL from the link
 */
this.imagePreview = function(){	
	var xOff = 400;
	var yOff = 0;
	$("a.preview").hover(function(e){
		this.t = this.title;
		this.title = "";	
		var c = (this.t != "") ? "<br/>" + this.t : "";
		/*
		 * build preview image URL by appending "?preview=LARGE"
		 */
		$("body").append("<p id='preview'><img src='"+ this.href +"?preview=LARGE'/>"+ c +"</p>");         
		$("#preview").css("top",(e.pageY - yOff) + "px").css("left",(e.pageX + (e.pageX < window.innerWidth/2 ? 0 : -xOff)) + "px").fadeIn("fast");      
	}, function(){
		this.title = this.t;	
		$("#preview").remove();
	});		   
	$("a.preview").mousemove(function(e){
		$("#preview").css("top",(e.pageY - yOff) + "px").css("left",(e.pageX + (e.pageX < window.innerWidth/2 ? 0 : -xOff)) + "px");
	});		     	      
};


/*
 * overloaded/added methods of the String class
 * 
 * XXX: Do we really need this? Seems dangerous.
 */
String.prototype.startsWith = function(s) { 
	return this.indexOf(s) == 0; 
};

String.prototype.trim = function () {
	return this.replace(/^\s+/g, '').replace(/\s+$/g, '');
};

/**
 * starts the preview rendering function
 */
$(document).ready(
		function(){imagePreview();}
);