var allNotChecked;
var action=[];
var index;

$(document).ready(function () {
	$('[data-toggle="tooltip"]').tooltip();

	countAndSetSelectedTagBoxes();
	countAndSetSelectedNormalizeBoxes();

	/**
	 * If allNotChecked is true, edit options will not be activated.
	 * */
	//FIXME: .not("[name$='normalize']") should be better
	allNotChecked = true;
	$('input[name^=posts]:checkbox:checked').not("[name$='normalize']").each(function() {
		allNotChecked = false;
	});
	
	/**
	 * handler to change all sub checkboxes with the select all option
	 */
	$('#selectAll').change(function() {
		var markAllChecked = $(this).is(':checked');
		
		/**
		 * mega haxxor jquery selector to select input checkboxes with name beginning
		 * with posts.
		 */
		$('input[name^=posts]:checkbox').not("[name$='normalize']").each(function() {
			$(this).prop('checked', markAllChecked);
			$(this).change();
		});
	});

	$('input[name^=posts]:checkbox').not("[name$='normalize']").change(function() {
		/**
		 * If oneNotChecked is false, 'select all' checkbox should be selected*
		 */
		
		var oneNotChecked = false;
		/**
		 * mega haxxor jquery selector to select input checkboxes with name beginning
		 * with posts that are not checked.
		 */
		$('input[name^=posts]:checkbox:not(:checked)').not("[name$='normalize']").each(function() {
			oneNotChecked = true;
		});
		$('#selectAll').prop('checked', !oneNotChecked);
		
		allNotChecked = true;
		$('input[name^=posts]:checkbox:checked').not("[name$='normalize']").each(function() {
			allNotChecked = false;
		});
		if(!allNotChecked){
			$('.selectPostAlert').toggleClass('invisible', true);
			$('.selectPostAlert').toggleClass('hidden', true);
			toggleTagEdit(false);
		}
		// if no post is checked, every thing should be hidden.
		if(allNotChecked){
			$('.selectPostAlert').toggleClass('invisible', false);
			$('.selectPostAlert').toggleClass('hidden', false);
			$('.emptyBlock').toggleClass('hidden', true);
			toggleTagEdit(true);
		}
		countAndSetSelectedTagBoxes();
	});

	/**
	 * handler to change all sub checkboxes with the normalize keys option
	 */
	$('#checkboxNormalize').change(function() {
		var markAllChecked = $(this).is(':checked');
		$('input[name^=posts][name$=normalize]:checkbox').each(function() {
			$(this).prop('checked', markAllChecked);
		});
		toggleNormalization(!markAllChecked);
		countAndSetSelectedNormalizeBoxes();
	});
	$('input[name^=posts][name$=normalize]:checkbox').change(function() {
		var anythingChecked = false;
		$('input[name^=posts][name$=normalize]:checkbox:checked').each(function() {
			anythingChecked = true;
		});
		toggleNormalization(!anythingChecked);
		countAndSetSelectedNormalizeBoxes();
	});

	/**
	 * Listeners to check if any of the tag edit options were used
	 */
	var addUpdateTagsAction = function (){
		if($.inArray(2, action) === -1)
			action.push(2);
	}
	$('input[name^=posts][name$=newTags]').change(addUpdateTagsAction)
	$('.addTagsButton').click(function(){
		addTags('input[name^=posts]:checkbox:checked', $('#tagsInput').val());
		$('#tagsInput').val("");
		addUpdateTagsAction();
	});
	$('.removeTagsButton').click(function(){
		removeTags('input[name^=posts]:checkbox:checked', $('#tagsInput').val());
		$('#tagsInput').val("");
		addUpdateTagsAction();
	});

	/**
	 * Listener to check if the visibility settings were changed
	 * (won't get removed in case the user changes it back to default, since this case can be handled in the controller)
	 */
	$('.abstractGroupingGroup').change(function(){
		if($.inArray(5, action) === -1)
			action.push(5);
	});

	$('.batchUpdateButton').click(function(){
		if($('#tagsInput').val() !== '' && !window.confirm($('input[name=editTagsWarning]').val())){
			return false
		}
		//We check all posts, since the controller currently only processes checked posts due to the legacy version of this page still used by the batch edit page
		$('input[name^=posts]:checkbox').not("[name$='normalize']").each(function() {
			$(this).prop('checked', true);
		});

		$('input[name=action]').val(action);
	});

});


function normalizeBibTexKey(){
	$('.emptyBlock').toggleClass('hidden', true);
	$('.batchUpdateButton').prop('disabled', false);
	$('.normalizeAlert').toggleClass('invisible', false);
	$('.normalizeAlert').toggleClass('hidden', false);
}

function normalizeBibTexKey_Uncheck(){
	$('.normalizeAlert').toggleClass('invisible', true);
	$('.normalizeAlert').toggleClass('hidden', true);
	$('.emptyBlock').toggleClass('hidden', false);
	
	var all_NotChecked_undirect = true;
	$('input[id^=checkbox]:checkbox:checked').each(function() {
		all_NotChecked_undirect = false;
		return;
	});
	if(all_NotChecked_undirect){
		$('.batchUpdateButton').prop('disabled', true);
	}
}

function addTags(selector, tags){
	$(selector).not("[name$='normalize']").each(function() {
		var attr = $(this).prop('name').replace('checked','newTags').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1');
		var textInput = $('input[name=' + attr + ']:text');
		var currentTags = textInput.val().split(" ");
		var newTags = tags.split(" ");
		newTags = newTags.filter(function(val) {
			return currentTags.indexOf(val) === -1;
		});
		var combinedTags = currentTags.concat(newTags);
		textInput.val(combinedTags.join(" "));
	});
}

function removeTags(selector, tags){
	$(selector).not("[name$='normalize']").each(function() {
		var attr = $(this).prop('name').replace('checked','newTags').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1');
		var textInput = $('input[name=' + attr + ']:text');
		var currentTags = textInput.val().split(" ");
		var tagsToDelete = tags.split(" ");
		currentTags = currentTags.filter(function(val) {
			return tagsToDelete.indexOf(val) === -1;
		});
		textInput.val(currentTags.join(" "));
	});
}

function toggleNormalization(disabled){
	//To simplify the code above we use this method to check if the status has changed and act accordingly
	if(disabled && ($.inArray(3, action) !== -1)){
		$('#checkboxNormalize').prop('checked', false);
		normalizeBibTexKey_Uncheck()
		action.splice(index, 2);
	} else if ($.inArray(3, action) === -1){
		$('#checkboxNormalize').prop('checked', true);
		normalizeBibTexKey()
		action.push(3);
	}
}

function toggleTagEdit(disabled){
	$('input[name=tags]').prop('disabled', disabled);
	$('#addAllTags').prop('disabled', disabled);
	$('#removeAllTags').prop('disabled', disabled);
}

function countAndSetSelectedTagBoxes(){
	var count = 0;
	$('input[name^=posts]:checkbox').not("[name$='normalize']").each(function() {
		if($(this).is(':checked'))
			count++;
	});
	$('#tagsCountBadge').text(count)
}

function countAndSetSelectedNormalizeBoxes(){
	var count = 0;
	$('input[name^=posts][name$=normalize]:checkbox').each(function() {
		if($(this).is(':checked'))
			count++;
	});
	$('#normalizeCountBadge').text(count)

}
