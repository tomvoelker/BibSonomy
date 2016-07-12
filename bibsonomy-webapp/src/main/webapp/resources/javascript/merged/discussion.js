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

includeJavaScript("/resources/star-rating/star-rating.js");
includeJavaScript("/resources/Chart.js/Chart.js");

includeJavaScript("/resources/javascript/discussion/discussion.js");
includeJavaScript("/resources/javascript/discussion/comments.js");
includeJavaScript("/resources/javascript/discussion/reviews.js");
includeJavaScript("/resources/javascript/discussion/csllinks.js");

includeJavaScript("/resources/javascript/citeproc/csllocales.js");
includeJavaScript("/resources/javascript/citeproc/xmldom.js");
includeJavaScript("/resources/javascript/citeproc/xmle4x.js");
includeJavaScript("/resources/javascript/citeproc/citeproc.js");
includeJavaScript("/resources/javascript/citeproc/cslstyles.js");