
$(document).ready(function() {
	$(".btnLinkPerson").on("click", function() {
		var e = $(this); 	
		$("#btnLinkPersonSubmit").attr("data-person-id", e.attr("data-person-id"));
		$("#btnLinkPersonSubmit").attr("data-person-name-id", e.attr("data-person-name-id"));
		$("#btnLinkPersonSubmit").attr("data-firstName", e.attr("data-firstName"));
		$("#btnLinkPersonSubmit").attr("data-lastName", e.attr("data-lastName"));
		
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
						formIntraHash: e.attr("data-resource-simhash2"),
						formPersonId: e.attr("data-person-id") ,
						formUser: e.attr("data-pub-owner"),
						formPersonRole: "AUTHOR",
						formPersonNameId: data
					}
			).done(function(data) {
				window.location.href = "/person/" + e.attr("data-person-id") + "/" + e.attr("data-lastName") + ", " + e.attr("data-firstName"); 
			});
		});
	});
});