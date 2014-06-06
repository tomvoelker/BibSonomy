var ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR='input[name="abstractGrouping"]';
var OTHER_GROUPING_CLASS_SELECTOR=".otherGroupsBox";

$(document).ready(function () {
	$(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).click(onAbstractGroupingClick);
	$.each($(".abstractGroupingGroup"),function(b,c){toggleGroupBox(c);});

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
	});

	$('#selector').change(function() {
		if($(this).val() == 0) {
			changeTagInputs('input[name^=posts]:checkbox:checked', true);
			$('input[name=tags]').prop('disabled', true);
			$('.batchUpdateButton').prop('disabled', true);
			$('.batchUpdateButtonViewable').prop('disabled',true);
		} else if($(this).val() == 1) {
			changeTagInputs('input[name^=posts]:checkbox:checked', false);
			$('input[name=tags]').prop('disabled', false);
			$('.batchUpdateButton').prop('disabled', false);
			$('.batchUpdateButtonViewable').prop('disabled',true);
		} else if($(this).val() == 5) {
			changeTagInputs('input[name^=posts]:checkbox:checked', true);
			$('input[name=tags]').prop('disabled', true);
			$('.batchUpdateButtonViewable').prop('disabled',false);
			$('.batchUpdateButton').prop('disabled', true);
		} else {
			var value = true;
			
			if($(this).val() == 3) {
				value = confirm(getString("batchedit.deleteSelected.confirm"));
			} else if($(this).val() == 4) {
				value = confirm(getString("batchedit.ignoreSelected.confirm"));
			}
			
			if(!value) {
				$(this).find('option:eq(0)').prop("selected", true);
				changeTagInputs('input[name^=posts]:checkbox:checked', true);
				$('input[name=tags]').prop('disabled', true);
				$('.batchUpdateButton').prop('disabled', true);
			} else {
				changeTagInputs('input[name^=posts]:checkbox:checked', true);
				$('input[name=tags]').prop('disabled', true);
				$('.batchUpdateButton').prop('disabled', false);
				$('.batchUpdateButtonViewable').prop('disabled',true);
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