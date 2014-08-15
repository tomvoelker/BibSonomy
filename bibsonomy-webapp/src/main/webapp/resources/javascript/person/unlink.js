var unlink = function(interhash, author) {
	document.unlinkForm.action = "/person/" + author + "/unlink/" + interhash;
};

$(document).ready(function() {
	$("#unlinkSubmit").on("click", function() {
		document.unlinkForm.submit();
	});
});