$(document).ready(function() {
	$("#btnAddNameSubmit").on("click", function() {
		$.post("/person",
				{ 	formAction: "addName",
					formPersonId: $("#formPersonId").val() ,
					formFirstName: $("#formFirstName").val(),
					formLastName: $("#formLastName").val()
				}
		).done(function(data) {
			$("#nameInsertionPoint").before("<div class='checkbox' id='personName_"+data+"'><label><input type='radio' name='formSelectedName' value='" + data + "' /> " + $("#formFirstName").val() + " " + $("#formLastName").val() + "</label> &#160;<span data-person-name-id='" + data + "' data-firstName='"+$("#formFirstName").val()+"' data-lastName='"+$("#formFirstName").val()+"' data-toggle='modal' data-target='#removeName' style='color:darkred;cursor:pointer' href='#remName' id='removeName_"+data+"' class='removeName fa fa-remove'></span></div>");
			$("#addName").modal("hide");
			$("#formFirstName").val("");
			$("#formLastName").val("");
			$("#removeName_"+data).on("click", function() {
				var e = $(this);
				$("#removeNameForm input[name=formPersonNameId]").val(e.attr("data-person-name-id"));
				$("#modalRemoveNameText").html(e.attr("data-firstName") + " " + e.attr("data-lastName"));
				$("#removeName").modal("hide");
			});
			
		});	
	});
	
	$(".removeName").on("click", function() {
		var e = $(this);
		$("#removeNameForm input[name=formPersonNameId]").val(e.attr("data-person-name-id"));
		$("#modalRemoveNameText").html(e.attr("data-firstName") + " " + e.attr("data-lastName"));
	});
	
	$("#btnRemoveNameSubmit").on("click", function() {
		$.post("/person",
				{ 	formAction: "deleteName",
					formPersonNameId: $("#formPersonNameId").val()
				}
		).done(function(data) {
			$("#personName_" +$("#formPersonNameId").val()).remove();
			$("#formPersonNameId").val("");
			$("#removeName").modal("hide");
		});
	});
});