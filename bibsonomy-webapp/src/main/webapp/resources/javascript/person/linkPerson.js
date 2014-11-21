$(document).ready(function() {
	$(".linkPerson").on("click", function(e) {
		e.preventDefault();
		$("#linkPersonForm input[name=formPersonId]").val($(this).attr("data-formPersonId"));
	});
});