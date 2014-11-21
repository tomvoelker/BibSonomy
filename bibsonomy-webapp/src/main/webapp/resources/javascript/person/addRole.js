$(document).ready(function() {
	$(".addRole").on("click", function() {
		$("#addRoleForm input[name=formPersonRole]").val($(this).attr("data-formPersonRole"));
		$("#addRoleForm input[name=formResourceHash]").val($(this).attr("data-formResourceHash"));
	});
});