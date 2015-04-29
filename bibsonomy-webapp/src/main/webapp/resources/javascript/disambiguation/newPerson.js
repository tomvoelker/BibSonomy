
$(document).ready(function() {
	$("#btnNewPersonSubmit").on("click", function() {
		var e = $(this);
		
		$.post("/person",
				{ 	formAction: "new",
					formInterHash: e.attr("data-resource-simhash1"),
					formIntraHash: e.attr("data-resource-simhash2"),
					formPersonRole: "AUTHOR",
					formFirstName: e.attr("data-person-firstName"),
					formLastName: e.attr("data-person-lastName"),
					formUser: e.attr("data-pubowner"),
					formAuthorIndex: e.attr("data-author-index")
				}
		).done(function(data) {
			window.location.href = "/person/" + data.personId + "/" + data.personName;
		});	
	});
});