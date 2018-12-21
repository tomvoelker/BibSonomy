$(document).ready(function () {
	setupPersonAutocomplete('.typeahead', "search", 'extendedPersonName', function (data) {
		var regex = new RegExp('person=([^=&]+)(&.*)?$').compile();
		var path = window.location.pathname;
		var match = regex.exec(path);
		if (match.length !== 0) {
			var lastIndex = regex.lastIndex;
			match = regex.exec(path);
			window.location.pathname = path.substring(0, lastIndex) + data.personId + path.substring(regex.lastIndex)
		} else if (window.location.pathname.search("=")) {
			window.location.pathname += "&person=" + data.personId;
		} else {
			window.location.pathname += "?person=" + data.personId;
		}
	});
	$("#searchPersonAutocomplete").focus();
});