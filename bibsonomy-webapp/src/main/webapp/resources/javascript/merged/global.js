/*
 * Scripts that are available on /every/ page of BibSonomy (i.e., part of layout.tagx).
 * Please add scripts only, if you are 100% sure that this makes sense (discuss
 * with senior developer).
 * 
 * Note: this file is overwritten in the WAR file - you must add your script 
 * also to the pom.xml into the correct "aggregation" section of the 
 * yuicompressor-maven-plugin
 * 
 */
function includeJavaScript(jsFile) {
	// separated end script tag to prevent IE bug
	document.write('<script type="text/javascript" src="' + jsFile + '"></scr' + 'ipt>');
}

includeJavaScript("/resources/jquery/jquery.js");
includeJavaScript("/resources/javascript/functions.js");
includeJavaScript("/resources/javascript/ajaxUtils.js");
includeJavaScript("/resources/javascript/fileUpload.js");
includeJavaScript("/resources/javascript/style.js");
includeJavaScript("/resources/jquery/plugins/textarearesizer/jquery.textarearesizer.js");
includeJavaScript("/resources/jquery/plugins/corner/jquery.corner.js");
includeJavaScript("/resources/jquery/plugins/hoverIntent/jquery.hoverIntent.js");
includeJavaScript("/resources/jquery/plugins/ui/jquery-ui-1.8.16.custom.min.js");
includeJavaScript("/resources/javascript/jquery.fadebox.js");
includeJavaScript("/resources/javascript/userRelation.js");
includeJavaScript("/resources/javascript/addToSpheres.js");