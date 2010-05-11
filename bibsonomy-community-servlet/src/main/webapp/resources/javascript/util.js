/**
 * Some useful utility functions
 */

/**
 * Parses query string for given variable and returns assign value if found.
 * e.g. getPostParameter('http://foo.bar?sna=fu', sna) = 'fu'
 * 
 * @return assigned value if found, otherwise null.
 */
function getPostParameter(queryString, variable) {
	var vars = queryString.split("&");
	for (var i=0;i<vars.length;i++) {
		var pair = vars[i].split("=");
		if (pair[0] == variable) {
			// given variable found
			return pair[1];
		}
	}

	// given variable not found
	return null;
}