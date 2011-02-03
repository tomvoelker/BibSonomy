(function($) {
  	var sidebar = null;
	var sidebarGrip = null;
	var lastMousePos = 0;
	var originalWidth = null;
	var cursor = (navigator.appVersion.indexOf("X11")!=-1)?'ew-resize':'e-resize';

	$.fn.SideBarResizer = function(sidebarGrip) {
		if(sidebarGrip == null)
			return;
		sidebar = $(this);
		$(sidebar).parent().css('overflow','hidden');
		$(sidebarGrip).bind("mousedown", startDrag);
	};
	
	function startDrag(e) {
		if(originalWidth == null) 
			originalWidth = parseInt($(sidebar).width());

		sidebar.css('opacity', 0.7).parent().css('cursor', cursor);
		$(window).bind("resize", resetSize);
		lastMousePos = mousePosition(e).x;
		$(document).mousemove(performDrag).mouseup(endDrag);
		return false;
	}

	function performDrag(e) {
		var thisMousePos = mousePosition(e).x;
		var gap = thisMousePos - lastMousePos;
		lastMousePos = thisMousePos;		
		if((gap > 0 && parseInt(sidebar.width())-gap > 20) ||
					(gap < 0 && ((parseInt(sidebar.width())+Math.abs(gap)) < parseInt(originalWidth))))
						sidebar.width((parseInt(sidebar.width())-gap));
		return false;
	}
	
	function resetSize(e) {
		$(window).unbind("resize", resetSize);
		$(sidebar).width('');
		originalWidth = null;
	}

	function endDrag(e) {
		$(document).unbind('mousemove', performDrag).unbind('mouseup', endDrag);
		lastMousePos = 0;
		if(sidebar != null)
			sidebar.css('opacity', 1).parent().css('cursor', 'default'); 
		return false;
	}

	function mousePosition(e) {
		return { x: e.clientX + document.documentElement.scrollLeft, y: e.clientY + document.documentElement.scrollTop };
	};
})(jQuery);

