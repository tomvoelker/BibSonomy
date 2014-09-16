
$(document).ready(function () {

	$('a[id = hideDiff]').toggleClass('invisible', true);
	$('a[id = hideDiff]').hide();
	$('li[id = postDiffDesc]').toggleClass('invisible', true);
	$('li[id = postDiffDesc]').hide();
	
	$('a[id = showDiff]').click(function() {
		$(this).toggleClass('invisible', true);
		$(this).hide();
		
		$(this).siblings().toggleClass('invisible', false);
		$(this).siblings().show();
		
		$(this).parent().siblings('.postDiffDesc').toggleClass('invisible', false);
		$(this).parent().siblings('.postDiffDesc').show();
		
	//	$(this).parent().siblings('hr').toggleClass('invisible', false);
		//$(this).parent().siblings('hr').show();
	});
	
	$('a[id = hideDiff]').click(function() {
		$(this).toggleClass('invisible', true);
		$(this).hide();
		
		$(this).siblings().toggleClass('invisible', false);
		$(this).siblings().show();
		
		$(this).parent().siblings('.postDiffDesc').toggleClass('invisible', true);
		$(this).parent().siblings('.postDiffDesc').hide();

		//$(this).parent().siblings('hr').toggleClass('invisible', true);
		//$(this).parent().siblings('hr').hide();
	});
	
	
});


/**
 * Return diff*/

  
maximizeById("general");