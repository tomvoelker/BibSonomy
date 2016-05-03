var REVIEW_INFO_SELECTOR = '#review_info_rating';
var MAX_DISCUSSION_ITEMS = 5;

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
		var discussionContainer = $(this).parents('.media-body').first();
		var subdiscussionList = discussionContainer.find('ul.subdiscussion').first();
		discussionContainer.find('.toggleReplies').first().toggleClass('active');
		if (!subdiscussionList.is(':visible')) {
			subdiscussionList.slideDown();
		} else {
			subdiscussionList.slideUp();
		}
		return false;
	});
	
	var discussionItems = $('.subdiscussion:first>li:not(.form)');
	if (discussionItems.length > MAX_DISCUSSION_ITEMS) {
		
		var items = $(discussionItems).slice(MAX_DISCUSSION_ITEMS - discussionItems.length);
		var link = $('<a data-visible="false" href="#" class="btn btn-default btn-block">' + getString('discussion.show.more') + '</a>');
		var listItem = $('<li class="moreless-discussion"></li>');
		link.click(function() {
			var visible = Boolean($(this).data('visible'));
			var items = $('.subdiscussion:first>li:not(.moreless-discussion):not(.form)').slice(5 - discussionItems.length);
			var text;
			if (!visible) {
				items.show();
				text = getString('discussion.show.less');
			} else {
				items.hide();
				text = getString('discussion.show.more');
			}
			$(this).text(text);
			$(this).data('visible', !visible);
			return false;
		});
		$('.subdiscussion:first>li.form').before(listItem.append(link));
		items.hide();
	}
	
	$('.editLink').click(editDiscussionItem);
	
	$('.deleteLink').click(deleteDiscussionItem);
	
	$('.reply').click(showReplyForm);
	
	if ($('#discussion li.review').length > 0) {
		$('.createreview').hide();
	}
	
	$('.updatecomment').hide().submit(function() {
		var form = $(this);
		var data = form.serialize();
		
		$.ajax({
			url: '/ajax/comments',
			method: 'POST',
			data: data,
			success: function(data) {
				
				var textfield = form.find('textarea[name=discussionItem\\.text]');
				var text = textfield.val();
				
				form.parent().find('.text:first').text(text);
				
				form.hide();
			}
		});
		return false;
	});
	
	$('.updatereview').hide().submit(function() {
		var form = $(this);
		var data = form.serialize();
		
		var item = form.parent();
		var ratingDiv = item.find('div.rating');
		var oldRating = ratingDiv.data('rating');
		
		var rating = form.find('input[name=discussionItem\\.rating]').val();
		if (rating == 0) {
			if (!confirm(getString("post.resource.review.rating0"))) {
				return false;
			}
		}
		
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
				
				var text = form.find('textarea[name=discussionItem\\.text]').val();
				item.find('.text:first').text(text);
				
				var ratingInput = item.find('input.reviewRating');
				
				ratingInput.rating('update', rating);
				
				ratingDiv.data('rating', rating);
				
				// update review count and distribution
				var currentReviewCount = getReviewCount();
				var currentAvg = getAvg();
				var ratingSum = currentAvg * currentReviewCount;
				ratingSum += rating - oldRating;
				var avg = ratingSum / currentReviewCount;
				$('#averageRating').rating('update', avg);
				$('[property=ratingAverage]').text(avg);
				
				plotRatingDistribution();
				
				form.hide();
			},
			error:		function(jqXHR, data, errorThrown) {
				handleAjaxErrors(reviewForm, jQuery.parseJSON(jqXHR.responseText));
			},
		});
		
		return false;
	});
	
	$('.createreview').submit(createReview);
	
	$('.createcomment').submit(createComment);
});

function setupActions(container, text, hash) {
	container.find('.reply').click(showReplyForm);
	container.find('.deleteLink').click(deleteDiscussionItem);
	// set text
	container.find('div.text').text(text);
	container.find('div.info').data('discussion-item-hash', hash);
	container.find('.createcomment').submit(createComment);
	container.find('.editLink').click(editDiscussionItem);
}

