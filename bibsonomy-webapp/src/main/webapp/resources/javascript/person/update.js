
$(document).ready(function() {
	$("#btnSaveSubmit").on("click", function() {
		var e = $(this);
		$.post("/person",
				{ 	formAction: "update",
					formPersonId: e.attr("data-person-id"),
					formSelectedName: $("input[name=formSelectedName]:checked").val(),
					formAcademicDegree: $("#formAcademicDegree").val(),
					formOrcid: $("#formOrcid").val(),
					formThatsMe: $("#formThatsMe").val()
					
				}
		).done(function(data) {
			e.removeClass("btn-primary");
			e.addClass("btn-success");
			
			window.setTimeout(function() {
				e.removeClass("btn-success");
				e.addClass("btn-primary");
			},2000); 
		});	
	});
});