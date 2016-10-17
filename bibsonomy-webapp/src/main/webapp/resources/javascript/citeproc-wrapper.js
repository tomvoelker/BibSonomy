$(function() {
	// if several of this scripts are embedded on another page this prevents the script from writing to the wrong div
	var container = $('#csl-container');
	if (container.length == 0) {
		return;
	}
	var url = container.data('url');
	var format = container.data('style');
	
	$.ajax({
		url: url,
		success: function(data) {
			//callback
			renderCSL(data, format, container);
		}
	});
});

function fixData(data) {
	$.each(data, function(index, citation) {
		delete citation.issued.literal;
	});
	
	return data;
}

function loadStyle(styleName, success) {
	$.ajax({
		type: "get",
		url: "/csl-style/" + styleName,
		dataType: "text",
		success: function(xml) {
			//callback
			xml = $.trim(xml);
			
			success(xml);
		}
	});
}

function renderCSL(csl, styleName, container) {
	csl = fixData(csl);
	// getting style for CSL from /csl-style/"style"
	loadStyle(styleName, function(xml) {
		//building CSL based on XML
		//kept this as it was
		var sys = new Sys(csl);
		for (var key in csl) {
			var citeproc = new CSL.Engine(sys, xml);
			var citation = {
				"citationItems" : [ {
					id : key
				} ],
				"properties" : {
					"noteIndex" : 1
				}
			};
			var renderedCitation = citeproc.appendCitationCluster(citation);
			var bibliographyEntry = citeproc.makeBibliography();
			var output = bibliographyEntry[1][0];
			container.empty();
			container.append(output);
		}
	});
}

//helper class for citeproc
function Sys(data) {
	this.data = data;

	this.retrieveLocale = function(lang) {
		return cslLocale[lang];
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