function linkBox(open) {
	var target = $("#linkBox");
	if(open) {
		target.show();
		target.children("input").focus().select();
	} else {
		target.hide();
	}
}