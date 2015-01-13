$(document).ready(function() {
	
	var personNames = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		remote: '/person?formAction=search&formSelectedName=%QUERY'
	});

	// kicks off the loading/processing of `local` and `prefetch`
	personNames.initialize();

	var personNameTypeahead = $('#addRoleAuto').typeahead({
		hint: true,
		highlight: true,
		minLength: 1
	},
	{
		name: 'personNames',
		displayKey: 'personName',
		// `ttAdapter` wraps the suggestion engine in an adapter that
		// is compatible with the typeahead jQuery plugin
		source: personNames.ttAdapter()
	});
	
	personNameTypeahead.on('typeahead:selected', function(evt, data) {
		$("#btnAddRoleSubmit").attr("data-person-name", data.personName);
		$("#btnAddRoleSubmit").attr("data-person-id", data.personId);
		$("#btnAddRoleSubmit").attr("data-person-name-id", data.personNameId);
	});

	function addRole(obj) {
		$("#btnAddRoleSubmit").attr("data-person-name-id", obj.attr("data-person-name-id"));
		$("#btnAddRoleSubmit").attr("data-relation-pubowner", obj.attr("data-relation-pubowner"));
		$("#btnAddRoleSubmit").attr("data-relation-simhash2", obj.attr("data-relation-simhash2"));
		$("#btnAddRoleSubmit").attr("data-relation-simhash1", obj.attr("data-relation-simhash1"));
		$("#btnAddRoleSubmit").attr("data-person-role", obj.attr("data-person-role"));
	};
	
	function editRole(obj) {
		$("#btnEditRoleSubmit").attr("data-rpr-id", obj.attr("data-rpr-id"));
	}
	
	function deleteRole(obj) {
		$("#btnDeleteRoleSubmit").attr("data-rpr-id", obj.attr("data-rpr-id"));
	}
	
	function addRoleHtml(rprid, personNameId, personFirstName, personLastName, resourceTitle, simhash1, simhash2, role, personId) {
		var s = $("<span class='rpr_"+rprid+"'></span");
		var a = $("<a href='/person/"+ personId + "/" + personLastName + "," + personFirstName+ "'> "+ personFirstName + " " + personLastName + " </a>");
		//var ss = $("<span data-toggle='modal' data-target='#editRole' data-resource-title='"+resourceTitle+"' data-person-name='"+personName+"' data-relation-simhash1='"+simhash1+"' data-relation-simhash2='"+simhash2+"' data-person-role='"+role+"' style='color:orange;cursor:pointer' href='#editRole' class='editRole glyphicon glyphicon-pencil'>&#160;</span>");
		var sss = $(" <span data-toggle='modal' data-target='#deleteRole' data-rpr-id='"+rprid+"' style='color:darkred;cursor:pointer' href='#deleteRole' class='deleteRole glyphicon glyphicon-remove'>&#160;</span>");
		
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
		if($("#addRoleAuto").typeahead('val') != e.attr("data-person-name")) {
			e.attr("data-person-name",$("#addRoleAuto").typeahead('val'));
			e.attr("data-person-id", 0);
			e.attr("data-person-name-id", 0)
			
			var nameSplit = e.attr("data-person-name").split(" ");
			if(nameSplit.length > 1) {
				var firstName = nameSplit[0];
				nameSplit[0] = "";
				var lastName = nameSplit.join(" ");
			} else {
				var firstName = "";
				var lastName = nameSplit[0];
			}
			
			
			
			$.post("/person",
				{ 	formAction: "new",
					formFirstName: firstName,
					formLastName: lastName,
					formUser: e.attr("data-relation-pubowner"),
					formIntraHash: e.attr("data-relation-simhash2"),
					formInterHash: e.attr("data-relation-simhash1"),
					formPersonRole: e.attr("data-person-role")
				}).done(function(data) {
					e.attr("data-person-id", data.personId);
					e.attr("data-person-name-id", data.personNameId);
					$("."+e.attr("data-relation-simhash1") + "_" + e.attr("data-relation-simhash2") + " ." + e.attr("data-person-role") + " .addRole").before(
							addRoleHtml(
									data.rprid, 
									data.personNameId,
									firstName,
									lastName,
									e.attr("data-resource-title"),
									e.attr("data-relation-simhash1"),
									e.attr("data-relation-simhash2"),
									e.attr("data-person-role"),
									data.personId
							));
					$("#addRole").modal("hide");
				});
		} else {
			
			$.post("/person",
					{ 	formAction: "addRole",
						formPersonNameId: e.attr("data-person-name-id"),
						formUser : e.attr("data-relation-pubowner"),
						formIntraHash: e.attr("data-relation-simhash2"),
						formInterHash: e.attr("data-relation-simhash1"),
						formPersonRole: e.attr("data-person-role")
					}
			).done(function(data) {
				$("."+e.attr("data-relation-simhash1") + "_" + e.attr("data-relation-simhash2") + " ." + e.attr("data-person-role") + " .addRole").before(
						addRoleHtml(
								data, 
								e.attr("data-person-name-id"),
								e.attr("data-person-name").split(",")[1],
								e.attr("data-person-name").split(",")[0],
								e.attr("data-resource-title"),
								e.attr("data-relation-simhash1"),
								e.attr("data-relation-simhash2"),
								e.attr("data-person-role"),
								e.attr("data-person-id")
						));
				$("#addRole").modal("hide");
			});
		}
	});
	
	$("#btnDeleteRoleSubmit").on("click", function() {
		var e = $(this);
		$.post("/person",
				{ 	formAction: "deleteRole",
					formRPRId: e.attr("data-rpr-id")
				}
		).done(function(data) {
			$(".rpr_"+e.attr("data-rpr-id")).remove();
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