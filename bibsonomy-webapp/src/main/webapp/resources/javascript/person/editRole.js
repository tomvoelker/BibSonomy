var editRole = function(interhash, author) {
	$("#editRole_resourceInterHash").val(interhash);
	$("#editRole_authorName").val(author);
};

$(document).ready(function() {
	$("#editRoleSubmit").on("click", function() {
		document.editRoleForm.submit();
	});
});