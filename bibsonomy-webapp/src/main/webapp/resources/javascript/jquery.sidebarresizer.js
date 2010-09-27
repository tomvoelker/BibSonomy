(function($) {
	var element;  
	var lastMousePos = 0;
	var minWidth = 40; 
	var sidebarGrip;
	var sidebarSpacer;
	var width;
  	
	$.fn.SideBarResizer = function(sidebarGrip) {
		if(sidebarGrip == null) {
			return;
		}

		sidebar = $(this), staticOffset = null;
		var sidebarSpacer = $('<div class="sidebarSpacer"></div>');

		$(this).wrap('<span class="resizableElement"><span></span></span>')							///
		.parent().prepend(sidebarSpacer.height($(this).height())).css('cursor', 'ew-resize');							
    $(sidebarGrip).bind("mousedown",{element: this, spacer: sidebarSpacer}, startDrag);      
	};
	
	/* private functions */
	function startDrag(e) {
		sidebar = $(e.data.element), sidebarSpacer = $(e.data.spacer);
		lastMousePos = mousePosition(e).x;
		originalWidth = parseInt(sidebar.width())+parseInt(sidebarSpacer.width());
		$(document).mousemove(performDrag).mouseup(endDrag);
		sidebar.css('opacity', 0.7);
		return false;
	}

	function performDrag(e) {
		var thisMousePos = mousePosition(e).x;
		var gap = thisMousePos - lastMousePos;

		if(gap == 0)
			return false;

		var maxOffset = parseInt(sidebar.position.left+sidebar.width());
		var minOffset = parseInt(sidebar.position.left);
		var sign = (lastMousePos < (thisMousePos))?-1:1; // determine wheter we need to in-/ or decrement
		var currentWidth = sidebarSpacer.width()+parseInt(sidebar.width());
		var missingPartial = originalWidth-currentWidth;
		var sidebarWidth = sidebar.width();

		if(thisMousePos > maxOffset || 
			thisMousePos < minOffset || 
				(parseInt(sidebarSpacer.width()) <= 10 && gap < 0) || 
					(parseInt(sidebar.width()) <= 10 && gap > 0)) {
			return endDrag(e);
		}

		sidebarSpacer.width(parseInt(sidebarSpacer.width())+gap);
		sidebar.width(originalWidth-parseInt(sidebarSpacer.width())+6);
		lastMousePos = thisMousePos;
		return false;
	}

	function endDrag(e) {
		$(document).unbind('mousemove', performDrag).unbind('mouseup', endDrag);
		sidebar.css('opacity', 1).css('cursor','default');
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

