var tagAction=0;
disableAll=true;
var action=[];
var index;
$(document).ready(function () {
	
	// no feature is shown
	hideAllFeatures();
	
	// buttons are shown, but disabled.
	disableAllButtons();
	
	// checkboxes should be reset, when the page is reloaded
	resetSelection();
	
	/*if the posts do not belong to the user */
	if($('input[name^=posts]:checkbox').prop('disabled')) {
			disableAllCheckboxes();
			$('div[id=notYours]').toggleClass('invisible', false);	
	} else{
		$('div[id=editHint]').toggleClass('invisible', false);
	}
	
	$('#SelectTagAll').change(function() {
		var checked = $(this).is(':checked');
		if(checked){
			$('#indirectTagAll').show();
			$('div[id=combiEditBtn]').show();
			$('div[id=cancelBtn]').show();
			//set action
			action.push(1);
			
		}else{
			$('#indirectTagAll').hide();
			showEditBtn();
			//remove action
			action.splice(index, 1);
		}
	});
	
	$('#SelectTagEach').change(function() {
		var checked = $(this).is(':checked');
		if(checked){
			showEachTag();
			$('div[id=combiEditBtn]').show();
			$('div[id=cancelBtn]').show();
			action.push(2);
		}
		else{
			$('li[name=eachPostTag]').hide();
			$('li[name=postTagsHeader]').hide();
			showEditBtn();
			action.splice(index, 2);
		}
	});
	
	$('#SelectNorm').change(function() {
		var checked = $(this).is(':checked');
		if(checked){
			$('div[id=combiEditBtn]').show();
			$('div[id=cancelBtn]').show();
			action.push(3);
		}
		else{
			showEditBtn();
			action.splice(index, 3);
		}
	});

	
	/*
	 * handler to change all sub checkboxes with the select all option
	 * */
	$('#selectAll').change(function() {
		var markAllChecked = $(this).is(':checked');
		
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
			$('div[id=editHint]').toggleClass('invisible', true);
		}
		// 	we have to call change() in this case, because a new tag input should be shown.
		if (tagAction==2){
			if (document.getElementById('tagEachId')) {
				$('#tagEachId').click();
			} else{
				showEachTag();
			}
		}
		// when this variable is false, it means that all posts are selected.
		var oneNotChecked = false;
		
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
			resetBackgroundColor();
			document.getElementById("batchedit").reset();
		};
		
	});

	$('#tagAllId').click(function() {
		if (disableAll){
			return false;
		}
		
		enableAllButtons();
		hideAllFeatures();
		hideAllMessages();
		resetBackgroundColor();
		
		$(this).css("background-color", "#6666CC");
		$('div[name=AllpostTagsHeader]').show();
		$('div[name=allTagBox]').show();
		$('div[id=cancelBtn]').show();
		tagAction=1;
		
	});

	$('#tagEachId').click(function() {
		if (disableAll){
			return false;
		}
		$(this).css("background-color", "#6666CC");
		hideAllFeatures();
		hideAllMessages();
		resetBackgroundColor();
		$(this).css("background-color", "#6666CC");
		$('div[id=cancelBtn]').show();
		$('div[name=eachTagBtn]').show();

		showEachTag();
	});	
	
	$('#delId').click(function() {
		if (disableAll){
			return false;
		}
		
		hideAllFeatures();
		resetBackgroundColor();
		hideAllMessages();
		$(this).css("background-color", "#6666CC");
		
		$('div[id=delConfirm]').toggleClass('invisible', false);
	});
	
	$('#delOk').click(function() {
		$('div[id=delConfirm]').toggleClass('invisible', true);
		//clear the action array
		action.splice(0,action.lenght);
		action.push(4);
		$('input[name=action]').val(action);
		submitForm("#deleted");
	});
	
	$('#delCancel').click(function() {
		$('div[id=delConfirm]').toggleClass('invisible', true);
		resetBackgroundColor();
	});
	
	$('#normId').click(function() {
		if (disableAll){
			return false;
		}
		hideAllFeatures();
		resetBackgroundColor();
		hideAllMessages();
		
		$(this).css("background-color", "#6666CC");
		$('div[id=normConfirm]').toggleClass('invisible', false);		
	});
	
	$('#normOk').click(function() {
		$('div[id=normConfirm]').toggleClass('invisible', true);
		//clear the action array
		action.splice(0,action.lenght);
		action.push(3);
		$('input[name=action]').val(action);
		submitForm("#normalized");
	});
	
	$('#normCancel').click(function() {
		$('div[id=normConfirm]').toggleClass('invisible', true);
		resetBackgroundColor();
	});
			
	$('#privacyId').click(function() {
		if (disableAll){
			return false;
		}
		
		hideAllFeatures();
		resetBackgroundColor();
		hideAllMessages();
		$('div[id=cancelBtn]').show();
		$('div[id=privacyBox]').show();
		$(this).css("background-color", "#6666CC");
	});
	
});


