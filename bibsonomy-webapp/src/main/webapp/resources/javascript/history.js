var diffMap;
$(document).ready(function () {
	$('td[id = postDiffNumCurr]').hide();
	$('td[id = postDiffCurr]').hide();
	
	
	$('span[id = curr]').click(function() {
		$(this).toggleClass('underline', false);
		$(this).toggleClass('mark', true);
		
		$(this).siblings().toggleClass('underline', true);
		$(this).siblings().toggleClass('mark', false);
		//$(this).siblings().show();
	
		$(this).parents('tr').next().find('.postDiffPre').toggleClass('invisible', true);
		$(this).parents('tr').next().find('.postDiffPre').hide();

		
		$(this).parents('tr').next().find('.postDiffCurr').toggleClass('invisible', false);
		$(this).parents('tr').next().find('.postDiffCurr').show();
		
		$(this).parents('td').next().find('a[id=restoreBtn]').toggleClass('invisible', false);
		
		show_hide_Checkboxes($(this).parents('tr').next().find('.postDiffCurr'),true);//invisible:false
		
	});
	
	$('span[id = pre]').click(function() {
		$(this).toggleClass('underline', false);
		$(this).toggleClass('mark', true);
		
		$(this).siblings().toggleClass('underline', true);
		$(this).siblings().toggleClass('mark', false);
		
		$(this).parents('tr').next().find('.postDiffPre').toggleClass('invisible', false);
		$(this).parents('tr').next().find('.postDiffPre').show();
		
		$(this).parents('tr').next().find('.postDiffCurr').toggleClass('invisible', true);
		$(this).parents('tr').next().find('.postDiffCurr').hide();
		
		$(this).parents('td').next().find('a[id=restoreBtn]').toggleClass('invisible', true);
		$(this).parents('tr').next().find('div[id=restore_alert_btn]').toggleClass('invisible', true);
		$(this).parents('tr').next().find('div[id=restore_alert_btn]').toggleClass('hidden', true);
		
	});
	/*
	 * if it is a button, this function should be changed to onclick()**/
	$('a[id = restoreBtn]').click(function() {	
			show_hide_Checkboxes($(this).parents('tr').next().find('.postDiffCurr'),false);//invisible:false
			$(this).parents('tr').next().find('div[id=restore_alert_btn]').toggleClass('invisible', false);
			$(this).parents('tr').next().find('div[id=restore_alert_btn]').toggleClass('hidden', false);
	});

	$('.submitBtn').click(function() {
		submitForm($(this).parents('td'));
	});


});

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
	$(element).find('input[name=diffEntryKey]').each(function() {
		var b = a.pop();
		if(b){
			diffEntryKey[i] = $(this).val();
			diffEntryValue +=($(this).siblings('input[name=diffEntryValue]').val()+"//");
			i++;
		}
	});
	$('input[name=differentEntryKeys]').val(diffEntryKey);
	$('input[name=differentEntryValues]').val(diffEntryValue);
	
	document.getElementById("history").submit();
}


maximizeById("general");