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
$.getScript("/resources/javascript/functions.js");
$.getScript("/resources/javascript/style.js");
$.getScript("/resources/javascript/chrome.js");
$.getScript("/resources/jquery/plugins/textarearesizer/jquery.textarearesizer.js");
$.getScript("/resources/jquery/plugins/corner/jquery.corner.js");
$.getScript("/resources/javascript/jquery.sidebarresizer.js");