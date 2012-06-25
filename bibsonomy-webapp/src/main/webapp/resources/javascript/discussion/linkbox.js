function linkBox(open) {
	var target = $("#linkBox");
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