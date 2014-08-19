/*
 * fade effect for system tags (and probably other things) 
 */
(function($) {
	$.fn.fadeBox = function(options) {
		$(this).each(
				function () {
					var self = this; // the small icon
					/*
					 * the system tags are in a list following the icon to show them
					 */
					var hiddenBox = $(this).siblings("ul:first");
					var hideTimeout = 0;

					// hide the system tags
					var hideCallback = function() {
						hideTimeout = setTimeout(function(){
							hiddenBox.fadeOut('slow');
						}, options.timeout );
					};
					/*
					 * hovering over the icon shows the system tags
					 * staying with the mouse over the icon stops them from disappearing
					 */
					$(self).mouseout(hideCallback).mouseover(function() {clearTimeout(hideTimeout); hiddenBox.fadeIn("slow");});
					/*
					 * hovering over the system tags stops them from disappearing  
					 */
					hiddenBox.mouseout(hideCallback).mouseover(function() {clearTimeout(hideTimeout);});
				}		
		);
	};
})(jQuery);
