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
var copyCollect;



function clearTagField() {
 	var sg = document.getElementById("tagField");
	while(sg.hasChildNodes()) 
		sg.removeChild(sg.firstChild);
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


function handleRecommendedTags(msg) {
	var tagSuggestions = [];
	var target = 'tagField';

	// lookup and clear target node
	var tagField = document.getElementById(target) 
	clearTagField();
	
	// lookup tags
	var root = msg.getElementsByTagName('tags').item(0);
	if( root == null ) {
		// FIXME: DEBUG
		alert("Invalid Ajax response: <tags/> not found.");
	}
	// append each tag to target field
	for (var iNode = 0; iNode < root.childNodes.length; iNode++) {
		var node = root.childNodes.item(iNode);
		// work around firefox' phantom nodes
		if( (node.nodeType == 1) && (node.tagName == 'tag') ) {
			// collect tag informations
			var tagName       = node.getAttribute('name');
			var tagScore      = node.getAttribute('score');
			var tagConfidence = node.getAttribute('confidence');
			
			// create link element from tag
			var newTag = document.createElement('a');
			var newText= document.createTextNode(tagName + " ");
			newTag.setAttribute('href', "javascript:copytag('inpf', '"
										+node.getAttribute('name')
										+"')");
			newTag.setAttribute('tabindex', '1');
			newTag.appendChild(newText);
			tagField.appendChild(newTag);
			
			// append tag to suggestion list
			var suggestion = new Object;
			suggestion.label      = tagName;
			suggestion.score      = tagScore;
			suggestion.confidence = tagConfidence;
			tagSuggestions.push(suggestion);
		}
	}

	// add recommended tags to suggestions
	populateSuggestionsFromRecommendations(tagSuggestions);

	// enable reload button
	document.getElementById("fsReloadLink").setAttribute("href","javascript:reloadRecommendation()");
	document.getElementById("fsReloadButton").setAttribute("src","/resources/image/button_reload.png");
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

/**
 * Called on /edit_tags to make the tags from the sidebar clickable such that
 * on a click they are added to the active input field.  
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


function clearSuggestion() {
	// remove selection
	$("#copytag a").css("color", "").css("backgroundColor", "");

	// remove all child nodes
	$("#suggested").empty();
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
    $("#fsReloadLink").attr("href","#");
    $("#fsReloadButton").attr("src","/resources/image/button_reload-inactive.png");

    clearTagField();      

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
function copyOptionTags(target, event){
	copytag(target, xget_event(event).getAttributeNode("value").value);
}


/**
 * adds a send-tag handler to the parameter tagbox
 * providing auto-complete functionality by suggesting friends
 * @param tagbox
 */
function addAutoCompleteSendTag(tagbox) {
	if(tagbox[0] == null)
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