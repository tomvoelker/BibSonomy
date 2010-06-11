
// the form we're receiving input from
var form_name = '#post\\.resource\\.title';

// our suggestion box
var suggestionBox = $("#suggestionBox");

/**
 * prepares the search parameters for lucene
 * 
 * @param title
 *				title entered by the user
 * @return 
 *				the constructed parameter that is passed to lucene
 */

function createParameters(title) {
	if(title[title.length-1] == " ") {
		title = title.substr(0, title.length-1);
	}
	var partials = title.split(" ");
	title = "";

	for(i = 0; i < parseInt(partials.length); i++) {
		title += "sys:title:"+partials[i]+((i+1 < parseInt(partials.length))?"+":"*"); 
	}

	return title;
}

/**
 * queries the titles and further details of publications by a partial title
 * 
 * @param partialTitle
 *            prefix of a title word
 * @param autocompletion
 *            boolean flag - true if autocompletion is on
 * @return
 */

function getSuggestions(partialTitle) {
	var partialTitle_copy = partialTitle;
	$(form_name).blur(function() {
		window.setTimeout(function() {
			suggestionBox.hide();
		},
		140);
	});
	
	if(parseInt(partialTitle.length) > 1) {
		var query = $.ajax({
			type: "GET",
			url: "/json/tag/"+createParameters(partialTitle)+"?items=10",
			dataType: "jsonp",
			success: function(json){
			processResponse(json);
		}});

		return true;
	}
	suggestionBox.hide();
	return true;
}

/**
 * Process the JSON Data and make it visible to the user
 * 
 * @param data
 *            contains the posts returned for the specific title entered
 * @return
 */
function processResponse(data) {
	var m = 1;
	var s = 0;
	var title = getString('post.resource.suggested');
	
	// if there's no data abort
	if(parseInt(data.items.length) == 0) {
		suggestionBox.hide();
		return;
	}

	var p = $('<div class="suggestionBoxContent">'
			+title
			+'</div>');

	var pos = $(form_name).offset();
	var width = parseInt($(form_name).width());
	var top = parseInt(pos.top+$(form_name).height());
	var delim = ' '+getString('and')+' ';
	suggestionBox.html(p);
	$.each(data.items, function(i, item) {
				var editors = "";
				var author = "";
				var year = "";
				var intraHash = item.intraHash;
				var tags = concatArray(item.tags, null, '+');
				//var k = m;
				var className = (((m++)%2 == 0)?'evenSuggestionEntry':'oddSuggestionEntry');
				var formatted_text = formatLabel(item.label);
				
				if(item.editor != 'undefined') {
					editors = concatArray(item.editor, 20, delim);
				}
		
				if(item.author != 'undefined') {
					author = concatArray(item.author, 27, delim);
				}
		
				if(item.year != 'undefined') {
					year = '('+item.year+')';
				}
		
				
				var element = 
					$('<div class="suggestion_entry"><span>'
						+formatted_text
						+'</span><br><span class="suggestionDetails">'
						+author
						+year
						+'</span></div>').addClass(
								className
						);
		
				element.attr('url', '/editPublication?hash='+intraHash+'&user='+item.user+'&copytag='+tags);
				element.attr('title', item.label);
				element.attr('selected', false);
				element.click(
						// get title specific data
						// and set the forms accordingly
						function () {
							window.location.href = '/editPublication?hash='+intraHash+'&user='+item.user+'&copytag='+tags;
							suggestionBox.hide();
						}
				);
				
				element.mouseover(
						function () {
							element.removeClass().addClass('selectedSuggestionEntry').addClass('suggestion_entry_selected');
							(element.children('span:first')).html(element.attr('title'));
						}
				)
				
				element.mouseout(
						function () {
							element.removeClass().addClass(className).addClass('suggestion_entry');
							(element.children('span:first')).html(formatted_text);
						}
				)
				suggestionBox.append(element);
	})

	if(suggestionBox.width() < (width)){
		suggestionBox.width(width+2);
	}

	suggestionBox.css(
			{
				"left":(pos.left)+"px",
				"top":top+"px"
			}
	);
	suggestionBox.show();
}

/**
 * create one-string representation of a list of strings
 * 
 * @param data
 *            array of strings
 * @param max_len
 *            return the representing string cut down to the size of max_len
 * @param delim
 * @return one string, containing concatenation of all strings, separated by
 *         either '\n' or the supplied delimeter
 */

