var request = null;
var style_sort = new Array("alph", "freq");
var style_show = new Array("cloud", "list");
var availableMinFreqCounts = new Array(1,2,5,10,50);
var userMinFreq = 1;
/*
 * points to the function with the current sort style, such that we can sort
 * tags again, if we retrieved them on a minFreq change 
 */
var currentSortStyle = null;

/**
 * XXX: variable "tagbox_minfreq_style" is defined in cloud.tagx!
 * 
 * Creates the link to change the tagbox 
 * 
 * @param tagbox
 * @param show
 * @param sort
 * @param minfreq
 * @return
 */
function init_tagbox(tagbox, show, sort, minfreq) {

	var styleList = $("<ul class='floatul'></ul>")
	.append(getStyleItem(tagbox, styleList, style_sort[sort], style_sort))
	.append(getStyleItem(tagbox, styleList, style_show[show], style_show));

	if (typeof tagbox_minfreq_style != "undefined") {
		if (tagbox_minfreq_style == "user") {
			styleList.append(getUserMinfreq(tagbox, styleList, minfreq));
		} else if (tagbox_minfreq_style == "default") {
			styleList.append(getUserMinfreq(tagbox, styleList, -1));  
		} 
	}
	changeTagBox(tagbox, styleList, style_show[show]);
	changeTagBox(tagbox, styleList, style_sort[sort]);

	tagbox.parentNode.insertBefore(styleList.get(0), tagbox); // XXX: refactor to jQuery
}

function changeTagBox(tagbox, style_list, mode) {
	if (mode == "list" || mode == "cloud"){
		$(tagbox).attr("class", "tag" + mode);
		style_list.children().eq(1).replaceWith(getStyleItem(tagbox, style_list, mode, style_show));
	} else if (mode == "alph" || mode == "freq") {
		style_list.children().eq(0).replaceWith(getStyleItem(tagbox, style_list, mode, style_sort));
		mode == "alph" ? setTagBoxAlph(tagbox) : setTagBoxFreq(tagbox);
	}
}


/**
 * Creates the style change links for changing sorting (alph/freq) and style (cloud/list) of the tag cloud.
 * 
 * @param tagbox
 * @param style_list
 * @param activeStyle
 * @param style_arr
 * @return
 */
function getStyleItem(tagbox, style_list, activeStyle, style_arr) {
	var styleSort = $("<li> (</li>");

	for (var i = 0; i < style_arr.length; i++) {
		if (activeStyle == style_arr[i]) {
			/*
			 * current style
			 */
			styleSort.append(getString("tagbox." + style_arr[i]));
		} else {
			/*
			 * other style
			 */
			styleSort.append(
					$("<a>" + getString("tagbox." + style_arr[i]) + "</a>")
					.css("cursor", "pointer")
					.click({"style" : style_arr[i]}, function(event) { // XXX: to circumvent the closure behavior, we must give the style information as "eventData"
						changeTagBox(tagbox, style_list, event.data.style);
					})
			);
		}
		if (i + 1 < style_arr.length) styleSort.append(" | ");
	}
	return styleSort.append(") ");
}



/**
 * create minfreq links for users (which are loaded via AJAX)
 * 
 * @param count
 * @return
 */
function getUserMinfreq(tagbox, styleList, count) {
	var minfreqList = $("<li> (" + getString("tagbox.minfreq") + " </li>");

	/*
	 * build links for each supported number of tags
	 */
	for (var i = 0; i < availableMinFreqCounts.length; i++) {
		if (count == availableMinFreqCounts[i]) {
			/*
			 * append count
			 */
			minfreqList.append(count);
		} else {
			/*
			 * append link
			 */
			var a = $("<a>" + availableMinFreqCounts[i] + "</a>");
			a.click({"count" : availableMinFreqCounts[i]}, function(event) { // XXX: to circumvent the closure behavior, we must give the count information as "eventData"
				sendMinfreqRequ(tagbox, event.data.count);
			});
			a.css("cursor", "pointer");
			minfreqList.append(a);
		}
		if (i + 1 < availableMinFreqCounts.length) minfreqList.append(" | ");
	}
	minfreqList.append(") ");

	return minfreqList;
}


