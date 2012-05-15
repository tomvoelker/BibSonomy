var typeindex = 2;
var hashindex = 3;
var nameindex = 4;

var linkImage = "<img src=\"/resources/image/wiki_link.png\">";

var style;


$(document).ready(function() {
	$('li.review' ).each(function(){handleLinks($(this))});
	$('li.comment').each(function(){handleLinks($(this))});
});

function handleLinks(discussionItem) {
	style = harvardStaffordshireUniversity;
	var links = discussionItem.find(".postlink");
	var size = links.size();
	var hanledLinks = new Array(size);
	var i = 0;
	for (i = 0; i < size; i++) {
		var link = links[i].href;
		if(containsLink(hanledLinks, link)) {
			continue;
		}
		hanledLinks[i] = link;
		var matches = link.match(/(.*?)\/{1}(bibtex|publication|url|bookmark)\/([0-9a-f]{32,33})(?:\/(.*))?/);
		var bibtex = matches[typeindex] != "url" && matches[typeindex] != "bookmark";
		
		if (typeof matches[nameindex] == 'undefined') {
			matches[nameindex] = ""
		}
		$(links[i]).attr("id", matches[hashindex] + matches[nameindex]);
		
		var callBack;
		
		if (bibtex) {
			//handle bibtex
			link = link.replace(matches[typeindex], "csl/" + matches[typeindex]);
			callBack = proceedCSL;
		} else {
			//handle bookmarks
			link = link.replace(matches[typeindex], "json/" + matches[typeindex]);
			callBack=proceedBookmark;
		}
		call(link, matches, callBack, discussionItem);
		
	}
}

function containsLink(array, link) {
	for (var i = 0; i < array.length; i++) {
		if (array[i] == link) {
			return true;
		}
	}
	return false;
}

/**
 * performs ajax request
 * @param link for request
 * @param matches split user link
 * @param callBack call back function to proceed bookmarks or bibtex
 */
function call(link, matches, callBack, discussionItem) {
	$.get(link, function(data) {
		callBack(data, matches, discussionItem);
	}).error(function() {
		alert("it's not possible that you see it!");
	});
}

function proceedBookmark(data, matches, discussionItem) {
	var id = matches[hashindex] + matches[nameindex];
	var bookmark = data.items[0];
	
	var newLink = discussionItem.find("#" + matches[hashindex] + matches[nameindex]);
	var oldLink = newLink.clone();
	
	newLink.text("(" + bookmark.label + ")");
	newLink.attr("href", "#div" + matches[hashindex] + matches[nameindex]);
	
	var bookCit = $("<div class=\"book-cit\" id=\"div" + id + "\"></div)");
	bookCit.append($("<b>" + bookmark.label + "</b> <i>" + bookmark.description + "</i>"));
	oldLink.html(linkImage);
	bookCit.append(oldLink);
	bookCit.append($("<br/><a href=\"" + bookmark.url + "\">" + bookmark.url + "</a>"));
	
	var bookmarkDiv = newLink.parent().siblings(".bookCiteBox");
	bookmarkDiv.append(bookCit);	
	bookmarkDiv.show();
}


function proceedCSL(data, matches, discussionItem) {
	var sys = new Sys(data);
	var citeproc = new CSL.Engine(sys, style);

	var id = matches[hashindex];
	if (id.length == 33) {
		id = id.substr(1);
	}
	id += matches[nameindex];

	id = constructId(data, id);

	var citation = {
		"citationItems" : [ {
			id : id
		} ],
		"properties" : {
			"noteIndex" : 1
		}
	};

	var renderedCitation = citeproc.appendCitationCluster(citation);
	var bibliographyEntry = citeproc.makeBibliography();

	var bibentry = $("" + bibliographyEntry[1]);
	bibentry.attr("id", "div" + matches[hashindex] + matches[nameindex]);

//	var newLink =  discussionItem.find("#" + matches[hashindex] + matches[nameindex]);
//	var oldLink = newLink.clone();

	var oldLink = discussionItem.find("#" + matches[hashindex] + matches[nameindex]).clone();
	var citeDiv = discussionItem.find("#" + matches[hashindex] + matches[nameindex]).parent().siblings(".citeBox");
	
	
	discussionItem.find("#" + matches[hashindex] + matches[nameindex]).
		each(function(){
			var newLink = $(this);
			newLink.text(renderedCitation[0][1]);
			newLink.attr("href", "#div" + matches[hashindex] + matches[nameindex]);
		});
	
	
//	newLink.text(renderedCitation[0][1]);
//	newLink.attr("href", "#div" + matches[hashindex] + matches[nameindex]);
	oldLink.html(linkImage);
	bibentry.append(oldLink);
	
	citeDiv.append(bibentry);	
	citeDiv.show();
}


function constructId(data, id) {
	var counter = 0;
	for ( var key in data) {
		counter++;
		if (key == id) {
			console.log("id found: " + id);
			return id;
		}
	}

	/*
	 * no id found => link should be [[bibtex/hash]].
	 * Check in this case the size of the map, if more then one post is available it's must be an error
	 */ 
	if (counter == 1) {
		for ( var key in data) {
			return key;
		}
	} else {
		alert("an error is occured, expected count of posts is 1, but was" + counter);
	}

}

function Sys(data) {
	this.data = data;

	this.retrieveLocale = function(lang) {
		return locale[lang];
	};

	this.retrieveItem = function(id) {
		return this.data[id];
	};

	this.getAbbreviations = function(name) {
		var ABBREVS = {
			"default" : {}
		};
		return ABBREVS[name];
	};

};