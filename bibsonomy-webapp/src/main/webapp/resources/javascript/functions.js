var activeField = null;
var tagbox      = null; // used in style.js!
var ckey        = null;
var currUser    = null;
var requUser	= null;
var projectName = null;
var pwd_id_postfix = "_form_copy"; // id of password input field copy

/*
 * variables for cursor position
 */
var getPos = null;
var setPos = null;
var getSetPos = 0;



/**
 * This method is called on document.ready. Thus, methods that should 
 * be called ON EVERY page should be added here.
 * 
 * @param tagbox_style
 * @param tagbox_sort
 * @param tagbox_minfreq
 * @param lrequUser
 * @param lcurrUser
 * @param lckey
 * @param lprojectName
 * @return
 */
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

	/*
	 * add a callback to every input that has the descriptiveLabel class
	 * so that hints get removed after focusing the input form 
	 */
	$('.descriptiveLabel').each(function() {
		$(this).descrInputLabel({});
	});

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
	
	prepareErrorBoxes('dissError');
	
	/*
	 * starts the preview rendering function
	 */
	imagePreview();
	
	
	/*
	 * adds list options (for bookmarks + publication lists)
	 */
	addListOptions();
}

/**
 * Adds [-] buttons to sidebar elements to toggle visibility of each element. 
 * 
 * @return
 */
function init_sidebar() {
	$("#sidebar li .sidebar_h").each(function(index,item){
		var span = $("<span class='toggler'><img src='/resources/image/icon_collapse.png'/></span>");
		span.click(function(){
			fadeNextList(item);
		});
		$(this).prepend(span); 
	});
}

function fadeNextList(target) {
	$(target).nextAll(".sidebar_collapse_content").toggle("slow", function(){
		$(target).find(".toggler img").attr("src", "/resources/image/icon_" + ($(this).css('display') == 'none' ? "expand" : "collapse") + ".png");
	});
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
	var copyId = '#' + el.get(0).id + pwd_id_postfix;
	return $(copyId).css('color', '#aaa').width(el.width()).click(function(){hideFormTextCopy({elementCopy : copyId, element : el});});
}


/**
 * on blur of the user name input field set the password form in front of the fake password form
 * @param map
 * @return
 */
function hideFormTextCopy(map) {
	$(map.elementCopy).hide();
	$(map.element).removeClass('hiddenElement').focus();
}

/**
 * adds hints to input fields
 * 
 * FIXME: all elements should be "input" fields (check is missing!)
 * 
 * @return
 */
