var ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR='input[name="abstractGrouping"]';
var OTHER_GROUPING_CLASS_SELECTOR=".otherGroupsBox";

$(document).ready(function () {
	hideAllFeatures();
	hideAllButtons();
	
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
			showAllButtons();
		}
		//$('#selector').change();
		//$('#selector').change();
		if ($('#selector').val()==2){
			$('#selector').change();
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
		
		var allNotChecked = true;
		$('input[name^=posts]:checkbox:checked').each(function() {
			allNotChecked = false;
			return;
		});
		if(allNotChecked){
			hideAllButtons();
			hideAllFeatures();
		}
		
	});

	$('#selector').change(function() {
		if($(this).val() == 0) {
			hideAllFeatures();
			//changeTagInputs('input[name^=posts]:checkbox:checked', true);
		//$('.batchUpdateButton').prop('disabled', false); needed?
			
		} else if($(this).val() == 1) {
			hideAllFeatures();
			$('div[name=AllpostTagsHeader]').show();
			$('div[name=allTagBox]').show();
			//if ($('#selectAll').is(':not(:checked)')){
				$('#selectAll').prop('checked', true);
				$('#selectAll').change();
			//}
			//disableAllCheckboxes();
			
			$('div[name=updateButton]').show();
			//$('.batchUpdateButton').show();
			//changeTagInputs('input[name^=posts]:checkbox:checked', false);
			
		} else if($(this).val() == 2) {
			hideAllFeatures();
			
			$('th[name=postTagsHeader]').show();
				
			var a = [];
			$('input[name^=posts]:checkbox').each(function() {
				if ($(this).is(':checked')) {
					a.push(true);
				}else{
					a.push(false);
				}
			});
			a=a.reverse();
			$('td[name=eachPostTag]').each(function() {
				if (a.pop()==true) {
					$(this).show();
				}
			});
			
			$('div[name=updateButton]').show();
			//$('.batchUpdateButton').show();
			//changeTagInputs('input[name^=posts]:checkbox:checked', false);
		} 
	});
	
	$('#delButton').click(function() {
		confirm("hallo");
		hideAllFeatures();
		
		
		var value = true;
		value = confirm(getString("batchedit.deleteSelected.confirm"));
	
	});
	
	$('#normaliizeButton').click(function() {
		hideAllFeatures();
		
		//show(getString("a test")); show a message and auto submit
		
		
	//	$('.batchUpdateButton').show(); auto submit
	
	});
	
	$('#privaccyButton').click(function() {
		hideAllFeatures();
		$('div[name=privacyBox]').show();
		$('div[name=updateButton]').show();
	});
	
	
	
});


function changeTagInputs(selector, disabled) {
	$(selector).each(function() {
		/*
		 * remove possible special characters from selector string
		 */
	//	var attr = $(this).prop('name').replace('posts','newTags').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1');
		//$('input[name=' + attr + ']:text').prop('disabled', disabled);
	});
}
/*
function disableAllCheckboxes(){
	$('#selectAll').prop('disabled', true);
	$('input[name^=posts]:checkbox').each(function() {
		$(this).prop('disabled', true);
	});
}

function enableAllCheckboxes(){
	$('#selectAll').prop('disabled', false);
	$('input[name^=posts]:checkbox').each(function() {
		$(this).prop('disabled', false);
	});
}*/


function hideAllFeatures(){
	$('td[name=eachPostTag]').hide();
	$('th[name=postTagsHeader]').hide();
	$('div[name=AllpostTagsHeader]').hide();
	
	$('div[name=privacyBox]').hide();
	$('div[name=allTagBox]').hide();
	
	$('div[name=updateButton]').hide();
}

function hideAllButtons(){
	$('div[name=updateButton]').hide();
	
	$('td[name=updateSelector]').hide();
	$('td[name=delButton]').hide();
	$('td[name=normalizeButton]').hide();
	$('td[name=privacyButton]').hide();
}

function showAllButtons(){
	$('td[name=updateSelector]').show();
	$('td[name=delButton]').show();
	$('td[name=normalizeButton]').show();
	$('td[name=privacyButton]').show();
}

function onAbstractGroupingClick(){toggleGroupBox($(this).parent());
}
function toggleGroupBox(c){var a=$(c).children("input:checked");
var b=$(c).siblings(OTHER_GROUPING_CLASS_SELECTOR);
if(!a.hasClass("otherGroups")){b.attr("disabled","disabled");
}else{b.removeAttr("disabled");
}}		           
maximizeById("general");