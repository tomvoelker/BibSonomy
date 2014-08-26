var tagAction=0;
disableAll=true;

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
			$('#tagEachId').click();
		}
		
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
	$('#allTagOk').click(function() {
		// action is set here
		$('input[name=action]').val("1");
		$("#tagAllAdded").toggleClass('invisible', false);
		submitForm("#tagAllAdded");
	});

	$('#tagEachId').click(function() {
		if (disableAll){
			return false;
		}
		
		hideAllFeatures();
		hideAllMessages();
		resetBackgroundColor();
		$(this).css("background-color", "#6666CC");
		$('div[id=cancelBtn]').show();
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
		
		$('div[name=eachTagBtn]').show();
		tagAction=2;
	});	
	
	$('#eachTagOk').click(function() {
		// action is set here
		$('input[name=action]').val("2");
		submitForm("#tagEachEdited");
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
		$('input[name=action]').val("4");
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
		$('input[name=action]').val("3");
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
	
	$('#privacyOk').click(function() {
		//action is set here
		$('input[name=action]').val("5");
		submitForm("#privacyChanged");
	});

	$('#cancelBtn').click(function() {
		$('input[name=action]').val("0");
		submitForm("#cancel");
	});
	
});


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
}

function submitForm(messageId){
	hideAllMessages();
	$(messageId).toggleClass('invisible', false);
	document.getElementById("batchedit").submit();
}

function hideAllFeatures(){
	
	$('li[name=eachPostTag]').hide();
	$('li[name=postTagsHeader]').hide();
	$('div[name=AllpostTagsHeader]').hide();
	
	$('div[id=privacyBox]').hide();
	$('div[name=allTagBox]').hide();
	$('div[name=eachTagBtn]').hide();
	$('div[id=cancelBtn]').hide();
}
/**
 changes the CSS class of an element.
 --> enable to disable */
function disableAllButtons(){
	$('div[id=cancelBtn]').hide();

	document.getElementById('delId').setAttribute('class', 'delClassDisabled');
	document.getElementById('tagEachId').setAttribute('class', 'tagEachClassDisabled');
	document.getElementById('tagAllId').setAttribute('class', 'tagAllClassDisabled');

	if (document.getElementById('normId')) {
		document.getElementById('normId').setAttribute('class', 'normClassDisabled');
	}
	document.getElementById('privacyId').setAttribute('class', 'privacyClassDisabled');
	tagAction=0;
	resetBackgroundColor();
	
	hideAllMessages();
	$('div[id=editHint]').toggleClass('invisible', false);
	
}

function resetBackgroundColor(){
	document.getElementById('delId').style.backgroundColor="#eee";
	document.getElementById('tagEachId').style.backgroundColor="#eee";
	document.getElementById('tagAllId').style.backgroundColor="#eee";	

	if (document.getElementById('normId')) {
		document.getElementById('normId').style.backgroundColor="#eee";
	}
	document.getElementById('privacyId').style.backgroundColor="#eee";
	tagAction=0;
}

/**
changes the CSS class of an element.
--> disable to enable */
function enableAllButtons(){
	document.getElementById('delId').setAttribute('class', 'delClass');
	document.getElementById('tagEachId').setAttribute('class', 'tagEachClass');
	document.getElementById('tagAllId').setAttribute('class', 'tagAllClass');
	if (document.getElementById('normId')) {
		document.getElementById('normId').setAttribute('class', 'normClass');
	}
	document.getElementById('privacyId').setAttribute('class', 'privacyClass');

}

function disableAllCheckboxes(){
	$('#selectAll').prop('disabled', true);
	$('input[name^=posts]:checkbox').each(function() {
		$(this).prop('disabled', true);
	});
}

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