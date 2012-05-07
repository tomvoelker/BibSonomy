/*
 * Scripts that are available on every discussion page
 */
function includeJavaScript(jsFile) {
	// separated end script tag to prevent IE bug
	document.write('<script type="text/javascript" src="' + jsFile + '"></scr' + 'ipt>');
}

includeJavaScript("/resources/jquery/plugins/ui/plugins/stars/jquery.ui.stars.js");
includeJavaScript("/resources/jquery/plugins/flot/excanvas.js"); // IE fix
includeJavaScript("/resources/jquery/plugins/flot/jquery.flot.js");
includeJavaScript("/resources/jquery/plugins/flot/jquery.flot.stack.js")
includeJavaScript("/resources/javascript/discussion/discussion.js");
includeJavaScript("/resources/javascript/discussion/comments.js");
includeJavaScript("/resources/javascript/discussion/reviews.js");
includeJavaScript("/resources/javascript/discussion/csllinks.js");