function createReview() {
	var form = $(this);
	var data = form.serialize();
	
	var rating = form.find('input[name=discussionItem\\.rating]').val();
	if (rating == 0) {
		if (!confirm(getString("post.resource.review.rating0"))) {
			return false;
		}
	}
	
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
			
			var reviewTemplate = $('#reviewTemplate').clone();
			var text = form.find('textarea[name=discussionItem\\.text]').val();
			form.parent().find('ul:first').prepend(reviewTemplate);
			setupActions(reviewTemplate, text, data.hash);
			reviewTemplate.show();
			var ratingInput = reviewTemplate.find('input.reviewRating');
			ratingInput.val(rating);
			ratingInput.rating({
				min : 0,
				max : 5,
				step : 0.5,
				size : 'xs',
				readonly : true,
				showCaption : false,
				glyphicon : false,
				ratingClass : 'rating-fa'
			});
			
			reviewTemplate.find('div.rating').data('rating', rating);
			reviewTemplate.attr('id', 'ownReview');
			$(window).scrollTo(reviewTemplate, {
				offset: -15,
				onAfter: function() {
							requestAnimationFrame(function() {
								reviewTemplate.effect("highlight", {}, 2500);
								
							});
						}
			});
			
			// update review count and distribution
			var currentReviewCount = getReviewCount();
			var currentAvg = getAvg();
			var ratingSum = currentAvg * currentReviewCount + rating;
			
			var reviewCount = currentReviewCount + 1;
			var avg = ratingSum / reviewCount;
			
			$('#averageRating').rating('update', avg);
			$('[property=ratingCount]').text(reviewCount);
			$('[property=ratingAverage]').text(avg);
			
			plotRatingDistribution();
			
			form.hide();
		},
		error:		function(jqXHR, data, errorThrown) {
			handleAjaxErrors(reviewForm, jQuery.parseJSON(jqXHR.responseText));
		},
	});
	return false;
}

function createComment() {
	var form = $(this);
	var data = form.serialize();
	
	$.ajax({
		url: '/ajax/comments',
		method: 'POST',
		data: data,
		success: function(data) {
			var textfield = form.find('textarea[name=discussionItem\\.text]');
			var text = textfield.val();
			
			var commentTemplate = $('#commentTemplate').clone();
			form.parent().parent().prepend(commentTemplate);
			commentTemplate.show();
			setupActions(commentTemplate, text, data.hash);
			
			$(window).scrollTo(commentTemplate, {
				offset: -15,
				onAfter: function() {
							requestAnimationFrame(function() {
								commentTemplate.effect("highlight", {}, 2500);
								
							});
						}
			});
			var textarea = commentTemplate.find('form.updatecomment').find('textarea');
			textarea.val(text);
			autosize(textarea);
			// reset form
			textfield.val('');
		}
	});
	
	return false;
}

function deleteDiscussionItem() {
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
				
				imageContainer.append($('<i class="fa fa-user fa-4x"></i>'));
				var content = item.children('.media-body');
				content.prepend($('<div class="alert alert-info">' + getString('post.resource.discussion.info') + '</div>'));
				content.find('.actions:first>div.edit-media-buttons:last').remove();
				content.find('.actions:first>div.edit-media-buttons>.reply').remove();
				content.find('.details:first').remove();
				content.find('.info:first').text('');
			}
			
			if (item.hasClass('review')) {
				var rating = parseFloat(item.find('div.rating').data('rating'));
				// update review count and distribution
				var currentReviewCount = getReviewCount();
				var currentAvg = getAvg();
				var ratingSum = currentAvg * currentReviewCount - rating;
				
				var reviewCount = currentReviewCount - 1;
				var avg;
				if (reviewCount != 0) {
					avg = ratingSum / reviewCount;
				} else {
					avg = 0;
				}
				
				$('#averageRating').rating('update', avg);
				$('[property=ratingCount]').text(reviewCount);
				$('[property=ratingAverage]').text(avg);
				
				plotRatingDistribution();
			}
		}
	});
	
	return false;
}

function editDiscussionItem() {
	var form = $(this).parents('div.actions').siblings('form');
	form.toggle();
	var textarea = form.find('textarea');
	textarea.putCursorAtEnd();
}

function showReplyForm() {
	var discussionContainer = $(this).parents('.media-body').first();
	var subdiscussionList = discussionContainer.find('ul.subdiscussion').first();
	discussionContainer.find('.toggleReplies').first().addClass('active');
	if (!subdiscussionList.is(':visible')) {
		subdiscussionList.slideDown();
	}
	
	subdiscussionList.find('> li.form textarea').focus();
}