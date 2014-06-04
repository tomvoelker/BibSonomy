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
	 */
	$('input[name^=posts]:checkbox').change(function() {
		if ($(this).is(':checked')) {
			showAllButtons();
		}
		// 	we have to call change() in this case, because a new tag input should be shown.
		if ($('#selector').val()==2){
			$('#selector').change();
		}
		// if user wants to add a tag to all posts, he shouldn't be allowed to deselect any post
		if ($('#selector').val()==1 && $(this).is(':not(:checked)')){
			$(this).prop('checked', true);
			alert(getString("batchedit.deselect.alert"));
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
		// if no post is checked, every thing should be hided.
		if(allNotChecked){
			hideAllButtons();
			hideAllFeatures();
		};
		
	});

	$('#selector').change(function() {
		if($(this).val() == 0) {
			hideAllFeatures();
			
		} else if($(this).val() == 1) {
			hideAllFeatures();
			$('div[name=AllpostTagsHeader]').show();
			$('div[name=allTagBox]').show();
			
			$('#selectAll').prop('checked', true);
			$('#selectAll').change();
			
			$('div[name=updateButton]').show();
			$('div[name=UpdateRedirectButton]').show();
			// action is set here
			$('input[name=action]').val("1");
			
		} else if($(this).val() == 2) {
			hideAllFeatures();
			
			$('th[name=postTagsHeader]').show();
			// the following lines are to show tag edit box, only for the selected posts
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
			$('div[name=UpdateRedirectButton]').show();
			
			$('input[name=action]').val("1");
		};
	});
	
	$('#delButtonId').click(function() {
		hideAllFeatures();
		resetSelector();
		
		var value = true;
		value = confirm(getString("batchedit.deleteSelected.confirm"));
		
		if(value){
			$('input[name=action]').val("3");
			//return to the edit page. 
			$('input[name=referer]').val("justUpdate");
			//submit the form, since we have no submit button in this case
			submitForm();
			
		}
	});
	
	$('#normalizeButtonId').click(function() {
		hideAllFeatures();
		resetSelector();
		
		$('input[name=action]').val("2");
		alert(getString("batchedit.normalizeBtn.alert"));
		//return to the edit page.
		$('input[name=referer]').val("justUpdate");
		//submit the form, since we have no submit button in this case
		submitForm();
	});
	
	$('#privacyButtonId').click(function() {
		hideAllFeatures();
		resetSelector();
		
		$('div[name=privacyBox]').show();
		$('div[name=updateButton]').show();
		$('div[name=UpdateRedirectButton]').show();
		//action is set here
		$('input[name=action]').val("4");

	});
	
	$('#updateBtnId').click(function() {
		//referer is set here
		$('input[name=referer]').val("justUpdate");
	});
	
	$('#UpRidBtnId').click(function() {
		//referer is set here
		$('input[name=referer]').val("updateANDredirect");
	});
	
});

function submitForm(){
	document.getElementById("batchedit").submit();
}

/*
 * maybe will be used later
function changeTagInputs(selector, disabled) {
	$(selector).each(function() {
		
		// * remove possible special characters from selector string
		 
	//	var attr = $(this).prop('name').replace('posts','newTags').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1');
		//$('input[name=' + attr + ']:text').prop('disabled', disabled);
	});
}

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
}
*/

function resetSelector(){
	$('#selector').val("0");
}

function hideAllFeatures(){
	$('td[name=eachPostTag]').hide();
	$('th[name=postTagsHeader]').hide();
	$('div[name=AllpostTagsHeader]').hide();
	
	$('div[name=privacyBox]').hide();
	$('div[name=allTagBox]').hide();
	
	$('div[name=updateButton]').hide();
	$('div[name=UpdateRedirectButton]').hide();
}

function hideAllButtons(){
	$('div[name=updateButton]').hide();
	$('div[name=UpdateRedirectButton]').hide();
	
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

function toggleGroupBox(c){
	var a=$(c).children("input:checked");
	var b=$(c).siblings(OTHER_GROUPING_CLASS_SELECTOR);
	if(!a.hasClass("otherGroups")){
		b.attr("disabled","disabled");
	}
	else{
		b.removeAttr("disabled");
}}		           
maximizeById("general");