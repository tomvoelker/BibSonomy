function setupPersonSearch(inputFieldSelector, buttonSelector) {
	setupPersonAutocomplete(inputFieldSelector, "search", 'extendedPersonName', function(data) {
		$(buttonSelector).attr("data-person-name", data.personName);
		$(buttonSelector).attr("data-extended-person-name", data.extendedPersonName);
		$(buttonSelector).attr("data-person-id", data.personId);
	});
}

function setupBibtexAuthorSearchForForm(inputFieldSelector, formSelector) {
	setupPersonAutocomplete(inputFieldSelector, "searchAuthor", 'extendedPublicationName', function(data) {
		$(formSelector + " input[name='formInterHash']").val(data.interhash);
		$(formSelector + " input[name='formPersonIndex']").val(data.personIndex);
		// already set in form:
		//FormPersonId
		//FormPersonRole  AUTHOR
	});
}

function setupBibtexSearchForForm(inputFieldSelector, formSelector) {
	setupPersonAutocomplete(inputFieldSelector, "searchPub", 'extendedPublicationName', function(data) {
		$(formSelector + " input[name='formInterHash']").val(data.interhash);
		$(formSelector + " input[name='formPersonIndex']").val(data.personIndex);
	});
}

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
		minLength: 1
	},
	{
		name: 'personNames',
		displayKey: displayKey, //display – For a given suggestion, determines the string representation of it. This will be used when setting the value of the input control after a suggestion is selected. Can be either a key string or a function that transforms a suggestion object into a string. Defaults to stringifying the suggestion.
		
		// `ttAdapter` wraps the suggestion engine in an adapter that
		// is compatible with the typeahead jQuery plugin
		source: personNames.ttAdapter()
	});
	
	personNameTypeahead.on('typeahead:selected', function(evt, data) {
		selectionHandler(data);
	}).on('typeahead:asyncrequest', function() {
	    $(this).addClass("ui-autocomplete-loading");
	})
	.on('typeahead:asynccancel typeahead:asyncreceive', function() {
	    $(this).removeClass("ui-autocomplete-loading");
	});
}