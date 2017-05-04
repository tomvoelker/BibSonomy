/**
 * Simple helper function that replaces the font awesome sort icons
 * according to the acutal sort order
 * @param 	div		the div where the icon is located
 * @returns
 */
function replaceFaClass(div) {
    sortOrdering = $(div).data('ordering');
    
    if (sortOrdering == 'ASC') {
    	if ($('span', div).hasClass('fa-sort-alpha-asc')) {
    		$('span', div).removeClass('fa-sort-alpha-asc');
    		$('span', div).addClass('fa-sort-alpha-desc');
    	}
    	else if ($('span', div).hasClass('fa-sort-numeric-asc')) {
    		$('span', div).removeClass('fa-sort-numeric-asc');
    		$('span', div).addClass('fa-sort-numeric-desc');
    	}
    } else {
    	if ($('span', div).hasClass('fa-sort-alpha-desc')) {
    		$('span', div).removeClass('fa-sort-alpha-desc');
    		$('span', div).addClass('fa-sort-alpha-asc');
    	}
    	else if ($('span', div).hasClass('fa-sort-numeric-desc')) {
    		$('span', div).removeClass('fa-sort-numeric-desc');
    		$('span', div).addClass('fa-sort-numeric-asc');
    	}
    }
}

/**
 * Validates a given url string
 * @param url
 * @returns	true if the given url is valid or empty, false otherwise
 */
function isValidURL(url) {
	if (!url) {
		return true;
	}
	return /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(url);
}

/**
 * Validates a given email string
 * @param mail
 * @returns true if the given mail is valid or empty, false otherwise
 */
