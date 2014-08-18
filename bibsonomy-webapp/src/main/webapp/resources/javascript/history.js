
$(document).ready(function () {

	$('a[id = hideDiff]').hide();
	$('li[id = postDiffDesc]').hide();
	
	$('a[id = showDiff]').click(function() {
			$(this).hide();
			$(this).siblings().show();
			$(this).parent().siblings('.postDiffDesc').show();
		});
	
	$('a[id = hideDiff]').click(function() {
			$(this).hide();
			$(this).siblings().show();
			$(this).parent().siblings('.postDiffDesc').hide();
	});
});


/**
 * Return diff*/

  
maximizeById("general");