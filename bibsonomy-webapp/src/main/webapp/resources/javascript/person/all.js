$(document).ready(function() {
	
	var changeRoleDialog = $("#changeRoleDialog").dialog({
		autoOpen: false,
		height: 300,
		width: 350,
		modal: true,
		buttons: {
			"Change Role": function() {
				var id = $("#changeRoleForId").val(); 
				var roles = "";
				changeRoleDialog.find("input:checked").each(function() {
					roles = roles + " " + $(this).val();
				})
				$("#"+id).find(".roleColoumn").text(roles);
				changeRoleDialog.dialog("close");
				$("#changeRoleField").val("");
			},
			Cancel: function() {
				changeRoleDialog.dialog( "close" );
			},
		},
		close: function() {
			document.changeRoleForm.reset();
		}
	});
});