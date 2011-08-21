/**
 * @author Bernd
 */
// "Handler"
$(function(){
	
	// Loading DIV handling

	$('#loadingDiv').hide() 
	.ajaxStart(function() {
		$(this).show();
	}).ajaxStop(function() {
		$(this).hide();
	});
	
  	$('#wikiTextArea').keydown(function (e) {
	  	if (e.ctrlKey) {
	  		if(e.keyCode == 13) {
	  			submitWiki('false');
	  		} else if (e.keyCode == 16) {
	  			submitWiki('true');
	  		}
		}
	});
  	
  	/*
  	 * Switches options hide and show details
  	 * This is just a quick fix to have the messages in some js file
  	 */
	$("a.hand").each(function(index, link) {
		$(link).click(function() {
			var result = $(this).next(".details").toggle();
			if ($(result).is(":visible")) {
				$(this).html(" " + getString("cv.options.hide_details"));
					
			} else {
				$(this).html(" " + getString("cv.options.show_details"));
			}
		});
	});
});
