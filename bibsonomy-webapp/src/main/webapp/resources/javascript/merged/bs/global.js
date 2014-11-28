/*
 * Scripts that are available on /every/ page of BibSonomy (i.e., part of layout.tagx).
 * Please add scripts only, if you are 100% sure that this makes sense (discuss
 * with senior developer).
 * 
 * NOTE: this file is overwritten in the WAR file - you must add your script 
 * also to the pom.xml into the correct "aggregation" section of the 
 * yuicompressor-maven-plugin
 * 
 */
function includeJavaScript(jsFile) {
	// separated end script tag to prevent IE bug
	document.write('<script type="text/javascript" src="' + jsFile + '"></scr' + 'ipt>');
}

includeJavaScript("/resources/javascript/bs/jquery-2.1.1.js");
includeJavaScript("/resources/jquery/plugins/ui/jquery-ui.js");
includeJavaScript("/resources/javascript/bs/bootstrap.js");
includeJavaScript("/resources/javascript/bs/holder.js");
includeJavaScript("/resources/javascript/bs/bootstrap-dialog.js");
includeJavaScript("/resources/javascript/bs/custom.js");
includeJavaScript("/resources/javascript/bs/bootstrap-datepicker.js");
includeJavaScript("/resources/javascript/bs/bootstrap-carousel.js");
includeJavaScript("/resources/javascript/bs/friendoverview.js");
includeJavaScript("/resources/javascript/bs/functions.js");
includeJavaScript("/resources/javascript/bs/actions/clipboard.js");
includeJavaScript("/resources/javascript/bs/ajaxUtils.js");
includeJavaScript("/resources/javascript/bs/fileUpload.js");
includeJavaScript("/resources/javascript/bs/style.js");
includeJavaScript("/resources/javascript/bs/jquery.fadebox.js");
includeJavaScript("/resources/javascript/bs/userRelation.js");
includeJavaScript("/resources/javascript/bs/addToSpheres.js");

includeJavaScript("/resources/javascript/bs/bootstrap-tagsinput.js");

includeJavaScript("/resources/javascript/less/less.js");