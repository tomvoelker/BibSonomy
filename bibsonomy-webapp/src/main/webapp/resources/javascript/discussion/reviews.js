var REVIEWS_URL = "/ajax/reviews";
var STAR_WIDTH = 16;
var RATING_STEPS = 11;
var STEP_RATING = 2;

var RATING_AVG_DIV_SELECTOR = '#ratingAvg';
var RATING_AVG_SELECTOR = RATING_AVG_DIV_SELECTOR + ' span[property=ratingAverage]';

var REVIEW_EDIT_LINK_SELECTOR = 'a.reviewEditLink';
var REVIEW_DELETE_LINK_SELECTOR = 'a.reviewDeleteLink';

var REVIEW_TEXTAREA_SELECTOR = 'textarea[name="discussionItem\\.text"]';
var REVIEW_ANONYMOUS_SELECTOR = 'input[name="discussionItem\\.anonymous"]';
var REVIEW_RATING_SELECTOR = '.reviewrating';

$(function() {
	plotRatingDistribution();
	
	// init all selectable stars
	initStars();
	
	$('a.rating-reset').click(function() {
		$('.newRating').rating('update', 0);
		return false;
	});
});

function initStars() {
	$('#discussion .reviewRating').rating({
		min : 0,
		max : 5,
		step : 0.0001,
		size : 'xs',
		readonly : true,
		showCaption : false,
		glyphicon : false,
		ratingClass : 'rating-fa'
	});
	
	$('.newRating').rating({
		size: 'xs',
		showCaption : false,
		glyphicon : false,
		min: 0,
		max: 5,
		step: 0.5,
		ratingClass : 'rating-fa'
	});
}

function getReviewCount() {
	return parseInt($('#review_info_rating span[property=ratingCount]').text());
}

function getAvg() {
	return Number($(RATING_AVG_SELECTOR).text().replace(',', '.'));
}

function updateRatingCounter(element) {
	if(element.target != undefined) element = element.target;
	var val = $(element).children('input[type=hidden]').val();
	
	if(val!=undefined) 
		$(element).next('.discussionRatingValue').children('b').html(val);
}

function getOwnReviewRating() {
	return Number($('#ownReview .rating').data('rating'));
}

function plotRatingDistribution() {
	$('#averageRating').rating({
		min : 0,
		max : 5,
		step : 0.0001,
		size : 'xs',
		readonly : true,
		showCaption : false,
		glyphicon : false,
		ratingClass : 'rating-fa'
	});
	
	var average = getAvg()
	$('#averageRating').rating('update', average);

	
	if ($("#rating-distribution").length == 0) {
		return;
	}
	
	var ratings = [];
	var rating_ticks = [];
	var maxValue = 0;

	if ($(".ratingDistributionData").length > 0) {
		// get all ratings from hidden statistic tags
		$('#ratingDistribution').find('.ratingDistributionData').each(function() {
			var key = $(this).data("rating");
			var value = $(this).data("count");
			ratings[key] = value;
			if (value > maxValue) { 
				maxValue = value; 
			}
		});
	} else {
		// get all ratings from all reviews
		$('#discussion li.review').not('#newReview').find('.rating').each(function() {
			var key = parseFloat($(this).data("rating"));
			if (ratings[key]) {
				ratings[key] += 1;
			} else {
				ratings[key] = 1;
			}
			if (ratings[key] > maxValue) {
				maxValue = ratings[key]; 
			}
		});
	}
	var ratingCounts = [];
	for (var i = 0; i < RATING_STEPS; i++) {
		var key = parseFloat(i) / parseFloat(STEP_RATING);
		var value = 0;
		if (ratings[key]) {
			value = ratings[key];
		}
		
		var count = getReviewCount();
		if (value > 0) {
			value = value / maxValue * 100;
		} else {
			value = 0;
		}
		
		ratingCounts.push(value);
		rating_ticks.push(key);
	}
	
	var data = {
		labels : rating_ticks,
		datasets : [ {
			label : "reviews",
			fillColor : "rgba(239,205,106,1)",
			strokeColor : "rgba(235,191,69,1)",
			highlightFill : "rgba(239,205,106,1)",
			highlightStroke : "rgba(235,191,69,1)",
			data : ratingCounts
		}]
	};
	
	var ctx = $("#rating-distribution").get(0).getContext("2d");
	var myBarChart = new Chart(ctx).Bar(data, {
		scaleShowLabels: false,
	});
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
						updateReviewView(reviewView, parseLinks(reviewText), reviewRating, abstractGrouping, groups);
						highlight(reviewView);
						
		     			// update over all values
		     			var count = getReviewCount();
		     			var oldAvg = getAvg();
		     			var newAvg = (oldAvg * count - oldReviewRating + reviewRating) / count;
		     			setAvg(newAvg);
		     			plotRatingDistribution();
		     			reviewForm.siblings(".originalText").remove();
		     			reviewForm.submit(updateReview);
		     			reviewForm.siblings(".citeBox").children("div").remove();
		     			reviewForm.siblings(".citeBox").hide();
		     			reviewForm.siblings(".bookCiteBox").children("div").remove();
		     			reviewForm.siblings(".bookCiteBox").hide();
		     			handleLinks(reviewForm.parent());
					},
		error:		function(jqXHR, data, errorThrown) {
						handleAjaxErrors(reviewForm, jQuery.parseJSON(jqXHR.responseText));
						reviewForm.submit(updateReview);
					},
	});
	return false;
}

function updateReviewView(reviewView, text, rating, abstractGrouping, groups) {
	var starWidth = getStarsWidth(rating);
	var ratingView = reviewView.find('.rating');
	ratingView.data("rating", rating);
	reviewView.find('.review.text').replaceWith(text);
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