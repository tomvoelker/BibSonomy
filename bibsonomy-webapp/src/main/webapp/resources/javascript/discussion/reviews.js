var REVIEWS_URL = "/ajax/reviews";
var STAR_WIDTH = 16;
var RATING_STEPS = 11;
var STEP_RATING = 2;

var RATING_AVG_DIV_SELECTOR = '#ratingAvg';
var RATING_AVG_SELECTOR = RATING_AVG_DIV_SELECTOR + ' span[property=v\\:average]';

var REVIEW_EDIT_LINK_SELECTOR = 'a.reviewEditLink';
var REVIEW_DELETE_LINK_SELECTOR = 'a.reviewDeleteLink';

var REVIEW_TEXTAREA_SELECTOR = 'textarea[name="discussionItem\\.text"]';
var REVIEW_ANONYMOUS_SELECTOR = 'input[name="discussionItem\\.anonymous"]';
var REVIEW_RATING_SELECTOR = '.reviewrating';

$(function() {
	plotRatingDistribution();
	
	// init all selectable stars
	initStars();
	
	$(REVIEW_UPDATE_FORM_SELECTOR).hide();
	
	// hide graph and info
	if ($('#noReviewInfo').length > 0) {
		$(RATING_AVG_DIV_SELECTOR).hide();
		$('#ratingDistribution').hide();
	}
	
	$('a.createReview').click(createReviewForm);
	
	// create review form
	$(REVIEW_CREATE_FORM_SELECTOR).submit(createReview);
	
	$(REVIEW_UPDATE_FORM_SELECTOR).submit(updateReview);
	
	// delete link for own review
	$(REVIEW_OWN_SELECTOR).find(REVIEW_DELETE_LINK_SELECTOR).click(deleteReview);
	
	// show edit form for own review
	$(REVIEW_OWN_SELECTOR).find(REVIEW_EDIT_LINK_SELECTOR).click(showUpdateReviewForm);
});

function showUpdateReviewForm() {
	removeAllOtherDiscussionForms();
	$(REVIEW_UPDATE_FORM_SELECTOR).toggle('slow');
}

function initStars() {
	$('.reviewrating').stars({
		split: STEP_RATING
	});
}

function getReviewCount() {
	return parseInt($('#review_info_rating span[property=v\\:count]').text());
}

function getAvg() {
	return Number($(RATING_AVG_SELECTOR).text().replace(',', '.'));
}

function setAvg(value) {
	value = value.toFixed(2);
	$(RATING_AVG_SELECTOR).text(value);
	var starWidth = getStarsWidth(value);
	$('#review_info_rating .stars-on-1').css('width', starWidth); // TODO
	$(PUBLICATION_LIST_SELECTOR + ' .stars-on-0\.75').css('width', starWidth);
	$(BOOKMARK_LIST_SELECTOR + ' .stars-on-0\.75').css('width', starWidth);
}

function createReviewForm() {
	removeAllOtherDiscussionForms();	
	
	/*
	 * get parent hash
	 */
	var parentHash = getHash($(this));
	var divForm = $('#createReview').clone();
	divForm.attr('id', REVIEW_REPLY_FORM_ID);
	divForm.addClass('createReview');
	
	var form = divForm.find('form');
	/*
	 * init review rating (ui plugin stars)
	 * FIXME: find a better solution
	 */
	form.find('div.ui-stars-star').remove();
	form.find('div.ui-stars-cancel').remove();
	form.find('[name=discussionItem\\.rating]').remove();
	for (var i = 1; i < RATING_STEPS; i++) {
		form.find('.reviewrating').append('<input name="discussionItem.rating" type="radio" value="' + (i / 2) + '"/>');
	}
	
	if (parentHash != undefined) {
		$(this).parent().parent().parent().append(divForm);
		form.append($('<input></input>').attr('name', 'discussionItem.parentHash').attr('value', parentHash).attr('type', 'hidden'));
	} else {
		$(DISCUSSION_SELECTOR).prepend(divForm);
	}
	
	// bind some actions
	form.find('textarea').TextAreaResizer();
	form.find(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).click(onAbstractGroupingClick);
	form.submit(createReview);
	
	divForm.show();
	
	initStars();
	scrollTo(REVIEW_REPLY_FORM_ID);
	return false;
}

function setReviewCount(value) {
	var title = getString("post.resource.review.review");
	if (value > 1) {
		title = getString("post.resource.review.reviews");
	}
	
	$('#review_info_rating span[property=v\\:count]').text(value);
	$('#review_info_rating span[property=v\\:count]').next('span').text(title);
}

function getOwnReviewRating() {
	return Number($('#ownReview .rating').data('rating'));
}

function validateRating(starsWrapperId) {
	var reviewRating = getRating(starsWrapperId);
	if (reviewRating == 0) {
		if (!confirm(getString("post.resource.review.rating0"))) {
			return false;
		}
	}
	
	return true;
}

