$(document).ready(function () {
	setupPersonAutocomplete('.typeahead', "search", 'extendedPersonName', function (data) {
		$('#personField').attr('value', data.personId);
	});
	$('.clean-typeahead').click(function() {
		$(this).siblings().find('.typeahead').attr('placeholder', '');
		var idToReset = $(this).data("id");
		$(idToReset).attr('value', '');
		return false;
	})
});