function eachTagOK(){
//	var canSubmit = checkEmptyFields();
	var canSubmit = true;
	if(canSubmit){
		//clear the action array
		action.splice(0,action.lenght);
		// action is set here
		action.push(2);
		$('input[name=action]').val(action);
		submitForm("#tagEachEdited");
	}
}
function allTagOK(){
		//clear the action array
		action.splice(0,action.lenght);
		//action is set here
		action.push(1);
		$('input[name=action]').val(action);
		$("#tagAllAdded").toggleClass('invisible', false);
		submitForm("#tagAllAdded");
	
}
function privacyOK(){
	//clear the action array
	action.splice(0,action.lenght);
	//action is set here
	action.push(5);
	$('input[name=action]').val(action);
	submitForm("#privacyChanged");
}
function combiEditOK(){
	var b= $('input[id=indirectTagAll]').val();
	$('input[name=tags]').val(b);
	var canSubmit = true;//checkEmptyFields(); 
	if(canSubmit){
		//action is set here
		$('input[name=action]').val(action);
		submitForm("#combiEditConfirm");
	}
}

function cancelBtnClick(){
	action.push(0);
	$('input[name=action]').val(action);
	submitForm("#cancel");
}
/**works only in new layout
 * this function checks input fields for tags.
 * If atleast one of them is empty,
 * shows a tooltip */
function checkEmptyFields(){
	var canSubmit = true;
	a=[];
	$('input[name^=posts]:checkbox').each(function() {
		if ($(this).is(':checked')) {
			a.push(true);
		}else{
			a.push(false);
		}
	});
	a = a.reverse();
	
	$('input[name^=newTags]').each(function() {
		if (a.pop()==true) {
			if($(this).val()==''){
				$(this).tooltip('show');
				canSubmit = false;
			}
		}
	});
	return canSubmit;
}
function resetSelection(){
	$('#selectAll').prop('checked', false);
	$('input[name^=posts]:checkbox').each(function() {
		$(this).prop('checked', false);
	});
}

function hideAllMessages(){
	$('div[id=normConfirm]').toggleClass('invisible', true);
	$('div[id=normalized]').toggleClass('invisible', true);
	$('div[id=privacyChanged]').toggleClass('invisible', true);
	$('div[id=delConfirm]').toggleClass('invisible', true);
	$('div[id=deleted]').toggleClass('invisible', true);
	$('div[id=tagAllAdded]').toggleClass('invisible', true);
	$('div[id=tagEachEdited]').toggleClass('invisible', true);
	$('div[id=back]').toggleClass('invisible', true);
	$('div[id=editHint]').toggleClass('invisible', true);
	$('div[id="cancel"]').toggleClass('invisible', true);
	$('li[name=eachPostTag]').toggleClass('invisible', true);
	$('div[id=combiEditConfirm]').toggleClass('invisible', true);
}

function submitForm(messageId){
	hideAllMessages();
	$(messageId).toggleClass('invisible', false);
	document.getElementById("batchedit").submit();
}

function hideAllFeatures(){
	$('#indirectTagAll').hide();
	$('li[name=eachPostTag]').hide();
	$('li[name=postTagsHeader]').hide();
	$('div[name=AllpostTagsHeader]').hide();
	
	$('div[id=privacyBox]').hide();
	$('div[name=allTagBox]').hide();
	$('div[name=eachTagBtn]').hide();
	$('div[id=cancelBtn]').hide();
	$('div[id=combiEditBtn]').hide();
	
	
}
/**
 changes the CSS class of an element.
 --> enable to disable */