function plotRatingDistribution() {
	if ($("#ratingDistributionGraph").length == 0) {
		return;
	}
	
	var ratings = [];
	var rating_ticks = [];
	var d1 = [];
	
	// get all ratings
	$('.subdiscussionItems li').not('#newReview').find('.rating').each(function() {
		var key = $(this).data("rating");
		if (ratings[key]) {
			ratings[key] += 1;
		} else {
			ratings[key] = 1;
		}
	});
	
	for (var i = 0; i < RATING_STEPS; i++) {
		var key = i / STEP_RATING;
		var value = 0;
		if (ratings[key]) {
			value = ratings[key];
		}
		
		var count = getReviewCount();
		if (value > 0) {
			value = value / count * 100;
		} else {
			// 0 values are producing a thin line
			value = Number.NaN;
		}
		d1.push([key, value]);
		rating_ticks.push(key);
	}
	
	$.plot($("#ratingDistributionGraph"), [ d1 ], {
		bars: {
			show: true,
			align: "center",
			barWidth: 0.2,
			fill: 0.7,
		},
		xaxis: {
			ticks: rating_ticks,
			tickDecimals: 1,
			tickColor: 'transparent',
			autoscaleMargin: 0.02,
		},
		yaxis: {
			show: false,
			min: 0,
		    max: 110,
		},
		grid: {
			markings: [ { xaxis: { from: getAvg(), to: getAvg() }, yaxis: { from: 0, to: 110 }, color: "#bb0000" }]
		}
    });
}

function createReview() {
	// first unbind the form (no double submit while calling server)
	var reviewForm = $(this);
	reviewForm.unbind('submit');
	
	var reviewRatingInput = reviewForm.find(REVIEW_RATING_SELECTOR);
	if (!validateRating(reviewRatingInput)) {
		reviewForm.submit(createReview);
		return false;
	}
	
	var spinner = reviewForm.find('.spinner');
	spinner.show('slow');
	
	var reviewText = reviewForm.find(REVIEW_TEXTAREA_SELECTOR).val();
	var anonymous = reviewForm.find(REVIEW_ANONYMOUS_SELECTOR).is(':checked');
	var reviewRating = getRating(reviewRatingInput);
	
	var abstractGrouping = reviewForm.find(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR + ':checked').val();
	var groups = reviewForm.find(OTHER_GROUPING_CLASS_SELECTOR).val();
	
	// TODO: allow multiple groups; remove the next five lines after this comment
	if (groups == null) {
		groups = new Array();
	} else {
		groups = new Array(groups);
	}
	
	// call service
	var reviewData = reviewForm.serialize();
	$.ajax({
		url:		REVIEWS_URL,
		type:		"POST",
		dataType:   "json",
		data:		reviewData,
		success:	function(response) {
						// display review
			 			var reviewView = $('#newReview').remove();
			 			reviewView.attr("id", "ownReview");
			 			updateReviewView(reviewView, reviewText, reviewRating, abstractGrouping, groups);
			 			
			 			// get parent view
			 			var subItems = reviewForm.parent().parent().children('ul.subdiscussionItems');
			 			var parentView;
			 			if (subItems.length != 0) {
			 				parentView = subItems;
			 			} else {
			 				parentView = $(DISCUSSION_SELECTOR + ' .subdiscussionItems:first');
			 			}
			 			
			 			updateHash(reviewView, response.hash);
			 			parentView.append(reviewView);
			 			highlight(reviewView);
						
						/*
						 * update update form
						 */
			 			var updateForm = $(REVIEW_UPDATE_FORM_SELECTOR);
						updateForm.find(REVIEW_TEXTAREA_SELECTOR).text(reviewText);
		     			updateForm.find(REVIEW_RATING_SELECTOR).stars({
		     				split: STEP_RATING,
		     			});
		     			
		     			updateForm.find(REVIEW_RATING_SELECTOR).stars("select", reviewRating.toFixed(1));
						if (anonymous) {
							reviewView.addClass(ANONYMOUS_CLASS);
							updateForm.find(REVIEW_ANONYMOUS_SELECTOR).attr("checked", "checked");
						}
						
						updateForm.submit(updateReview);
						
						// groups
						populateFormWithGroups(updateForm, abstractGrouping, groups);
						updateForm.find(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).click(onAbstractGroupingClick);
		     			/*
		     			 * update and bind links
		     			 */
		     			removeReviewActions();
		     			reviewView.find(REVIEW_EDIT_LINK_SELECTOR).click(showUpdateReviewForm);
		     			reviewView.find(REVIEW_DELETE_LINK_SELECTOR).click(deleteReview);
		     			reviewView.find(REPLY_SELECTOR).click(reply);
		     			
		     			/*
		     			 * update rating view 
		     			 */
		     			var oldCount = getReviewCount();
		     			var oldAvg = getAvg();
		     			var newCount = oldCount + 1;
		     			var newAvg = (oldAvg * oldCount + reviewRating) / newCount;	     			
		     			setReviewCount(newCount);
		     			setAvg(newAvg);
		     			plotRatingDistribution();
		     			
		     			$('#noReviewInfo').hide();
		     			$('#ratingAvg').show('slow');
		     			$('#ratingDistribution').show('slow');
		     			scrollTo(REVIEW_OWN_ID);
					},
		error:		function(jqXHR, data, errorThrown) {
						handleAjaxErrors(reviewForm, jQuery.parseJSON(jqXHR.responseText));
						reviewForm.submit(updateReview);
					},
	});
	
	return false;
}

