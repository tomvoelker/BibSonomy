var resultLimit = 100;

function setupPersonAutocomplete(inputFieldSelector, formAction, displayKey, selectionHandler) {
	// constructs the suggestion engine
	var personNames = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		remote: '/person?formAction=' + formAction + '&formSelectedName=%QUERY',
		wildcard: '%QUERY',
		rateLimitWait: 800
	});

	// kicks off the loading/processing of `local` and `prefetch`
	personNames.initialize();

	var personNameTypeahead = $(inputFieldSelector).typeahead({
			hint: true,
			highlight: true,
			minLength: 1,
		},
		{
			name: 'personNames',
			limit: resultLimit,

			// display – For a given suggestion, determines the string representation of it.
			// This will be used when setting the value of the input control after a suggestion is selected.
			// Can be either a key string or a function that transforms a suggestion object into a string. Defaults to stringifying the suggestion.
			// `ttAdapter` wraps the suggestion engine in an adapter that is compatible with the typeahead jQuery plugin
			displayKey: displayKey,

			source: personNames.ttAdapter(),
			templates: {
				header: printResults,
				empty: ['<h5 class="response">' + getString('persons.intro.search.result0') + '</h5>']
			},
		});

	personNameTypeahead.on('typeahead:selected', function (evt, data) {
		selectionHandler(data);
	}).on('typeahead:asyncrequest', function () {
		$(this).addClass("ui-autocomplete-loading");
	})
		.on('typeahead:asynccancel typeahead:asyncreceive', function () {
			$(this).removeClass("ui-autocomplete-loading");
		})
		.on('typeahead:change', function (evt, data, async, name) {
			$('#btnAddRoleSubmit').removeClass('disabled');
		});
}

var printResults = function (context) {
	var responseText = "";
	if (context.suggestions.length < resultLimit)
		responseText = getString('persons.intro.search.result', [context.suggestions.length]);
	else
		responseText = getString('persons.intro.search.resultMax', [context.suggestions.length]);

	return '<h5 class="response">' + responseText + '</h5>';
}