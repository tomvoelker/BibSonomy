(function($) {
	$.fn.fadeBox = function(options) {
       	options.className = options.className;
       	
        $(this).each(
				function () {
					var hiddenBox = ((options.element && typeof options.element != 'undefined')?$(options.element).addClass(options.className):$('<div style="position:absolute" class="'+options.className+'"></div>'));
					var hideTimeout = 0;
					var content = ((typeof options.contentCallback === "string")?options.contentCallback:options.contentCallback(this));
					var self = this;
					var timeout = options.timeout;
					
					var callback = function() 
						{hideTimeout = setTimeout (function(){hiddenBox.fadeOut('slow');}, timeout );};
					var drawCallback = function() {
						var left = ((options.leftOffset && options.leftOffset != 'undefined')?options.leftOffset($(self)):
							(($(self).offset().left + $(self).width()/2-hiddenBox.width()/2 > 0)?
								(($(document).width() > ($(self).offset().left + $(self).width()/2)-hiddenBox.width()/2)?
									$(self).offset().left + $(self).width()/2-hiddenBox.width()/2:$(document).width()-hiddenBox.width()):0));
						var top = ((options.topOffset && typeof options.topOffset != 'number')?options.topOffset($(self)):$(self).offset().top-options.topOffset);
						hiddenBox.
						css(
								{ "left": left + "px", 
								"top": top + "px" } 
						);
						clearTimeout(hideTimeout);
						hiddenBox.show().fadeIn("slow");
					};
					$("body").append(hiddenBox);
					hiddenBox.html(content);
					$(self).mouseover(function(){timeout = options.timeout;drawCallback();})
					.mouseout(callback)
					.css("cursor","pointer")
					.click(function(){timeout = 3*options.timeout;drawCallback();callback();});
					
					hiddenBox.mouseover(function() {clearTimeout(hideTimeout);})
					.mouseout(callback);
				}		
			);
	};
})(jQuery);

function sysTagFadeBox(self) {
$(".hiddenSystemTag").fadeBox(
		{
			className: 'initiallyHidden listhButtons',
			contentCallback: function (self) {
				var tags = ($(self).attr('tags')).replace(/^\s|\s$/g, '').split(" ");
				var url = $(self).attr('url');
				var content = "";
				for (var t = 0; t < tags.length; t++) {
					content += ' <a href="' + url + tags[t] + '" title="' + getString('systemtag') + ' ' + tags[t] + '">' + tags[t] + '</a>';
				}
				return content;
			},
			timeout:1500,
			topOffset: 25
		}
	);
};