function updateReview() {
	var reviewForm = $(this);
	reviewForm.unbind('submit');
	var reviewRatingInput = reviewForm.find(REVIEW_RATING_SELECTOR);
	if (!validateRating(reviewRatingInput)) {
		reviewForm.submit(updateReview);
		return false;
	}
	
	var abstractGrouping = reviewForm.find(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR + ':checked').val();
	var groups = reviewForm.find(OTHER_GROUPING_CLASS_SELECTOR).val();
	// TODO: allow multiple groups; remove the next five lines after this comment
	if (groups == null) {
		groups = new Array();
	} else {
		groups = new Array(groups);
	}
	
	// show spinner "updating review"s
	var spinner = reviewForm.find('.spinner');
	spinner.show('slow');
	
	// save all values for success action
	var reviewText = reviewForm.find(REVIEW_TEXTAREA_SELECTOR).val();
	var anonymous = reviewForm.find(REVIEW_ANONYMOUS_SELECTOR).is(':checked');
	var reviewRating = getRating(reviewRatingInput);
	var oldReviewRating = getOwnReviewRating();
	
	// call service
	var reviewData = reviewForm.serialize();
	
	$.ajax({
		url:		REVIEWS_URL,
		type:		"POST",
		dataType:  "json",
		data: 		reviewData,
		success:	function(response) {
						reviewForm.hide('slow');
						spinner.hide('slow');
						
						var reviewView = $(REVIEW_OWN_SELECTOR);
		     			
						updateHash(reviewView, response.hash);
						
						if (anonymous) {
							reviewView.addClass(ANONYMOUS_CLASS);
						} else {
							reviewView.removeClass(ANONYMOUS_CLASS);
						}
						
						// update values
						updateReviewView(reviewView, reviewText, reviewRating, abstractGrouping, groups);
						highlight(reviewView);
						
		     			// update over all values
		     			var count = getReviewCount();
		     			var oldAvg = getAvg();
		     			var newAvg = (oldAvg * count - oldReviewRating + reviewRating) / count;
		     			setAvg(newAvg);
		     			plotRatingDistribution();
		     			reviewForm.submit(updateReview);
					},
		error:		function(jqXHR, data, errorThrown) {
						handleAjaxErrors(reviewForm, jQuery.parseJSON(jqXHR.responseText));
						reviewForm.submit(updateReview);
					},
	});
	return false;
}

function deleteReview() {
	// TODO: confirm?
	var deleteLink = $(this);
	$(this).siblings('.deleteInfo').show();
	
	var hash = getInterHash();
	var review = $(REVIEW_OWN_SELECTOR);
	var reviewHash = review.find('.info').data(DISCUSSIONITEM_DATA_KEY);
	
	var oldReviewRating = getOwnReviewRating();
	deleteLink.remove();
	var revDelUrl = REVIEWS_URL + "?hash=" + hash + "&ckey=" + ckey + "&discussionItem.hash=" + reviewHash;
	$.ajax({
		url:		revDelUrl,
		type:		"DELETE",
		success:	function(msg) {
						deleteDiscussionItemView(review, function() {
							// update overall count
							var oldCount = getReviewCount();
			     			var oldAvg = getAvg();
			     			var newAvg = 0;
			     			var newCount = oldCount - 1;
			     			
			     			if (newCount > 0) {
			     				newAvg = (oldAvg * oldCount - oldReviewRating) / newCount;	 
			     			}
			     			    			
			     			setReviewCount(oldCount - 1);
			     			setAvg(newAvg);
			     			plotRatingDistribution();
			     			addReviewActions();
						});						
					},
		// TODO: handle error
	});
	return false;
}

function updateReviewView(reviewView, text, rating, abstractGrouping, groups) {
	var starWidth = getStarsWidth(rating);
	var ratingView = reviewView.find('.rating');
	ratingView.data("rating", rating);
	reviewView.find('.review.text').text(text);
	ratingView.find('.stars-on-1').css('width', starWidth);
	
	// groups
	reviewView.find('.' + GROUPS_CLASS).remove();
	var groupsView = buildGroupView(abstractGrouping, groups);
	reviewView.find('.info').append(groupsView);
}

function getRating(element) {
	var stars = $(element).data("stars");
	return parseFloat(stars.options.value);
}

function getStarsWidth(rating) {
	return STAR_WIDTH * rating;
}