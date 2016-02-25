var REVIEW_INFO_SELECTOR = '#review_info_rating';

$(function() {
	$('.group-selector .dropdown-menu a').click(function() {
		var abstractGrouping = $(this).data('abstract-grouping');
		var grouping = $(this).data('group');
		var form = $(this).parents('form');
		
		form.find('input[name="abstractGrouping"]').attr('value', abstractGrouping);
		form.find('input[name="groups"]').attr('value', grouping);
		
		var button = $(this).parents('.group-selector').find('button');
		
		button.find('.group').html($(this).html());
		
		$(this).parents('ul').find('li').removeClass('checked');
		$(this).parent().addClass('checked');
		
		button.dropdown("toggle");
		return false;
	});
	
	$('.toggleReplies').click(function() {
		var discussionContainer = $(this).parents('.media-body')
		var subdiscussionList = discussionContainer.find('ul.subdiscussion').first();
		discussionContainer.find('.toggleReplies').first().toggleClass('active');
		if (!subdiscussionList.is(':visible')) {
			subdiscussionList.slideDown();
		} else {
			subdiscussionList.slideUp();
		}
		return false;
	});
	
	$('.deleteLink').click(function() {
		var type = $(this).data("type");
		if (!confirmDeleteByUser(type)) {
			return false;
		}
		var interhash = $('#discussion').data('interhash');
		var hash = $(this).parents('.media-body').find('.info').first().data('discussion-item-hash');
		$.ajax({
			url: '/ajax/' + type + "s"
		});
		alert(interhash)
		alert(hash)
		return false;
	});
	
	$('.reply').click(function() {
		var discussionContainer = $(this).parents('.media-body').first();
		var subdiscussionList = discussionContainer.find('ul.subdiscussion').first();
		discussionContainer.find('.toggleReplies').first().addClass('active');
		if (!subdiscussionList.is(':visible')) {
			subdiscussionList.slideDown();
		}
		
		subdiscussionList.find('> li.form textarea').focus();
	});
	
	if ($('li.review').length > 0) {
		$('.createreview').hide();
	}
	
	$('.createcomment').submit(function() {
		var data = $(this).serialize();
		
		alert(data);
		return false;
	});
});