//instantiate the bloodhound suggestion engine
$(document).ready(function() {
	setupPersonAutocomplete('.typeahead', function(data) {
		$("#btnOtherPersonSubmit").attr("data-person-id", data.personId);
		$("#btnOtherPersonSubmit").attr("data-person-name-id", data.personNameId);
	});
	
	$("#btnOtherPersonSubmit").on("click", function() {
		var e = $(this);
		$.post("/person",
				{ 	formAction: "addName",
					formFirstName: e.attr("data-person-firstName"),
					formLastName: e.attr("data-person-lastName") ,
					formPersonId: e.attr("data-person-id"),
				}
		).done(function(data) {
			$.post("/person",
					{ 	formAction: "addRole",
						formInterHash: e.attr("data-resource-simhash1"),
						formPersonRole: "AUTHOR",
						formPersonId: e.attr("data-person-id"),
						formAuthorIndex: e.attr("data-author-index")
					}
			).done(function(data) {
				document.location.href = "/person/" +  e.attr("data-person-id") + "/" + e.attr("data-person-lastname" + ", " + e.attr("data-person-firstName"));
			});
		});
	});
}); 