(function($) {
	var element;  
	var lastMousePos = 0;
	var minWidth = 40; 
	var sidebarGrip;
	var sidebarSpacer;
	var width;
	var cursor = (navigator.appVersion.indexOf("X11")!=-1)?'ew-resize':'e-resize';
  	
	$.fn.SideBarResizer = function(sidebarGrip) {
		if(sidebarGrip == null) {
			return;
		}
		sidebar = $(this), staticOffset = null;
		var sidebarSpacer = $('<div class="sidebarSpacer"></div>');

		$(this).wrap('<span class="resizableElement"></span>')
		.parent().prepend($(sidebarSpacer).height($(this).height()));
		
		$(sidebarGrip).bind("mousedown",{element: this, spacer: sidebarSpacer}, startDrag);      
	};
	
	function startDrag(e) {
		sidebar = $(e.data.element), sidebarSpacer = $(e.data.spacer);
		sidebar.css('opacity', 0.7).parent().css('cursor', cursor);
		
		lastMousePos = mousePosition(e).x;
		originalWidth = parseInt(sidebar.width())+parseInt(sidebarSpacer.width());
		$(document).mousemove(performDrag).mouseup(endDrag);
		return false;
	}

	function performDrag(e) {
		var thisMousePos = mousePosition(e).x;
		var gap = thisMousePos - lastMousePos;
		var maxOffset = parseInt($(sidebar).position().left+sidebar.width());
		var minOffset = parseInt($(sidebar).position().left);

		if(!(thisMousePos > maxOffset || 
			(parseInt(sidebarSpacer.width()) <= 10 && gap < 0) || 
				(parseInt(sidebar.width()) <= 10 && gap > 0))) {
				sidebarSpacer.width(parseInt(sidebarSpacer.width())+gap);
				sidebar.width(originalWidth-parseInt(sidebarSpacer.width()));
				lastMousePos = thisMousePos;
		}
		return false;
	}

	function endDrag(e) {
		$(document).unbind('mousemove', performDrag).unbind('mouseup', endDrag);
		sidebar.css('opacity', 1).parent().css('cursor', 'default');
		sidebar = null, 
		sidebarSpacer = null, 
		staticOffset = null;
		lastMousePos = 0;
		return false;
	}

	function mousePosition(e) {
		return { x: e.clientX + document.documentElement.scrollLeft, y: e.clientY + document.documentElement.scrollTop };
	};
})(jQuery);

