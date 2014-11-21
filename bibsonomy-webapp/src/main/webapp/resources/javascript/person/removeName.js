$(document).ready(function() {
	$(".removeName").on("click", function() {
		$("#removeNameForm input[name=formFirstName]").val($(this).attr("data-firstName"));
		$("#removeNameForm input[name=formLastName]").val($(this).attr("data-lastName"));
		$("#modalRemoveNameText").html($(this).attr("data-firstName") + " " + $(this).attr("data-lastName"))
	});
});