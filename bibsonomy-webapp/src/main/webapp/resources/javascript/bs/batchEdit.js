var ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR='input[name="abstractGrouping"]';
var OTHER_GROUPING_CLASS_SELECTOR=".otherGroupsBox";
var allNotChecked;
var action=[];
var index;

$(document).ready(function () {
	$(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).click(onAbstractGroupingClick);
	$.each($(".abstractGroupingGroup"),function(b,c){toggleGroupBox(c);});
	$('#selector').find('option:eq(0)').prop("selected", true);
	$('input[name=abstractGrouping]').prop('disabled', true);
	
	allNotChecked = true;
	$('input[name^=posts]:checkbox:checked').each(function() {
		allNotChecked = false;
		return;
	});
	//allNotChecked = !($('#selectAll').is(':checked'));
	/*
	 * handler to change all sub checkboxes with the select all option
	 */
	$('#selectAll').change(function() {
		var markAllChecked = $(this).is(':checked');
		
		/*
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
		var oneNotChecked = false;
		/*
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
			trigger_checkboxes();
		};
		// if no post is checked, every thing should be hided.
		if(allNotChecked){
		//	$('#selector').find('option:eq(0)').prop("selected", true);
			//$('#selector').change();
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
		if(!allNotChecked){
			
			$('.selectPostAlert').toggleClass('invisible', true);
			$('.selectPostAlert').toggleClass('hidden', true);
			$('.emptyBlock').toggleClass('hidden', false);
			if($(this).val() == 1) {
				resetSelection();
				AddAllTag();
			}
			else if($(this).val() == 2){
				resetSelection();
				AddEachTag();
			} 
			else if($(this).val() == 3) {
				resetSelection();
				normalizeBibTexKey();
			}
			else if($(this).val() == 4) {
				resetSelection();
				deletePosts();
			} 
			else if($(this).val() == 5) {
				resetSelection();
				updatePrivacy();
			}
		}
	});
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
	$('input[name=abstractGrouping]').prop('disabled', false);
}

function updatePrivacy_Uncheck(){
	$('td[id=viewable]').css({'font-weight':'normal'});
	$('input[name=abstractGrouping]').prop('disabled', true);
	
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
	$('input[name=abstractGrouping]').prop('disabled', true);
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
		var attr = $(this).prop('name').replace('posts','newTags').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1');
		$('input[name=' + attr + ']:text').prop('disabled', disabled);
	});
}
function onAbstractGroupingClick(){toggleGroupBox($(this).parent());
}
function toggleGroupBox(c){var a=$(c).children("input:checked");
var b=$(c).siblings(OTHER_GROUPING_CLASS_SELECTOR);
if(!a.hasClass("otherGroups")){b.attr("disabled","disabled");
}else{b.removeAttr("disabled");
}}		           
maximizeById("general");