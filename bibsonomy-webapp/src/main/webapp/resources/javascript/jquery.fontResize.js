(function($) {
	$.fn.fontResize = function(neededFontSize) {
				var el = this;
				currentFontSize = parseInt($(el).css("font-size"));
				method = (currentFontSize-neededFontSize > 0)?-1:1;
				if(method*currentFontSize >= method*neededFontSize) {
					return el;
				}
				$(el).css("font-size", (method+currentFontSize)+'px');
				window.setTimeout(function(){$(el).fontResize(neededFontSize);}, 30);
	};
})(jQuery);

