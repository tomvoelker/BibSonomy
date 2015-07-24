$(document).ready(function() {
	setupPersonAutocomplete('#addRoleAuto', "search", 'extendedPersonName', function(data) {
		$("#btnAddRoleSubmit").attr("data-person-name", data.personName);
		$("#btnAddRoleSubmit").attr("data-extended-person-name", data.extendedPersonName);
		$("#btnAddRoleSubmit").attr("data-person-id", data.personId);
	});

	function addRole(obj) {
		$("#btnAddRoleSubmit").attr("data-person-id", obj.attr("data-person-id"));
		$("#btnAddRoleSubmit").attr("data-author-index", obj.attr("data-author-index"));
		$("#btnAddRoleSubmit").attr("data-relation-simhash1", obj.attr("data-relation-simhash1"));
		$("#btnAddRoleSubmit").attr("data-relation-simhash2", obj.attr("data-relation-simhash2"));
		$("#btnAddRoleSubmit").attr("data-person-role", obj.attr("data-person-role"));
	};
	
	function editRole(obj) {
		$("#btnEditRoleSubmit").attr("data-resourcePersonRelation-id", obj.attr("data-resourcePersonRelation-id"));
	}
	
	function deleteRole(obj) {
		$("#btnDeleteRoleSubmit").attr("data-resourcePersonRelation-id", obj.attr("data-resourcePersonRelation-id"));
	}
	
	function addRoleHtml(resourcePersonRelationid, personFirstName, personLastName, personId, personUrl) {
		var s = $("<span class='resourcePersonRelation_"+resourcePersonRelationid+"'></span");
		var a = $("<a href='" + personUrl + "'> "+ personFirstName + " " + personLastName + " </a>");
		var sss = $(" <span data-toggle='modal' data-target='#deleteRole' data-resourcePersonRelation-id='"+resourcePersonRelationid+"' style='color:darkred;cursor:pointer' href='#deleteRole' class='deleteRole glyphicon glyphicon-remove'>&#160;</span>");
		
		sss.on("click", function() {
			deleteRole($(this));
		}); 

		s.append(a);
		//s.append(ss);
		s.append(sss);
		
		return s;
	}
	
	
	$(".addRole").on("click", function() {
		addRole($(this));
	});
	
	$(".deleteRole").on("click", function() {
		deleteRole($(this));
	});
	
	$(".editRole").on("click", function() {
		editRole($(this));
	});

	$("#btnAddRoleSubmit").on("click", function() {
		var e = $(this);
		if ($("#addRoleAuto").typeahead('val') != e.attr("data-extended-person-name")) {
			// init values for implicitly adding a new person
			e.attr("data-person-name",$("#addRoleAuto").typeahead('val'));
			e.attr("data-person-id", '');
		}
		
		var nameSplit = e.attr("data-person-name").split(" ");
		if (nameSplit.length > 1) {
			var firstName = nameSplit[0];
			nameSplit[0] = "";
			var lastName = nameSplit.join(" ");
		} else {
			var firstName = "";
			var lastName = nameSplit[0];
		}

		$.post("/person",
			{ 	formAction: "addRole",
				formFirstName: firstName,
				formLastName: lastName,
				formPersonId: e.attr("data-person-id"),
				formInterHash: e.attr("data-relation-simhash1"),
				formPersonRole: e.attr("data-person-role"),
				formPersonIndex: e.attr("data-author-index")
			}).done(
				function(data) {
					if (data.exception) {
						alert(getString('person.show.error.addRoleFailed'));
					} else {
						e.attr("data-person-id", data.personId);
						var htmlToAdd = addRoleHtml( data.resourcePersonRelationid, firstName, lastName, data.personId, data.personUrl);
						var selector = "."+e.attr("data-relation-simhash1") + "_" + e.attr("data-relation-simhash2") + " ." + e.attr("data-person-role") + " .addRole";
						var selectedNode = $(selector);
						selectedNode.before(htmlToAdd);
						$("#addRole").modal("hide");
					}
				}
			);
		
	});
	
	$("#btnDeleteRoleSubmit").on("click", function() {
		var e = $(this);
		$.post("/person",
				{ 	formAction: "deleteRole",
					formResourcePersonRelationId: e.attr("data-resourcePersonRelation-id")
				}
		).done(function(data) {
			$(".resourcePersonRelation_"+e.attr("data-resourcePersonRelation-id")).remove();
			$("#deleteRole").modal("hide");
		});
	});
	
	$("#btnEditRoleSubmit").on("click", function() {
		var e = $(this);
		$("#editRole input[type=checkbox]:checked").each(function() {
			alert($(this).val());
		});

	})
});