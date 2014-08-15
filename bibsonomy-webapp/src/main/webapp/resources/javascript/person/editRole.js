var editRole = function(interhash, author) {
	document.editRoleForm.action = "/person/" + author + "/editRole/" + interhash;
};

$(document).ready(function() {
	$("#editRoleSubmit").on("click", function() {
		document.editRoleForm.submit();
	});
});