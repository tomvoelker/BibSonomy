//instantiate the bloodhound suggestion engine
$(document).ready(function() {
	var persons = [{"value":"Max Mustermann","firstname":"Max","lastName":"Mustermann","id":"hansii"}];
	// constructs the suggestion engine
	var persons = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		 local: $.map(persons, function(person) { return person; })
	});

	// kicks off the loading/processing of `local` and `prefetch`
	persons.initialize();

	$('.typeahead').typeahead({
		hint: true,
		highlight: true,
		minLength: 1
	},
	{
		name: 'persons',
		displayKey: 'value',
		// `ttAdapter` wraps the suggestion engine in an adapter that
		// is compatible with the typeahead jQuery plugin
		source: persons.ttAdapter()
	});
	$('.typeahead').on("typeahead:selected", function(jobj, sobj, dataset) {
		window.location.pathname = "/person/" + sobj.id + "/" + sobj.value;
	})
}); 