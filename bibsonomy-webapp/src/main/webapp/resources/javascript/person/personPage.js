
$(function() {
	$(".personPageEnableForm").click(function() {
		parent = $(this).parents('.form-group');
		$(".personPageFormField", parent).show();
		$(".personPageFormPlaceholder", parent).hide();
	});
});