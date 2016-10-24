$(document).ready(function() {
	
	// toggle view/hide all available roles
	$(".personPageShowAdditionalRoleFields").click(function() {
		$(".personPageAdditionalRoleFields", $(this).parent()).toggle();
	});
	
	// enables the form field of the clicked element
	$(".personPageEnableForm").click(function() {
		parent = $(this).parents('.form-group');
		$(".personPageFormField", parent).show().css("display", "inline");
		$(".personPageFormPlaceholder", parent).hide();
	});
	
	// sends the update request
	$(".personProfileUpdate").on("click", function() {
		parent = $(this).parents('.form-group');
		
		orcid =  $("#formOrcid").val();
		academicDegree = $("#formAcademicDegree").val();
		
		// both values orcid and academic degree are sent to the server,
		// but maybe only one is updated according to the defined updateOperation
		// this operation is set at the update button definition
		$.post("/person",
			{ 	
				formAction: "update",
				updateOperation: $(this).attr("data-operation"),
				formPersonId: $(this).attr("data-person-id"),
				formAcademicDegree: academicDegree,
				formOrcid: orcid,					
			}
		).done(function(data) {
			// error handling
			if (data.status) {
				// everything is fine
				$(".personPageFormPlaceholder", parent).show();
				$(".personPageFormField", parent).hide();
				
				// TODO: update the preview values
				$("#personPageFormAcademicDegreeValue").text(academicDegree);
				$("#personPageFormOrcidValue").text(orcid);
				
			} else {
				// error during update
				if (data.message != "") {
					// display the error somewhere
					$("#personPageAjaxError").text(data.message).show();
				} else {
					$("#personPageAjaxError").show();
					$("personPageAjaxErrorDefaultMessage").show();
				}
			}
		});
	});
	
	// add a new name to the alternative names list
	$("#btnAddNameSubmit").on("click", function() {
		$.post("/person",
			{ 	
				formAction: "addName",
				formPersonId: $("#formPersonId").val() ,
				formFirstName: $("#formFirstName").val(),
				formLastName: $("#formLastName").val()
			}
		).done(function(data) {
			// error handling
			if (data.status) {
				// everything is fine
				
				// no alternative names so far, delete the placholder
				if ($("#personPageAlternativeNameList").hasClass("hidden")) {
					$("#personPageAlternativeNamePlaceholder").remove();
					$("#personPageAlternativeNameList").removeClass("hidden");
				}

				// add the name to the list (includes the delete button)
				$("#personPageAlternativeNameList").append('<li id="personPageAlternativeNameID_'+data.personNameChangeId+'">'+''
					+ $("#formFirstName").val()+' '+$("#formLastName").val()+' '
					+ '<span '
					+ 'data-person-name-id="'+data.personNameChangeId+'" '
					+ 'data-firstName="'+$("#formFirstName").val()+'" '
					+ 'data-lastName="'+$("#formLastName").val()+'" '
					+ 'data-toggle="modal" '
					+ 'data-target="#removeName" '
					+ 'id="removeName_'+data.personNameChangeId+'" '
					+ 'class="removeName fa fa-remove"> '
					+ '</span>'
					+ '</li>'
				);
				
				// hide the modal and reset the form fields					
				$("#addName").modal("hide");
				$("#formFirstName").val("");
				$("#formLastName").val("");
				
				// register the onclick function for the new added button:-
				$("#removeName_"+data.personNameChangeId).on("click", function() {
					var e = $(this);
					$("#removeNameForm input[name=formPersonNameId]").val(e.attr("data-person-name-id"));
					$("#modalRemoveNameText").html(e.attr("data-firstName") + " " + e.attr("data-lastName"));
					$("#removeName").modal("hide");
				});
				
			} else {
				// error during update
				if (data.message != "") {
					// display the error somewhere
					$("#personPageAjaxError").text(data.message).show();
				} else {
					$("#personPageAjaxError").show();
					$("personPageAjaxErrorDefaultMessage").show();
				}
			}
		});	
	});
	
	// inserts the the values into the modal (TODO: check if data can be taken from fields)
	$(".removeName").on("click", function() {
		var e = $(this);
		$("#removeNameForm input[name=formPersonNameId]").val(e.attr("data-person-name-id"));
		$("#modalRemoveNameText").html(e.attr("data-firstName") + " " + e.attr("data-lastName"));
	});
	
	// submit the remove alternative name modal
	$("#btnRemoveNameSubmit").on("click", function() {
		$.post("/person",
			{ 	
				formAction: "deleteName",
				formPersonNameId: $("#formPersonNameId").val()
			}
		).done(function(data) {
			// hide the modal
			var id = $("#formPersonNameId").val();
			$("#formPersonNameId").val("");
			$("#removeName").modal("hide");
			
			// error handling
			if (data.status) {
				// everything is fine
				// remove the name from the list
				$("#personPageAlternativeNameID_"+id).remove();
			} else {
				// error during update
				if (data.message != "") {
					// display the error somewhere
					$("#personPageAjaxError").text(data.message).show();
				} else {
					$("#personPageAjaxError").show();
					$("personPageAjaxErrorDefaultMessage").show();
				}
			}
		});
	});
	
});