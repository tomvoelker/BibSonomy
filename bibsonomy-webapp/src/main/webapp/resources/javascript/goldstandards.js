/**
 * for deleting and adding references
 */
var FADE_DURATION = 1000;
var GOLD_REFERENCE_URL = '/ajax/goldstandards/relation';

/**
 * Loads the select2 combobox layout selection
 * @param formatUrl
 * @param element
 * @returns
 */
function loadLayoutSelect(formatUrl, element) {
	$.ajax({
		url: formatUrl, 
		dataType: "html", 
		success: function(data) {
			$("#goldstandard-quick-cite-select").html(data).find("select").addClass("form-control input-sm");
			$("#goldstandard-quick-cite").hide();
			openSelect2(element);
		}
	});
	return;
}

/**
 * Opens the select2 element
 * @param element
 * @returns
 */
function openSelect2(element) {
	if ($(element).next().find('#selectAllStyles').hasClass("select2-hidden-accessible")) {
		$(element).next().find('#selectAllStyles').removeAttr("onchange")
			.bind("change", function(){ ajaxLoadLayout(this.value); });
		$(element).next().find('#selectAllStyles').select2('open');
	}
}

function ajaxLoadLayout(link) {
	link_parts = link.split("/");

	switch (link_parts[1]) {
		case "bib":
			$("#sidebar-quick-cite-box-modal .modal-body").html($("#sidebar-quick-cite-box-bibtex").html());
			$("#sidebar-quick-cite-box-modal").modal("show");
			break;
		case "csl":
		case "layout":
			if (link_parts[2] == "endnote") {
				$("#sidebar-quick-cite-box-modal .modal-body").html($("#sidebar-quick-cite-box-endnote").html());
				$("#sidebar-quick-cite-box-modal").modal("show");
			} else {
				self.location = link
			}
			break;
		case "csl-layout":
			// load CSL via AJAX
			csl_style = link_parts[2];
			csl_url = "/csl/bibtex/" + link_parts[4];
			container = $("#sidebar-quick-cite-box-modal .modal-body");
			container.empty();

			$.ajax({
				url: csl_url,
				success: function(data) {
					renderCSL(data, csl_style, container, false);
					$("#sidebar-quick-cite-box-modal").modal("show");
				}
			});
			break;
		default:
			alert("Error during CSL rendering;");
	}
}

$(function() {

	initNewClipboard("#sidebar-quick-cite-box-modal-clipboard-button", "#sidebar-quick-cite-box-modal .modal-body");

	// remove the dummy element and replace it by select2 combobox layout selection
	$("#goldstandard-quick-cite").focus(function() {
		loadLayoutSelect($(this).data("formaturl"), this);
	})

	// init title autocomplete
	var publicationSource = new Bloodhound({
		datumTokenizer: function (datum) {
			return Bloodhound.tokenizers.whitespace(datum.value);
		},
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		remote: {
			url: '/json/tag?tag=sys:title:%QUERY',
			prepare: function(query, settings) {
				var url = '/json/tag/';
				var tokens = query.split(' ');
				
				for (var i = 0; i < tokens.length; i++) {
					url += 'sys:title:' + encodeURIComponent(tokens[i]);
					url += '%20';
				}
				url += '?resourcetype=goldstandardPublication';
				settings.url = url;
				return settings;
			},
			filter: function (data) {
				return $.map(data.items, function (publication) {
					return {
						value: publication.label,
						interhash: publication.interHash,
						authors: publication.authors,
						user: publication.user,
						year: publication.year
					};
				});
			}
		}
	});
	publicationSource.initialize();
	
	var searchInput = $('.addRelation').typeahead({
		highlight: true,
		minLength: 1
	}, {
		displayKey: 'value',
		source: publicationSource.ttAdapter(),
		templates: {
			suggestion: Handlebars.compile("<p>{{value}} ({{year}})<br /><span class='author text-muted'>{{#each authors}}{{first}} {{last}}{{#unless @last}}, {{/unless}}{{/each}}</span></p>"),
		}
	});
	
	searchInput.on('typeahead:selected', function(evt, typeaheadData) {
		var relation = $(evt.target).data('relation');
		var dataToSend = 'ckey=' + ckey + '&hash=' + getGoldInterHash() + '&references=' + typeaheadData.interhash + '&relation=' + relation;
		
		$.ajax({
			url: GOLD_REFERENCE_URL,
			data: dataToSend,
			type: 'POST',
			success: function(data) {
				location.reload();
			}
		});
	});
	
	// delete relation links
	
	$('.deleteRelation').click(function() {
		var relation = $(this).data('relation');
		var referenceHash = $(this).data('interhash');
		if (confirm(getString('post.actions.edit.gold.references.delete.confirm'))) {
			$.ajax({
				url: GOLD_REFERENCE_URL + '?ckey=' + ckey + '&hash=' + getGoldInterHash() + '&references=' + referenceHash + '&relation=' + relation,
				type: 'DELETE',
				success: function(response) {
							location.reload();
						}
			});
		}
	});
});

/** 
 * @returns the hash of the publication
 */
function getGoldInterHash() {
	return $('#goldstandard').data('interhash');
}