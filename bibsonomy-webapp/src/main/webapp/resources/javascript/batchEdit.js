var ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR='input[name="abstractGrouping"]';
var OTHER_GROUPING_CLASS_SELECTOR=".otherGroupsBox";
var tagAction=0;
disableAll=true;

$(document).ready(function () {

	// no feature is shown
	hideAllFeatures();
	
	// buttons are shown, but disabled.
	disableAllButtons();
	
	// checkboxes should be reset, when the page is reloaded
	resetSelection();
	
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
			disableAll=false;
			enableAllButtons();
		}
		// 	we have to call change() in this case, because a new tag input should be shown.
		if (tagAction==2){
			$('#tagOption2').click();
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
			disableAll = true;
			disableAllButtons();
			hideAllFeatures();
		};
		
	});

	$('#tagOption1').click(function() {
		if (disableAll){
			return false;
		}
		tagAction=1;
		hideAllFeatures();
		$('div[name=AllpostTagsHeader]').show();
		$('div[name=allTagBox]').show();
		
		
		$('div[name=updateButton]').show();
		$('div[name=UpdateRedirectButton]').show();
		// action is set here
		$('input[name=action]').val("1");
		
	});
	
	$('#tagOption2').click(function() {
		if (disableAll){
			return false;
		}
		tagAction=2;
		hideAllFeatures();
		
		$('li[name=postTagsHeader]').show();
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
		$('li[name=eachPostTag]').each(function() {
			if (a.pop()==true) {
				$(this).show();
			}
		});
		
		$('div[name=updateButton]').show();
		$('div[name=UpdateRedirectButton]').show();
		
		$('input[name=action]').val("1");
		
	});	
	
	$('#delId').click(function() {
		if (disableAll){
			return false;
		}
		hideAllFeatures();
		
		var value = true;
		value = confirm(getString("batchedit.deleteSelected.confirm"));
		
		if(value){
			$('input[name=action]').val("3");
			//return to the edit page. 
			$('input[name=referer]').val("justUpdate");
			//submits the form, since we have no submit button in this case
			submitForm();
			
		}
	});
	
	$('#normId').click(function() {
		if (disableAll){
			return false;
		}
		hideAllFeatures();
		
		$('input[name=action]').val("2");
		alert(getString("batchedit.normalizeBtn.alert"));
		//return to the edit page.
		$('input[name=referer]').val("justUpdate");
		//submit the form, since we have no submit button in this case
		submitForm();
	});
	
	$('#privacyId').click(function() {
		if (disableAll){
			return false;
		}
		hideAllFeatures();
	
		
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


function resetSelection(){
	$('#selectAll').prop('checked', false);
	$('input[name^=posts]:checkbox').each(function() {
		$(this).prop('checked', false);
	});
}
function submitForm(){
	document.getElementById("batchedit").submit();
}

function hideAllFeatures(){
	$('li[name=eachPostTag]').hide();
	$('li[name=postTagsHeader]').hide();
	$('div[name=AllpostTagsHeader]').hide();
	
	$('div[name=privacyBox]').hide();
	$('div[name=allTagBox]').hide();
	
	$('div[name=updateButton]').hide();
	$('div[name=UpdateRedirectButton]').hide();
}
/**
 changes the CSS class of an element.
 --> enable to disable */
function disableAllButtons(){
	$('div[name=updateButton]').hide();
	$('div[name=UpdateRedirectButton]').hide();

	document.getElementById('options').setAttribute('class', '');
	document.getElementById('delId').setAttribute('class', 'delClassDisabled');
	document.getElementById('tagId').setAttribute('class', 'tagClassDisabled');

	if (document.getElementById('normId')) {
		document.getElementById('normId').setAttribute('class', 'normClassDisabled');
	}
	document.getElementById('privacyId').setAttribute('class', 'privacyClassDisabled');
	}

/**
changes the CSS class of an element.
--> disable to enable */
function enableAllButtons(){
	document.getElementById('options').setAttribute('class', 'showOptions');
	document.getElementById('delId').setAttribute('class', 'delClass');
	document.getElementById('tagId').setAttribute('class', 'tagClass');
	if (document.getElementById('normId')) {
		document.getElementById('normId').setAttribute('class', 'normClass');
	}
	document.getElementById('privacyId').setAttribute('class', 'privacyClass');
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



/**
 * useful functions, for later use
 */
/*
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
