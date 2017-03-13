/**
 * this file contains a wrapper for the cslproc implementation
 */
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
			renderCSL(data, format, container, false);
		}
	});
});

// FIXME: workaround for the wrong csl export
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

function renderCSL(csl, styleName, container, clearContainer) {
	csl = fixData(csl);
	// getting style for CSL from /csl-style/"style"
	loadStyle(styleName, function(xml) {
		//building CSL based on XML
		//kept this as it was
		var sys = new Sys(csl);
		
		var citationItems = [];
		
		for (var key in csl) {
			citationItems.push({
				id : key
			});
		}
		
		if (clearContainer) {
			container.empty();
		}
		
		var citeproc = new CSL.Engine(sys, xml);
		var citation = {
			"citationItems" : citationItems,
			"properties" : {
				"noteIndex" : 1
			}
		};
		var renderedCitation = citeproc.appendCitationCluster(citation);
		var bibliographyEntry = citeproc.makeBibliography();
		var output = bibliographyEntry[1];
		for (var i = 0; i < output.length; i++) {
			container.append(output[i]);
		}
	});
}

function loadExportLayout(clickedElement, targetElement, publicationLink) {
	var source = clickedElement.data("source");
	if (source == 'CSL') {
		var style = clickedElement.data("style");
		var tabContainer = clickedElement.closest('ul');
		var csl = tabContainer.data('csl');
		if (csl == undefined) {
			$.ajax({
				url: '/csl' + publicationLink,
				dataType: "json",
				success: function(data) {
					csl = data;
					tabContainer.data('csl', data);
					renderCSL(csl, style, targetElement, true);
				}
			})
		} else {
			renderCSL(csl, style, targetElement, true);
		}
	} else {
		loadSimpleLayout(clickedElement, targetElement);
	}
}

function loadSimpleLayout(clickedElement, targetElement) {
	var url = clickedElement.data("formaturl");
	if (url == undefined) {
		url = clickedElement.attr('href');
	}
	$.ajax({
		url: url,
		dataType: "html",
		success: function(data, status, xhr) {
			var contentType = xhr.getResponseHeader("content-type");
			var plain = !contentType.startsWith("text/html");
			if (plain) {
				var pre = $('<pre></pre>').addClass('export');
				targetElement.html(pre);
				targetElement = pre;
			} else {
				data = cleanupHtml(data);
			}
			
			var isXML = contentType.startsWith("text/xml");
			if (isXML) {
				targetElement.text(data);
			} else {
				targetElement.html(data);
			}
		}
	});
}

// FIXME: jabref renderer returns html which jquery can not parse
function cleanupHtml(htmlString) {
	var htmlParsed = $.parseHTML(htmlString);
	var result = [];
	var html = $(htmlParsed);
	$.each(html, function(index, element) {
		var nodeName = $(element).prop('nodeName');
		if (nodeName != 'META' && nodeName != 'TITLE') {
			result.push(element);
		}
	});
	
	return result;
}

//helper class for citeproc
function Sys(data) {
	this.data = data;

	this.retrieveLocale = function(lang) {
		var xhr = new XMLHttpRequest();
		xhr.open('GET', '/csl-locale/' + lang, false);
		xhr.send(null);
		return xhr.responseText;
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