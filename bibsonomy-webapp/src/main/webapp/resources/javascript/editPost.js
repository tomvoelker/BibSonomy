// general functions for all resource types


/**
 * Ajax callback function for inserting recommended tags:
 *    1) insert tags to form's recommendations field
 *    2) append recommended tags to potential suggestions
 *    3) enable recommendation reload button
 */
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
    document.getElementById("fsReloadLink").setAttribute("href","#");
    document.getElementById("fsReloadButton").setAttribute("src","/resources/image/button_reload-inactive.png");

    clearTagField();      

    $('#postForm').ajaxSubmit(tagRecoOptions); 
}

/**
 * toggles the visibility of the content element encapsulated within the fieldset 
 * 
 * @param el
 *            the toggle element that has been clicked
 * @return 
 */
function toggleFieldsetVisibility(el) {
	var content = null;
	var icon_url = null;
	var className = null;
	if((content = getNextByClass(el.parentNode, "")) == null) {
		return;
	}
	
	if(cmpClass(el.parentNode.parentNode, 'fsHidden')) {
		$(content).hide();
		$(el.parentNode.parentNode).removeClass('fsHidden');
		icon_url = "/resources/image/icon_collapse.png";
	} else {
			icon_url = "/resources/image/icon_expand.png";
			className = "fsHidden";		
	}
	
	$(content).css('visibility', 'hidden').slideToggle(200, function() {
			el.src = icon_url;
			if(className)
				$(el.parentNode.parentNode).addClass(className);
			$(this).css('visibility', 'visible');
		});
}
