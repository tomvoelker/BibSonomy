$(document).ready(function () {
	setupOrganizationAutocomplete('.typeahead', "search", 'extendedPersonName', function (data) {
		if (window.location.pathname.search("=")) {
			window.location.pathname += "&organization=" + data.name;
		} else {
			window.location.pathname += "?organization=" + data.name;
		}
	});
	$("#searchOrganizationAutocomplete").focus();
});