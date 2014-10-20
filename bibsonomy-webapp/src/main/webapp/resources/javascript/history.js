
$(document).ready(function () {

	//$('a[id = hideDiff]').toggleClass('invisible', true);
	//$('a[id = compPre]').hide();
	//$('li[id = postDiffDesc]').toggleClass('invisible', true);
	//$('li[id = postDiffPre]').hide();
	//$('li[id = postDiffNumCurr]').toggleClass('invisible', true);
	$('li[id = postDiffNumCurr]').hide();
	$('li[id = postDiffCurr]').hide();
	
	
	$('span[id = curr]').click(function() {
		$(this).toggleClass('underline', false);
		$(this).toggleClass('selected', true);
	//	$(this).hide();
		
		$(this).siblings().toggleClass('underline', true);
		$(this).siblings().toggleClass('selected', false);
		//$(this).siblings().show();
		
		$(this).parents('li').siblings('.postDiffPre').toggleClass('invisible', true);
		$(this).parents('li').siblings('.postDiffPre').hide();

		
		$(this).parents('li').siblings('.postDiffCurr').toggleClass('invisible', false);
		$(this).parents('li').siblings('.postDiffCurr').show();
		
		$(this).parents('li').siblings('#postDiffNumPre').toggleClass('invisible', true);
		$(this).parents('li').siblings('#postDiffNumPre').hide();

		
		$(this).parents('li').siblings('#postDiffNumCurr').toggleClass('invisible', false);
		$(this).parents('li').siblings('#postDiffNumCurr').show();

/*		$('li[id = postDiffNumPre]').toggleClass('invisible', true);
		$('li[id = postDiffNumPre]').hide();
		
		$('li[id = postDiffNumCurr]').toggleClass('invisible', false);
		$('li[id = postDiffNumCurr]').show();
	*/	
		
	});
	
	$('span[id = pre]').click(function() {
		$(this).toggleClass('underline', false);
		$(this).toggleClass('selected', true);
	//	$(this).hide();
		
		$(this).siblings().toggleClass('underline', true);
		$(this).siblings().toggleClass('selected', false);
		//$(this).siblings().show();
		
		$(this).parents('li').siblings('.postDiffPre').toggleClass('invisible', false);
		$(this).parents('li').siblings('.postDiffPre').show();

		
		$(this).parents('li').siblings('.postDiffCurr').toggleClass('invisible', true);
		$(this).parents('li').siblings('.postDiffCurr').hide();
		
		$(this).parents('li').siblings('#postDiffNumPre').toggleClass('invisible', false);
		$(this).parents('li').siblings('#postDiffNumPre').show();

		
		$(this).parents('li').siblings('#postDiffNumCurr').toggleClass('invisible', true);
		$(this).parents('li').siblings('#postDiffNumCurr').hide();

	/*	$('li[id = postDiffNumPre]').toggleClass('invisible', false);
		$('li[id = postDiffNumPre]').show();
		
		$('li[id = postDiffNumCurr]').toggleClass('invisible', true);
		$('li[id = postDiffNumCurr]').hide();*/
	});
	/*
	 * if it is a button, this function should be changed to onclick()**/
	$('a[id = restoreBtn]').click(function() {	
	//	if($('#postDiffPre').hasClass('invisible')){
			$(this).parents('li').siblings('#restoreConfirm').toggleClass('invisible', false);
		//$('div[id=restoreConfirm]').toggleClass('invisible', false);
			show_hide_Checkboxes($(this).parents('li'),false);//invisible:false
	//	}
	});
	$('a[id=restoreOk]').click(function() {
		submitForm($(this).parent().siblings('.postDiffCurr'));
	});

	$('#restoreCancel').click(function() {
		$('div[id=restoreConfirm]').toggleClass('invisible', true);
		show_hide_Checkboxes(true);//invisible:true
	});

});

function show_hide_Checkboxes(element,invisible){
	//if($('#postDiffPre').hasClass('invisible')){
//		element.siblings('#postDiffPre').find('input').toggleClass('invisible', invisible);
		//$('a:input[id=preEntryCheckbox]').each(function() {
			//$(this).toggleClass('invisible', invisible);
		//});
	//}
	//else{
		element.siblings('#postDiffCurr').find('input[id=CurrEntryCheckbox]').toggleClass('invisible', invisible);
	//	$('input[id=currEntryCheckbox]').each(function() {
		//	$(this).toggleClass('invisible', invisible);
		//});
	//}
}  
function submitForm(element){
	var diffArray = [];
	var historyPost;
	$(element).find('input[id=CurrEntryCheckbox]').each(function() {
		alert("first of loop!");
		var checked = $(this).is(':checked');
		if(checked){


			alert("in loop");
			// here, the problem is that sibling(..) is looking for something. we should do it in two steps.
			diffArray.push($(this).siblings($('input[name=diffEntry]').val()));
			historyPost =  $(this).parent().siblings($('input[name=HistoryPostTMP]').val());
		}
	});
	
	//alert(diffArray);
	$('input[name=differentEntryArray]').val(diffArray);
	alert('pause');
	alert($('input[name=differentEntryArray]').val());
	$('input[name=HistoryPost]').val(historyPost);
	
	document.getElementById("history").submit();
}


maximizeById("general");