/*
 * Scripts that are available on every discussion page
 * 
 * NOTE: this file is overwritten in the WAR file - you must add your script 
 * also to the pom.xml into the correct "aggregation" section of the 
 * yuicompressor-maven-plugin
 */
function includeJavaScript(jsFile) {
	// separated end script tag to prevent IE bug
	document.write('<script type="text/javascript" src="' + jsFile + '"></scr' + 'ipt>');
}

includeJavaScript("/resources/jquery/plugins/ui/plugins/stars/jquery.ui.stars.js");
includeJavaScript("/resources/jquery/plugins/flot/excanvas.js"); // IE fix
includeJavaScript("/resources/jquery/plugins/flot/jquery.flot.js");
includeJavaScript("/resources/jquery/plugins/flot/jquery.flot.stack.js");

includeJavaScript("/resources/javascript/bs/discussion/discussion.js");
includeJavaScript("/resources/javascript/bs/discussion/comments.js");
includeJavaScript("/resources/javascript/bs/discussion/reviews.js");
includeJavaScript("/resources/javascript/bs/discussion/csllinks.js");

includeJavaScript("/resources/javascript/citeproc/csllocales.js");
includeJavaScript("/resources/javascript/citeproc/xmldom.js");
includeJavaScript("/resources/javascript/citeproc/xmle4x.js");
includeJavaScript("/resources/javascript/citeproc/citeproc.js");
includeJavaScript("/resources/javascript/citeproc/cslstyles.js");