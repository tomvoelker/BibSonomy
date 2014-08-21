var unlink = function(interhash, author) {
	document.unlinkForm.action = "/person/unlink/" + author + "/" + interhash;
};

$(document).ready(function() {
	$("#unlinkSubmit").on("click", function() {
		document.unlinkForm.submit();
	});
});