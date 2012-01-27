/*
 * XXX: almost all methods depend on the DOM tree of the layout !NEW_LAYOUT!
 */

var request = null;
var style_list = null;
var style_sort = new Array("alph", "freq");
var style_show = new Array("cloud", "list");
var userMinFreq = 1;

//var style_sort = new Array(getString("tagbox.sort.alpha"), getString("tagbox.sort.freq"));
//var style_show = new Array(getString("tagbox.style.cloud"), getString("tagbox.style.list"));

function init_tagbox(show, sort, minfreq, requUser) {
	style_list = document.createElement("ul");
	style_list.className= "floatul";

	style_list.appendChild(document.createElement("li"));
	style_list.appendChild(document.createElement("li"));
	style_list.appendChild(document.createElement("li"));

	style_list.replaceChild(getStyleItem(style_sort[sort], style_sort), style_list.childNodes[0]);
	style_list.replaceChild(getStyleItem(style_show[show], style_show), style_list.childNodes[1]);
	if (typeof tagbox_minfreq_style != "undefined") {
		if (tagbox_minfreq_style == "user") {
			showUserMinfreq(minfreq, requUser);
		} else if (tagbox_minfreq_style == "default") {
			showMinfreq();  
		} else if (tagbox_minfreq_style == "none"){
			// do nothing
		}
	}

	var span = document.createElement("span");
	span.appendChild(style_list);  

	changeTagBox(style_show[show]);
	changeTagBox(style_sort[sort]);

	tagbox.parentNode.insertBefore(span, tagbox);
}

function attachChangeTagBox(mode) { return(function() {	changeTagBox(mode);	});}

function changeTagBox(mode) {
	if (mode == "list" || mode == "cloud"){
		tagbox.className = "tag" + mode;
		style_list.replaceChild(getStyleItem(mode, style_show), style_list.childNodes[1]);
	} else if (mode == "alph" || mode == "freq") {
		style_list.replaceChild(getStyleItem(mode, style_sort), style_list.childNodes[0]);
		mode == "alph" ? setTagBoxAlph() : setTagBoxFreq();
	}
}


function getStyleItem(style, style_arr) {
	var style_sort = document.createElement("li");

	var node = document.createElement("a");
	node.style.cursor = "pointer";

	style_sort.appendChild(document.createTextNode(" ("));
	if(style == style_arr[0]) {
		style_sort.appendChild(document.createTextNode(getString("tagbox." + style_arr[0]) + " | "));

		node.onclick = attachChangeTagBox(style_arr[1]);
		node.appendChild(document.createTextNode(getString("tagbox." + style_arr[1])));
		style_sort.appendChild(node);
	} else {
		node.onclick = attachChangeTagBox(style_arr[0]);
		node.appendChild(document.createTextNode(getString("tagbox." + style_arr[0])));
		style_sort.appendChild(node);

		style_sort.appendChild(document.createTextNode(" | " + getString("tagbox." + style_arr[1])));
	}
	style_sort.appendChild(document.createTextNode(") "));

	return style_sort;
}
function attachMinUsertags(count) { return(function() { minUsertags(count);});}

function getMinUsertagsLink (count) {
	var node = document.createElement("a");
	node.onclick = attachMinUsertags(count);
	node.appendChild(document.createTextNode(count));
	node.style.cursor = "pointer";
	return node;
}

function getMinTagsLink (count) {
	var node = document.createElement("a");
	if (userMinFreq != count) {
		node.onclick = attachMinUsertags(count);
		node.style.cursor = "pointer";
	}

	node.appendChild(document.createTextNode(count));

	return node;
}

//create minfreq links for users (which are loaded via AJAX)
function showUserMinfreq(count, currUser) {

	var minfreqList = document.createElement("li");

	minfreqList.appendChild(document.createTextNode(" (" + getString("tagbox.minfreq") + "  "));

	if (count == 1) {
		minfreqList.appendChild(document.createTextNode("1 | "));
		minfreqList.appendChild(getMinUsertagsLink (2));
		minfreqList.appendChild(document.createTextNode(" | "));
		minfreqList.appendChild(getMinUsertagsLink (5));
	} else if(count == 2) {
		minfreqList.appendChild(getMinUsertagsLink (1));
		minfreqList.appendChild(document.createTextNode(" | 2 | "));
		minfreqList.appendChild(getMinUsertagsLink (5));
	} else if(count == 5) {
		minfreqList.appendChild(getMinUsertagsLink (1));
		minfreqList.appendChild(document.createTextNode(" | "));
		minfreqList.appendChild(getMinUsertagsLink (2));
		minfreqList.appendChild(document.createTextNode(" | 5"));
	} else {	
		minfreqList.appendChild(getMinUsertagsLink (1));
		minfreqList.appendChild(document.createTextNode(" | "));
		minfreqList.appendChild(getMinUsertagsLink (2));
		minfreqList.appendChild(document.createTextNode(" | "));
		minfreqList.appendChild(getMinUsertagsLink (5));		
	}
	minfreqList.appendChild(document.createTextNode(") "));

	style_list.replaceChild(minfreqList, style_list.childNodes[2]);

}

