// general functions for all resource types


function clearTagField() {
 	var sg = document.getElementById("tagField");
	while(sg.hasChildNodes()) 
		sg.removeChild(sg.firstChild);
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
 * handler for the 'reload recommendations button'
 */
function reloadRecommendation() {
    $("#fsReloadLink").attr("href","#");
    $("#fsReloadButton").attr("src","/resources/image/button_reload-inactive.png");

    clearTagField();      

    $('#postForm').ajaxSubmit(tagRecoOptions); 
}


/**
 * @param select
 * @return
 */
function showTagSets(select) {
	$(select).children("option").each(function() {
		$("#field_" + $(this).val()).css("display", $(this).get(0).selected ? '' : 'none');
	});
}

/*
 * check if a group in the relevant for field is selected and 
 * add its name as system tag
 * 
 * FIXME: this method is not used (also on current BibSonomy!), which is bad.
 * Therefore, the "sys:relevantFor:group" tag is currently not added when clicking on a group! 
 * 
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