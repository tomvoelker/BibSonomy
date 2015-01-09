//instantiate the bloodhound suggestion engine
$(document).ready(function() {
	// constructs the suggestion engine
	var personNames = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		remote: '/person?formAction=search&formSelectedName=%QUERY'
	});

	// kicks off the loading/processing of `local` and `prefetch`
	personNames.initialize();

	var personNameTypeahead = $('.typeahead').typeahead({
		hint: true,
		highlight: true,
		minLength: 1
	},
	{
		name: 'personNames',
		displayKey: 'personName',
		// `ttAdapter` wraps the suggestion engine in an adapter that
		// is compatible with the typeahead jQuery plugin
		source: personNames.ttAdapter()
	});
	
	personNameTypeahead.on('typeahead:selected', function(evt, data) {
		$("#btnOtherPersonSubmit").attr("data-person-id", data.personId);
		$("#btnOtherPersonSubmit").attr("data-person-name-id", data.personNameId);
	});
	
	$("#btnOtherPersonSubmit").on("click", function() {
		var e = $(this);
		alert();
		$.post("/person",
				{ 	formAction: "addName",
					formFirstName: e.attr("data-person-firstName"),
					formLastName: e.attr("data-person-lastName") ,
					formPersonId: e.attr("data-person-id"),
				}
		).done(function(data) {
			alert();
			e.attr("data-person-name-id", data);
			$.post("/person",
					{ 	formAction: "addRole",
						formInterHash: e.attr("data-resource-simhash1"),
						formIntraHash: e.attr("data-resource-simhash2"),
						formUser: e.attr("data-pubowner"),
						formPersonRole: "AUTHOR",
						formPersonNameId: e.attr("data-person-name-id")
					}
			).done(function(data) {
				alert();
				document.location.href = "/person/" +  e.attr("data-person-id") + "/" + e.attr("data-person-lastname" + "," + e.attr("data-person-firstName"));
			});
		});
	});
}); 