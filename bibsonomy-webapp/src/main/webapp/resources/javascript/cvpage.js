/*
 * Switches options hide and show details
 * This is just a quick fix to have the messages in some js file
 */
$(function() {
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