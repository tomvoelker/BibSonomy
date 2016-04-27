$(function() {
	var container = $('#csl-container');
	var url = container.data('url');
	
	
	var format = container.data('style');
	
	$.get( "/csl-style/" + format, function( data ) {
		
		alert( data );
		});
	
		$.ajax({
			url: url,
			success: function(data) {
				var sys = new Sys(data);
				for (var key in data) {
					// TODO: use the correct style
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
					container.append(output);
				}
			}
		});
	
	
});

// FIXME: code copy
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