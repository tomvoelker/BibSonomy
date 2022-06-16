var action=[];
var showNormalize;
var isImport;

var tagCheckBoxSelector = 'input[name^=posts][name$=checked]:checkbox';
var visibilityCheckBoxSelector = 'input[name^=posts][name$=updateVisibility]:checkbox';
var normalizeCheckBoxSelector = 'input[name^=posts][name$=normalize]:checkbox';
var deleteCheckBoxSelector = 'input[name^=posts][name$=delete]:checkbox';

$(document).ready(function () {
	$('[data-toggle="tooltip"]').tooltip();
 	showNormalize=$('input[name=resourcetype]').val()==='bibtex';
 	isImport=$('input[name=isImport]').val()==='true';

	updateBadges();
	toggleTagEdit(countCheckedBoxes(tagCheckBoxSelector)===0);
	if(!isImport)
		toggleEditVisibility(countCheckedBoxes(visibilityCheckBoxSelector)===0);
	if(showNormalize)
		toggleNormalize(countCheckedBoxes(normalizeCheckBoxSelector)===0);
	if(!isImport)
		toggleDelete(countCheckedBoxes(deleteCheckBoxSelector)===0);

	/**
	 * handler to change all tag checkboxes with the select all option
	 */
	$('#selectAllTags').change(function() {
		var markAllChecked = $(this).is(':checked');
		$(tagCheckBoxSelector).each(function() {
			if(!$('input[name=' + $(this).prop('name').replace('checked','delete').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1')+ ']:checkbox:checked').length)
				$(this).prop('checked', markAllChecked).change();
		});
	});
	$(tagCheckBoxSelector).change(function() {
		//If oneNotChecked is false, 'select all' checkbox should be selected
		var oneNotChecked = false;

		//jquery selector to select tag input checkboxes that are not checked.
		$(tagCheckBoxSelector+':not(:checked)').each(function() {
			oneNotChecked = true;
		});
		$('#selectAllTags').prop('checked', !oneNotChecked);

		var allNotChecked = countCheckedBoxes(tagCheckBoxSelector)===0;
		toggleTagEdit(allNotChecked);
		updateBadges();
	});

	if(showNormalize) {
		/**
		 * handler to change all sub checkboxes with the normalize keys option
		 */
		$('#checkboxNormalize').change(function () {
			var markAllChecked = $(this).is(':checked');
			$(normalizeCheckBoxSelector).each(function () {
				if (!$('input[name=' + $(this).prop('name').replace('normalize', 'delete').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1') + ']:checkbox:checked').length)
					$(this).prop('checked', markAllChecked).change();
			});
		});
		$(normalizeCheckBoxSelector).change(function () {
			toggleNormalize(countCheckedBoxes(normalizeCheckBoxSelector) === 0);
			updateBadges();
		});
	}

	if(!isImport) {
		/**
		 * handler to change all sub checkboxes with the update visibility option
		 */
		$('#checkboxVisibility').change(function () {
			var markAllChecked = $(this).is(':checked');
			$(visibilityCheckBoxSelector).each(function () {
				if (!$('input[name=' + $(this).prop('name').replace('updateVisibility', 'delete').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1') + ']:checkbox:checked').length)
					$(this).prop('checked', markAllChecked).change();
			});
		});
		$(visibilityCheckBoxSelector).change(function () {
			toggleEditVisibility(countCheckedBoxes(visibilityCheckBoxSelector) === 0);
			updateBadges();
		});

		/**
		 * handler to change all sub checkboxes with the delete option
		 */
		$('#checkboxDelete').change(function () {
			var markAllChecked = $(this).is(':checked');
			$(deleteCheckBoxSelector).each(function () {
				$(this).prop('checked', markAllChecked).change();
			});
		});
		$(deleteCheckBoxSelector).change(function () {
			if ($(this).is(':checked')) {
				$(this).parent().parent().prop('style', "background-color: #f2dede; ");
				$('input[name=' + $(this).prop('name').replace('delete', 'checked').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1') + ']:checkbox').prop('checked', false).prop('disabled', true).change();
				$('input[name=' + $(this).prop('name').replace('delete', 'normalize').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1') + ']:checkbox').prop('checked', false).prop('disabled', true).change();
				$('input[name=' + $(this).prop('name').replace('delete', 'updateVisibility').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1') + ']:checkbox').prop('checked', false).prop('disabled', true).change();
				$('input[name=' + $(this).prop('name').replace('delete', 'newTags').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1') + ']:text').prop('disabled', true);
			} else {
				$(this).parent().parent().prop('style', "");
				$('input[name=' + $(this).prop('name').replace('delete', 'checked').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1') + ']:checkbox').prop('disabled', false);
				$('input[name=' + $(this).prop('name').replace('delete', 'normalize').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1') + ']:checkbox').prop('disabled', false);
				$('input[name=' + $(this).prop('name').replace('delete', 'updateVisibility').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1') + ']:checkbox').prop('disabled', false);
				$('input[name=' + $(this).prop('name').replace('delete', 'newTags').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1') + ']:text').prop('disabled', false);
			}
			toggleDelete(countCheckedBoxes(deleteCheckBoxSelector) === 0);
			updateBadges();
		});
	}
	else {
		$('#visibilitySelection').change(function () {
			if($.inArray(5, action) === -1)
				action.push(5);
		});
	}

	/**
	 * Listeners to check if any of the tag edit options were used
	 */
	var addUpdateTagsAction = function (){
		if($.inArray(2, action) === -1)
			action.push(2);
	}
	$('input[name^=posts][name$=newTags]').change(addUpdateTagsAction)
	$('.addTagsButton').click(function(){
		addTags($('#tagsInput').val());
		$('#tagsInput').val("");
		addUpdateTagsAction();
	});
	$('.removeTagsButton').click(function(){
		removeTags($('#tagsInput').val());
		$('#tagsInput').val("");
		addUpdateTagsAction();
	});

	$('.batchUpdateButton').click(function(){
		if($('#tagsInput').val() !== '' && !window.confirm($('input[name=editTagsWarning]').val())){
			return false
		}
	});

	$('#batchedit').submit(function(){
		//We check all posts, since the controller currently only processes checked posts due to the legacy version of this page still used by the batch edit page
		$(tagCheckBoxSelector).each(function() {
			$(this).prop('checked', true);
			$(this).prop('disabled', false);
			$('input[name=' + $(this).prop('name').replace('checked', 'newTags').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1') + ']:text').prop('disabled', false);
		});

		$('input[name=action]').val(action);
	});

});

function toggleTagEdit(disabled){
	$('input[name=tags]').prop('disabled', disabled);
	$('#addAllTags').prop('disabled', disabled);
	$('#removeAllTags').prop('disabled', disabled);
}
function toggleEditVisibility(disabled){
	$('#checkboxVisibility').prop('checked', !disabled);
	if(disabled) {
		$('#visibilitySelection').prop('style', "pointer-events: none; filter: grayscale(100%);");
		if($.inArray(5, action) !== -1)
			action.splice( $.inArray(5,action) ,1 );
	} else {
		$('#visibilitySelection').prop('style', "");
		if($.inArray(5, action) === -1)
			action.push(5);
	}
}
function toggleNormalize(disabled){
	$('.emptyBlock').toggleClass('hidden', !disabled);
	$('.normalizeAlert').toggleClass('invisible', disabled);
	$('.normalizeAlert').toggleClass('hidden', disabled);
	$('#checkboxNormalize').prop('checked', !disabled);
	if(disabled && $.inArray(3, action) !== -1){
		action.splice( $.inArray(3,action) ,1 );
	} else if (!disabled && $.inArray(3, action) === -1) {
		action.push(3);
	}
}
function toggleDelete(disabled){
	$('.emptyBlock').toggleClass('hidden', !disabled);
	$('.deleteAlert').toggleClass('invisible', disabled);
	$('.deleteAlert').toggleClass('hidden', disabled);
	$('#checkboxDelete').prop('checked', !disabled);
	if(disabled && $.inArray(4, action) !== -1){
		action.splice( $.inArray(4,action) ,1 );
	} else if (!disabled && $.inArray(4, action) === -1) {
		action.push(4);
	}
}

function addTags(tags){
	$(tagCheckBoxSelector+':checked').each(function() {
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
function removeTags(tags){
	$(tagCheckBoxSelector+':checked').each(function() {
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

function updateBadges(){
	$('#tagsCountBadge').text(countCheckedBoxes(tagCheckBoxSelector));
	if(!isImport)
		$('#visibilityCountBadge').text(countCheckedBoxes(visibilityCheckBoxSelector));
	if(showNormalize)
		$('#normalizeCountBadge').text(countCheckedBoxes(normalizeCheckBoxSelector));
	if(!isImport)
		$('#deleteCountBadge').text(countCheckedBoxes(deleteCheckBoxSelector));
}

function countCheckedBoxes(querySelector){
	var count = 0;
	$(querySelector+':checked').each(function() {
		count++;
	});
	return count;
}