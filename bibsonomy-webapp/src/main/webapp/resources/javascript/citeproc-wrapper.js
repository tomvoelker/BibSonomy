$(function() {
	var container = $('#csl-container');
	var url = container.data('url');
	var format = container.data('style');
	
	
	$.ajax({
		type: "get",
	    url: "/csl-style/" + format,
	    dataType: "xml",
	    success: function(xmldata) {
	    	
	    	var serializer = new XMLSerializer(); 
	    	var original = serializer.serializeToString(xmldata);
	    	var xml = $("<div/>").html(original).text();
	    	xml = $.trim(xml);
	    	
	    	//xml is now a string with given style.csl as content
	    	//alert(xml);
	    	 
	    	$.ajax({
				url: url,
				success: function(data) {
					var sys = new Sys(data);
					for (var key in data) {
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