function add_hints() {
	/*
	 * username
	 */
	var un = $("#un[name='username']");
	if (un.length) {
		if (un.val() == "" || un.val() == getString("navi.username")) {
			un.val(getString("navi.username"));
			un.addClass("descriptiveLabel");
		}
		un.blur(function(){hideFormTextCopy({elementCopy : '#pw' + pwd_id_postfix, element : '#pw'});});
	}
	/*
	 * password
	 */
	var pw = $("#pw[name='password']");
	if (pw.length) {
		getFormTextCopy(pw).val(getString("navi.password"));
	}
	/*
	 * LDAP username
	 */
	var unldap = $("#unldap[name='username']");
	if (unldap.length) {
		if (unldap.val() == "" || unldap.val() == getString("navi.username.ldap")) {
			unldap.val(getString("navi.username.ldap"));
			unldap.addClass("descriptiveLabel");
		}
		unldap.blur(function(){hideFormTextCopy({elementCopy : '#pwldap' + pwd_id_postfix, element : '#pwldap'});});
	}
	/*
	 * LDAP password
	 */
	var pwldap = $("#pwldap[name='password']");
	if (pwldap.length) {
		getFormTextCopy(pwldap).val(getString("navi.password.ldap"));
	}
	/*
	 * OpenID
	 */
	var openid = $("#openID[name='openID']");
	if (openid.length) {
		if (openid.val() == "" || openid.val() == getString("navi.openid")) {
			openid.val(getString("navi.openid"));
			openid.addClass("descriptiveLabel");
		}
	}
	/*
	 * tag input field
	 */
	var tag = $("#inpf[name|='tag']");
	if (tag.length && (tag.val() == "" || tag.val() == getString("navi.tag.hint"))) {
		tag.val(getString("navi.tag.hint"));
		tag.addClass("descriptiveLabel");
	}
	/*
	 * search input field
	 */
	var search = $("#inpf[name='search']");
	if (search.length && (search.val() == "" || search.val() == getString("navi.search.hint"))) {
		search.val(getString("navi.search.hint"));
		search.addClass("descriptiveLabel");
	}

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
 * returns the node of an event
 * 
 * FIXME: legacy code
 * 
 * @param event
 * @return
 */
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


/**
 * FIXME: legacy code
 * 
 * @return
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



/**
 * Sorts two values
 * 
 * @param ersterWert
 * @param zweiterWert
 * @return
 */
function unicodeCollation(ersterWert, zweiterWert) {
	if (!isNaN(ersterWert) && !isNaN(zweiterWert)) { // vergleich von 2 Zahlen
		return ersterWert - zweiterWert;
	}
	if (!isNaN(ersterWert) && isNaN(zweiterWert)) { // vergleich erster Wert ist eine Zahl und zweiter Wert ist ein String
		return -1;
	}
	if (!isNaN(zweiterWert) && isNaN(ersterWert)) { // vergleich zweiter Wert ist eine Zahl und erster Wert ist ein String
		return 1;
	}
	if (ersterWert.toLowerCase() < zweiterWert.toLowerCase()) { // vergleich zweier Strings
		return -1;
	} 
	if (zweiterWert.toLowerCase() < ersterWert.toLowerCase()) { // vergleich zweier Strings
		return 1;
	}
	if (zweiterWert.toLowerCase() == ersterWert.toLowerCase()) { // vergleiche zwei gleiche Strings(im toLower Fall)
		if (ersterWert < zweiterWert) {
			return -1;
		} 
		if (zweiterWert < ersterWert) {
			return 1;
		} 
		return 0;
	}
	return 0;
}


/**
 * removes a relation from the list of shown relations
 * @param evt
 * @return
 */
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
					"<li>" + 
					"<a onclick='hideConcept(event)' href='/ajax/pickUnpickConcept?action=hide&tag=" + encodeURIComponent(upper) + "&ckey=" + ckey + "'>" + String.fromCharCode(8595) + "</a> " +
					"<a href='/concept/user/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(upper) + "'>" + upper + "</a>" +
					" " + String.fromCharCode(8592) + " " +
					"</li>"
			);

			// add subtags
			var lowerul = $("<ul></ul>");

			// iterate over lower tags
			var lowers = rel.getElementsByTagName("lower");
			for (y = 0; y < lowers.length; y++) {
				var lower = lowers[y].firstChild.nodeValue;
				// add item
				lowerul.append("<li><a href'/user/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(lower) + "'>" + lower + "</a> </li>");
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
	updateBasket("action=" + pickUnpick + "&hash=" + encodeURIComponent(param));

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
	updateBasket(xget_event(evt).getAttribute("href").replace(/^.*?\?/, ""));

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




/**
 * picks/unpicks publications in AJAX style
 * 
 * @param param
 * @return
 */
function updateBasket (param) {
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

	/*
	 * NOTE: don't refactor, otherwise localization (I18N) get's broken, because
	 * generate_localized_strings.pl does not find the keys for the messages 
	 */
	if (type == "bibtex") {
		details.appendChild(document.createTextNode(getString("bibtex.actions.details")));
		details.title=getString("bibtex.actions.details.title");
	} else {
		details.appendChild(document.createTextNode(getString("bookmark.actions.details")));
		details.title=getString("bookmark.actions.details.title");
	}

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


/**
 * adds a fade-out effect to error boxes
 * 
 * @param className
 * @return
 */
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
	if ($("." + className)) {
		// this is a workaround because the tags input element's id is not 'tags.so-and-so' but 'inpf'
		$('#inpf').keyup(function() {$('#tags\\.errors').parent().fadeOut('slow');});
	}
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
function addBibtexExportOptions() {
	/*
	 * add and show export options when hovering over the link
	 */
	var elm = $("#bibtexListExport"); 
	elm.mouseover(function() {
		/*
		 * anchor element where to put the options
		 */
		var anchor = $("#bibtexListExportOptions");

		/*
		 * create form
		 */
		var form = $(
				"<form class='exportoptions' method='get' action='" + elm.attr("href") + "'>" +
				"<input type='checkbox' name='generatedBibtexKeys'/>" + getString("post.resource.generateBibtexKey.export") + "<br/>" +
				"<input type='checkbox' name='firstLastNames'/>" + getString("post.resource.personnames.export") + "<br/>" +
				getString("posts") + ": " +
				"</form>"
		);
		
		// number of posts to be exported
		var items = new Array(5, 10, 20, 50, 100, 1000);
		for (var i = 0; i < items.length; i++) {
			form.append("<input type='radio' name='items' value='" + items[i] + "'/>" + items[i] + " ");
		}

		// submit button
		form.append("<br/><input type='submit' value='" + getString("export.bibtex.title") + "'/>");

		
		// insert form after export link (make it empty before)
		anchor.empty();
		anchor.append(form);

		// show export box 
		anchor.show("fade", {}, 500);
	});
}

/**
 * adds javascript to the list headers to create a dropdown menu with
 * list action options (export, basket, sort, ...)
 */
function addListOptions() {
	/*
	 * show list action options when hovering over the div
	 */
	$.each(["bookmark", "publication"], function(index, value) {
		var optBoxAnchor = $("#" + value + "ListConfig");
		optBoxAnchor.mouseover(function() {
			var optBox =  $("#" + value + "ListOptions");
			/*
			 * close option box (and bibtex detail option box) when leaving it with the mouse
			 */
			optBox.mouseleave(function() {
				$(this).hide("fade", {}, 500);
				$("#bibtexListExportOptions").hide();
			});
			if (optBox.css("display") == "none") {
				optBox.show("fade", {}, 500);	
			}
		});
		/*
		 * option box can be closed by clicking on the div
		 */
		optBoxAnchor.click(function() {
			$("#" + value + "ListOptions").hide("fade", {}, 500); 
		});
	});
	addBibtexExportOptions();
}





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
							self.val('').removeClass('descriptiveLabel' ).trigger('focus');
							return false;
						}
					});
				}		
		);
	};
})(jQuery);

/**
 * For mobile layout: add edit links to toolbar
 * 
 * @return
 */
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