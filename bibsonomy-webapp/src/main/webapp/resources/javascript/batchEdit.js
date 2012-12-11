function markAll(input) {
	if(input) {	
		if($(input).is(':checkbox')) {
			var markAllChecked = $(input).is(':checked');
			/*
			 * mega haxxor jquery selector to select input checkboxes with name beginning
			 * with posts.
			 */
			$('input[name^=posts]:checkbox').each(function() {
				$(this).attr('checked', markAllChecked);
			});
		}
	}
}

function checkMarkAll(input) {
	if(input) {
		if($(input).is(':checkbox')) {
			var oneNotChecked = false;
			/*
			 * mega haxxor jquery selector to select input checkboxes with name beginning
			 * with posts that are not checked.
			 */
			$('input[name^=posts]:checkbox:not(:checked)').each(function() {
				oneNotChecked = true;
				return;
			});
			$('#all').attr('checked', !oneNotChecked);
		}
	}
}

function askForMarked(input) {
	var confirmed = confirm(input);
	manipulateTagArea(confirmed);
}

function manipulateTagArea(disabled) {
	$('input[name^=posts]:checkbox:checked').each(function() {
		/*
		 * remove possible special characters from selector string
		 */
		var attr = $(this).attr('name').replace('posts','newTags').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1');
		$('input[name=' + attr + ']:text').attr('disabled', disabled);
	});
}
		           
maximizeById("general");