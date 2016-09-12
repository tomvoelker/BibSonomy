var LAYOUT_TEMPLATE = Handlebars.compile("<p>{{displayName}} ({{source}})</p>");
$(function() {
	/* TODO: merge with friendsoverview logic */
	$('.groupUnshare').hover(function() {
		$(this).removeClass('btn-success').addClass('btn-danger');
		$(this).children('.fa').removeClass('fa-check').addClass('fa-times');
		$(this).children('.button-text').text(getString('groups.actions.unshareDocuments'));
	}).mouseleave(function() {
		$(this).removeClass('btn-danger').addClass('btn-success');
		$(this).children('.fa').removeClass('fa-times').addClass('fa-check');
		$(this).children('.button-text').text(getString('groups.documentsharing.shared'));
	});
	
	// typeahead configuration
	// firstly two "simple" styles
	var bibTex = {"source": "SIMPLE", "displayName": "BibTeX", "name":"BIBTEX"};
	var endnote = {"source": "SIMPLE", "displayName": "EndNote", "name":"ENDNOTE"};
	var simpleFormatsData = [bibTex, endnote];
	
	var simpleFormats = new Bloodhound({
		datumTokenizer: function (datum) {
			return layoutTokenizer(datum);
		},
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		local: simpleFormatsData
	});
	
	// csl formats
	var cslFormats = new Bloodhound({
		datumTokenizer: function (datum) {
			return layoutTokenizer(datum);
		},
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch:{
			url: '/csl-style',
			cache: false, // TODO: discuss
			transform: function(response) {
				return response.layouts;
			}
		}
	});
	
	// jabref formats
	var jabRefFormats = new Bloodhound({
		datumTokenizer: function (datum) {
			return layoutTokenizer(datum);
		},
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch:{
			url: '/layoutinfo/',
			cache: false, // TODO: discuss
			transform : function(response) {
				return $.map(response.layouts, function(item) {
					return {
						displayName: item.displayName,
						source: "JABREF",
						name: item.name
					};
				});
			}
		}
	})
	var citationAutocomplete = $('#searchCitationAutocomplete');
	citationAutocomplete.typeahead({
		minLength: 1,
		highlight: true
	}, {
		name: 'simple-formats',
		displayKey: 'displayName',
		source: simpleFormats,
		templates: {
			suggestion: LAYOUT_TEMPLATE
		}
	}, {
		name: 'csl-formats',
		displayKey: 'displayName',
		source: cslFormats,
		templates: {
			suggestion: LAYOUT_TEMPLATE
		}
	}, {
		name: 'jabref-formats',
		displayKey: 'displayName',
		source: jabRefFormats,
		templates: {
			suggestion: LAYOUT_TEMPLATE
		}
	});
	
	/* 
	 * triggers when something is selected in the typeahead
	 * adds a new list item to the list including a remove button and an input field with correct ID, source and displayName
	 * value has to be "source"/"id" for the StringToFavouriteLayoutConverter to read
	 */
	citationAutocomplete.on('typeahead:select', function (e, datum) {
		var source = datum.source.toUpperCase();
		var style = datum.name.toUpperCase();
		var id = source + '/' + style;
		var favList = $('#favouriteLayoutsList');
		var items = favList.find('li[data-source="' + source + '"][data-style="' + style + '"]');
		var toHighlight;
		var deleteMsg = getString('delete');
		if (items.length == 0) {
			var toBeAppended = $('<li class="list-group-item favouriteLayoutsListItem clearfix" data-source="' + source + '" data-style="' + style + '"></li>');
			
			var input = $('<input type="hidden" name="user.settings.favouriteLayouts"  id="' + id + '" value="' + id + '"/>');
			var deleteButton = $('<span class="btn btn-danger btn-xs pull-right delete-Style">' + deleteMsg + '</span>');
			deleteButton.click(deleteStyle);
			
			toBeAppended.append(input);
			toBeAppended.append(deleteButton);
			toBeAppended.append(datum.displayName);
			favList.append(toBeAppended);
			toHighlight = toBeAppended;
		} else {
			toHighlight = items;
		}
		
		// highlight new or already added export format
		toHighlight.effect("highlight", {}, 2500);
		
		// reset input field
		citationAutocomplete.typeahead('val','');
	});
	
	// catching presses of "enter", else the form would be submitted by each "accidental" press
	citationAutocomplete.on('keydown', function(event) {
		if (event.which == 13) {// if pressing enter
			event.preventDefault();
		}
	});
	
	// getting the "Delete" batch to work
	$('.delete-Style').click(deleteStyle);
	
	function clearFavouriteLayoutsList() { // removing duplicates
		var seen = {};
		$('.favouriteLayoutsListItem').each(function() {
			var txt = $(this).data("source") + "/" + $(this).data("style");
			if (seen[txt]) {
				$(this).remove();
			} else {
				seen[txt] = true;
			}
		});
	}
	
	clearFavouriteLayoutsList();
});

function deleteStyle() {
	$(this).parent().slideUp(200, function() {
		$(this).remove();
	});
}

function layoutTokenizer(datum) {
	return Bloodhound.tokenizers.whitespace(datum.displayName);
}