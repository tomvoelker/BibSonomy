
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
	
	
});


/**
 * Return diff*/

  
maximizeById("general");