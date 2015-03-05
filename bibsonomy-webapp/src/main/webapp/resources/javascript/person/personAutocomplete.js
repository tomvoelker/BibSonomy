
function setupPersonAutocomplete(inputFieldSelector, selectionHandler) {
	// constructs the suggestion engine
	var personNames = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		remote: '/person?formAction=search&formSelectedName=%QUERY'
	});

	// kicks off the loading/processing of `local` and `prefetch`
	personNames.initialize();

	var personNameTypeahead = $(inputFieldSelector).typeahead({
		hint: true,
		highlight: true,
		minLength: 1
	},
	{
		name: 'personNames',
		displayKey: 'extendedPersonName',
		// `ttAdapter` wraps the suggestion engine in an adapter that
		// is compatible with the typeahead jQuery plugin
		source: personNames.ttAdapter()
	});
	
	personNameTypeahead.on('typeahead:selected', function(evt, data) {
		selectionHandler(data);
	});
}