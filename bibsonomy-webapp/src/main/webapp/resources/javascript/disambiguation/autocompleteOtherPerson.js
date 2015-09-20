// instantiate the bloodhound suggestion engine
$(document).ready(function() {
	setupPersonAutocomplete('.typeahead', "search", 'extendedPersonName', function(data) {
		$("#otherPersonId").val(data.personId);
	});
}); 