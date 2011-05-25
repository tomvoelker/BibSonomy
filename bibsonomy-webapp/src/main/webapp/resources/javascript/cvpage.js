/*
 * Switches Options hide and show details
 * This is just a quick fix to have the messages in some js file
 */
function switchOption(self) {
	var result = $(self).next(".details").toggle();
	if ($(result).is(":visible")) {
		$(self).html(" " + getString("cv.options.hide_details"));
	} else {
		$(self).html(" " + getString("cv.options.show_details"));
	}
}