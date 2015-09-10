$(document).ready(function() {
	$(".btnLinkPerson").on("click", function() {
		var e = $(this);
		$("#linkShowPerson").attr('href', e.attr("data-person-url"));
		$("#fieldLinkPersonId").val( e.attr("data-person-id"));
	});
});