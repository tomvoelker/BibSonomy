
$(document).ready(function() {
	$(".btnLinkPerson").on("click", function() {
		var e = $(this); 	
		$("#btnLinkPersonSubmit").attr("data-person-id", e.attr("data-person-id"));
		$("#btnLinkPersonSubmit").attr("data-firstName", e.attr("data-firstName"));
		$("#btnLinkPersonSubmit").attr("data-lastName", e.attr("data-lastName"));
		$("#btnLinkPersonSubmit").attr("data-author-index", e.attr("data-author-index"));
		
		$("#linkShowPerson").attr("href", "/person/" + e.attr("data-person-id") + "/" + e.attr("data-lastName") + ", " + e.attr("data-firstName"));
	});
	
	$("#btnLinkPersonSubmit").on("click", function() {
		var e = $(this); 
		$.post("/person", {
			formAction: "addName",
			formPersonId: e.attr("data-person-id"),
			formFirstName: e.attr("data-firstName"),
			formLastName: e.attr("data-lastName")
		}).done(function(data) {
			$.post("/person",
					{ 	formAction: "addRole",
						formInterHash: e.attr("data-resource-simhash1"),
						formPersonId: e.attr("data-person-id") ,
						formPersonRole: "AUTHOR",
						formPersonIndex: e.attr("data-author-index")
					}
			).done(function(data) {
				window.location.href = "/person/" + e.attr("data-person-id");
			});
		});
	});
});