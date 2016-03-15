function linkBox(open, map) {
	var target = null;
	
	if(map==undefined) target = $("#linkBox");
	else {
		var regexp = new RegExp("(^|[ ])"+map.className+"($|[ ])");
		if(map.target.parentNode!=undefined 
			&& map.target.parentNode.className!=undefined
				&& (map.target.parentNode.className).match(regexp)!=null) { 
			target = $(map.target.parentNode);
		} else target = $(map.target).next('.'+map.className);
	}
	
	if(open) {
		target.show();
		target.children("input").focus().select();
	} else {
		target.hide();
	}
}

$("#linkBox").keydown(function(event){
	if(event.which == 27){
		$(this).hide();
	};
});

$(".linkDiscussionBox").keydown(function(event){
	if(event.which == 27){
		$(this).hide();
	};
});