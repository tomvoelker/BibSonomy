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

includeJavaScript("/resources/jquery/jquery.js");
includeJavaScript("/resources/jquery/plugins/ui/jquery-ui.js");
includeJavaScript("/resources/jquery/plugins/autosize/autosize.js");
includeJavaScript("/resources/jquery/plugins/scrollTo/jquery.scrollTo.js");
includeJavaScript("/resources/jquery/plugins/putCursorAtEnd/putCursorAtEnd.js");
includeJavaScript("/resources/bootstrap/js/bootstrap.js");
includeJavaScript("/resources/javascript/holder.js");
includeJavaScript("/resources/javascript/bootstrap-dialog.js");
includeJavaScript("/resources/javascript/custom.js");
includeJavaScript("/resources/javascript/bootstrap-datepicker.js");
includeJavaScript("/resources/select2/js/select2.full.js");
includeJavaScript("/resources/javascript/bootstrap-tagsinput.js");
includeJavaScript("/resources/javascript/citeproc/xmldom.js");
includeJavaScript("/resources/javascript/citeproc/csllocales.js");
includeJavaScript("/resources/javascript/citeproc/citeproc.js");
includeJavaScript("/resources/javascript/citeproc-wrapper.js");
includeJavaScript("/resources/javascript/friendoverview.js");
includeJavaScript("/resources/javascript/functions.js");
includeJavaScript("/resources/javascript/actions/clipboard.js");
includeJavaScript("/resources/javascript/ajaxUtils.js");
includeJavaScript("/resources/javascript/fileUpload.js");
includeJavaScript("/resources/javascript/style.js");
includeJavaScript("/resources/javascript/jquery.fadebox.js");
includeJavaScript("/resources/javascript/userRelation.js");
includeJavaScript("/resources/javascript/addToSpheres.js");
includeJavaScript("/resources/javascript/logging.js");
/* the following scripts are for development only */
includeJavaScript("/resources/less/less.js");