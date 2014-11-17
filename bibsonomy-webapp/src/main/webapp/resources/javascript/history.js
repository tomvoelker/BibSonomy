var diffMap;
$(document).ready(function () {
	var m = new Map();
	m.set('nasim','nabavi');
	m.set('neda','nagan');
	alert(m.get('neda'));
	//$('a[id = hideDiff]').toggleClass('invisible', true);
	//$('a[id = compPre]').hide();
	//$('li[id = postDiffDesc]').toggleClass('invisible', true);
	//$('li[id = postDiffPre]').hide();
	//$('li[id = postDiffNumCurr]').toggleClass('invisible', true);
	$('td[id = postDiffNumCurr]').hide();
	$('td[id = postDiffCurr]').hide();
	
	
	$('span[id = curr]').click(function() {
		$(this).toggleClass('underline', false);
		$(this).toggleClass('label label-info', true);
	//	$(this).hide();
		
		$(this).siblings().toggleClass('underline', true);
		$(this).siblings().toggleClass('label label-info', false);
		//$(this).siblings().show();
	
		$(this).parents('tr').next().find('.postDiffPre').toggleClass('invisible', true);
		$(this).parents('tr').next().find('.postDiffPre').hide();

		
		$(this).parents('tr').next().find('.postDiffCurr').toggleClass('invisible', false);
		$(this).parents('tr').next().find('.postDiffCurr').show();
		
/*		$(this).parents('td').siblings('#postDiffNumPre').toggleClass('invisible', true);
		$(this).parents('td').siblings('#postDiffNumPre').hide();

		
		$(this).parents('td').siblings('#postDiffNumCurr').toggleClass('invisible', false);
		$(this).parents('td').siblings('#postDiffNumCurr').show();
*/
/*		$('li[id = postDiffNumPre]').toggleClass('invisible', true);
		$('li[id = postDiffNumPre]').hide();
		
		$('li[id = postDiffNumCurr]').toggleClass('invisible', false);
		$('li[id = postDiffNumCurr]').show();
	*/	
		
	});
	
	$('span[id = pre]').click(function() {
		$(this).toggleClass('underline', false);
		$(this).toggleClass('label label-info', true);
	//	$(this).hide();
		
		$(this).siblings().toggleClass('underline', true);
		$(this).siblings().toggleClass('label label-info', false);
		//$(this).siblings().show();
		
		$(this).parents('tr').next().find('.postDiffPre').toggleClass('invisible', false);
		$(this).parents('tr').next().find('.postDiffPre').show();

		
		$(this).parents('tr').next().find('.postDiffCurr').toggleClass('invisible', true);
		$(this).parents('tr').next().find('.postDiffCurr').hide();
		/*
		$(this).parents('td').siblings('#postDiffNumPre').toggleClass('invisible', false);
		$(this).parents('td').siblings('#postDiffNumPre').show();

		
		$(this).parents('td').siblings('#postDiffNumCurr').toggleClass('invisible', true);
		$(this).parents('td').siblings('#postDiffNumCurr').hide();
*/
	/*	$('li[id = postDiffNumPre]').toggleClass('invisible', false);
		$('li[id = postDiffNumPre]').show();
		
		$('li[id = postDiffNumCurr]').toggleClass('invisible', true);
		$('li[id = postDiffNumCurr]').hide();*/
	});
	/*
	 * if it is a button, this function should be changed to onclick()**/
	$('a[id = restoreBtn]').click(function() {	
	//	if($('#postDiffPre').hasClass('invisible')){
		//	$(this).parents('td').siblings('#restoreConfirm').toggleClass('invisible', false);
		//$('div[id=restoreConfirm]').toggleClass('invisible', false);
			show_hide_Checkboxes($(this).parents('tr').next().find('.postDiffCurr'),false);//invisible:false
	//	}
	});
	$('.restoreButton').click(function() {
	//	alert('funltoiniert');
		submitForm($(this).parent());
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
		//element.siblings('#postDiffCurr').find('input[id=CurrEntryCheckbox]').toggleClass('invisible', invisible);
	element.find('input[id=CurrEntryCheckbox]').toggleClass('invisible', invisible);
	//	$('input[id=currEntryCheckbox]').each(function() {
		//	$(this).toggleClass('invisible', invisible);
		//});
	//}
}  
function submitForm(element){
	//alert('here');
	//var diffMap = new Map();
	var a=[];
	
	//alert(diffMap.get("title"));
	$(element).find('input[id=CurrEntryCheckbox]').each(function() {
		//alert("first of loop!");
		var checked = $(this).is(':checked');
		if(checked){
			//alert('checkbox');
			a.push(true);
			//var test= $(this).siblings('input[name=diffEntry]').val();
			//alert(test);
			// here, the problem is that sibling(..) is looking for something. we should do it in two steps.
			//diffArray.push($(this).siblings('input[name=diffEntry]').val());
		}
		else{
			a.push(false);
		}
	});
	a=a.reverse();
	
	//alert('before span');
	/*$('input[name=kooft]').each(function() {
		alert($(this).val());
	});
	*/
	var diffEntryKey = [];
	var diffEntryValue = [];
	var i=0;
	$(element).find('input[name=diffEntryKey]').each(function() {
/*	var i = 0;
	alert('diffEntryghabl');

	for (var key in diffMap) {
		alert('diffEntry');

		var b = a.pop();
		if(b){
			alert(diffMap.get(key));
			diffMap2.set(key,diffMap.get(key));
			
			//alert(key+" "+diffMap[key]);
		}
	}*/
		var b = a.pop();
//		alert('here');
		
		if(b){
			//alert('diffEntry: ' + $(this).val()+ $(this).siblings('input[name=diffEntryValue]').val());
			diffEntryKey[i] = $(this).val();
			diffEntryValue[i]=($(this).siblings('input[name=diffEntryValue]').val()+"//");
			i++;
			//alert($(this).val()+"  "+$(this).siblings().val());
		}
	});

	//alert(diffMap.size);
	//historyPost =  $(element).find($('input[name=HistoryPostTMP]')).val();
	//alert(diffArray);*/
	//diffMap.get('title');
	alert(diffEntryValue.length);
	$('input[name=differentEntryKeys]').val(diffEntryKey);
	$('input[name=differentEntryValues]').val(diffEntryValue);
	alert($('input[name=differentEntryValues]').val().length);
	
	//var test = $('input[name=differentEntryArray]').val();
	//alert(test.size);
//	alert($('input[name=differentEntryArray]').val().get('title'));
//	$('input[name=action]').val(action);
//	alert('pause');
//	alert($('input[name=differentEntryArray]').val());
	//$('input[name=historyPost]').val(historyPost);
//	alert($('input[name=historyPost]').val());
	//var m = $(element).find('input[name=tmpIntraHashToUpdate]').val();
	//alert(m);
	//$('input[name=intraHashToUpdate]').val(m);
	document.getElementById("history").submit();
}


maximizeById("general");