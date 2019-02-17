$(function () {
	setupPersonAutocomplete('#searchPersonAutocomplete', "search", 'extendedPersonName', function (data) {
		$('#personField').attr('value', data.personId);
	});
	$('.clean-typeahead').click(function() {
		$(this).siblings().find('.typeahead').attr('placeholder', '');
		var idToReset = $(this).data("id");
		$("#" + idToReset).attr('value', '');
		return false;
	});

	// organization autocomplete
	setupOrganizationAutocomplete('#searchOrganizationAutocomplete', "search", 'realname', function (data) {
		$('#organizationField').attr('value', data.name);
	});

	$.each($('div.autocomplete-field'), function() {
		var dataUrl = $(this).data('autocomplete-url');

		var staticData = new Bloodhound({
			datumTokenizer: Bloodhound.tokenizers.whitespace,
			queryTokenizer: Bloodhound.tokenizers.whitespace,
			prefetch: dataUrl
		});

		$(this).find('input').typeahead(null, {
			name: 'fieldSelector',
			source: staticData
		});
	});
});

var organizationResultLimit = 100;

function setupOrganizationAutocomplete(inputFieldSelector, formAction, displayKey, selectionHandler) {

	// constructs the suggestion engine
	var organizationNames = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		remote: {
			url: '/json/groups?search=%QUERY&organizations=true',
			// the items of the retrieved data are the actual matches
			filter: function (data) {
				return data.items;
			}
		},
		wildcard: '%QUERY',
		rateLimitWait: 800
	});

	// kicks off the loading/processing of `local` and `prefetch`
	organizationNames.initialize();

	var organizationNameTypeahead = $(inputFieldSelector).typeahead({
			hint: true,
			highlight: true,
			minLength: 1,
		},
		{
			name: 'organizationNames',
			limit: organizationResultLimit,

			displayKey: displayKey,

			source: organizationNames.ttAdapter(),
			templates: {
				header: printResults,
				empty: ['<h5 class="response">' + getString('organizations.intro.search.result0') + '</h5>']
			},
		});

	organizationNameTypeahead.on('typeahead:selected', function (evt, data) {
		selectionHandler(data);
	}).on('typeahead:asyncrequest', function () {
		$(this).addClass("ui-autocomplete-loading");
	}).on('typeahead:asynccancel typeahead:asyncreceive', function () {
		$(this).removeClass("ui-autocomplete-loading");
	}).on('typeahead:change', function (evt, data, async, name) {
		$('#btnAddRoleSubmit').removeClass('disabled');
	});
}

var printResults = function (context) {
	var responseText = "";
	if (context.suggestions.length < organizationResultLimit) {
		responseText = getString('organizations.intro.search.result', [context.suggestions.length]);
	} else {
		responseText = getString('organizations.intro.search.resultMax', [context.suggestions.length]);
	}
	return '<h5 class="response">' + responseText + '</h5>';
}