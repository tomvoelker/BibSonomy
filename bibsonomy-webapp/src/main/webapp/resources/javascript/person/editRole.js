var editRole = function(interhash, author) {
	document.editRoleForm.action = "/person/editRole/" + author +"/" + interhash;
};

$(document).ready(function() {
	$("#editRoleSubmit").on("click", function() {
		document.editRoleForm.submit();
	});
});