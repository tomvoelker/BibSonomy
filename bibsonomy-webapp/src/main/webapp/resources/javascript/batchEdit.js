var allNotChecked;
var action=[];
var index;

$(document).ready(function () {
	$('#selector').find('option:eq(0)').prop("selected", true);
	$('#batchedit').find('input[name=abstractGrouping]').prop('disabled', true);
	
	/**
	 * If allNotChecked is true, edit options will not be activated.
	 * */
	allNotChecked = true;
	$('input[name^=posts]:checkbox:checked').each(function() {
		allNotChecked = false;
		return;
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
		$('input[name^=posts]:checkbox').each(function() {
			$(this).prop('checked', markAllChecked);
			$(this).change();
		});
	});

	$('input[name^=posts]:checkbox').change(function() {
		if ($(this).is(':checked')) {
			if ($('#selector').val() == 1) {
				changeTagInputs(this, false);
		    } else {
		    	changeTagInputs(this, true);
		    }
		} else {
			changeTagInputs(this, true);
		}
		/**
		 * If oneNotChecked is false, 'select all' checkbox should be selected*
		 */
		
		var oneNotChecked = false;
		/**
		 * mega haxxor jquery selector to select input checkboxes with name beginning
		 * with posts that are not checked.
		 */
		$('input[name^=posts]:checkbox:not(:checked)').each(function() {
			oneNotChecked = true;
			return;
		});
		$('#selectAll').prop('checked', !oneNotChecked);
		
		allNotChecked = true;
		$('input[name^=posts]:checkbox:checked').each(function() {
			allNotChecked = false;
			return;
		});
		if(!allNotChecked){
			$('#selector').change();
			/**
			 * The following function works only in indirect mode*
			 */
			trigger_checkboxes();
		};
		// if no post is checked, every thing should be hided.
		if(allNotChecked){
			$('.selectPostAlert').toggleClass('invisible', false);
			$('.selectPostAlert').toggleClass('hidden', false);
			$('.emptyBlock').toggleClass('hidden', true);
			resetSelection();
			
		};
	});

	$('#selector').change(function() {
		
		if($(this).val() == 0) {
			$('.selectPostAlert').toggleClass('invisible', false);
			$('.selectPostAlert').toggleClass('hidden', false);
			resetSelection();
		}
		/**
		 * If at least one post is selected, options can be enabled and do their job!
		 */
		if(!allNotChecked){
			
			$('.selectPostAlert').toggleClass('invisible', true);
			$('.selectPostAlert').toggleClass('hidden', true);
			$('.emptyBlock').toggleClass('hidden', false);
			if($(this).val() == 1) { //If edit all tag
				resetSelection();
				AddAllTag();
			}
			else if($(this).val() == 2){ // If edit each posts' tag
				resetSelection();
				AddEachTag();
			} 
			else if($(this).val() == 3) { // If normalize BibTex Key
				resetSelection();
				normalizeBibTexKey();
			}
			else if($(this).val() == 4) { // If delete post
				resetSelection();
				deletePosts();
			} 
			else if($(this).val() == 5) { // If update privacy
				resetSelection();
				updatePrivacy();
			}
		}
	});
	/**
	 * This function is called in indirect mode*/
	$('#checkboxAllTag').change(function() {
		if(!allNotChecked){
			if ($(this).is(':checked')) {
				action.push(1);
				AddAllTag();			
			}
			else{
				//remove action
				action.splice(index, 1);
				AddAllTag_Uncheck();
			}}
		else{
			$('.emptyBlock').toggleClass('hidden', true);
			$('.selectPostAlert').toggleClass('invisible', false);
			$('.selectPostAlert').toggleClass('hidden', false);
		}
	});
	/**
	 * This function is called in indirect mode*/
	$('#checkboxEachTag').change(function() {
		if(!allNotChecked){
			if ($(this).is(':checked')) {
				action.push(2);
				AddEachTag();		
			}
			else{
				//remove action
				action.splice(index, 2);
				AddEachTag_Uncheck();		
			}}
		else{
			$('.emptyBlock').toggleClass('hidden', true);
			$('.selectPostAlert').toggleClass('invisible', false);
			$('.selectPostAlert').toggleClass('hidden', false);
		}
	});
	/**
	 * This function is called in indirect mode*/
	$('#checkboxNormalize').change(function() {
		if(!allNotChecked){
			if ($(this).is(':checked')) {
				action.push(3);
				normalizeBibTexKey();
			}
			else{
				//remove action
				action.splice(index, 3);
				normalizeBibTexKey_Uncheck();
			}}
		else{
			$('.emptyBlock').toggleClass('hidden', true);
			$('.selectPostAlert').toggleClass('invisible', false);
			$('.selectPostAlert').toggleClass('hidden', false);
		}
	});
	/**
	 * This function is called in indirect mode*/
	$('#checkboxPrivacy').change(function() {
		if(!allNotChecked){
			if ($(this).is(':checked')) {
				action.push(5);
				updatePrivacy();			
			}
			else{
				//remove action
				action.splice(index, 5);
				updatePrivacy_Uncheck();
			}}
		else{
			$('.emptyBlock').toggleClass('hidden', true);
			$('.selectPostAlert').toggleClass('invisible', false);
			$('.selectPostAlert').toggleClass('hidden', false);
		}
	});
	$('.batchUpdateButton').click(function(){
		/**
		 * If  we are in indirect mode, action variable is set via this .js file,
		 * therefore action var is not empty. Otherwise action is set in content.tagx file 
		 * via selector option value and it is empty here*/
		
		if(action.length!=0){ 
			$('input[name=action]').val(action);
		}
		else{
			$('input[name=action]').val($('#selector').val());
		}
	});
	
});

