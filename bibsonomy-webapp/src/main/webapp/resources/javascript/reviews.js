var REVIEWS_URL = "/ajax/reviews";
var MARK_REVIEWS_URL = REVIEWS_URL + "/mark";
var STAR_WIDTH = 16;
var MAX_RATING = 10;
var STEP_RATING = 2;

$(function() {
	plotRatingDistribution();
	
	// init all selectable stars
	$('.reviewrating').stars({
		split: 2
	});
	
	$('#updateReviewForm').hide();
	
	// hide reviews on list pages
	if ($('#bibtexList').length > 0 || $('#bookmarkList').length > 0) {
		$('#reviewlist').hide();
		$('#postReview').hide();
	}
	
	// hide graph and info
	if ($('#noReviewInfo').length > 0) {
		$('#ratingAvg').hide();
		$('#ratingDistribution').hide();
	}
	
	$('#toggleReviews a').click(function() {
		var visible = $('#reviewlist').is(":visible");
		$('#reviewlist').toggle('slow');
		$('#postReview').toggle('slow');
		
		var text = getString('post.resource.review.action.show');
		if (!visible) {
			text = getString('post.resource.review.action.hide');
		}
		
		$(this).text(text);
	});
	
	// create review form
	$('#createReviewForm').submit(function() {
		if (!validateRating('#createReviewRating')) {
			return false;
		}
		
		$('#createSpinner').show('slow');
		
		var reviewText = $('#createReviewText').val();
		var reviewRating = getRating('#createReviewRating');
		var starWidth = getStarsWidth(reviewRating);
		
		// call service
		var reviewData = $(this).serialize();
		$.ajax({
			url:		REVIEWS_URL,
			type:		"POST",
			data:		reviewData,
			success:	function(msg) {
							// remove review create form
			     			$('#postReview').fadeOut(1000, function() {
			     				$(this).remove();
			     			});
			     			
			     			// update update form
			     			$('#updateReviewText').text(reviewText);
			     			$('#updateReviewRating').stars("select", reviewRating);
			     			
			     			// display values
			     			$('#newReview .rating').data("rating", reviewRating);
			     			$('#newReview .reviewText').text(reviewText);
			     			$('#newReview .reviewinfo .stars-on-1').css('width', starWidth);
			     			$('#newReview').fadeIn(1000);
			     			$('#newReview').attr("id", "ownReview");
			     			
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
						}
		});
		return false;
	});
	
	$('#updateReviewForm').submit(function() {
		if (!validateRating('#updateReviewRating')) {
			return false;
		}
		
		$('#updateSpinner').show('slow');
		
		var reviewText = $('#updateReviewText').val();
		var reviewRating = getRating('#updateReviewRating');
		var starWidth = getStarsWidth(reviewRating);
		
		var oldReviewRating = getOwnReviewRating();
		// call service
		var reviewData = $(this).serialize();
		$.ajax({
			url:		REVIEWS_URL,
			type:		"POST",
			data: 		reviewData,
			success:	function(msg) {
							$('#updateReviewForm').hide('slow');
							$('#updateSpinner').hide();
			     			
							// update values
							$('#ownReview .rating').data("rating", reviewRating);
			     			$('#ownReview .reviewText').text(reviewText);
			     			$('#ownReview .reviewinfo .stars-on-1').css('width', starWidth);
			     			
			     			// update over all values
			     			var count = getReviewCount();
			     			var oldAvg = getAvg();
			     			var newAvg = (oldAvg * count - oldReviewRating + reviewRating) / count;
			     			setAvg(newAvg);
			     			plotRatingDistribution();
						}
		});
		return false;
	});
	
	// delete link for own review
	$('a#reviewDelete').click(function() {
		// TODO: confirm?
		$('#deleteSpinner').show();
		
		var hash = $('#reviewlist').data('interHash');
		var username = $(this).parents('.reviewinfo:last').data('username');
		var cKey = $('#reviewlist').data('ckey');
		var review = $('li#ownReview');
		
		var oldReviewRating = getOwnReviewRating();
		// TODO: encode user name?
		// TODO: handle error?
		var revDelUrl = REVIEWS_URL + "?hash=" + hash + "&username=" + username + "&ckey=" + ckey;
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
	});
	
	// show edit form for own review
	$('a#reviewEdit').click(function() {
		$('#updateReviewForm').toggle('slow');
	});
	
	$('.helpful').submit(function() {
		var helpfulData = $(this).serialize();
		var container = $(this).parents('.helpfulContainer');
		var helpful = $(this).children("input[name=helpful]").val() === "true";
		// call services
		$.ajax({
			url:		MARK_REVIEWS_URL,
			type:		"POST",
			data:		helpfulData,
			success:	function(msg) {
							// TODO: update feedback counter?
							container.text(getString("post.resource.review.helpful.thankyou"));
						},
			statusCode: {
		    400:  		function() {
		    				container.text(getString("post.resource.review.helpful.thankyou"));
		    			}
		    },
		});
		return false;
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
	var ratings = [];
	var d1 = [];
	
	$('#reviewlist li').not('#newReview').find('.rating').each(function() {
		var key = $(this).data("rating");
		if (ratings[key]) {
			ratings[key] += 1;
		} else {
			ratings[key] = 1;
		}
	});
	
	for (var i = 0; i <= MAX_RATING; i += 1) {
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
	}
	
	$.plot($("#ratingDistributionGraph"), [ d1 ], {
		bars: {
			show: true,
			align: "center",
			barWidth: 0.2,
			fill: 0.7,
		},
		xaxis: {
			// TODO: MAX and STEP_RATING
			ticks: [0, 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5],
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

function getRating(starsWrapperId) {
	var stars = $(starsWrapperId).data("stars");
	return Number(stars.options.value);
}

function getStarsWidth(rating) {
	return STAR_WIDTH * rating;
}