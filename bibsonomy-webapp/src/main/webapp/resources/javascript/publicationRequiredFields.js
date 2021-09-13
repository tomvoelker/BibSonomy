$(function(){
	 myownTagInit($('#myownChkBox'), $('#inpf_tags'));

	function authorWarning() {
		if(isAuthor() && !$("#myownChkBox").is(':checked')){
			$('#imAmAuthorWarning').attr("style", "display:block")
		} else {
			$('#imAmAuthorWarning').attr("style", "display:none")
		}
	}

	$("#post\\.resource\\.editor").on('change',authorWarning);
	$("#post\\.resource\\.author").on('change',authorWarning);
	$('#myownChkBox').click( function() {
		$('#imAmAuthorWarning').attr("style", "display:none")
	})

});

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

/**
 * Determines whether the currently logged in user has entered a form of their own name in the authors field
 * @returns {boolean}
 */
function isAuthor() {
	let allPossibleNames = [];
	if(!$("#claimedPersonMainNameID").length){ //uses the User.realName as a fallback if no Person was claimed by the user
		let individualUserRealName = $('#userRealnameID').val()
		allPossibleNames.push(individualUserRealName)
	} else { //uses all names saved in the person claimed by the current user
		let userClaimedPersonMainName = $('#claimedPersonMainNameID').val()
		allPossibleNames.push(userClaimedPersonMainName)
		let userClaimedPersonNames = $('#claimedPersonNamesID').val()
		allPossibleNames = allPossibleNames.concat(userClaimedPersonNames.split(" and "))
	}

	let enteredAuthors = $("#post\\.resource\\.author").val();
	let individualEnteredAuthors = enteredAuthors.split("\n");
	let enteredEditors = $("#post\\.resource\\.editor").val();
	individualEnteredAuthors = individualEnteredAuthors.concat(enteredEditors.split("\n"));

	if (enteredAuthors.length !== 0 || enteredEditors.length !== 0){
		return individualEnteredAuthors.some(enteredAuthor => allPossibleNames.some(userRealName => enteredAuthor === userRealName));
	} else{ //Fallback if the input was left empty or the input was deleted
		return false;
	}
}