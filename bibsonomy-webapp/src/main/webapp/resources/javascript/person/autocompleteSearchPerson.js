$(document).ready(function() {
	setupPersonAutocomplete('.typeahead', "search", 'extendedPersonName', function(data) {
		window.location.pathname = "/person/" + data.personId;
	});
}); 