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
	
	/*
	 * Shortcuts for the textarea
	 */
  	$('#wikiTextArea').keydown(function (e) {
	  	if (e.ctrlKey) {
	  		if(e.keyCode == 13) {
	  			submitWiki('false');
	  			return false;
	  		} else if (e.keyCode == 16) {
	  			submitWiki('true');
	  			return false;
	  		}
		}
	});
  	
  	/*
  	 * Change publication format
  	 *
  	$('select.layout').change(function(){
  		formatPublications(this);
  	})/
  	
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
