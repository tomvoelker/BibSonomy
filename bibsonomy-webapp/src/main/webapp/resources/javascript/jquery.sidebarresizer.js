(function($) {
	var element;
	var lastMousePos = 0;
	var sidebarGrip;
	var originalWidth = null;
	var cursor = (navigator.appVersion.indexOf("X11")!=-1)?'ew-resize':'e-resize';
  	
	$.fn.SideBarResizer = function(sidebarGrip) {
		if(sidebarGrip == null) {
			return;
		}
		sidebar = $(this), originalWidth = parseInt($(sidebar).width());
		$(sidebar).parent().css('overflow','hidden');
		$(sidebarGrip).bind("mousedown",{element: this}, startDrag);
	};
	
	function startDrag(e) {
		sidebar = $(e.data.element);
		sidebar.css('opacity', 0.7).parent().css('cursor', cursor);
		$(window).bind("resize",{element: sidebar}, resetSize);
		lastMousePos = mousePosition(e).x;
		$(document).mousemove(performDrag).mouseup(endDrag);
		return false;
	}

	function performDrag(e) {
		var thisMousePos = mousePosition(e).x;
		var gap = thisMousePos - lastMousePos;
		lastMousePos = thisMousePos;		
		if((parseInt(sidebar.width())-gap > 20 && gap > 0) ||
					(((parseInt(sidebar.width())+Math.abs(gap)) < parseInt(originalWidth)) && gap < 0)) 
						sidebar.width((parseInt(sidebar.width())-gap));
		return false;
	}
	
	function resetSize(e) {
		$(e.data.element).width('');
		$(window).unbind("resize", resetSize);
		console.log(originalWidth);
		originalWidth = parseInt($(e.data.element).width());
		console.log(originalWidth);
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

