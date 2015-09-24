$(document).ready(function() {
	$("#btnLinkSubmit").on("click", function(e) {
		var e = $(this);
		$.post("/person",
				{ 	formAction: "link",
					formPersonId: e.attr("data-person-id")
				}
		).done(function(data) {
			$("#btnLink").removeClass("btn-default");
			$("#btnLink").addClass("btn-success");
			$("#linkPerson").modal("hide");
			
			window.setTimeout(function() {
				$("#btnLink").hide();
				$("#btnUnlink").show();
				$("#btnLink").removeClass("btn-success");
				$("#btnLink").addClass("btn-default");
			},2000); 
		});
	});
	
	$("#btnUnlinkSubmit").on("click", function(e) {
		var e = $(this);
		$.post("/person",
				{ 	formAction: "unlink",
					formPersonId: e.attr("data-person-id")
				}
		).done(function(data) {
			$("#btnUnlink").removeClass("btn-default");
			$("#btnUnlink").addClass("btn-success");
			$("#unlinkPerson").modal("hide");
			
			window.setTimeout(function() {
				$("#btnLink").show();
				$("#btnUnlink").hide();
				$("#btnUnlink").removeClass("btn-success");
				$("#btnUnlink").addClass("btn-default");
			},2000); 
		});
	});
});