//create default minfreq links
function showMinfreq() {
	var minfreqList = document.createElement("li");

	minfreqList.appendChild(document.createTextNode(" (" + getString("tagbox.minfreq") + "  "));

	minfreqList.appendChild(getMinTagsLink(1));
	minfreqList.appendChild(document.createTextNode(" | "));
	minfreqList.appendChild(getMinTagsLink(2));
	minfreqList.appendChild(document.createTextNode(" | "));
	minfreqList.appendChild(getMinTagsLink(5));

	minfreqList.appendChild(document.createTextNode(") "));

	style_list.replaceChild(minfreqList, style_list.childNodes[2]);	
}



/**
 * Sorts the tag cloud in the sidebar alphabetically.
 * 
 * Don't try to use jQuery for this, it slows down the code (by a factor of approx. 2).
 * 
 * @return
 */
function setTagBoxAlph() {
	var collection_tagname = new Array(); // array of tagnames
	var collection_li = new Object(); // map tagname -> li

	/* store tagbox */
	var tagbox = document.getElementById("tagbox");
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
 * Don't try to use jQuery for this, it slows down the code (by a factor of approx. 2).
 * 
 * @return
 */
function setTagBoxFreq() {
	var collection_tagname = new Array();
	var collection_li = new Object();
	var collection_tagposts = new Object();
	var collection_numberofposts = new Array();

	/* store tagbox */
	var tagbox = document.getElementById("tagbox");
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

//FIXME: check, if method still works
//FIXME: removed ckey from request, should not be necessary any longer - check!
function sendMinfreqRequ(minfreq, currUser) {
	if (minfreq == null) minfreq = 1;
	userMinFreq = minfreq;

	$.ajax({
		url : "?tagcloud.minFreq=" + minfreq + "&tagstype=default&format=tagcloud",
		dataType : "text",
		success : function (data) {
			/*
			 * replace the tags
			 * XXX: depends on DOM tree !NEW_LAYOUT!
			 */
			// ensure that we look in the correct list (the tagCloud / list)
			var start = data.indexOf("<li class=\"tag");
			var end = data.indexOf("</ul>", start);
	
			tagbox.innerHTML = data.slice(start, end); // FIXME: use jQuery to insert
	
			var sListStartTag = "<span>";
			start = data.indexOf(sListStartTag) + sListStartTag.length;
			end = data.indexOf("</span>", start);
	
			// re-order tags
			if (data.slice(start, end) == "ALPHA") {
				setTagBoxAlph();
			} else{
				setTagBoxFreq();
			}
		}
	});
}

function minUsertags(minfreq) {
	sendMinfreqRequ(minfreq);
	showMinfreq(minfreq);
}

/**
 * 
 * switches page default path to full navigation path
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
}


/*
 * TODO: new layout
 */
$(function() { 
	initBookmarksPublicationsLists();
	initSidebarHeader();
});

function initBookmarksPublicationsLists() {
	$(".action").hide();
	$(".edittags").find(".editimage").hide();
	if ($(".post") != 0) {
		$(".post").hover(
				function(){
					$(this).find(".action").show();
					$(this).find(".edittags").find(".editimage").show();
					
				},
				function(){
					$(this).find(".action").hide();
					$(this).find(".edittags").find(".editimage").hide();
				}
		);
	}

	if ( ($("#bookmarks ul.posts").length != 0) && ($("#publications ul.posts").length != 0) && ($("#sidebar").length != 0) ) { 
		// hoehe der postlisten anpassen auf groesste hoehe
		// get heights
		bookmarks_height	= $("#bookmarks ul.posts").height();
		publications_height	= $("#publications ul.posts").height();
		sidebar_height		= $("#sidebar").height();
	
		// get maximum height
		maxheight = (bookmarks_height > publications_height ) ? bookmarks_height : publications_height;
		maxheight = (maxheight > sidebar_height) ? maxheight : sidebar_height;
	
		// set heights to maximum_heights
		$("#bookmarks ul.posts").height(maxheight);
		$("#publications ul.posts").height(maxheight);
		$("#sidebar").height(maxheight);
	}
}


function initSidebarHeader() {
	if ( ($("#postcontainer").length != 0) && ($("#bookmarks").length != 0) && ($("#publications").length != 0) && ($("#sidebar").length != 0) ) { 
		// calculate scrollbar-width
		var c = $("#postcontainer").width();
		var s = $("#sidebar").width();
		var b = $("#bookmarks").width();
		var p = $("#publications").width();
		var scrollbarWidth = c-(b+p);  // sidebar is in padding. width is width without padding
		var sidebarWidth = $("#sidebarheader").width();
		var sidebarWidthBody = $("#sidebar").width();
	    var scrollbarWidth_default = 0;


	    var new_sidebarWidth = sidebarWidthBody + scrollbarWidth;
	    
	    // set new width of header, regarding to scrollbarwidth and hide scrollbars in header
		$("#sidebarheader").width(new_sidebarWidth);
		$('#headercontainer, #footercontainer').css({"scroll": "hidden", "padding-right" : new_sidebarWidth});
		
		
	}
}
