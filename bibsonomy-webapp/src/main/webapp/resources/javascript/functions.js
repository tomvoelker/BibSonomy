var activeField = null;
var tagbox      = null; // used in style.js!
var pwd_id_postfix = "_form_copy"; // id of password input field copy

/*
 * variables for cursor position
 */
var getPos = null;
var setPos = null;
var getSetPos = 0;

var constants = {
	RESPONSE_TIMEOUT: 5000
}

$(function() {
	initView();
	$('a.confirmdelete').click(function() {
		var messageKey = $(this).data('type');
		return confirmDeleteByUser(messageKey);
	});
	/*
	 * adds a click event handler for the search scope form option entries
	 */
	$('#scopeDomain').children().each(function(i, el){
		$(el.childNodes[0]).click(function(e){
			e.preventDefault();
			$("#scope").val($(this).data("domain"));
			$('#searchForm').attr('action','/redirect').submit();
		});
		
	});
});

function confirmDeleteByUser(messageKey) {
	// get confirmation
	if (userSettings.confirmDelete) {
		var message = getString("delete.confirm." + messageKey);
		message += "\n" + getString("delete.confirm");
		return confirm(message);
	}
	return true;
}

/**
 * This method is called on document.ready.
 * @return
 */
function initView() {
	var tagbox_style = userSettings.tagbox.style;
	var tagbox_sort = userSettings.tagbox.sort;
	var tagbox_minfreq = userSettings.tagbox.minfreq;
	/*
	 * assign functions for cursor position; FIXME: why here?
	 */
	setPos = function(p) {
		if (p == null)
			return getSetPos;
		return (getSetPos = p);
	};
	getPos = function() {
		return setPos(null);
	};
	
	/* scope: text input fields
	 * 
	 * adds hints for text input fields
	 * 
	 */
	add_hints();

	/* scope: text input fields
	 * 
	 * add a callback to every input that has the descriptiveLabel class
	 * so that hints get removed after focusing the input form 
	 */
	$('.descriptiveLabel').each(function() {
		$(this).descrInputLabel({});
	});
	
	/* scope: error boxes
	 * 
	 */
	prepareErrorBoxes('dissError');
	
	/* *************************************************************************
	 * scope: sidebar
	 */
	/*
	 * initialize the tag box
	 */
	tagbox = $(".tagbox");
	$(tagbox).each(function(index, item) {
		init_tagbox(item, tagbox_style, tagbox_sort, tagbox_minfreq);
	});

	/* *************************************************************************
	 * scope: post lists
	 */
	/*
	 * adds list options (for bookmarks + publication lists)
	 */
	addListOptions();
	/*
	 * in-place tag edit for posts
	 */
	$(".editTags").click(editTags);
	if($('.extend').hoverIntent!== undefined)
		$('.extend').hoverIntent(function(event) {
			var infoBox = $('div', this);
			infoBox.show("fade", {}, 500);
		}, function(event){
			var infoBox = $('div', this);
			infoBox.hide("fade", {}, 500);
		});
}

/**
 * Method to delegate processing of codes depending on code type.
 * 
 * @param text The code.
 * @param type The code type.
 */
function urlFromFlash(text, type) {
	if(type == "ISBN") {
		processISBN(text)
	} else if( type == "URI" ) {
		processQRCode(text);
	}
}

/**
 * Reads ISBN from reader app and automatically posts publication.
 * 
 * @param text The ISBN
 */
function processISBN(text) {
	
	/*
	 * check that we are on post publication site
	 */
	if(document.URL.indexOf("/postPublication") != -1) {
		document.location.href = "/editPublication?selection=" + escape(text);
	}
}

/**
 * Adds read entry from reader app to the clipboard or to post it automatically.
 * Entry is only added if it has a pick link. Afterwards the pick link is clicked.
 * Entry is pulled via ajax one more time to get the actual entry with unpick link.
 * 
 * @param text The query link.
 */
