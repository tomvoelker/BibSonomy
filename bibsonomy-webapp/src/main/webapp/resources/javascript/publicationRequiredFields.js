$(function(){
	myownTagInit($('#myownChkBox'), $('#inpf_tags'));

	document.getElementById("post.resource.author").onblur = function() {
		if(isAuthor() && !document.getElementById("myownChkBox").checked){
			document.getElementById("imAmAuthorWarning").style.display = "block"
		} else {
			document.getElementById("imAmAuthorWarning").style.display = "none"
		}
	}

	document.getElementById("myownChkBox").onclick = function() {
		document.getElementById("imAmAuthorWarning").style.display = "none"
	}

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
	if(document.getElementById("claimedPersonMainNameID") == null){ //uses the User.realName as a fallback if no Person was claimed by the user
		let individualUserRealName = document.getElementById("userRealnameID").value
		allPossibleNames.push(individualUserRealName)
	} else { //uses all names saved in the person claimed by the current user
		let userClaimedPersonMainName = document.getElementById("claimedPersonMainNameID").value;
		allPossibleNames.push(userClaimedPersonMainName)
		let userClaimedPersonNames = document.getElementById("claimedPersonNamesID").value;
		allPossibleNames = allPossibleNames.concat(userClaimedPersonNames.split(" and "));
	}

	let enteredAuthors = document.getElementById("post.resource.author").value;
	let individualEnteredAuthors = enteredAuthors.split("\n");

	if (enteredAuthors.length !== 0){
		return individualEnteredAuthors.some(enteredAuthor => allPossibleNames.some(userRealName => enteredAuthor === userRealName));
	} else{ //Fallback if the input was left empty or the input was deleted
		return false;
	}
}