/**
 * Sorts the tag cloud in the sidebar alphabetically.
 * 
 * XXX: Don't try to use jQuery for this, it slows down the code (by a factor of approx. 2).
 * 
 * @return
 */
function setTagBoxAlph(tagbox) {
	currentSortStyle = setTagBoxAlph;
	var collection_tagname = new Array(); // array of tagnames
	var collection_li = new Object(); // map tagname -> li

	/* store tagbox */
	var litags = tagbox.getElementsByTagName("li");
	for (var x = 0; x < litags.length; x++){
		var tagname = litags[x].getElementsByTagName("a")[0].firstChild.nodeValue;
		collection_tagname.push(tagname);
		collection_li[tagname] = litags[x];//.cloneNode(true); // does new code work in all browsers?
	}

	/* sort tags */
	collection_tagname.sort(unicodeCollation);

	/* build new tagbox */
	for (var x = 0; x < collection_tagname.length; x++) {
		var newli = collection_li[collection_tagname[x]];
		newli.appendChild(document.createTextNode(" "));
		tagbox.appendChild(newli);
	}

	/* clean box */
	delete collection_tagname;
	delete collection_li;
}

/**
 * Sorts the tag cloud in the sidebar by frequency.
 * 
 * XXX: Don't try to use jQuery for this, it slows down the code (by a factor of approx. 2).
 * 
 * @return
 */
function setTagBoxFreq(tagbox) {
	currentSortStyle = setTagBoxFreq;
	var collection_tagname = new Array();
	var collection_li = new Object();
	var collection_tagposts = new Object();
	var collection_numberofposts = new Array();

	/* store tagbox */
	var litags = tagbox.getElementsByTagName("li");
	for (var x = 0; x < litags.length; x++) {
		var tags = litags[x].getElementsByTagName("a");		
		var tagname = tags[0].firstChild.nodeValue;
		collection_tagname.push(tagname);
		collection_li[tagname] = litags[x];//.cloneNode(true); //NOTE: does new code work in all browsers?

		// extract post count
		var numberofpost = parseInt(tags[0].getAttribute("title").split(" ")[0]);
		collection_tagposts[tagname] = numberofpost;
		var newnumberofposts = true;
		for (y = 0; y < collection_numberofposts.length; y++) {
			if (collection_numberofposts[y] == numberofpost) {
				newnumberofposts = false;
			}					
		}
		// remember post count
		if (newnumberofposts) {
			collection_numberofposts.push(numberofpost);
		}
	}

	/* sort by number of posts (descending) */ 
	collection_numberofposts.sort(unicodeCollation).reverse();

	/* build new tagbox */
	for (var x = 0; x < collection_numberofposts.length; x++){
		var tags = new Array();
		for (var y = 0; y < collection_tagname.length; y++){
			var tagname = collection_tagname[y];
			if (collection_tagposts[tagname] == collection_numberofposts[x]) {
				tags.push(tagname);
			}
		}
		// sort tags with the same number of posts alphabetically
		tags.sort(unicodeCollation);
		for(var y = 0; y < tags.length; y++) {
			var newli = collection_li[tags[y]];
			newli.appendChild(document.createTextNode(" "));
			tagbox.appendChild(newli);
		}
		delete tags;
	}

	/* clean up */	
	delete collection_tagname;
	delete collection_li;
	delete collection_tagposts;
	delete collection_numberofposts;

}


/**
 * Retrieve the HTML tag cloud with the given parameters and replace the 
 * existing tag cloud with it.
 * 
 * @param tagbox
 * @param minfreq
 * @return
 */
