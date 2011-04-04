(function($) {
	$.fn.fadeBox = function(options) {
       	options.className = options.className;
       	
        $(this).each(
				function () {
					var hiddenBox = ((options.element && typeof options.element != 'undefined')?$(options.element).addClass(options.className):$('<div style="position:absolute" class="'+options.className+'"></div>'));
					var hideTimeout = 0;
					var content = ((typeof options.contentCallback === "string")?options.contentCallback:options.contentCallback(this));
					var callback = function() 
						{
							hideTimeout = setTimeout (function(){hiddenBox.fadeOut('slow');}, options.timeout );
						};
					$("body").append(hiddenBox);
					hiddenBox.html(content);
					$(this).mouseover(
							function() {
								var left = ((options.leftOffset && options.leftOffset != 'undefined')?options.leftOffset($(this)):
											(($(this).offset().left + $(this).width()/2-hiddenBox.width()/2 > 0)?
											(($(document).width() > ($(this).offset().left + $(this).width()/2)-hiddenBox.width()/2)?
													$(this).offset().left + $(this).width()/2-hiddenBox.width()/2:$(document).width()-hiddenBox.width()):0));
								var top = ((options.topOffset && typeof options.topOffset != 'number')?options.topOffset($(this)):$(this).offset().top-options.topOffset);
								hiddenBox.
								css( 
										{ "left": left + "px", 
										"top": top + "px" } 
								);
								clearTimeout(hideTimeout);
								hiddenBox.show().fadeIn("slow");
							}
					).mouseout(
							callback
					).css("cursor","pointer");
					
					hiddenBox.mouseover(
							function() {
								clearTimeout(hideTimeout);
							}
					).mouseout(
							callback
					);
				}		
			);
	};
})(jQuery);