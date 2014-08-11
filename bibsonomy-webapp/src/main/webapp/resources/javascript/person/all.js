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
	
	var refresh = function() {	
		$(".authorEditorList").each(function(indexA) {
			$(this).find("table").attr("id", indexA);
			var c = -1;
			$(this).find(".personLine").each(function(indexB) {
				$(this).attr("id", indexA + "_" + indexB);
				c++;
			});
			$(this).find("table").attr("data-latestid", c);
		})
		$(".editPersonRole").unbind("click");
		$(".editPersonRole").on("click", function() {
			var id = $(this).parent().parent().attr("id");
			$("#changeRoleForId").val(id);
			changeRoleDialog.dialog("open");
		});
		$(".deletePersonRole").unbind("click");
		$(".deletePersonRole").on("click", function(e) {
				e.preventDefault();
				$(this).parent().parent().remove();
			});
		}
		refresh();
});