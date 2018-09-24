$(document).ready(function() {
	$(".btnLinkPerson").on("click", function() {
		var link = $(this);
		$("#linkShowPerson").attr('href', link.attr("data-person-url"));
		$("#fieldLinkPersonId").val(link.attr("data-person-id"));
	});
});