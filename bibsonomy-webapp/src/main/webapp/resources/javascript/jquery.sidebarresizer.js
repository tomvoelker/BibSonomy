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
		
		lastMousePos = mousePosition(e).x;
		$(document).mousemove(performDrag).mouseup(endDrag);
		return false;
	}

	function performDrag(e) {
		var thisMousePos = mousePosition(e).x;
		var gap = thisMousePos - lastMousePos;
		var maxOffset = parseInt($(sidebar).position().left+sidebar.width());
		var minOffset = parseInt($(sidebar).position().left);
		
		if(((parseInt(sidebar.width())-gap > 20 && gap > 0) || 
				(((parseInt(sidebar.width())+gap) < originalWidth) && gap < 0))) 
					sidebar.width((parseInt(sidebar.width())-gap));
		if(parseInt(sidebar.width())>parseInt(originalWidth))
			sidebar.width(parseInt(originalWidth)+'px');
		lastMousePos = thisMousePos;
		return false;
	}

	function endDrag(e) {
		$(document).unbind('mousemove', performDrag).unbind('mouseup', endDrag);
		sidebar.css('opacity', 1).parent().css('cursor', 'default');
		sidebar = null, 
		lastMousePos = 0;
		return false;
	}

	function mousePosition(e) {
		return { x: e.clientX + document.documentElement.scrollLeft, y: e.clientY + document.documentElement.scrollTop };
	};
})(jQuery);

