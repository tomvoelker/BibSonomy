var activeField = null;
var tagbox      = null; // used in style.js!
var tags_toggle = 0;
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
	add_tags_toggle();
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

/*
 * functions to toggle background color for required bibtex fields
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

/*
 * Setting the current input Field
 * and clears the suggestions
 */
function setActiveInputField(id) {

	activeField = id;

	// FIXME: dafür gibt's IIRC jQuery.empty() oder clear() für?
	var sg = document.getElementById("suggested");
	if (sg) {
		while(sg.hasChildNodes())
			sg.removeChild(sg.firstChild);
	}
}

/*
 * functions for copytag handling
 */
function toggle(event) {
	clear_tags(); // remove getString("navi.tag.hint") 

	toggleTag(
			document.getElementById(activeField ? activeField : "inpf"), 
			xget_event(event).childNodes[0].nodeValue
	);
}

function add_toggle() {
	tags_toggle = 1;
}

function add_tags_toggle() {
	if (tags_toggle == 1) {
		$("#tagbox li a:first").each(function() {
			$(this).click(toggle)
			.attr("text", $(this).attr("href"))
			.attr("href", "") // FIXME: how to remove an attribute?
			.css("cursor", "pointer");
		});
		/*
	  var links = tagbox.getElementsByTagName("li");
    for (x=0; x<links.length; x++) {
         var aNode = links[x].getElementsByTagName("a")[0];
         aNode.onclick=toggle;
         aNode.setAttribute('text', aNode.getAttribute("href"));
         aNode.removeAttribute("href");
         aNode.style.cursor = "pointer";
    }*/

		// FIXME: does equivalent work?
		$("#copytag li").click(toggle);
		/*
    var ul = document.getElementById("copytag");
    if (ul!=null) {
	    var links = ul.getElementsByTagName("li");
    	for (x=0; x<links.length; x++) {
    	     links[x].onclick=toggle;
	    }
	 }
		 */
	}
}

/* ********************************** *
       clickable relations for edit_tags
 * ********************************** */