function sendMinfreqRequ(tagbox, minfreq) {
	if (minfreq == null) minfreq = 1;
	userMinFreq = minfreq;

	$.ajax({
		url : "?tagcloud.minFreq=" + minfreq + "&tagstype=default&format=tagcloud",
		dataType : "html",
		success : function (html) {
			/*
			 * replace tags
			 */
			$(tagbox).empty().append($(html).find("ul.tagcloud li"));
			/*
			 * re-order tags
			 */
			currentSortStyle(tagbox);
			/*
			 * update minfreq links
			 */
			var styleList = $(tagbox).prev("ul");
			styleList.children().eq(2).replaceWith(getUserMinfreq(tagbox, styleList, minfreq));
		}
	});
}


/**
 * 
 * Switch the page default path to the full navigation path + form.
 * 
 * @param scope
 * @param event
 * @return
 */
function switchNavi(scope, event) {

	/*
	 * TODO: How to replace xget_event()?
	 */
	var element = $(xget_event(event));

	/*
	 * XXX: a hack to "unhover" the list. the worst part of it: we have to wait 
	 * some time until we make the list visible again (though it's then 
	 * otherwise hidden by CSS).
	 */
	var ul = element.parents("ul");
	ul.css("visibility", "hidden");
	window.setTimeout(function() {ul.css("visibility", "visible");}, 10);

	/*
	 * change form action to redirect with the given scope
	 */
	var form = $("#search form").attr("action", "/redirect").append("<input type='hidden' name='scope' value='" + scope + "'/>");

	/*
	 * Exchange text before form input field to the selected text.  
	 */
	var text = element.html();
	if (text.search(/- /) != -1) { // search in a group
		text = getString("navi.group") + ":" + text.substr(2); 
	}
	$("#search a:first").html(text);
	/*
	 * remove all remaining list elements 
	 */
	$("#search > ul > li").each(function(){
		if (!$(this).find("form, ul").length) $(this).remove(); 
	});

	/*
	 * heuristic to get the hint for the input field  
	 */
	var hint = getString("navi." + scope.replace(/\/.*/, "") + ".hint");
	if (hint.search(/\?\?\?.*\?\?\?/) != -1) { 
		hint = getString("navi.search.hint"); // fallback
	}

	/*
	 * prepare input field
	 */
	$("#inpf")
	.attr("name", "search") // always do a search
	.val(hint) // set hint as value
	.addClass('descriptiveLabel') // add class
	.descrInputLabel({}); // make the label disappear on click/submit
	$("#inpf").parents("li").removeClass("hidden"); // show form
}

$(function() { 
	initSidebarHeader();
	// FIXME: to ensure that the method is called after "most" other methods, we add a timeout ...
	window.setTimeout("initBookmarksPublicationsListsLast()", 500);
});

/* http://www.alexandre-gomes.com/?p=115 */
function getScrollBarWidth () {  
    var inner = document.createElement('p');  
    inner.style.width = "100%";  
    inner.style.height = "200px";  
  
    var outer = document.createElement('div');  
    outer.style.position = "absolute";  
    outer.style.top = "0px";  
    outer.style.left = "0px";  
    outer.style.visibility = "hidden";  
    outer.style.width = "200px";  
    outer.style.height = "150px";  
    outer.style.overflow = "hidden";  
    outer.appendChild (inner);  
  
    document.body.appendChild (outer);  
    var w1 = inner.offsetWidth;  
    outer.style.overflow = 'scroll';  
    var w2 = inner.offsetWidth;  
    if (w1 == w2) w2 = outer.clientWidth;  
  
    document.body.removeChild (outer);  
  
    return (w1 - w2);  
};  