function disableAllButtons(){
	$('div[id=cancelBtn]').hide();
	$('div[id=combiEditBtn]').hide();
	
	if (document.getElementById('delId')) {	// if direct edit:
		document.getElementById('delId').setAttribute('class', 'delClassDisabled');
		document.getElementById('tagEachId').setAttribute('class', 'tagEachClassDisabled');
		document.getElementById('tagAllId').setAttribute('class', 'tagAllClassDisabled');

		if (document.getElementById('normId')) {
			document.getElementById('normId').setAttribute('class', 'normClassDisabled');
		}
		document.getElementById('privacyId').setAttribute('class', 'privacyClassDisabled');
	}else{
		document.getElementById('SelectNorm').disabled = true;
		document.getElementById('SelectTagAll').disabled = true;
		document.getElementById('SelectTagEach').disabled = true;
	}
	tagAction=0;
	//resetBackgroundColor();
	
	hideAllMessages();
	$('div[id=editHint]').toggleClass('invisible', false);
	
}

function resetBackgroundColor(){
	if (document.getElementById('delId')) {	// if direct edit:
		document.getElementById('delId').style.backgroundColor="#eee";
		document.getElementById('tagEachId').style.backgroundColor="#eee";
		document.getElementById('tagAllId').style.backgroundColor="#eee";	

		if (document.getElementById('normId')) {
			document.getElementById('normId').style.backgroundColor="#eee";
		}
		document.getElementById('privacyId').style.backgroundColor="#eee";
	}
	tagAction=0;
}

/**
changes the CSS class of an element.
--> disable to enable */
function enableAllButtons(){

	if (document.getElementById('delId')) {	// if direct edit:
		document.getElementById('delId').setAttribute('class', 'delClass');
		document.getElementById('tagEachId').setAttribute('class', 'tagEachClass');
		document.getElementById('tagAllId').setAttribute('class', 'tagAllClass');
		if (document.getElementById('normId')) {
			document.getElementById('normId').setAttribute('class', 'normClass');
		}
		document.getElementById('privacyId').setAttribute('class', 'privacyClass');
	}else{
		document.getElementById('SelectNorm').disabled = false;
		document.getElementById('SelectTagAll').disabled = false;
		document.getElementById('SelectTagEach').disabled = false;
	}
}

function disableAllCheckboxes(){
	$('#selectAll').prop('disabled', true);
	$('input[name^=posts]:checkbox').each(function() {
		$(this).prop('disabled', true);
	});
}

function showEachTag(){
	

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
		$(this).show();
	});
	
	$('li[name=eachPostTag]').each(function() {
		if (a.pop()==true) {
			$(this).toggleClass('invisible', false);
		}else{
			$(this).toggleClass('invisible', true);
		}
	});
	
	tagAction=2;

}

function showEditBtn(){
	var oneIsChecked = false;
	// if at least one edit option is selected, edit button should be visible, 
	//otherwise it should be hidden.
	$('input[id^=Select]:checkbox:checked').each(function() {
		oneIsChecked = true;
	});
	if(oneIsChecked){
		$('div[id=combiEditBtn]').show();
		$('div[id=cancelBtn]').show();
	}else{
		$('div[id=combiEditBtn]').hide();
		$('div[id=cancelBtn]').hide();
	}
}
maximizeById("general");

/**
 * useful functions, for future maybe
 */
/*
function enableAllCheckboxes(){
	$('#selectAll').prop('disabled', false);
	$('input[name^=posts]:checkbox').each(function() {
		$(this).prop('disabled', false);
	});
}

function checkedPostsNum(){
	
	$('input[name^=posts]:checkbox:checked').each(function() {
		checkedPost++;
		return;
	});
	$('input[name=CheckedPostsNum]').val(checkedPost);
	
}
*/
/*
function simulateDeletePost(){
	$('input[name^=posts]:checkbox:checked').each(function() {
		$(this).parent().hide();
		$(this).parent().siblings().hide();
		return;
	});
	
}
*/