function add_toggle_relations() {
	/*
	 * add toggler for supertags
	 */
	$("#relations > li a:first").each(function() {
		$(this).click(function() {
			// FIXME: does "this" exist here?
			var value = this.childNodes[0].nodeValue;
			$("#delete_up").val(value);
			$("#insert_up").val(value);
		})
		.css("cursor", "pointer")
		.attr("title", "add as supertag") // FIXME: I18N
		.attr("href", ""); // FIXME: how to remove attribute?
	});
	/*
	 * add toggler for subtags
	 */
	$("#relations > li ul:first li a:first").each(function() {
		$(this).click(function() {
			var delete_lo = $("delete_lo");
			// FIXME: does "this" exist here? How to access clicked element?
			delete_lo.val(addIfNotContained(delete_lo.val(), this.childNodes[0].nodeValue.replace(/ /, "")));
			delete_lo.focus();
		})
		.css("cursor", "pointer")
		.attr("title", "add as subtag") // FIXME: I18N
		.attr("href", ""); // FIXME: how to remove attribute?
	});
	/* old code
    	var relation_list  = document.getElementById("relations");
	    var relation_items = relation_list.childNodes;
	    // iterate over supertags
    	for (x=0; x<relation_items.length; x++) {
        	var node = relation_items[x];
        	if (node.nodeName == "LI") {
             	// supertag found
        	 	var aNode = node.getElementsByTagName("a")[0];
		        aNode.onclick = add_supertag_to_input;
    		    aNode.removeAttribute("href");
        	 	aNode.style.cursor = "pointer";
        	 	aNode.setAttribute("title", "add as supertag");
        	 	// iterate over subtags
        	    var sub_items = node.getElementsByTagName("ul")[0].getElementsByTagName("li");
                for (y=0;y<sub_items.length; y++) {
	        		var bNode = sub_items[y].getElementsByTagName("a")[0];
		        	bNode.onclick = add_subtag_to_input;
    		    	bNode.removeAttribute("href");
        			bNode.style.cursor = "pointer";
	        		bNode.setAttribute("title", "add as subtag");
        	 	}
        	}
	    }
	 */
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
	if(parseInt(json.items.length) == 0)
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

/*
 * Hier werden die Tags aus der Tagwolke, Copytags und Recommendations in Listen gepackt
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
	var input = getTags(document.getElementById(activeField ? activeField : "inpf").value.toLowerCase());

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
	if ($("#copytag")) {
		for (var i = 0; i < copyRows.length; ++i) {
			copyRows[i].style.color = "";
			copyRows[i].style.backgroundColor = "";
		}
	}


	// FIXME: jQuery .clear() oder .empty()?!
	var sg = document.getElementById("suggested");
	while(sg.hasChildNodes())
		sg.removeChild(sg.firstChild);
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
					for(var i = 0; i < tags.length; i++) {
					}
				}
			}
		}
		if(reset) {
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
	var tag_name = tag.replace(/^\s+|\s+$/g, '');

	var links = $("#tagField a"); // FIXME: klappt der Loop?
	for (var i = 0; i < links.length; i++) {
		// FIXME: wie lautet das jQuery-Äquivalent zu firstChild?
		if (tag_name == links[i].firstChild.nodeValue.replace(/^\s+|\s+$/g, '')) {
			return links[i];
		}
	}
	return null;
}

function simulateClick(target) {
	var evt;
	var el = target;
	if (document.createEvent) {
		evt = document.createEvent("MouseEvents");
		if (evt.initMouseEvent) {
			evt.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
		} else {
			evt = false;
		}
	}
	(evt)? el.dispatchEvent(evt):(el.click && el.click());
} 

/*
 * FIXME: wofür werden setButton(), makeParagraph() und makeText() benötigt?
 * Für die Private Note? 
 */
function setButton() {

	if (document.getElementById("privnote").firstChild) {
		$("#makeP").remove();
		$("#note").append("<input type='button' id='makeP' value='update' onclick='makeParagraph()'/>");
	}
}
//FIXME: refactor
function makeParagraph() {
	var tNode = document.getElementById("privnote");
	var note = "";
	if (tNode.firstChild) {
		note = tNode.firstChild.data;
	}

	if (note != "") {
		tNode.style.display = "none";

		var div = $("#note");
		div.append("<p id='pText'>" + note + "</p>");

		// FIXME: da wird ein bestehendes Element umgehängt? Geht das?
		var button = document.getElementById("makeP");
		button.setAttribute("onClick","makeText()");
		button.setAttribute("value","edit");
		div.append(button);
	}
}

function makeText() {
	$("#privnote").css("display", "inline");

	$("#note").append("<input type='submit' value='update'/>");	
	$("#makeP").remove();
	$("#pText").remove();
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





/* ********************************************************
 * AJAX functions
 * ********************************************************/

// updates the relations in AJAX style TODO: simplify using jQuery
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
	$("#relations").empty(); // FIXME: or clear()?

	// parse XML input
	var xml = data.documentElement; // FIXME: doesn't work that way with jQuery

	if (xml) {
		var conceptnames = new Array();					

		var currUser = xml.getAttribute("user");

		// get all relations
		var requestrelations = xml.getElementsByTagName("relation");

		// iterate over the relations
		for(x=0; x<requestrelations.length; x++){       		    
			// one relation
			var rel = requestrelations[x];		    
			// the upper tag of the relation
			var upper = rel.getElementsByTagName("upper")[0].firstChild.nodeValue;
			// new list item for this supertag
			var rel_item = document.createElement("li");
			rel_item.className = "box_upperconcept";
			// store upper tag in array
			conceptnames.push(upper);

			// add the symbol to hide the relation
			var linkupperx = document.createElement("a");
			var linkupperxhref = document.createAttribute("href");
			linkupperxhref.nodeValue = "/ajax/pickUnpickConcept?action=hide&tag=" + upper + "&ckey=" + ckey;
			linkupperx.setAttributeNode(linkupperxhref);
			// changed from 215 (&times;) to 8595 (&darr;)
			var linkupperxtext = document.createTextNode(String.fromCharCode(8595));
			linkupperx.appendChild(linkupperxtext);
			rel_item.appendChild(linkupperx);
			rel_item.appendChild(document.createTextNode(" "));

			// attach function to onlick event // FIXME: check if works
			$(linkupperx).click(function() {
				// get concept name // FIXME: does "this" in this context work?
				var concept = this.parentNode.getElementsByTagName("a")[1].firstChild.nodeValue;
				// update relations list, hide concept
				updateRelations(evt, "hide", concept);
			}); 

			// add link for upper tag
			var linkupper = document.createElement("a");
			var linkupperhref = document.createAttribute("href");
			linkupperhref.nodeValue = "/concept/user/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(upper);
			linkupper.setAttributeNode(linkupperhref);
			var linkuppertext = document.createTextNode(upper);
			linkupper.appendChild(linkuppertext);
			rel_item.appendChild(linkupper);

			// add arrow
			rel_item.appendChild(document.createTextNode(" " + String.fromCharCode(8592) + " "));


			// add lower tags
			var lowers = rel.getElementsByTagName("lower");
			var lowerul = document.createElement("ul");
			lowerul.className = "box_lowerconcept_elements";
			var lowerulid = document.createAttribute("id");
			lowerulid.nodeValue = upper;
			lowerul.setAttributeNode(lowerulid);

			// iterate over lower tags
			// FIXME: implement using jQuery.each()
			for(y=0; y<lowers.length; y++) {
				var lower = lowers[y].firstChild.nodeValue;

				// create new list item for lower tag
				var lowerli = document.createElement("li");
				lowerli.className = "box_lowerconcept";

				// add link
				var lowerlink = document.createElement("a");
				var lowerlinkhref = document.createAttribute("href");
				lowerlinkhref.nodeValue = "/user/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(lower);
				lowerlink.setAttributeNode(lowerlinkhref);
				var lowerlinktext = document.createTextNode(lower + " ");
				lowerlink.appendChild(lowerlinktext);
				lowerli.appendChild(lowerlink);

				// add item
				lowerul.appendChild(lowerli);
			}

			// append list of lower tags to supertag item
			rel_item.appendChild(lowerul);

			// insert relations_list for this supertag
			$("#relations").append(rel_item);


		}
		delete conceptnames;
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

// this picks or unpicks a publication
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



// picks/unpicks publications in AJAX style
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

String.prototype.startsWith = function(s) { 
	return this.indexOf(s) == 0; 
};


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
	var tags = tagString.value.split(" ");

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

//the old tag toggler: add or remove tagname tagn to/from input field target 
function toggleTag(eingabe, tagname) {
	clear_tags(); // remove getString("navi.tag.hint") 

	activeTag = "";

	if (sortedCollection) {
		sortedCollection[0] = "";
		clearSuggestion();
	}

	eingabe.focus();
	eingabe.value = addIfNotContained(eingabe, tagname.replace(/^\s+|\s+$/g, '').replace(/ /g,"_"));
}

//add/remove tagname to/from target field 
function copytag(target, tagname) {
	var targetNode = document.getElementById(target);
	if (targetNode) {
		toggleTag(targetNode, tagname);
	}
}

/** FUNCTIONS USED IN THE POSTING VIEWS **/

//hide and show the tagsets in the relevant for field
//FIXME: use jQuery.each()
function showTagSets(select) {
	$(select).children("option").each(function() {
		$("#field_" + $(this).val()).css("display", $(this).selected ? '' : 'none'); // FIXME: how to check for selected forms options? (see also next method)
	});
	/*
	for (var i = 0; i < select.options.length; i++) {
		var op = select.options[i];
		var field = document.getElementById("field_" + op.value);
		if (field != null) {
			if (op.selected) {
				field.style.display = '';
			} else {
				field.style.display = 'none';
			}
		}   
	}
	 */
}

/*
 * check if a group in the relevant for field is selected and 
 * add its name as system tag
 */
function addSystemTags() {
	clear_tags();
	var tags = $("#inpf").val();
	var relGroup;
	var systemtags = "";
	var counter = 0;
	while (relGroup = $("#relgroup" + counter)) {
		if (relGroup.attr('selected') == true) { // FIXME: does this work?
			var value = relGroup.val();
			// only add the system tag if it does not yet exist in the tag field
			if (tags.match(":" + value) == null) {
				systemtags += " " + "sys:relevantFor:" + value;
			}
		}
		counter++;
	}
	// if a systemtag was build, add it to the tag field
	if (systemtags != null) {
		// add systemtags to the tag field
		copytag("inpf", systemtags);
	}

}

//copy a value from a option field to the target
function copyOptionTags(target, event){
	copytag(target, xget_event(event).getAttributeNode("value").value);
}

//trim
String.prototype.trim = function () {
	return this.replace(/^\s+/g, '').replace(/\s+$/g, '');
};

/** switch between db, ldap, and openid login form */
/* @param methodsList
 * @param id (optional)
 * @param prefix (optional)
 */ 
function switchLogin() {
	var methodsList = "db,openid";
	var id = null;
	var prefix="login";

	switch (arguments.length) {
	case 3:
		if (arguments[2]!="") prefix = arguments[2];
	case 2:
		id = arguments[1];
	case 1: 
		if (arguments[0]!="") methodsList = arguments[0]; 
	}

	// id E {standard, ldap, openid}
	methods = methodsList.split(",");
	if (!id) id = methods[0];
	// if method changes from outside select-element
	// hole alle options
	var elSel = document.getElementById(prefix.concat("MethodSelect"));

	// if select element exists
	if (elSel) {
		// iterate over all options
		for (i = 0; i<elSel.length; i++) {
			// if value of option is equal to id select option, otherwise deselect it
			if (elSel.options[i].value == id) {
				elSel.options[i].selected = true;
			}
			else
			{
				elSel.options[i].selected = false;
			}

		}					
	}
	for (i = 0; i<methods.length; i++) {
		elementId = prefix.concat(methods[i]);

		if (id == methods[i]) {
			document.getElementById(elementId).style.display = "block";
			var elMethod = document.getElementById(prefix.concat("Method").concat(id)); // loginMethoddb, loginMethodldap,...
			if (elMethod)  { elMethod.value = id; }
		} else {
			document.getElementById(elementId).style.display = "none";
		}
	}

}

function prepareErrorBoxes(className) {
	$("." + className).each(function () {
		if (parseInt($(this).html().length) == 0) {
			return true;
		}

		$(this).mouseover(function() {
			$(this).fadeOut('slow');
		});

		var first = $(this).children(':first');
		if (typeof first != undefined && first.attr('id')) {
			var id = ("#"+(first.attr('id')).substr(0, (first.attr('id')).length-".errors".length)).replace(/\./g, "\\.");
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
	/*
	 * FIXME: check if the jQuery expression for varriable "content" is equivalent to
	content = getNextByClass(el.parentNode, "");

with

function getNextByClass(match_el, className) {
	while(match_el != null) {
		if(match_el.tagName == 'DIV'){
			if(cmpClass(match_el, className)) {
				return match_el;
			}
		}
		match_el = match_el.nextSibling;
	}
	return null;
}

FIXME: calling cmpClass() with an empty className ("") should always return FALSE - if (content == null) was always true?
	 */

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
				var form = document.createElement("form");
				form.setAttribute("class", "exportoptions");
				form.setAttribute("action", elm.getAttribute("href"));
				form.setAttribute("method", "get");
				form.style.display = "none";

				var generatedBibtexKeys = document.createElement("input");
				generatedBibtexKeys.setAttribute("type", "checkbox");
				generatedBibtexKeys.setAttribute("name", "generatedBibtexKeys");
				form.appendChild(generatedBibtexKeys);
				form.appendChild(document.createTextNode(getString("post.resource.generateBibtexKey.export")));
				form.appendChild(document.createElement("br"));

				var firstLastNames = document.createElement("input");
				firstLastNames.setAttribute("type", "checkbox");
				firstLastNames.setAttribute("name", "firstLastNames");
				form.appendChild(firstLastNames);
				form.appendChild(document.createTextNode(getString("post.resource.personnames.export")));
				form.appendChild(document.createElement("br"));

				// different number of posts
				form.appendChild(document.createTextNode(getString("posts") + ": "));
				var items = new Array(5, 10, 20, 50, 100, 1000);
				for (var i = 0; i < items.length; i++) {
					var item = document.createElement("input");
					item.setAttribute("type", "radio");
					item.setAttribute("name", "items");
					item.setAttribute("value", items[i]);
					form.appendChild(item);
					form.appendChild(document.createTextNode(items[i] + " "));
				}

				form.appendChild(document.createElement("br"));

				var submit = document.createElement("input");
				submit.setAttribute("type", "submit");
				submit.setAttribute("value", getString("export.bibtex.title"));
				form.appendChild(submit);

				// insert form after export link
				$(elm).after(form);

				// close export box by leaving it
				$(form).mouseleave(function() {
					$(this).hide("fade", {}, 1000);
				});

				next = $(form);
			}

			next.show("fade", {}, 500);
		});

	});
});




