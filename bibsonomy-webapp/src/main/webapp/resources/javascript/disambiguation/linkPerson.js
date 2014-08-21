var linkPerson = function(interhash, author) {
	document.linkPersonForm.action = "/persondisambiguation/link/" + author + "/" + interhash;
};

$(document).ready(function() {
	$("#linkPersonSubmit").on("click", function() {
		document.linkPersonForm.submit();
	});
});