$(document).ready(function() {
	setupPersonAutocomplete('.typeahead', function(data) {
		// FIXME: dangerous: personName needs to be escaped! -> better submit some hidden form with regular (non-path) parameters and follow a redirect returned by the controller
		window.location.pathname = "/person/" + data.personId + "/" + data.personName;
	});
}); 