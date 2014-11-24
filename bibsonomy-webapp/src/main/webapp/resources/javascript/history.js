var diffMap;
$(document).ready(function () {
	//$('td[id = postDiffNumCurr]').hide();
	$('td[id = postDiffCurr]').hide();
	
	$('select[id = preCurrSelector]').find('option:eq(0)').prop("selected", true);

	$('select[id = preCurrSelector]').change(function() {
		if($(this).val() == 0) {
			compare_to_previous($(this));
		}
		else if ($(this).val() == 1) {
			compare_to_current($(this));
		}
	});

	/*
	 * if it is a button, this function should be changed to onclick()**/
	$('a[id = restoreBtnEnabled]').click(function() {	
			show_hide_Checkboxes($(this).parents('tr').next().find('.postDiffCurr'),false);//invisible:false
			$(this).parents('tr').next().find('div[id=restore_alert_btn]').toggleClass('invisible', false);
			$(this).parents('tr').next().find('div[id=restore_alert_btn]').toggleClass('hidden', false);
	});

	$('.submitBtn').click(function() {
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

	element.parents('td').next().find('a[id=restoreBtnEnabled]').toggleClass('invisible', false);
	element.parents('td').next().find('a[id=restoreBtnEnabled]').show();

	element.parents('td').next().find('a[id=restoreBtnDisabled]').toggleClass('invisible', true);
	element.parents('td').next().find('a[id=restoreBtnDisabled]').hide();

	show_hide_Checkboxes(element.parents('tr').next().find('.postDiffCurr'),true);//invisible:false

}

function show_hide_Checkboxes(element,invisible){
	element.find('input[id=CurrEntryCheckbox]').toggleClass('invisible', invisible);
}  
function submitForm(element){
	var a=[];

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
	$(element).find('input[name=diffEntryKey]').each(function() {
		var b = a.pop();
		if(b){
			diffEntryKey[i] = $(this).val();
			entryValue = $(this).siblings('input[name=diffEntryValue]').val();
			if(entryValue==""){
				entryValue=" ";
			}
			diffEntryValue +=(entryValue+"//");
			i++;
			alert(diffEntryValue);
		}
	});
	$('input[name=differentEntryKeys]').val(diffEntryKey);
	$('input[name=differentEntryValues]').val(diffEntryValue);
	
	document.getElementById("history").submit();
}


maximizeById("general");