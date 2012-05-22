var typeindex = 2;
var hashindex = 3;
var nameindex = 4;

var linkImage = "<img src=\"/resources/image/reference_link_icon.png\">";

var style;

$(document).ready(function() {

	//	var style=harvardStaffordshireUniversity;
	var style = harvardEducationalReview;

	// handle all list elements with class review
	$('li.review').each(function() {
		handleLinks($(this))
	});

	// handle all list elements with class review
	$('li.comment').each(function() {
		handleLinks($(this))
	});
});

function handleLinks(discussionItem) {

	//find all required links:
	style = harvardStaffordshireUniversity;
	var proceededLinks = new Array();
	discussionItem.find("a.postlink").each(function() {
		var matches = $(this).attr("href").match(/(.*?)\/{1}(bibtex|publication|url|bookmark)\/([0-9a-f]{32,33})(?:\/(.*))?/);
		var name;
		if (matches[nameindex] == undefined) {
			name = "";
		} else {
			name = matches[nameindex];
		}

		$(this).attr("class", matches[hashindex] + name + " postlink");
		var publication = matches[typeindex] != "url" && matches[typeindex] != "bookmark";

		//create data object to store information
		var postLinkData = new PostLinkResult(publication, matches[hashindex], name);

		var link;
		if (publication) {
			link = $(this).attr("href").replace(matches[typeindex], "csl/" + matches[typeindex]);
		} else {
			link = $(this).attr("href").replace(matches[typeindex], "json/" + matches[typeindex]);
		}

		//check that this is the first occurrence of this link
		if (proceededLinks.indexOf(link) != -1) {
			return;
		} else {
			proceededLinks.push(link);
		}

		call(link, postLinkData, $(this));
	});
}

function call(link, postLinkData, anchor) {
	$.get(link, function(data) {
		requestSuccessful(data, postLinkData, anchor);
	}).error(function() {
		alert("can't happen");
	});
}

/**
 * will be called if the request was successful
 */
function requestSuccessful(data, postLinkData, anchor) {
	if (postLinkData.isPublication()) {
		parsePublicationResult(data, postLinkData);
	} else {
		parseBookmarkResult(data, postLinkData);
	}

	var referenceDiv = anchor.parent().siblings(postLinkData.getDivClassSelector());

	referenceDiv.show();
	var reference = $(postLinkData.getReference());
	reference.attr("id", postLinkData.getClassId());

	var referenceAnchor = anchor.clone();
	anchor.parent().find("." + postLinkData.getHash() + postLinkData.getName()).each(function() {
		$(this).html(postLinkData.getCitation());
		$(this).attr("href", "#" + postLinkData.getClassId());
	});

	reference.append(referenceAnchor);
	referenceAnchor.html(linkImage);
	referenceDiv.append(reference);
}

function parseBookmarkResult(data, postLinkData) {
	var bookmark = data.items[0];
	postLinkData.setCitation("(" + bookmark.label + ")");
	postLinkData.setReference("<div class=\"book-cit\" id=\"" + postLinkData.getClassId() + "\"><b>" + bookmark.label + "</b> <i>" + bookmark.description + "</i></div)");
}

function parsePublicationResult(data, postLinkData) {
	var sys = new Sys(data);
	var citeproc = new CSL.Engine(sys, style);
	var id = constructId(data, postLinkData.getHash());
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

	postLinkData.setCitation(renderedCitation[0][1]);
	postLinkData.setReference($("" + bibliographyEntry[1]));
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

};

function PostLinkResult(publication, hash, name) {

	this.publication = publication;

	if (publication) {
		this.divClassSelector = ".citeBox";
	} else {
		this.divClassSelector = ".bookCiteBox";
	}

	if (hash.length == 33) {
		this.classId = hash.substr(1);
	} else {
		this.classId = hash;
	}
	this.classId += name;

	this.getCitation = function() {
		return this.citation;
	};

	this.setCitation = function(citation) {
		this.citation = citation;
	};

	this.getReference = function() {
		return this.reference;
	};

	this.setReference = function(reference) {
		this.reference = reference;
	}

	this.getDivClassSelector = function() {
		return this.divClassSelector;
	};

	this.isPublication = function() {
		return this.publication;
	};

	this.getClassId = function() {
		return this.classId;
	};

	this.getHash = function() {
		return hash;
	};

	this.getName = function() {
		return name;
	};
};

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

/**
 * source: http://stackoverflow.com/questions/2390789/how-to-replace-all-points-in-a-string-in-javascript/9918856#9918856 
 */
String.prototype.replaceAll = function(token, newToken, ignoreCase) {
	var str, i = -1, _token;
	if ((str = this.toString()) && typeof token === "string") {
		_token = ignoreCase === true ? token.toLowerCase() : undefined;
		while ((i = (_token !== undefined ? str.toLowerCase().indexOf(_token, i >= 0 ? i + newToken.length : 0) : str.indexOf(token, i >= 0 ? i + newToken.length : 0))) !== -1) {
			str = str.substring(0, i).concat(newToken).concat(str.substring(i + token.length));
		}
	}
	return str;
};