function isValidEMail(mail) {
	if (!mail) {
		return true;
	}
	
	pattuser = /^([A-Z0-9_%+\-!#$&'*\/=?^`{|}~]+\.?)*[A-Z0-9_%+\-!#$&'*\/=?^`{|}~]+$/i;
    pattdomain = /^([A-Z0-9-]+\.?)*[A-Z0-9-]+(\.[A-Z]{2,9})+$/i;

    tab = mail.split("@");
    if (tab.length != 2) {    	
    	return false;
    }
    
    return (pattuser.test(tab[0]) && pattdomain.test(tab[1]));
}


$(document).ready(function() {
	
	// handles the sorting for the publication lists
	$('.pubSort').click(function() {
	    sortBy = $(this).data('sort');
	    sortOrdering = $(this).data('ordering');
	    sourceDiv = "#"+$(this).data('div');
	    
	    var $divs = $(sourceDiv+" div.simplePubEntry");
	    
	    var opOrder = $divs.sort(function (a, b) {
	    	if (sortOrdering == 'ASC') {	    		
	    		return $(a).data(sortBy) > $(b).data(sortBy);
	    	} else {
	    		return $(a).data(sortBy) < $(b).data(sortBy);
	    	}
	    });
	    
	    if (sortOrdering == 'ASC') {
	    	$(this).data('ordering', 'DESC');
	    } else {
	    	$(this).data('ordering', 'ASC');
	    }
	    
	    $('.pubSort').each(function() {
	    	$(this).css('color', '#ccc');
	    });
	    
	    $(this).css('color', '#333');
	    
	    replaceFaClass(this);
	     
	    $(sourceDiv).html(opOrder)

	});
	
	// orcid formatter
	$("#formOrcid").mask("9999-9999-9999-9999", {
		completed: function() {
			$(this).parent().parent().find(".personProfileUpdate").removeClass("disabled");
		}
	});
	
	// toggle view/hide all available roles
	$(".personPageShowAdditionalRoleFields").click(function() {
		$(".personPageAdditionalRoleFields", $(this).parent()).toggle();
		
		// toggle the link text
		if ($(".personPageShowAdditionalRoleFieldsMore", this).is(":visible")) {
			$(".personPageShowAdditionalRoleFieldsMore", this).hide();
			$(".personPageShowAdditionalRoleFieldsLess", this).show();
		} else {
			$(".personPageShowAdditionalRoleFieldsLess", this).hide();
			$(".personPageShowAdditionalRoleFieldsMore", this).show();		
		}
		
		// resize the sidebar
		// TODO: maybe get the path somehow else?
		$.getScript("/resources/javascript/custom.js", function() {
			//sidebarAdjusts();
			var sidebarAdjustments = sidebarAdjusts;
			sidebarAdjustments();
			$(window).resize(sidebarAdjustments);
		});
	});
	
	// enables the form field of the clicked element
	$(".personPageEnableForm").click(function() {
		parent = $(this).parents('.form-group');
		$(".personPageFormField", parent).show().css("display", "inline");
		$(".personPageFormPlaceholder", parent).hide();
	});
	
	// sends the update request	
	$(".personProfileUpdate").on("click", function() {
		
		// reject update if button is disabled
		if ($(this).hasClass("disabled")) {
			return;
		}
		
		parent = $(this).parents('.form-group');
		
		thatsMe = $("#formThatsMe").val();
		
		// save the form values to update the preview
		orcid =  $("#formOrcid").val();
		academicDegree = $("#formAcademicDegree").val();
		college = $("#formCollege").val();
		email = $("#formEmail").val();
		homepage = $("#formHomepage").val();
		
		// all values (college, emil, homepage, orcid and academic degree are sent to the server,
		// but maybe only one is updated according to the defined updateOperation
		// this operation is set at the update button definition
		var form_data = $("#personForm").serializeArray();
		form_data.push({name: "formAction", value: "update"});
		form_data.push({name: "updateOperation", value: $(this).attr("data-operation")});
		form_data.push({name: "formPersonId", value: $(this).attr("data-person-id")});
		form_data.push({name: "formThatsMe", value: thatsMe});
		
		// validate URL
		if ($(this).attr("data-operation") == "UPDATE_HOMEPAGE") {
			if (!isValidURL(homepage)) {
				$("#formHomepage").css("border-color", "red");
				return;
			}
		}
		
		// validate E-Mail
		if ($(this).attr("data-operation") == "UPDATE_EMAIL") {
			if (!isValidEMail(email)) {
				$("#formEmail").css("border-color", "red");
				return;
			}
		}		
		
		$.post("/person", form_data).done(function(data) {
			// error handling
			if (data.status) {
				// everything is fine
				$(".personPageFormPlaceholder", parent).show();
				$(".personPageFormField", parent).hide();
				
				// TODO: update the preview values (only the updated one)
				$("#personPageFormAcademicDegreeValue").text(academicDegree);
				$("#personPageFormOrcidValue").text(orcid);
				$("#personPageFormCollegeValue").text(college);
				$("#personPageFormEmailValue").text(email);
				$("#personPageFormHomepageValue").text(homepage);
				$("#personPageFormHomepageValue").attr("href", homepage);
				// TODO put success text somewhere??
				
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
		var form_data = $("#addNameForm").serializeArray();
		form_data.push({name: "formAction", value: "addName"});
		
		$.post("/person", form_data).done(function(data) {
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
	
	// empty the input field for the add role dialog
	$('#addRole').on('show.bs.modal', function (e) {
		$('#addRoleAuto').val('');
	    // also remove the fields set by typeahead
		$('#btnAddRoleSubmit').removeAttr('data-person-name');
		$('#btnAddRoleSubmit').removeAttr('data-extended-person-name');
		$('#btnAddRoleSubmit').removeAttr('data-person-id');
		$("#addRoleAuto").typeahead('val', '');
	    
	    $('#btnAddRoleSubmit').addClass('disabled');
	});
	
	
	// inserts the the values into the modal
	$(".personPageAlternativeName").on("click", function() {
		var e = $(this);
		$("#setMainNameForm input[name=formSelectedName]").val(e.attr("data-person-name-id"));
		$("#modalMainNameText").html(e.attr("data-firstName") + " " + e.attr("data-lastName"));
	});
	
	// submit the new main name form
	$("#btnSetMainNameSubmit").on("click", function() {
		var form_data = $("#setMainNameForm").serializeArray();
		form_data.push({name: "formAction", value: "setMainName"});
		
		$.post("/person", form_data).done(function(data) {
			// error handling
			if (data.status) {
				// everything is fine - reload to render the page again
				location.reload();
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