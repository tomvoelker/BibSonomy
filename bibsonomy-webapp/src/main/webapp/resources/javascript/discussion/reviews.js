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

	if ($("#ratingDistribution").length > 0) {
		// get all ratings from hidden statistic tags
		var distribution = $('#ratingDistribution').data('distribution');
		
		$.each(distribution, function(index, ratingStat) {
			var key = ratingStat.rating;
			var value = ratingStat.count;
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
			form.parent().find('ul.subdiscussion:first>li.form').after(reviewTemplate);
			setupActions(reviewTemplate, text, data.hash);
			reviewTemplate.show();
			var ratingInput = reviewTemplate.find('input.reviewRating:first');
			ratingInput.val(rating);
			ratingInput.rating({
				min : 0,
				max : 5,
				step : 0.5,
				size : 'xs',
				readonly : true,
				showCaption : false,
				theme: 'krajee-fa',
				filledStar: '<i class="fa fa-star"></i>',
				emptyStar: '<i class="fa fa-star-o"></i>'
			});
			
			reviewTemplate.find('div.rating').data('rating', rating);
			reviewTemplate.attr('id', 'ownReview');
			
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
			reviewTemplate.effect("highlight", {}, 2500);
			form.hide();
			$('#comment-review-info').hide();
			$('.createcomment:first').show();
		},
		error:		function(jqXHR, data, errorThrown) {
			handleAjaxErrors(reviewForm, jQuery.parseJSON(jqXHR.responseText));
		},
	});
	return false;
}