// general functions for all resource types



/*
 * tag recommendation variables (!?)
 */
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



$(function() {
	initTagAutocompletion($('#inpf_tags'));
});


/**
 * Initializes the auto-completion for the tag box, e.g., 
 * - adds a send-tag handler to the parameter tagbox
 * providing auto-complete functionality by suggesting friends
 * 
 * @param tagbox
 */
function initTagAutocompletion(tagbox) {
	/*
	 * tags from side bar and from copied post
	 */
	$("#copiedTags li, #tagbox li a").each(function() {
		$(this).click(copytag).removeAttr("href").css("cursor", "pointer");
	});

	initTagAutocompletionForSendTag(tagbox);
}

  

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
	var inputValue = document.getElementById(activeField ? activeField : "inpf_tags").value;

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


/**
 * AJAX callback that shows the recommended tags. 
 * 
 * @param xml
 * @return
 */
function handleRecommendedTags(xml) {
	var tagSuggestions = [];

	// lookup and clear target node
	var tagField = $("#recommendedTags");
	
	// clear previous recommendations
	tagField.empty();
	
	// lookup tags
	var root = xml.getElementsByTagName('tags').item(0);
	if (root == null) {
		// FIXME: DEBUG
		alert("Invalid Ajax response: <tags/> not found.");
	}
	// append each tag to target field
	for (var iNode = 0; iNode < root.childNodes.length; iNode++) {
		var node = root.childNodes.item(iNode);
		// work around to firefox' phantom nodes
		if ((node.nodeType == 1) && (node.tagName == 'tag')) {
			var tagName       = node.getAttribute('name');
			
			var newTag = $("<li tabindex='1'>" + tagName + " </li>");
			newTag.click(copytag);
			tagField.append(newTag);
			
			// append tag to suggestion list
			var suggestion = new Object;
			suggestion.label      = tagName;
			suggestion.score      = node.getAttribute('score');
			suggestion.confidence = node.getAttribute('confidence');
			tagSuggestions.push(suggestion);
		}
	}

	// add recommended tags to suggestions
	populateSuggestionsFromRecommendations(tagSuggestions);

	// enable reload button
	$("#fsReloadLink").click(reloadRecommendation);
	$("#fsReloadButton").attr("src","/resources/image/button_reload.png");
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
 * Called on /editTags - collects tags from the sidebar.
 * 
 * TODO: it seems that there is currently no autocompletion functionality that
 * uses these tags.
 * 
 * @return
 */
function collectTags() {
	/*
	 * tags from sidebar
	 */
	$("#tagbox li a").each(function(index) {
		var el = $(this).get(0);
		var tagName = el.firstChild.data.trim();
		listElements[tagName] = index;
		nodeList[tagName] = el;
		list.push(tagName);
	});

	list.sort(unicodeCollation);		
}


/**
 * Parses tag cloud from JSON text into list of objects, each containing 
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
	for (var i in tagCloud) {
		tagList.push(tagCloud[i]);
	}
	// now sort this array
	tagList.sort(tagCompare);

	// set global tag list, storing maximal tag frequency in maxTagFreq
	for (var i=0; i<tagList.length; i++) {
		var label = tagList[i].label;
		var count = tagList[i].count;
		if (count>maxTagFreq) 
			maxTagFreq = count;

		list.push(label);
		var node = new Object;
		node.title = count + " ";
		node.count = count;
		nodeList[label] = node;
	}

	// all done.
	return tagList;
}

/**
 * Auto-completion for tag input field
 * 
 *  tab -> sortedCollection[0]
 *	mouseclick -> parameter value (tag)
 *
 * @param tag
 * @return
 */
function completeTag(tag) {
	
	var inpf = document.getElementById(activeField ? activeField : "inpf_tags");
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

	var links = $("#recommendedTags a").get();
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


/**
 * Gibt eine Liste aus Tags zurück. Bei Relationen werden die Tags gesplittet.
 * @param s
 * @return
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

/**
 * Findet das Wort, welches gerade editiert wird
 * 
 * @param backspace
 * @return
 */
function getActiveTag(backspace) {
	var input = getTags(document.getElementById(activeField ? activeField : "inpf_tags").value);

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

/**
 * Suggestions for tag-inbut box, completed when user presses TAB.
 * 
 * 
 * @return
 */
function suggest() {
	delete collect;
	collect = new Array();

	delete sortedCollection;
	sortedCollection = new Array();

	var copyCollect = new Array();
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
	var tags = document.getElementById(activeField ? activeField : "inpf_tags").value.toLowerCase().split(" ");

	/*
	 * if the following lines are activated, it's not allowed to post duplicates in relations.
	 * var tagString = activeField ? document.getElementById(activeField).value.toLowerCase()
	 *  					       : document.getElementById("inpf").value.toLowerCase();
	 * var tags = getTags(tagString);
	 */

	setPos(0);

	/*
	 * Bin?re Suche über die Listenelemente, in denen die Tags stehen
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
	collect.sort(byWeight);

	/*
	 * add tags from copied post
	 */
	$("#copiedTags li").each(function(i) {
		var index = $(this).get(0).firstChild.data.replace(/^\s+/, '').replace(/\s+$/, '');
		copyListElements[index] = i;
		copyList[index] = index;
	});
	

	/*	collects suggested entrys inside copytag elements	*/
	for (var copyTag in copyListElements) {
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

/**
 * 
 * clickable relations on /editTags page
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


function clearSuggestion() {
	// remove selection
	$("#copiedTags li").css("color", "").css("backgroundColor", ""); // FIXME: why not remove()?

	// remove all child nodes
	$("#suggestedTags").empty();
}


/**
 * Set the current input field to the given id and clear the suggestions.
 * 
 * @param id
 * @return
 */
function setActiveInputField(id) {
	activeField = id;
	$("#suggestedTags").empty();
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

/**
 * adds list elements to the suggested list
 * 
 * @param sortedCollection
 * @return
 */
function addToggleChild(sortedCollection) {
	var sg = $("#suggestedTags");

	for (var i in sortedCollection) {
		var tag = sortedCollection[i];
		var li = $("<li>" + tag + " </li>").click(function(){completeTag(tag.replace(/"/g,'\\"'));});
		sg.append(li);
		
		if (i == getPos()) {
			li.css({"color" : "white", "background" : "#006699"});
		}

		if (i == 3)
			break;
	}
}


/*
 * Sortiert 2 Tags nach Gewichtung
 */

function byWeight(a, b) {
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


/**
 * add or remove tag to/from target field
 * 
 * @param targetId
 * @param tagName
 * @return
 */
function copytag(targetId, tagName) {
	/*
	 * since jQuery gives us other arguments, we overwrite if no string is given
	 */
	if (!targetId || typeof targetId != 'string') {
		targetId = activeField ? activeField : "inpf_tags"; // default fall back 
	}
	// target is given - check tagName
	if (!tagName || typeof tagName != 'string') {
		tagName = $(this).html();
	}
	var targetNode = document.getElementById(targetId);
	if (targetNode) {
		toggleTag(targetNode, tagName);
	}
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


/**
 * handler for the 'reload recommendations button'
 */
function reloadRecommendation() {
    $("#fsReloadLink").click("");
    $("#fsReloadButton").attr("src","/resources/image/button_reload-inactive.png");
    $('#postForm').ajaxSubmit(tagRecoOptions); 
}


/**
 * Activates/shows the tag set corresponding to the group the user clicked on 
 * (in the "relevant:for" field). 
 * 
 * @param select
 * @return
 */
function showTagSets(select) {
	$(select).children("option").each(function() {
		$("#field_" + $(this).val()).css("display", $(this).get(0).selected ? '' : 'none');
	});
}



/**
 * copy a value from an "option" form field to the input field with the target id.
 * 
 * @param target
 * @param event
 * @return
 */
function copyOptionTags(event) {
	copytag("inpf_tags", xget_event(event).getAttributeNode("value").value);
}


function initTagAutocompletionForSendTag(tagbox) {

	if (tagbox[0] == null)
		return;
	
	var friends = null;
	getFriends = function () {return friends;};
	$.ajax({
		url: '/json/friends?userRelation=FRIEND_OF',
		async: false,
		dataType: "jsonp",
		success: function (data) {
			friends = $.map( data.items, function( item ) {
				return item.name;
			});
		}
	});

	tagbox[0].onclick = tagbox[0].onblur = tagbox[0].onfocus = null;

	var c = null;	
	var inpfValue = function(t) {
		if(t == null)
			return c;
		return (c = t);
	};
	var suggestSendTo = function (partialName) {
		var x = 0;
		var regexp = new RegExp("^"+partialName);
		var friends = getFriends();
		delete sortedCollection;
		sortedCollection = new Array();
		clearSuggestion();
		while(x < friends.length) {
			if(("send:"+friends[x]).match(regexp) 
				&& tagbox.val().match(new RegExp("([ ]|^)send:"+friends[x]+"([ ]|$)")) == null)  
					sortedCollection.push("send:"+friends[x]);
			x++;
		}
		addToggleChild(sortedCollection);
		activeTag = partialName;
	};
	
	
	var evalKeyInput = function (e) {
		var keyCode = e.keyCode;
		switch( keyCode ) {
        case keyCode.ENTER:
        case keyCode.NUMPAD_ENTER:
        case keyCode.TAB:{
        	e.preventDefault();
        	break;
        }	
        default: {
        		if(e.type == 'keydown')
        			inpfValue(tagbox.val().split(" "));
        	}
        }
		return handler(e);
	};
	
	tagbox.keydown(function (e) {
		evalKeyInput(e);
	}).keypress(function (e) {
		return handler(e);
	}).keyup(function (e) {
		evalKeyInput(e);
		var t = inpfValue();
		var reverse = false;
		if(getFriends() != null) {
			var tagsNew = tagbox.val().split(" ");
			var x = 0;
			if(tagsNew.length < t.length) {
				tagsNew = tagsNew.reverse();t = t.reverse();reverse = true;
			}
			
			while( t.length > x ) {
				if(tagsNew[x] != undefined 
						&& tagsNew[x] != t[x]) {
					if(tagsNew[x].match(/^send:/) == null
							|| t[x].length == 0)
						break;
					return suggestSendTo(tagsNew[x]);
				}
				x++;
			}
		}
	});
}


/** 
 * used in postPublication.jspx to toggle parsed post on and off
 * 
 * @param pictureId
 * @param pictureActive
 * @param pictureInactive
 * @param divId
 * @return
 */
function toggleImage(pictureId, pictureActive, pictureInactive, divId) {
	$(document.getElementById(divId)).slideToggle("slow"); 
	var  imageSource= $(document.getElementById(pictureId)).attr("src");
	if(imageSource==pictureActive) {
		$('#'+pictureId).attr('src',pictureInactive);
	} else {
		$('#'+pictureId).attr('src',pictureActive);
	}
}