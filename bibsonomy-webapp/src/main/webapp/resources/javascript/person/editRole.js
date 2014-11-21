$(document).ready(function() {
	$(".editRole").on("click", function() {
		$("#editRoleForm input[name=formPersonRole]").val($(this).attr("data-formPersonRole"));
		$("#editRoleForm input[name=formResourceHash]").val($(this).attr("data-formResourceHash"));
		$("#editRoleForm input[name=formPersonId]").val($(this).attr("data-formPersonId"));
		$("#modalEditRoleHeaderText").text($(this).attr("data-personName") + " - " + $(this).attr("data-resourceTitle"));
	});
});