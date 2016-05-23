$(function() {
	//if serveral of this scripts are embedded on another page this prevents the script from writing to the wrong div
	var container = $('#csl-container-' + layoutToUse);
	var url = container.data('url');
	var format = container.data('style');
	
	//getting source XML for CSL from /csl-style/"style"
	$.ajax({
		type: "get",
	    url: "/csl-style/" + format,
	    dataType: "text",
	    success: function(xml) {
	    	//callback
	    	
	    	xml = $.trim(xml);
	    		    	
	    	//xml is now a string with given style.csl as content
	    	 
	    	//building CSL based on XML
	    	//kept this as it was
	    	$.ajax({
				url: url,
				success: function(data) {
					//callback
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