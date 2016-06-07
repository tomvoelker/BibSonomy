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
		theme: 'krajee-fa',
		filledStar: '<i class="fa fa-star"></i>',
		emptyStar: '<i class="fa fa-star-o"></i>'
	});
	
	$('.newRating').rating({
		size: 'xs',
		showCaption : false,
		min: 0,
		max: 5,
		step: 0.5,
		theme: 'krajee-fa',
		filledStar: '<i class="fa fa-star"></i>',
		emptyStar: '<i class="fa fa-star-o"></i>'
	});
}

function getReviewCount() {
	return parseInt($('#review_info_rating span[property=ratingCount]').text());
}

function getAvg() {
	var avgText = $(RATING_AVG_SELECTOR).text().replace(',', '.');
	return parseFloat(avgText);
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
		filledStar: '<i class="fa fa-star"></i>',
		emptyStar: '<i class="fa fa-star-o"></i>',
		theme: 'krajee-fa',
		filledStar: '<i class="fa fa-star"></i>',
		emptyStar: '<i class="fa fa-star-o"></i>'
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

function getRating(element) {
	var stars = $(element).data("stars");
	return parseFloat(stars.options.value);
}