function trigger_checkboxes(){
	if(!allNotChecked){
		$('.selectPostAlert').toggleClass('invisible', true);
		$('.selectPostAlert').toggleClass('hidden', true);
	}
	$('#checkboxAllTag').change();
	$('#checkboxEachTag').change();
	$('#checkboxNormalize').change();
	$('#checkboxPrivacy').change();
}
function updatePrivacy(){
	$('.batchUpdateButton').prop('disabled', false);
	$('td[id=viewable]').css({'font-weight':'bold'});
	$('#batchedit').find('input[name=abstractGrouping]').prop('disabled', false);
}

function updatePrivacy_Uncheck(){
	$('td[id=viewable]').css({'font-weight':'normal'});
	$('#batchedit').find('input[name=abstractGrouping]').prop('disabled', true);
	
	/**
	 * var all_NotChecked_undirect is true if we are in indirect mode 
	 * and no other edit option is selected. 
	 * In this case update button should be disabled.*/
	var all_NotChecked_undirect = true;
	$('input[id^=checkbox]:checkbox:checked').each(function() {
		all_NotChecked_undirect = false;
		return;
	});
	
	if(all_NotChecked_undirect){
		$('.emptyBlock').toggleClass('hidden', false);
		$('.batchUpdateButton').prop('disabled', true);
	}

}

function deletePosts(){
	$('.emptyBlock').toggleClass('hidden', true);
	$('.deleteAlert').toggleClass('invisible', false);	
	$('.batchUpdateButton').prop('disabled', false);
	$('.deleteAlert').toggleClass('hidden', false);
}
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

function AddEachTag(){
	changeTagInputs('input[name^=posts]:checkbox:checked', false);
	$('td[id=yourTags]').css({'font-weight':'bold'});
	$('.batchUpdateButton').prop('disabled', false);
}

function AddEachTag_Uncheck(){
	changeTagInputs('input[name^=posts]:checkbox:checked', true);
	$('td[id=yourTags]').css({'font-weight':'normal'});
	
	var all_NotChecked_undirect = true;
	$('input[id^=checkbox]:checkbox:checked').each(function() {
		all_NotChecked_undirect = false;
		return;
	});
	if(all_NotChecked_undirect){
		$('.emptyBlock').toggleClass('hidden', false);
		$('.batchUpdateButton').prop('disabled', true);
	}
}

function AddAllTag(){
	$('td[id=allTags]').css({'font-weight':'bold'});
	$('input[name=tags]').prop('disabled', false);
	$('.batchUpdateButton').prop('disabled', false);
}
function AddAllTag_Uncheck(){
	$('td[id=allTags]').css({'font-weight':'normal'});
	$('input[name=tags]').prop('disabled', true);
	
	var all_NotChecked_undirect = true;
	$('input[id^=checkbox]:checkbox:checked').each(function() {
		all_NotChecked_undirect = false;
		return;
	});
	if(all_NotChecked_undirect){
		$('.emptyBlock').toggleClass('hidden', false);
		$('.batchUpdateButton').prop('disabled', true);
	}
}
function resetSelection(){
	changeTagInputs('input[name^=posts]:checkbox:checked', true);
	$('input[name=tags]').prop('disabled', true);
	$('.batchUpdateButton').prop('disabled', true);
	$('#batchedit').find('input[name=abstractGrouping]').prop('disabled', true);
	$('.deleteAlert').toggleClass('invisible', true);
	$('.deleteAlert').toggleClass('hidden', true);
	$('.normalizeAlert').toggleClass('invisible', true);
	$('.normalizeAlert').toggleClass('hidden', true);
	
	
	$('td[id=viewable]').css({'font-weight':'normal'});
	$('td[id=yourTags]').css({'font-weight':'normal'});
	$('td[id=allTags]').css({'font-weight':'normal'});	
}
function changeTagInputs(selector, disabled) {
	$(selector).each(function() {
		/*
		 * remove possible special characters from selector string
		 */
		var attr = $(this).prop('name').replace('checked','newTags').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1');
		$('input[name=' + attr + ']:text').prop('disabled', disabled);
	});
}