/**
 * 	removes the light-grey label - if present - after focussing the input
 * 	(before allowing a form to submit we also check if the value equals the hint
 * 	or an empty string on every input field with a 'label') 
 **/

(function($) {
	$.fn.descrInputLabel = function(options) {
		$(this).each(
				function () {
					var self = this;
					var parentForm = getParentForm(self);
					var inputValue = ((typeof options.valueCallback == 'function') ? options.valueCallback : self.value);

					$(self).bind("focus", function() {
						if($(self).hasClass( 'descriptiveLabel' )){self.value='';$(self).removeClass( 'descriptiveLabel' );}
					});
					$(parentForm).submit(function() {
						if($(self).hasClass( 'descriptiveLabel' ) || self.value=='') {
							$(self).val('').removeClass( 'descriptiveLabel' ).trigger('focus');
							return false;
						}
					});
				}		
		);
	};
})(jQuery);

function overwriteLabel(el) {
	var value = el.val();
	return ((!value.length 
			|| value == getString("navi.author.hint") 
			|| value == getString("navi.tag.hint") 
			|| value == getString("navi.user.hint") 
			|| value == getString("navi.group.hint") 
			|| value == getString("navi.concept.hint") 
			|| value == getString("navi.bibtexkey.hint")) 
			|| (el != null && value == getString("navi.search.hint")));
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

function getParentForm(el) {
	el = $(el).parent()[0];
	return (validElement(el, 'form') ? el : getParentForm(el)); 
}

function appendToToolbar() {
	var appendA = function(id, title) {return $('<a></a>').attr('id',id).html(title);};
	$("#toolbar")
	.append(
			$('<div></div>')
			.attr('id', 'post-toggle')
			.append((appendA("post-method-isbn", getString("post_bibtex.doi_isbn.isbn"))).addClass('active'))
			.append(appendA("post-method-manual", getString("post_bibtex.manual.title")))
			.append($("<div></div>").css({'clear':'both','height':'0'}).html('&nbsp;'))
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
/**
 * starts the preview rendering function
 */
$(document).ready(
		function(){imagePreview();}
);