function processQRCode(text) {
	
	var query = text.substring(text.indexOf("/bibtex"));
	
	/*
	 * check that we are on post publication site
	 */
	if(document.URL.indexOf("/postPublication") != -1) {
		
		var split = query.split("/");
		
		/*
		 * get second to the last and last entry of split URL.
		 * should be hash and user.
		 */
		document.location.href = "/editPublication?hash=" + escape(split[split.length - 2]) + "&user=" + escape(split[split.length - 1]);
	
	/*
	 * we should be on clipboard site
	 */
	} else {
		$.ajax({
			url : "/posts" + escape(query),
			dataType : "html",
			success : function(data) {
				
				var found = false;
				
				if($(data).find('a').is('.unpick')) {
					found = true;
				}
				
				if(!found) {
					
					$(data).find('a.pick').click();
					$(document).ajaxStop(function() {
	
						$.ajax({
							url : "/posts" + escape(query),
							dataType : "html",
							success : function(actData) {
								
								/*
								 * remove no entry message
								 */
								$('span.post, span.none').remove();
								
								$("#publications_0 ul.posts").prepend($(actData));
								
								/*
								 * FIXME: does this really always work? 
								 * What about posts that have already been prepared?
								 * Are there any methods missing?
								 */
								$(".editTags").click(editTags);
							}
						});
						$(this).unbind('ajaxStop');
					});
	
				}
			}
		});	
	}
}


/**
 * Retrieves the posts for the given query and appends them to the given list. 
 * 
 * @param query - A path + query that describes the posts to retrieve.
 * @param list - The list where the posts shall be appended.
 * @return
 */
function renderPosts(query, list) {
	$.ajax({
		url : "/posts" + query,
		dataType : "html",
		success : function(data) {
			$(list).append($(data));
			/*
			 * FIXME: does this really always work? 
			 * What about posts that have already been prepared?
			 * Are there any methods missing?
			 */
			$(".editTags").click(editTags);
		}
	});
}

function updatePosts(query, seconds) {
	setInterval(function() {
		$.each(["bookmark", "publication"], function(index, resourceType) {
			/*
			 * we will add the posts to this list
			 */
			var list = $("#" + resourceType + "s_0 ul.posts");
			/*
			 * get the date of the most recent post 
			 */
			var date = list.find("li:first span[itemprop='dateCreated']").attr("content");
			/*
			 * get newer posts
			 */
			$.ajax({
				url : "/posts" + query + "?startDate=" + encodeURIComponent(date) + "&resourcetype=" + resourceType,
				dataType : "html",
				success : function(data) {
					/*
					 * add hidden posts to list and show them in reverse order (after some time)
					 */
					$(data).hide().prependTo(list).reverse().each(function(index) {
						var post = $(this);
						// fix class for background color (FIXME: sometimes doesn't work) 
						if (post.next().hasClass("odd")) {
							post.removeClass("odd").addClass("even");
						} else {
							post.removeClass("even").addClass("odd");
						}
						setTimeout(fadePostIn, index * 1000, post);
					});

					/*
					 * FIXME: does this really always work? 
					 * What about posts that have already been prepared?
					 * Are there any methods missing?
					 */
					$(".editTags").click(editTags);
				}
			});
		});
	}, 1000 * seconds);
}

/**
 * Fades the given post in and removes the last post of the surrounding post list.
 * 
 * @param post
 * @return
 */
