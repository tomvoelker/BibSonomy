$(document).ready(function() {
	$("#btnLinkSubmit").on("click", function(e) {
		var e = $(this);
		
		text = $("#btnLinkSubmit").text();
		$("#btnLinkSubmit").html(text + " <i class='fa fa-spinner fa-spin '></i>");
		$("#btnLinkSubmit").addClass("disabled");
		
		$.post("/person",
				{ 	formAction: "link",
					formPersonId: e.attr("data-person-id")
				}
		).done(function(data) {
			$("#linkPerson").modal("hide");		
			location.reload();
		});
	});
	
	$("#btnUnlinkSubmit").on("click", function(e) {
		var e = $(this);
		
		text = $("#btnUnlinkSubmit").text();	
		$("#btnUnlinkSubmit").html(text + " <i class='fa fa-spinner fa-spin '></i>");
		$("#btnUnlinkSubmit").addClass("disabled");
		
		$.post("/person",
				{ 	formAction: "unlink",
					formPersonId: e.attr("data-person-id")
				}
		).done(function(data) {
			$("#unlinkPerson").modal("hide");
			location.reload();
		});
	});
});