function initBookmarksPublicationsListsLast() {
	numberOfBookmarkLists = $(".bookmarksContainer").size(); // every id bookmarks_* must have a class bookmarksContainer
	numberOfPublicationLists = $(".publicationsContainer").size(); // every id publications_* must have a class publicationsContainer
	if ( ($("#sidebar").length != 0) ) { 

		// set heigth of fullscreen area above post lists, if available
		fullscreenHeight = 0;
		if ($("#fullscreen").length != 0) fullscreenHeight = $("#fullscreen").height(); 

		// get heights
		bookmarksHeight = fullscreenHeight; 
		$('.bookmarksContainer').each(function(index) {
			bookmarksHeight += $(this).height();
		});
			
		publicationsHeight = fullscreenHeight; 
		$('.publicationsContainer').each(function() {
			publicationsHeight += $(this).height();
		});

		sidebarHeight = $("#sidebar").height();

		// calculate maximum height
		maxheight = (bookmarksHeight > publicationsHeight ) ? bookmarksHeight : publicationsHeight;
		maxheight = (maxheight > sidebarHeight) ? maxheight : sidebarHeight;
		// set heights to maximum_heights
		// only every last list will adjusted in height 
		if (numberOfBookmarkLists != 0) {
			//$("#bookmarks_"+(numberOfBookmarkLists-1)).height(maxheight-bookmarksHeight+$("#bookmarks_"+(numberOfBookmarkLists-1)).height());
			$("#bookmarks_"+(numberOfBookmarkLists-1)+">ul.posts").height(maxheight-bookmarksHeight+$("#bookmarks_"+(numberOfBookmarkLists-1)).height());
		}
		
		if (numberOfBookmarkLists != 0) {
			//$("#publications_"+(numberOfPublicationLists-1)).height(maxheight-publicationsHeight+$("#publications_"+(numberOfPublicationLists-1)).height());
			$("#publications_"+(numberOfPublicationLists-1)+">ul.posts").height(maxheight-publicationsHeight+$("#publications_"+(numberOfPublicationLists-1)).height());
		}
		
		
		// if there are no post lists and the only content is within element with id fullscreen, adjust length of this element
		// (if it is smaller than sidebar, otherwise new height is already maxheight)
		if (numberOfBookmarkLists==0 && numberOfPublicationLists==0 && fullscreenHeight>0) {
			fullscreenHeight = maxheight;
		}

		$("#sidebar").height(maxheight);
	}
	
}

function initSidebarHeader() {
	scrollbarWidth = getScrollBarWidth();
	if ( ($("#postcontainer").length != 0) && ($("#bookmarks_0").length != 0) && ($("#publications_0").length != 0) && ($("#sidebar").length != 0) ) { 
//		// calculate scrollbar-width
//		var c = $("#postcontainer").width();
//		//var s = $("#sidebar").width();
//		var b = $("#bookmarks_0").width();
//		var p = $("#publications_0").width();
//		var scrollbarWidth = c-(b+p);  // sidebar is in padding. width is width without padding
//		var sidebarWidth = $("#sidebarheader").width();
//		var sidebarWidthBody = $("#sidebar").width();
//		var scrollbarWidth_default = 0;

		var new_sidebarWidth = $("#sidebar").width() + scrollbarWidth;
		// set new width of header, regarding to scrollbarwidth and hide scrollbars in header
		$("#sidebarheader").css("width", new_sidebarWidth);
	
		if ($("#headercontainer").length != 0) {
			$('#headercontainer').css({"scroll": "hidden", "padding-right" : new_sidebarWidth});
		}
	
		if ($("#footercontainer").length != 0) {
			$('#footercontainer').css({"scroll": "hidden", "padding-right" : new_sidebarWidth});
		}
	}

	if ($("#headercontainer").length != 0) {
		$('#headercontainer').css({"scroll": "hidden", "padding-right" : new_sidebarWidth});
	}
	
	if ($("#footercontainer").length != 0) {
		$('#footercontainer').css({"scroll": "hidden", "padding-right" : scrollbarWidth});
	}
	
	
}
