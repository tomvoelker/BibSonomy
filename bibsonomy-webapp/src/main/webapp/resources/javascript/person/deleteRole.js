$(document).ready(function() {
	$(".deleteRole").on("click", function() {
		$("#deleteRoleForm input[name=formPersonRole]").val($(this).attr("data-formPersonRole"));
		$("#deleteRoleForm input[name=formResourceHash]").val($(this).attr("data-formResourceHash"));
		$("#deleteRoleForm input[name=formPersonId]").val($(this).attr("data-formPersonId"));
		$("#modalDeleteRoleHeaderText").text($(this).attr("data-resourceTitle"));
		$("#modalDeleteRoleContentText").text($(this).attr("data-personName") + " - " + $(this).attr("data-formPersonRole"));
	});
});