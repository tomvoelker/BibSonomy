var REVIEWS_URL = "/ajax/reviews";
var STAR_WIDTH = 16;
var RATING_STEPS = 11;
var STEP_RATING = 2;

$(function() {
	plotRatingDistribution();
	
	// init all selectable stars
	$('.reviewrating').stars({
		split: 2
	});
	
	$('#updateReviewForm').hide();
	
	// hide graph and info
	if ($('#noReviewInfo').length > 0) {
		$('#ratingAvg').hide();
		$('#ratingDistribution').hide();
	}
	
	$('a.createReview').click(createReviewForm);
	
	// create review form
	$('#createReviewForm').submit(createReview);
	
	$('#updateReviewForm').submit(updateReview);
	
	// delete link for own review
	$('#ownReview').children('.reviewMenu').find('.deleteLink').click(deleteReview);
	
	// show edit form for own review
	$('#ownReview').children('.reviewMenu').find('.editLink').click(function() {
		$('#updateReviewForm').toggle('slow');
	});
});

function getReviewCount() {
	return parseInt($('#review_info_rating span[property=v\\:count]').text());
}

function getAvg() {
	return Number($('#ratingAvg span[property=v\\:average]').text().replace(',', '.'));
}

function setAvg(value) {
	value = value.toFixed(2);
	$('#review_info_rating span[property=v\\:average]').text(value);
	var starWidth = getStarsWidth(value);
	$('#review_info_rating .stars-on-1').css('width', starWidth); // TODO
	$('#bibtexList .stars-on-1').css('width', starWidth);
	$('#bookmarkList .stars-on-1').css('width', starWidth);
}

function createReviewForm() {
	alert("create review");
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
		var key = i/STEP_RATING;
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
	if (!validateRating('#createReviewRating')) {
		return false;
	}
	
	var reviewForm = $(this);
	var spinner = reviewForm.find('.spinner');
	spinner.show('slow');
	
	var reviewText = $('#createReviewText').val();
	var reviewRating = getRating('#createReviewRating');
	
	var abstractGrouping = reviewForm.find(ABSTRACT_GROUPING_RADIO_BOXES + ':checked').val();
	var groups = reviewForm.find(OTHER_GROUPING_CLASS).val();
	// TODO: allow multiple groups; remove line after this comment
	groups = new Array(groups);
	
	// call service
	var reviewData = reviewForm.serialize();
	$.ajax({
		url:		REVIEWS_URL,
		type:		"POST",
		dataType:   "json",
		data:		reviewData,
		success:	function(response) {
						// remove review create form
		     			$('.createReview').fadeOut(1000, function() {
		     				$(this).remove();
		     			});
		     			
		     			// update update form
		     			$('#updateReviewText').text(reviewText);
		     			$('#updateReviewRating').stars("select", reviewRating);
		     			
		     			// TODO: update group input boxes
		     			
		     			// display review
		     			var reviewView = $('#newReview');
		     			reviewView.attr("id", "ownReview");
		     			updateReviewView(reviewView, reviewText, reviewRating, abstractGrouping, groups);
		     			reviewView.fadeIn(1000);
		     			
		     			// rating view update
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
		     			
		     			removeReviewActions();
		     			updateHash(reviewTemplate, response.hash);
					},
		statusCode: {
			400:	function(jqXHR, data, errorThrown) {
						handleAjaxErrors(reviewForm, jQuery.parseJSON(jqXHR.responseText));
					}
	    }
	});
	return false;
}

function updateReview() {
	// TODO: disable submit button
	if (!validateRating('#updateReviewRating')) {
		return false;
	}
	var reviewForm = $(this);
	
	var abstractGrouping = reviewForm.find(ABSTRACT_GROUPING_RADIO_BOXES + ':checked').val();
	var groups = reviewForm.find(OTHER_GROUPING_CLASS).val();
	// TODO: allow multiple groups; remove line after this comment
	groups = new Array(groups);
	
	// show spinner "updating review"s
	var spinner = reviewForm.find('.spinner');
	spinner.show('slow');
	
	// save all values for success action
	var reviewText = reviewForm.find('textarea[name="comment\\.text"]').val();
	var anonym = reviewForm.find('input[name="comment\\.anonym"]:checked').length > 0;
	var reviewRating = getRating(reviewForm.find('.reviewrating'));
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
						
						var reviewView = $('#ownReview');
		     			
						updateHash(reviewView, response.hash);
						
						if (anonym) {
							reviewView.addClass('anonym');
						} else {
							reviewView.removeClass('anonym');
						}
						
						// update values
						updateReviewView(reviewView, reviewText, reviewRating, abstractGrouping, groups);
						
		     			// update over all values
		     			var count = getReviewCount();
		     			var oldAvg = getAvg();
		     			var newAvg = (oldAvg * count - oldReviewRating + reviewRating) / count;
		     			setAvg(newAvg);
		     			plotRatingDistribution();
					},
		statusCode: {
			400:	function(jqXHR, data, errorThrown) {
						handleAjaxErrors(reviewForm, jQuery.parseJSON(jqXHR.responseText));
					}
	    }
	});
	return false;
}

function deleteReview() {
	// TODO: confirm?
	var deleteLink = $(this);
	$(this).siblings('.deleteInfo').show();
	
	var hash = getInterHash();
	var review = $('li#ownReview');
	var reviewHash = review.find('.info').data('discussionitemhash');
	
	var oldReviewRating = getOwnReviewRating();
	deleteLink.remove();
	var revDelUrl = REVIEWS_URL + "?hash=" + hash + "&ckey=" + ckey + "&discussionItem.hash=" + reviewHash;
	// TODO: handle error
	$.ajax({
		url:		revDelUrl,
		type:		"DELETE",
		success:	function(msg) {
						review.fadeOut(1000, function() {
		     				$(this).remove();
		     				
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
						});
					}
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
	reviewView.find('.meta').append(groupsView);
}

function getRating(starsWrapperId) {
	var stars = $(starsWrapperId).data("stars");
	return Number(stars.options.value);
}

function getStarsWidth(rating) {
	return STAR_WIDTH * rating;
}