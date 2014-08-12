var unlink = function(interhash, author) {
	$("#unlink_resourceInterHash").val(interhash);
	$("#unlink_authorName").val(author);
};

$(document).ready(function() {
	$("#unlinkSubmit").on("click", function() {
		document.unlinkForm.submit();
	});
});