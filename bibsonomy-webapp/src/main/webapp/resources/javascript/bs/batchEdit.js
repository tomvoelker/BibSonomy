var ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR='input[name="abstractGrouping"]';
var OTHER_GROUPING_CLASS_SELECTOR=".otherGroupsBox";
var allNotChecked = false;

$(document).ready(function () {
	$(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).click(onAbstractGroupingClick);
	$.each($(".abstractGroupingGroup"),function(b,c){toggleGroupBox(c);});
	$('#selector').find('option:eq(0)').prop("selected", true);
	$('input[name=abstractGrouping]').prop('disabled', true);
	//$('.normalizeAlert').toggleClass('hidden',true);
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

	/*
	 * handler for changing of sub checkboxes
	 * changes state of tag inputs depending on selected action 
	 * in selection box. tag areas only get activated on update tag
	 * action
	 */
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
		};
		// if no post is checked, every thing should be hided.
		if(allNotChecked){
			$('#selector').find('option:eq(0)').prop("selected", true);
			$('#selector').change();
		//	document.getElementById("batchedit").reset();
		};
	
	});

	$('#selector').change(function() {
		
		if($(this).val() == 0) {
			changeTagInputs('input[name^=posts]:checkbox:checked', true);
			$('input[name=tags]').prop('disabled', true);
			$('.batchUpdateButton').prop('disabled', true);
			$('input[name=abstractGrouping]').prop('disabled', true);
			$('.deleteAlert').toggleClass('invisible', true);
			$('.deleteAlert').toggleClass('hidden', true);
			$('.normalizeAlert').toggleClass('invisible', true);
			$('.normalizeAlert').toggleClass('hidden', true);
			$('.selectPostAlert').toggleClass('invisible', false);
			$('.selectPostAlert').toggleClass('hidden', false);
			
			$('td[id=viewable]').css({'font-weight':'normal'});
			$('td[id=yourTags]').css({'font-weight':'normal'});
			$('td[id=allTags]').css({'font-weight':'normal'});
		}
		if(!allNotChecked){
			$('.selectPostAlert').toggleClass('invisible', true);
			//$('.selectPostAlert').toggleClass('hidden', true);
			if($(this).val() == 1) {
				changeTagInputs('input[name^=posts]:checkbox:checked', true);
				$('td[id=allTags]').css({'font-weight':'bold'});
				$('td[id=viewable]').css({'font-weight':'normal'});
				$('td[id=yourTags]').css({'font-weight':'normal'});
				$('input[name=tags]').prop('disabled', false);
				$('.batchUpdateButton').prop('disabled', false);
				$('input[name=abstractGrouping]').prop('disabled', true);
				$('.deleteAlert').toggleClass('invisible', true);
				$('.normalizeAlert').toggleClass('invisible', true);
			}
			else if($(this).val() == 2){
				changeTagInputs('input[name^=posts]:checkbox:checked', false);
				$('td[id=yourTags]').css({'font-weight':'bold'});
				$('td[id=viewable]').css({'font-weight':'normal'});
				$('td[id=allTags]').css({'font-weight':'normal'});
				$('input[name=tags]').prop('disabled', true);
				$('.batchUpdateButton').prop('disabled', false);
				$('input[name=abstractGrouping]').prop('disabled', true);
				$('.deleteAlert').toggleClass('invisible', true);
				$('.normalizeAlert').toggleClass('invisible', true);
				
			} 
			else if($(this).val() == 3) {
				changeTagInputs('input[name^=posts]:checkbox:checked', true);
				$('input[name=tags]').prop('disabled', true);
				$('.batchUpdateButton').prop('disabled', false);
				$('input[name=abstractGrouping]').prop('disabled', true);
				$('.selectPostAlert').toggleClass('hidden', true);
				$('.deleteAlert').toggleClass('invisible', true);
				$('.deleteAlert').toggleClass('hidden', true);
				$('.normalizeAlert').toggleClass('invisible', false);
				$('.normalizeAlert').toggleClass('hidden', false);
				
				$('td[id=viewable]').css({'font-weight':'normal'});
				$('td[id=yourTags]').css({'font-weight':'normal'});
				$('td[id=allTags]').css({'font-weight':'normal'});
			}
			else if($(this).val() == 4) {
				$('.deleteAlert').toggleClass('invisible', false);	
				changeTagInputs('input[name^=posts]:checkbox:checked', true);
				$('input[name=tags]').prop('disabled', true);
				$('.batchUpdateButton').prop('disabled', false);
				$('input[name=abstractGrouping]').prop('disabled', true);
				$('.selectPostAlert').toggleClass('hidden', true);
				$('.normalizeAlert').toggleClass('invisible', true);
				$('.deleteAlert').toggleClass('hidden', false);
				$('.normalizeAlert').toggleClass('hidden', true);
				
				$('td[id=viewable]').css({'font-weight':'normal'});
				$('td[id=yourTags]').css({'font-weight':'normal'});
				$('td[id=allTags]').css({'font-weight':'normal'});
			} 
			else if($(this).val() == 5) {
				changeTagInputs('input[name^=posts]:checkbox:checked', true);
				$('input[name=tags]').prop('disabled', true);
				$('.batchUpdateButton').prop('disabled', false);
				$('td[id=viewable]').css({'font-weight':'bold'});
				$('td[id=yourTags]').css({'font-weight':'normal'});
				$('td[id=allTags]').css({'font-weight':'normal'});
				$('input[name=abstractGrouping]').prop('disabled', false);
				$('.deleteAlert').toggleClass('invisible', true);
				$('.normalizeAlert').toggleClass('invisible', true);
			}
		}
	});
});
	
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