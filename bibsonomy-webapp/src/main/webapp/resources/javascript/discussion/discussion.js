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
		var link = $(this);
		var type = $(this).data("type");
		if (!confirmDeleteByUser(type)) {
			return false;
		}
		var interhash = $('#discussion').data('interhash');
		var hash = $(this).parents('.media-body:first').find('.info').first().data('discussion-item-hash');
		
		$.ajax({
			url: '/ajax/' + type + "s",
			method: 'POST',
			data:'hash=' + interhash + "&discussionItem.hash=" + hash + "&_method=delete&ckey=" + ckey,
			success: function() {
				var item = link.parents('li.media:first');
				var replyButton = item.parent().siblings('.actions').find('.toggleReplies');
				var badge = replyButton.find('.badge');
				
				var subCount = parseInt(badge.text());
				if (subCount == 1) {
					replyButton.remove();
				} else {
					badge.text(subCount - 1);
				}
				
				var subItems = item.find('.subdiscussion:first>li.media').length;
				if (subItems == 0) {
					item.remove();
				} else {
					var left = item.children('.media-left:first');
					var imageContainer = left.find('a.thumbnail');
					imageContainer.find('img').remove();
					imageContainer.find('span').remove();
					
					imageContainer.append($('<i class="fa fa-user fa-3x"></i>'));
					var content = item.children('.media-body');
					content.prepend($('<div class="alert alert-info">' + getString('post.resource.discussion.info') + '</div>'));
					content.find('.actions:first>div.edit-media-buttons:last').remove();
					content.find('.details:first').remove();
					content.find('.info:first').text('');
				}
			}
		});
		
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
	
	$('.createreview').submit(function() {
		var data = $(this).serialize();
		$.ajax({
			url: '/ajax/reviews',
			method: 'POST',
			data: data,
			success: function(data) {
				var reload = (data.reload);
				if (reload == "true") {
					window.location.reload();
					return;
				}
				alert("review created");
			}
		});
		return false;
	});
	
	$('.createcomment').submit(function() {
		var data = $(this).serialize();
		$.ajax({
			url: '/ajax/comments',
			method: 'POST',
			data: data,
			success: function() {
				alert("created");
			}
		});
		
		return false;
	});
});