function concatArray(data, max_len, delim) {
	var retVal = "";
	var entry;
	if(delim == null) {
		delim = "\n";
	}
	for(entry in data) {
		retVal += data[entry] + ((entry < data.length-1)?delim:"");
	}
	return ((max_len != null) && (retVal.length > max_len))?retVal.substr(0, max_len)+"...":retVal;
}

/**
 * go one entry up/down in the list
 * 
 * @param keyup
 *            event
 * @return
 */

function getKey(e) {
	var parent = 
		document.getElementById('suggestionBox');
	if(e.keyCode == 38){
		var el = null;
		var selected_field = null;
		if((selected_field = DOMTraverseFlatByClass(parent.childNodes[0], 'suggestion_entry_selected')) == null) {
			if((el = getPreviousByClass(parent.lastChild, 'suggestion_entry')) != null) {
				triggerEvt(el, "mouseover");
			}
		} else {
			triggerEvt(selected_field, "mouseout");
			if((el = getPreviousByClass(selected_field.previousSibling, 'suggestion_entry')) != null) {
				triggerEvt(el, "mouseover");
			}
		}
		
		return false;
	} else if(e.keyCode == 40){
		if((selected_field = DOMTraverseFlatByClass(parent.childNodes[0], 'suggestion_entry_selected')) == null) {
			if((el = getNextByClass(parent.firstChild, 'suggestion_entry')) != null) {
				triggerEvt(el, "mouseover");
			}
		} else {
			triggerEvt(selected_field, "mouseout");
			if((el = getNextByClass(selected_field.nextSibling, 'suggestion_entry')) != null) {
				triggerEvt(el, "mouseover");
			}
		}
		return false;
	} else if(e.keyCode == 13 && $('.suggestion_entry_selected').is('div')){
		window.location.href = $('.suggestion_entry_selected').attr('url');
		suggestionBox.hide();
		return false;
	} else if(e.keyCode == 13 
			|| e.keyCode == 37 
			|| e.keyCode == 39) {
		return false;
	}
	
	return getSuggestions($(form_name).val());
}


function DOMTraverseFlatByClass(el, className) {
	var valid_ed = null;

	while(el != null) {
		if(el.tagName == 'DIV' && cmpClass(el, className)) {
			return el;
		}
		el = el.nextSibling;
	}
	return null;
}

function triggerEvt(el, eventName) {
	var evt;
	
	if (document.createEvent){
		evt = document.createEvent("MouseEvents");
		evt.initMouseEvent(eventName, true, true, window,
		0, 0, 0, 0, 0, false, false, false, false, 0, null);
	}
	(evt)? el.dispatchEvent(evt):
	((eventName == 'mouseover')?el.mouseover && el.mouseover():el.mouseout && el.mouseout());
	
}

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

function getPreviousByClass(match_el, className) {
	while(match_el != null) {
		if(match_el.tagName == 'DIV'){
			if(cmpClass(match_el, className)) {
				return match_el;
			}

		}
		match_el = match_el.previousSibling;
	}
	return null;
}

/**
 * format the matching part of a string bold
 * 
 * @param label
 *            what to match our partial string to
 * @return a formatted string
 */
function formatLabel (label) {
	max_len = 50;
	label  = label.substr(0, max_len) + ((max_len < parseInt(label.length))?" ... ":"");
	partials = $(form_name).val().split(" ");
	for(i = 0; i < parseInt(partials.length); i++) {
		label = label.replace(eval('/'+partials[i]+'/ig'), '<b>'+partials[i].toUpperCase()+'</b>');
	}
	return label;
}

/**
 * adds a given class to the element
 * 
 * @param el
 *            the element to add the class to
 * @param value
 *            the class name we want to add
 * @return 
 */
function addClass(el, value) {
	if(el) {
		el.className += " "+value;
	} else {
		el.className = value;
	}
}

/**
 * look for a match with the elements classes and a given class name
 * 
 * @param el
 *            the element to match the class with
 * @param value
 *            the class we're looking for
 * @return true if a match is given, false otherwise
 */
function cmpClass(el, value) {
	for
	(i = 0, partials = el.className.split(" "); 
	parseInt(partials.length) > i; 
	i++) {
		if(value == partials[i]) {
			return true;
		}
	}
	return false;
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
	if((content = getNextByClass(el.parentNode, "")) == null) {
		return;
	}
	
	if(el.parentNode.parentNode.className == 'fsHidden') {
		el.parentNode.parentNode.className = "fsInner";
		el.src = "/resources/image/icon_expand.png";
		content.style.display = "";
	} else {
		el.parentNode.parentNode.className = "fsHidden";
		el.src = "/resources/image/icon_expand.png";
		content.style.display = "none";
	}
}