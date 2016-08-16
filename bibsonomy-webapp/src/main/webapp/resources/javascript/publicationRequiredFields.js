function highlightMatches(text, input) {
	var terms = input.split(" ");
	for (var i=0; i < terms.length; i++) {
		text = highlightMatch(text, terms[i]);
	} 
	return text;
}

function highlightMatch(text, term) {
	return text.replace( new RegExp("(?![^&;]+;)(?!<[^<>]*)(" +
			$.ui.autocomplete.escapeRegex(term) +
			")(?![^<>]*>)(?![^&;]+;)", "gi"
	), "<strong>$1</strong>"
	);
}

function myownTagInit(chkbox, tagbox) {
	var expr = /((^|[ ])myown($|[ ]))/gi;
	if(!(chkbox.length > 0 
			&& tagbox.length > 0)) 
		return;

	if(tagbox.val().search(expr) != -1) {
		chkbox[0].checked = true;
	} 

	tagbox.keyup(function(){
		if(tagbox.val().search(expr) != -1){
			chkbox[0].checked = true;
			return;
		}
		chkbox[0].checked = false;
	}
	);

	chkbox.click(
			function() {
				clear_tags ();
				if(this.checked 
						&& tagbox.val().search(expr) == -1){
					tagbox.removeClass('descriptiveLabel').val('myown '+tagbox.val());
				} else if(!this.checked) {
					tagbox.val(tagbox.val().replace(expr, ' ').replace(/^[ ]?/, ''));
				}
			}).parent().removeClass('hiddenElement');
}

$(function(){
		myownTagInit($('#myownChkBox'), $('#inpf_tags'));
});