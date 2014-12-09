var diffMap;
$(document).ready(function () {
	
	/**
	 * comparison to the current version is by default hidden, 
	 * it will be visible on user select. */
	$('td[id = postDiffCurr]').hide();
	$('a[id=restoreBtnEnabled]').hide();
	
	/**
	 * Select option is by default: 'previous version'*/
	$('select[id = preCurrSelector]').find('option:eq(0)').prop("selected", true);

	/**
	 * if selector's value changes*/
	$('select[id = preCurrSelector]').change(function() {
		if($(this).val() == 0) {
			compare_to_previous($(this));
		}
		else if ($(this).val() == 1) {
			compare_to_current($(this));
		}
	});

	/**
	 * if restore button is clicked:*/
	$('a[id = restoreBtnEnabled]').click(function() {	
		show_hide_Checkboxes($(this).parents('tr').next().find('.postDiffCurr'),false);
		$(this).parents('tr').next().find('div[id=restore_alert_btn]').toggleClass('invisible', false);
		$(this).parents('tr').next().find('div[id=restore_alert_btn]').toggleClass('hidden', false);
	});

	$('.submitBtn').click(function() {
		var isPub = $('input[name = isPub]').val();
		var isCommunityPost = $('input[name = isCommunityPost]').val();
		/**
		 * Here we have four cases:
		 * publication and community post, we should call editGoldstandardPublicationController
		 * publication, we should call editPublicationController
		 * bookmark and community post, we should call editGoldstandardBookmarkController
		 * bookmark, we should call editBookmarkController
		 */
		if(isPub=="true"){
			if(isCommunityPost=="true"){
				document.getElementById("history").action="/editGoldStandardPublication";
			}
			else{
				document.getElementById("history").action="/editPublication";
			}
		}
		else{
			if(isCommunityPost=="true"){
				document.getElementById("history").action="/editGoldStandardBookmark";
			}
			else{
				document.getElementById("history").action="/editBookmark";
			}
		}
		
		submitForm($(this).parents('td'));
	});
});


function compare_to_previous(element){
	
	element.parents('tr').next().find('.postDiffPre').toggleClass('invisible', false);
	element.parents('tr').next().find('.postDiffPre').show();

	element.parents('tr').next().find('.postDiffCurr').toggleClass('invisible', true);
	element.parents('tr').next().find('.postDiffCurr').hide();

	element.parents('td').next().find('a[id=restoreBtnEnabled]').toggleClass('invisible', true);
	element.parents('td').next().find('a[id=restoreBtnEnabled]').hide();
	
	element.parents('td').next().find('a[id=restoreBtnDisabled]').toggleClass('invisible', false);
	element.parents('td').next().find('a[id=restoreBtnDisabled]').show();

	element.parents('tr').next().find('div[id=restore_alert_btn]').toggleClass('invisible', true);
	element.parents('tr').next().find('div[id=restore_alert_btn]').toggleClass('hidden', true);

}

function compare_to_current(element){

	element.parents('tr').next().find('.postDiffPre').toggleClass('invisible', true);
	element.parents('tr').next().find('.postDiffPre').hide();

	element.parents('tr').next().find('.postDiffCurr').toggleClass('invisible', false);
	element.parents('tr').next().find('.postDiffCurr').show();
	
	/**
	 * if we are not dealing with a post which is not identical to the current post,
	 * we should enable restore button*/
	if(element.parents('tr').next().find('div[id=currentVer]').length==0){
		
		element.parents('td').next().find('a[id=restoreBtnEnabled]').toggleClass('invisible', false);
		element.parents('td').next().find('a[id=restoreBtnEnabled]').show();

		element.parents('td').next().find('a[id=restoreBtnDisabled]').toggleClass('invisible', true);
		element.parents('td').next().find('a[id=restoreBtnDisabled]').hide();

		show_hide_Checkboxes(element.parents('tr').next().find('.postDiffCurr'),true);//invisible:false		
	}
}

/**
 * show or hides checkboxes if restore button is clicked or not.*/
function show_hide_Checkboxes(element,invisible){
	element.find('input[id=CurrEntryCheckbox]').toggleClass('invisible', invisible);
}  


function submitForm(element){
	var a=[];

	/**
	 * finds checked check boxes*/
	$(element).find('input[id=CurrEntryCheckbox]').each(function() {
		var checked = $(this).is(':checked');
		if(checked){
			a.push(true);
		}
		else{
			a.push(false);
		}
	});
	a=a.reverse();
	
	var diffEntryKey = [];
	var diffEntryValue="";
	var i=0;
	var entryValue="";
	
	/**
	 * for each changed field:*/
	$(element).find('input[name=diffEntryKey]').each(function() {
		var b = a.pop();
		if(b){
			diffEntryKey[i] = $(this).val();
			entryValue = $(this).siblings('input[name=diffEntryValue]').val();
			/**
			 * in some cases, user wants to restore an empty field, 
			 * eg. current_year: 2014, revision_year(which is going to be restored):-- 
			 * in such a case, we send a " " as value of the field to avoid nullPointerException*/
			if(entryValue==""){
				entryValue=" ";
			}
			/**
			 * for some 'split(delimiter)' reason (eg. in author case), we cannot send an array of strings 
			 * to the controller. A string of field values delimited by '<8>' will be 
			 * sent to the controller and values will be splited there*/
			diffEntryValue +=(entryValue+"<8>");
			i++;
		}
	});
	$('input[name=differentEntryKeys]').val(diffEntryKey);
	$('input[name=differentEntryValues]').val(diffEntryValue);
	
	document.getElementById("history").submit();
}


maximizeById("general");