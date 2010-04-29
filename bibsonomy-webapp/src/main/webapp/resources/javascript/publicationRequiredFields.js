var form_name = '#post\\.resource\\.title';
var suggestionBox = $("#suggestionBox");
var bg_color_over = "#006699";
var text_color = '#006699';

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

	if(partialTitle.length > 1) {
		var query = $.ajax({
			type: "GET",
			url: "http://www.bibsonomy.org/json/tag/sys:title:"+encodeURIComponent(partialTitle)+"*?items=10",
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
	if(data.items.length == 0) {
		suggestionBox.hide();
		return;
	}

	var p = $('<div class="suggBox" style="background-color: #006699; color: #FFFFFF; padding:3px;">'
			+title
			+'</div>');

	var pos = $(form_name).offset();
	var width = $(form_name).width();
	var top = parseInt(pos.top+$(form_name).height());
	suggestionBox.html(p);
	var has_elements = false;
	$.each(data.items, function(i, item) {
		if(item.label.toUpperCase().indexOf($(form_name).val().toUpperCase()) >= 0) {
				has_elements = true;
				var editors = "";
				var author = "";
				var year = "";
		
				if(item.editor != 'undefined') {
					editors = concatArray(item.editor, 20);
				}
		
				if(item.author != 'undefined') {
					author = concatArray(item.author, 27);
				}
		
				if(item.year != 'undefined') {
					year = '('+item.year+')';
				}
		
				var intraHash = item.intraHash;
				var tags = concatArray(item.tags, null, '+');
				var k = m;
				var bg_color = (((m++)%2 == 0)?'#FFFFFF':'#EEEEEE');
				var formatted_text = formatLabel(item.label);
				var element = 
					$('<div class="suggestion_entry" style="cursor:pointer; color:'+text_color+';background-color:'
							+bg_color // change the background-color every step
							+'"><span>'
						+formatted_text
						+'</span><br><span style="font-size:10px;">'
						+author
						+year
						+'</span></div>');
		
				element.attr('url', '/editPublication?hash='+intraHash+'&user='+item.user+'&copytag='+tags);
				element.attr('title', item.label);
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
							element.css("background-color", bg_color_over);
							element.css("color", "#FFFFFF");
							(element.children('span:first')).html(element.attr('title'));
						}
				)
				
				element.mouseout(
						function () {
							element.css("background-color", bg_color);
							element.css("color", text_color);
							(element.children('span:first')).html(formatted_text);
							suggestionBox.children(".suggestion_entry_selected").css({"background-color":bg_color_over, "color":"#FFFFFF"});
						}
				)
		
				suggestionBox.append(element);
		}
	})

	if(suggestionBox.width() < (width)){
		suggestionBox.width(width+2);
	}

	suggestionBox.css(
			{
				"left":(pos.left)+"px",
				"top":top+"px",
				"background-color":"#FFFFFF",
				"z-index":"999",
				"border":"1px solid #006699",
				"border-left":"3px solid #006699"
			}
	);
	if(!has_elements)
		suggestionBox.hide();
	else 
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
		retVal += data[entry] + delim;
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
				el.className = 'suggestion_entry_selected';
				if(el.style != 'undefined') {
					el.setAttribute("bg", el.style.backgroundColor);
				}
				el.style.backgroundColor = bg_color_over;
				el.style.color = "#FFFFFF";
			}
		} else {
			selected_field.className = 'suggestion_entry';
			selected_field.style.backgroundColor = selected_field.getAttribute("bg");
			selected_field.removeAttribute("bg");
			selected_field.style.color = text_color;
			if((el = getPreviousByClass(selected_field.previousSibling, 'suggestion_entry')) != null) {
				el.className = 'suggestion_entry_selected';
				if(el.style != 'undefined') {
					el.setAttribute("bg", el.style.backgroundColor);
				}
				el.style.backgroundColor = bg_color_over;
				el.style.color = "#FFFFFF";
			}
		}
		
		return false;
	} else if(e.keyCode == 40){
		if((selected_field = DOMTraverseFlatByClass(parent.childNodes[0], 'suggestion_entry_selected')) == null) {
			if((el = getNextByClass(parent.firstChild, 'suggestion_entry')) != null) {
				el.className = 'suggestion_entry_selected';
				if(el.style != 'undefined') {
					el.setAttribute("bg", el.style.backgroundColor);
				}
				el.style.backgroundColor = bg_color_over;
				el.style.color = "#FFFFFF";
			}
		} else {
			selected_field.className = 'suggestion_entry';
			selected_field.style.backgroundColor = selected_field.getAttribute("bg");
			selected_field.removeAttribute("bg");
			selected_field.style.color = text_color;
			if((el = getNextByClass(selected_field.nextSibling, 'suggestion_entry')) != null) {
				el.className = 'suggestion_entry_selected';
				if(el.style.backgroundColor != 'undefined') {
					el.setAttribute("bg", el.style.backgroundColor);
				}
				el.style.backgroundColor = bg_color_over;
				el.style.color = "#FFFFFF";
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
		if(el.tagName == 'DIV' && el.className == className) {
			return el;
		}
		el = el.nextSibling;
	}
	return null;
}

function getNextByClass(match_el, className) {
	while(match_el != null) {
		if(match_el.tagName == 'DIV'){
			if(match_el.className == className) {
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
			if(match_el.className == className) {
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
	form_length = $(form_name).val().length;
	pos = label.toUpperCase().indexOf($(form_name).val().toUpperCase());
	prepend = "";
	append = "";
	if(pos > 0) {
		prepend = label.substr(0, pos);
	}
	if(label.length > pos+form_length) {
		append = label.substr(pos + $(form_name).val().length);
	}
	
	max_len -= form_length;
	
	if(prepend != "") {
		l = prepend.length-Math.min(max_len, prepend.length);
		prepend = ((max_len < prepend.length)?"... ":"")+prepend.substr(l, prepend.length-l);
		max_len -= Math.min(max_len, prepend.length);
	} 

	if(append != "" && max_len > 0) {
		append = append.substr(0, max_len)+((max_len < append.length)?" ...":"");
	}

	var title =
		prepend
		+'<b>'
		+label.substr(pos, $(form_name).val().length)
		+'</b>'
		+append;

	return title;
}
