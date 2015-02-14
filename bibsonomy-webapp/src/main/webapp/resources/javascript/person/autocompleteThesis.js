//instantiate the bloodhound suggestion engine
$(document).ready(function() {
	// constructs the suggestion engine
	var unix = Math.round(+new Date());
	var thesis = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		remote: '/json/tag/sys:title:%QUERY*?items=10&resourcetype=publication&duplicates=no&_='+unix
	});

	// kicks off the loading/processing of `local` and `prefetch`
	thesis.initialize();

	var thesisTypeahead = $('#addThesisAuto').typeahead({
		hint: true,
		highlight: true,
		minLength: 1
	},
	{
		name: 'thesis',
		displayKey: 'title',
		// `ttAdapter` wraps the suggestion engine in an adapter that
		// is compatible with the typeahead jQuery plugin
		source: thesis.ttAdapter()
	});
	
	thesisTypeahead.on('typeahead:selected', function(evt, data) {
		alert();
	});
}); 