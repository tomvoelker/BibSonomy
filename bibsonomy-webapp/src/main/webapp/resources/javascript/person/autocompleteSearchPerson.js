$(document).ready(function() {
	setupPersonAutocomplete('.typeahead', function(data) {
		window.location.pathname = "/person/" + data.personId;
	});
}); 