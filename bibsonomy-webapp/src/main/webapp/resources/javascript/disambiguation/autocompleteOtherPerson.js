//instantiate the bloodhound suggestion engine
$(document).ready(function() {
	setupPersonAutocomplete('.typeahead', function(data) {
		$("#btnOtherPersonSubmit").attr("data-person-id", data.personId);
		$("#btnOtherPersonSubmit").attr("data-person-name-id", data.personNameId);
	});
	
	$("#btnOtherPersonSubmit").on("click", function() {
		var e = $(this);
		$.post("/person", {
			formAction: "addName",
			formPersonId: e.attr("data-person-id"),
			formFirstName: e.attr("data-person-firstName"),
			formLastName: e.attr("data-person-lastName") ,
		}).done(function(data) {
			$.post("/person",
					{ 	formAction: "addRole",
						formInterHash: e.attr("data-resource-simhash1"),
						formPersonId: e.attr("data-person-id"),
						formPersonRole: "AUTHOR",
						formPersonIndex: e.attr("data-author-index")
					}
			).done(function(data) {
				window.location.href = "/person/" +  e.attr("data-person-id");
			});
		});
	});
}); 