function fadePostIn(post) {
	post.fadeIn("slow").parents("ul.posts").find("li.post:last").fadeOut("slow").remove();
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
	 * group
	 */
	var tag = $("#inpf[name|='group']");
	if (tag.length && (tag.val() == "" || tag.val() == getString("navi.group.hint"))) {
		tag.val(getString("navi.group.hint"));
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
 * Clears #inpf_tags if it's value is equal to the tag hint.
 * 
 * @return
 */
function clear_tags() {
	var tag = $("#inpf_tags");
	if (tag.val() == getString("navi.tag.hint")) {tag.val('');}
}

/**
 * 
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
 * @param element
 * @return
 */
function hideConcept(element) {
	// get concept name
	var concept = element.parentNode.getElementsByTagName("a")[1].firstChild.nodeValue;
	// update relations list, hide concept
	return updateRelations("hide", concept);
} 

/**
 * updates the user's relations in the sidebar
 * 
 * @param action
 * @param concept
 * @return
 */
function updateRelations (action, concept) {
	$.ajax({
		url : "/ajax/pickUnpickConcept?action=" + action + "&tag=" + encodeURIComponent(concept) + "&ckey=" + ckey,
		success : ajax_updateRelations,
		dataType : "xml"
	});
	return false;
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
					"<a onclick='return hideConcept(this);' href='/ajax/pickUnpickConcept?action=hide&tag=" + encodeURIComponent(upper) + "&ckey=" + ckey + "'>" + String.fromCharCode(8595) + "</a> " +
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

/**
 * Edit tags for a post in-place.
 * 
 * @return
 */
function editTags() {
	alert("editing tags")
	/*
	 * div around all tags (will be hidden/replaced with tag edit box)
	 */
	var ptags = $(this).parents(".ptags");
	/*
	 * load the jQuery script for sending the form
	 */
	$.getScript(
			"/resources/jquery/plugins/form/jquery.form.js",
			function() {
				ptags.hide();
				/*
				 * collect regular tags + system tags into a string
				 */
				var tagString = "";
				ptags.find("ul li a").each(function() {tagString += $(this).html() + " ";});
				ptags.find("div.hiddenSystemTag div a").each(function() {tagString += $(this).html() + " ";});
				/*
				 * extract hash of post from edit URL
				 */
				var editUrl = ptags.parents(".post").find(".action .edit").attr("href");
				var hash = editUrl.substr(editUrl.indexOf("intraHashToUpdate=") + "intraHashToUpdate=".length, 32);
				/*
				 * extract type of post
				 */
				var type = editUrl.search(/^\/editPublication/) != -1 ? "bibtex" : "bookmark";
				/*
				 * add form to edit the tags
				 * also add old tags and marked posts to iterate through and update action, 
				 * because batchedit controller needs it
				 */
				/*
				 * XXX: this is the only way to get the attribute value in the encoding we need
				 * not nice!
				 */
				var encodedTagString = $('<div />').html(tagString).text(); 
				var form = $("<form method='post' action='/batchEdit?updateExistingPost=true&format=ajax&resourcetype=" + type + "&action=2' class='editTags'><input type='hidden' name='ckey' value='" + ckey + "'/></form>");
				var input = $("<input type='text' class='postTagInput' name=\"newTags['" + hash + "']\"'/>").attr("value", encodedTagString);
				var oldTagsInput = $("<input type='hidden' name=\"oldTags['" + hash + "']\" />").attr("value", encodedTagString);
				var checkedInput = $("<input type='hidden' name=\"posts['" + hash + "']\" value='true' checked='checked' />");
				var submit = $("<input type='submit' class='postTagButton' value='" + getString("post.meta.edit") + "'/>");
				form.append(input).append(oldTagsInput).append(checkedInput).append(submit);
				/*
				 * resize input field
				 */
//				input.width(parseInt(ptags.width()) + "px");
				/*
				 * show form
				 */
				ptags.after(form);
				/*
				 * start the tag autocompletion
				 */
				startTagAutocompletion(input, true, true, true, false);
				/*
				 * add submit handler (that removes the form and re-builds the tags)
				 */
				form.ajaxForm(function() {
					/*
					 * get new tags from form input field
					 */
					var tags = input.val().split(" ");
					/*
					 * close the autocompletion
					 */
					endTagAutocompletion(input);
					/*
					 * remove form
					 */
					form.remove();
					/*
					 * remove old tags
					 */
					ptags.find("li").remove();
					/*
					 * add new tags
					 */
					var ul = ptags.find("ul.tags");
					for (var t = 0; t < tags.length; t++) {
						ul.append("<li><a href='/user/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(tags[t]) + "'>" + tags[t] + "</a></li>");
					}
					ptags.show();
				});
			}
	);
}

/**
 * Provides localized messages for JavaScript functions. 
 * 
 * Always use this method to get your messages! If you add a new message,
 * call generate_localized_strings.pl afterwards.
 * 
 */
function getString(key, params) {
	if (typeof LocalizedStrings == "undefined") return "???" + key + "???"; 
	var s = LocalizedStrings[key];
	if (!s) return "???" + key + "???";
	
	if (params != undefined) {
		for (var i = 0; i < params.length; i++) {
			s = s.replace(new RegExp('\\{' + i + '\\}', "g"), params[i]);
		}
	}
	
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
		// this is a workaround because the tags input element's id is not 'tags.so-and-so' but 'inpf_tags'
		$('#inpf_tags').keyup(function() {$('#tags\\.errors').parent().fadeOut('slow');});
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
	if(elm!=null && elm !== undefined && elm.hoverIntent !== undefined)
	elm.hoverIntent(function() {
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
		/*
		 * prevent click to be propagated to parent (otherwise the whole 
		 * action box will dissappear, see click handler in addListOptions() ).
		 */
		form.click(function(event) {
			event.stopPropagation();
		});		

		// insert form after export link (make it empty before)
		anchor.empty();
		anchor.append(form);

		// show export box 
		anchor.show("fade", {}, 500);
	}, function() {});
}

/**
 * adds javascript to the list headers to create a dropdown menu with
 * list action options (export, clipboard, sort, ...)
 */
function addListOptions() {
	
	/*
	 * show and hide list actions when clicking on the div
	 */
	$.each(["bookmarkList", "publicationList", "page"], function(index, value) {
		
		var optBoxAnchor = $("#" + value + "Config");
		var optBox 		 = $("#" + value + "Options");
		var timeout;
		
		/*
		 * Getter for the timeout
		 */
		var getTo = function() {
			return timeout;
		};
		
		/*
		 * Setter for the timeout
		 */
		var setTo = function(t) {
			timeout = t;
		};
		
		/*
		 * Function to hide the list options
		 */
		var callbackHide = function() {
			setTo(setTimeout(function(){ optBox.hide("fade", {}, 500); optBoxAnchor.css("background-position", "0px -235px");}, 400));
		};
		
		/*
		 * Function to show the list options
		 */
		var callbackShow = function() {
			if( ! optBox.is(":visible")) {

				optBox.show("fade", {}, 500);
				
				optBoxAnchor.css("background-position", "-61px -236px");

				// hide extended bibtex export options each time when opening the menu
				$("#bibtexListExportOptions").hide();
			}
			window.clearTimeout(getTo());
		};	
		
		if ( optBoxAnchor.length ) {
			optBoxAnchor.mousemove(function() {callbackShow();}).mouseover(function() {callbackShow();}).mouseleave(function() {callbackHide();});
		}
		
	});
	
	/*
	 * add mouseover to "BibTeX" link (publications only) to display extended options
	 */
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
		return this;
	};
})(jQuery);

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
 * jQuery "plugin" to get elements in reverse order. Use like this:
 * 
 * $(selector).reverse();
 */
jQuery.fn.reverse = [].reverse;

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
 * Function to start the tag autocompletion.
 * 
 * @param textfield 	- the textfield for the autocompletion - e.g. $("#inpf")
 * @param isPost 		- only true if the autocompletion is started in the post list (quick tag edit), false if it is started e.g. in the search bar
 * @param multiTags 	- true if several tags are allowed in the text field, false: the textfield will be emptied and the suggested tag put in it. 
 * @param sendAllowed	- true if send:USER is allowed in the given textfield, otherwise false
 * @param showOrigin	- true if the origin of a tag (user, copy, recommended) should be shown, otherwise false
 */
function startTagAutocompletion (textfield, isPost, multiTags, sendAllowed, showOrigin) {
	
	if (textfield[0] == null)
		return;
	
	var textfieldValue 	= null;
	var valueArray 		= null;
	var ajaxTagArray	= null;
	var userInput		= null;	
	var friends 		= null;

	
	/*
	 * only if sendAllowwed == true, get the friends of the user to recommend them in the case of "send:"
	 */
	if(sendAllowed) {
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
	}
		
	var autocompleteObj = textfield.autocomplete({		
		source: function( request, response ) {
			
			textfieldValue 	= textfield.val();
			valueArray 		= textfieldValue.split(" ");
			userInput 		= valueArray[valueArray.length - 1];
			
			/*
			 * abort if the user types "send:" and sendAllowed is set to false.
			 */
			if (userInput.indexOf("send:") != -1 && !sendAllowed) {
				return;
			}
			
			/*
			 * if the user typed nothing - do nothing 
			 */
			if (userInput.length != 0) {
				$.ajax({
					url: "/json/prefixtags/user/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(userInput),
					dataType: "jsonp",
					success: function( data ) {
						/*
						 * "send:" is part of the last array index - so show the friends of the loginUser
						 * else
						 * we recommend the tags
						 */
						if (userInput.indexOf("send:") != -1) {	
							//Get the user input of the user and slice it, so userInput doesn't contain "send:"
							userInput = String(userInput).slice(5);
							var regex = new RegExp(userInput);

							response($.map( friends, function(friend) {
								/*
								 * If the post is already sent to a user (Example: "sent:bsc"), don't recommend this user ("bsc")
								 * If the user input is "nra", recommend only users which username begins with "nra" 
								 */
								if(textfieldValue.indexOf(friend) == -1 &&
										friend.search(regex) == 0) {
									return { value: friend};
								}
							}));
						} else {
							
							var regex 			= new RegExp(userInput);
							var recommendedTags = new Array();
							var copiedTags 		= new Array();
							ajaxTagArray		= new Array();
							
							/*
							 * Get the tag-name's of "copied post" and "recommendation" -> Save them in a detached array
							 * Only add the tags which are matching with the following patterns:
							 * textfieldValue.indexOf(name) == -1		-> By now the tag isn't used for this post yet -> suggest it !
 							 * name.search(regex) == 0 					-> The tag starts with the letters of the userInput
							 */
							$("#recommendedTags li, .tagbox li a").each(function(index, item) {
								var name = item.innerHTML.substring(0, item.innerHTML.length - 1);
								var tags = name.split(",");
								
								if(textfieldValue.indexOf(name) == -1 && name.search(regex) == 0 ) {
									if(name.indexOf(",")>-1)
										for(var i in tags) 
											if(recommendedTags.indexOf(tags[i])==-1) recommendedTags.push(tags[i]);
									else recommendedTags.push(name);
								}
							});

							$("#copiedTags li, .tagbox li a").each(function(index, item) {
								var name = item.innerHTML.substring(0, item.innerHTML.length - 1);
								if(textfieldValue.indexOf(name) == -1 && name.search(regex) == 0 ) {
									if(name.indexOf(",")>-1) 
										for(var i in tags) 
											if(copiedTags.indexOf(tags[i])==-1) copiedTags.push(tags[i]);
									else copiedTags.push(name);
								}
							});
							
							/*
							 * Get the Tags which the user already used in other posts
							 */
							var tags = $.map( data.items, function(item) {
								
								var recommendedTagsIndex 	= recommendedTags.indexOf(item.label);
								var copiedTagsIndex			= copiedTags.indexOf(item.label);
								
								/*
								 * If the array of "copied post" and "recommendation" tags contains the actual tag name,
								 * remove the tag name in the array "copied post", "recommendation" or both.
								 */
								if(recommendedTagsIndex != -1) {
									recommendedTags.splice(recommendedTagsIndex, 1);
								}
								
								if(copiedTagsIndex != -1) {
									copiedTags.splice(copiedTagsIndex, 1);
								}
								
								/*
								 * don't suggest tags, which are already included in the input field
								 */
								if(textfieldValue.indexOf(item.label) == -1) {
									
									/*
									 * Store all tags with origin user to this array.
									 * Later in the "open:" method of the autocompletion plugin we need them to distingush the tag origin.
									 */
									ajaxTagArray.push(item.label);
									
									return { value: (item.label),
											 label: (item.label),
											 count: (item.count)};
								}
							});
																					
							/*
							 * Now, the tags which are in the array of "copied post" and "recommendation" tags 
							 * aren't used in other posts of the user by now.
							 * Suggest them by adding them to the tags map.
							 */
							recommendedTags.forEach(function(name) {
								if( $.grep(tags, function(t){ return t.value == name; }).length == 0) {
									tags.push({value: name, 
											   label: name,
											   count: Number.MAX_VALUE});
								}								
							});

							copiedTags.forEach(function(name) {
								if( $.grep(tags, function(t){ return t.value == name; }).length == 0) {
									tags.push({value: name, 
											   label: name,
											   count: Number.MAX_VALUE});
								}
							});
								
							/*
							 * Response all tags with origin user, recommended and copy.
							 * Later in the "open:" method we add the span for the tag origin.
							 */
							response(tags);
						}
					}
				});
			}
		},
		minLength: 1,
		response: function( event, ui ) {						
			/*
			 * Sort all tags by count of usage 
			 */
			function compare(a,b) {
			  if (a.count > b.count)
			     return -1;
			  if (a.count < b.count)
			    return 1;
			  return 0;
			}

			ui.content.sort(compare);

			/*
			 * Append for each tag the count of usage
			 */
			$.each(ui.content, function(index, value) {
				if(value.count != Number.MAX_VALUE) {
					value.label = value.value + " (" + value.count + ")";
				} else {
					value.label = value.value;
				}
			});

			return;
		},
		select: function( event, ui ) {
			
			var item = ui.item;
			var textArea = $(event.target);
			var text = item.value;
			var substring = textfieldValue.substr(0, textfieldValue.length - (userInput.length));
			
			/*
			 * If multiTags is true, the user can apply more as one tag.
			 * Otherwise the user can only use one tag (we replace the textInput Field with the recommended tag)
			 */
			if(multiTags) {
				/*
				 * Distinguish if the user has typed "send:" and want to get and set the recommended friends
				 * or he wants only the normal tags set
				 */
				if(userInput.indexOf("send:") != -1) {
					textArea.val(substring + "send:" + text + " ");
				} else {
					textArea.val(substring + text + " ");	
				}
			} else {
				textArea.val(text);
			}
			
			return false;
		},
		focus: function( event, ui ) {
			return false;
		},
        open: function(event,ui) {
        	// TODO: why not using formatResult callback for this?
        	/*
        	 * 
        	 * 1.	Bold letters
        	 * This functionality below causes the bold letters of the autocompletion.
        	 * The entire part of the recommendation starts with bold text-width (style.css -> .ui-menu-item).
        	 * Below in the acData iteration, we replace the input of the user of the textfield (e.g. "Buil")
        	 * in the recommendation (The recommendation contains "Building" and we replace "Buil" with normal text-width
        	 * in a span with the class ".ui-autocomplete-term" (style.css)).
        	 * 
        	 * 2.	Tag origin
        	 * Add a span with the tag origin in every (tag-) list element.
        	 * 
        	 */
        	
            var acData = $(this).data('autocomplete');
            var styledTerm		= null;
            var termHighlighted = null;
			var recommendedTags = null;
			var copiedTags 		= null;
			
            /*
             * Here we distinguish if the user wants to send the post to a user (user input contains "send:"),
        	 * or the user wants the tags. If the user used "send:", we don't want to suggest like "send:nraabe".
        	 * We want to suggest "nraabe" in the autocompletion list.
             */
			if (userInput.indexOf("send:") != -1) {
				termHighlighted = String(userInput).slice(5);
			} else {
				termHighlighted = userInput;
			}

			/*
			 * This part is only used, if we want to show the tag origin.
			 * The functionality below gives us the tags with origin recommended and copy. 
			 */
			if(showOrigin) {
	            var regex 		= new RegExp(userInput);
				recommendedTags = new Array();
				copiedTags 		= new Array();
				
				/*
				 * Get the tag-name's of "copied post" and "recommendation" -> Save them in a detached array
				 * Only add the tags which are matching with the following patterns:
				 * textfieldValue.indexOf(name) == -1		-> By now the tag isn't used for this post yet -> suggest it !
				 * name.search(regex) == 0 					-> The tag starts with the letters of the userInput
				 */
				$("#recommendedTags li, .tagbox li a").each(function(index, item) {
					var name = item.innerHTML.substring(0, item.innerHTML.length - 1);
					if(textfieldValue.indexOf(name) == -1 && name.search(regex) == 0 ) {
						recommendedTags.push(name);
					}
				});

				$("#copiedTags li, .tagbox li a").each(function(index, item) {
					var name = item.innerHTML.substring(0, item.innerHTML.length - 1);
					if(textfieldValue.indexOf(name) == -1 && name.search(regex) == 0 ) {
						copiedTags.push(name);
					}
				});
			}

			acData
                .menu
                .element
                .find('a')
                .each(function() {
                    var me = $(this);
                    
                    
                    /*
                     * Example: user types "goo" - we want to suggest "google"
                     * We take "google" and replace "goo" with the span with thin letters.
                     */
					var recommendedTag = me.text();					
					
					var regex = /(\(.*?\))/;
					regex.exec(recommendedTag);
					recommendedTag = recommendedTag.replace(/(\s\(.*?\))/, "");
					var usageCounter = RegExp.$1;
					
					me.empty(); // remove old content
                    
					var styledTerm = $('<span class="ui-autocomplete-term"></span>').text(termHighlighted);
                    
        			if (userInput.indexOf("send:") != -1) {
        				me.append(sytledTerm).append(userInput.substring(5)); // 5 is used to slice "send:"
        			} else {
        				me.append(styledTerm).append(recommendedTag.substring(termHighlighted.length));
        			}
        			
        			/*
        			 * Show the user the tag origin.
        			 * Build a string tagOrigin. After that create a span with the text of the tag originand 
        			 * and append it to the list element oh this tag suggestion. 
        			 */
        			if (showOrigin) {

        				var tagOrigin = "";

            			if(ajaxTagArray.indexOf(me.text()) != -1) {
            				tagOrigin = tagOrigin + getString("post.actions.edit.tags.myTags");
            			}

            			if(recommendedTags.indexOf(me.text()) != -1) {
            				if(tagOrigin.length > 0 ) {
            					tagOrigin = tagOrigin + ", " + getString("post.actions.edit.tags.recommended");
            				} else {
            					tagOrigin = getString("post.actions.edit.tags.recommended");
            				}
            			}

            			
            			if(copiedTags.indexOf(me.text()) != -1) {
            				if(tagOrigin.length > 0 ) {
            					tagOrigin = tagOrigin + ", " + getString("post.actions.edit.tags.copied");
            				} else {
            					tagOrigin = getString("post.actions.edit.tags.copied");
            				}
            			}
            			
        				var tagOriginSpan 		= document.createElement("span"); 
        				tagOriginSpan.innerHTML = tagOrigin + " " + usageCounter;
        				tagOriginSpan.className	= "ui-autocomplete-tagOrigin";

        				me.append(tagOriginSpan);
        			}
            });
			
			/*
			 *	- create a listener for the tab key
			 *	- if the tab key is pressed without selected the tag -> add the first tag and close the autocompletion   
			 */
			textfield.keydown(function(event){
			    var newEvent = $.Event('keydown', {
			        keyCode: event.keyCode
			    });
			    
			    if (newEvent.keyCode == $.ui.keyCode.TAB) {
			        newEvent.keyCode = $.ui.keyCode.DOWN;
			        $(this).trigger(newEvent);
			        newEvent.keyCode = $.ui.keyCode.ENTER;
			        $(this).trigger(newEvent);

			        return false;
			    }
			});
			
	        $(this).autocomplete('widget').css('z-index', 127);
	        
	        return false;
        }
	});
	
	if(isPost) {
		autocompleteObj.autocomplete( "option", "appendTo", textfield.parent() );
	}
	
	autocompleteObj.autocomplete('enable');
};

/**
 * Function to finish the tag autocompletion.
 * 
 * @param textfield - the textfield for the autocompletion - e.g. $("#inpf")
 */
function endTagAutocompletion (textfield) {
	textfield.autocomplete('disable');
};


/**
 * Function to start the post autocompletion.
 * 
 * @param textfield - the textfield for the autocompletion - e.g. $("#inpf")
 */
function startPostAutocompletion (textfield) {
	if (textfield[0] == null)
		return;
	
	var userInput = null;
	var autocompleteObj = textfield.autocomplete({
		source: function( request, response ) {
			userInput = textfield.val();
			/*
			 * if the user typed nothing - do nothing 
			 */
			if (userInput.length != 0) {
				$.ajax({
					url: "/suggestionservice?prefix=" + encodeURIComponent(userInput),
					success: function( data ) {
						var tags = $.map( data, function(item) {
								return { value: (item.name),
										 label: (item.name),
										 count: (item.members)};
							});
						response(tags);
					},
					error: function (data) {
						console.log("Ajax Error in startPostAutocompletion - functions.js");
					}
				});
			}
		},
		minLength: 1,
		select: function( event, ui ) {
			var item = ui.item;
			var textArea = $(event.target);
			var text = item.value;
			
			textArea.val(text);
			
			return false;
		},
		focus: function( event, ui ) {
			return false;
		},
		open: function(event,ui) {
			var acData = $(this).data('autocomplete');
			var styledTerm		= null;
			var termHighlighted = userInput;
			
			acData.menu.element.find('a').each(function() {
			
			var me = $(this);
			
			/*
			 * Example: user types "goo" - we want to suggest "google"
			 * We take "google" and replace "goo" with the span with thin letters.
			*/
			var recommendedTag = me.text();
			var regex = /(\(.*?\))/;
			regex.exec(recommendedTag);
			recommendedTag = recommendedTag.replace(/(\s\(.*?\))/, "");
			var usageCounter = RegExp.$1;
			
			me.empty(); // remove old content
			
			var styledTerm = $('<span class="ui-autocomplete-term"></span>').text(termHighlighted);
			me.append(styledTerm).append(recommendedTag.substring(termHighlighted.length));
		});
		}
	});
	$(this).autocomplete('widget').css('z-index', 110);
	autocompleteObj.autocomplete('enable');
}


/**
 * Function to setup radio buttons on /export/ page for the number of posts to export
 */
function setupPostExportSize() {
	
	var exportPostSize = null;
	
	//get the checked value of the radio buttons and set the variable exportPostSize
	var radioBtns = $("[name='items']");
	for(i = 0; i < radioBtns.length; i++) {
	    if(radioBtns[i].checked){
	        exportPostSize = radioBtns[i].value;
	    }
	}
	
	var links = $(".export-link");
	
	// append to all links '?items=5' - exportPostSize initiated with '5'
	$.each(links, function(index, value) {
		// get the elements of all links [<a..] 
		var linkHref = $(value).attr('href');
			
		// contains the href any other parameters? Distinguish this cases.
		if (linkHref.indexOf("?") != -1) {
			if (linkHref.indexOf("items=") != -1) {
				linkHref = linkHref.replace(/\items=\d*/g, "items=" + exportPostSize);
			} else {
				linkHref = linkHref + '&items=' + exportPostSize;
			}
		} else {
			linkHref = linkHref + "?items=" + exportPostSize;
		}
		
		$(value).attr('href', linkHref);
	});
	
	//A click on a radio button replaces in any link the old value X '?items=X' with the new value Y '?items=Y'
	$("#exportOptions").click(function(event) {
		
		//get the checked value of the radio buttons and set the variable exportPostSize
		for(i = 0; i < radioBtns.length; i++){
		    if(radioBtns[i].checked){
		        exportPostSize = radioBtns[i].value;
		    }
		}
		
		//set the new value "exportPostSize" for parameter "items"
		$.each(links, function(index, value) {
			if(value.href.indexOf("#jabref") == -1) {
				value.href = value.href.replace(/\items=\d*/g, "items=" + exportPostSize);	
			}
		});
	});
};

/**
 * Function to setup link generation for export (in formats.tagx) on /export/ page (only for <select ... >)
 * 
 * @param value - the link of the website which should be exported
 */
function generateExportPostLink(value) {
	
	var exportPostSize = null;
	
	if(value.length == 0) {
		return;
	}
	
	//get the checked value of the radio buttons and set the variable exportPostSize
	var radioBtns = $("[name='items']");
	for(i = 0; i < radioBtns.length; i++){
	    if(radioBtns[i].checked){
	        exportPostSize = radioBtns[i].value;
	    }
	}
	
	if(exportPostSize != null) {
		if(value.indexOf("?") != -1) {
			if(value.indexOf("items=") != -1) {
				self.location = value.replace(/\items=\d*/g, "items=" + exportPostSize);
			} else {
				self.location = value + '&items=' + exportPostSize;
			}
		} else {
			self.location = value + '?items=' + exportPostSize;
		}
	} else {
		self.location = value;
	}
};