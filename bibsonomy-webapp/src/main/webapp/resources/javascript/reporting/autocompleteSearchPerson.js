$(document).ready(function () {
	setupPersonAutocomplete('.typeahead', "search", 'extendedPersonName', function (data) {
		//var regex = new RegExp('person=([^=&]+)(&.*)?$').compile();
		if (window.location.pathname.search("=")) {
			window.location.pathname += "&person=" + data.personId;
		} else {
			window.location.pathname += "?person=" + data.personId;
		}
	});
	$("#searchPersonAutocomplete").focus();
});