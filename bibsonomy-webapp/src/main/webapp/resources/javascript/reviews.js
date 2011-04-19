var REVIEWS_URL = "/ajax/reviews";
var MARK_REVIEWS_URL = REVIEWS_URL + "/mark";
var STAR_WIDTH = 16;

$(function() {	
	// init all selectable stars
	$('.reviewrating').stars({
		split: 2
	});
	
	$('#updateReviewForm').hide();
	
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
			     			$('#updateReviewRating').stars("select", reviewRating + "");
			     			
			     			// display values
			     			$('#newReview .reviewText').text(reviewText);
			     			$('#newReview .reviewinfo .stars-on').css('width', starWidth);
			     			$('#newReview').fadeIn(1000);
			     			
			     			var oldCount = getReviewCount();
			     			var oldAvg = getAvg();
			     			var newCount = oldCount + 1;
			     			var newAvg = (oldAvg * oldCount + reviewRating) / newCount;	     			
			     			setReviewCount(newCount);
			     			setAvg(newAvg);
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
			     			$('#ownReview .reviewText').text(reviewText);
			     			$('#ownReview .reviewinfo .stars-on').css('width', starWidth);
			     			
			     			// update over all values
			     			var count = getReviewCount();
			     			var oldAvg = getAvg();
			     			var newAvg = (oldAvg * count - oldReviewRating + reviewRating) / count;
			     			setAvg(newAvg);
						}
		});
		return false;
	});
	
	// delete link for own review
	$('a#reviewDelete').click(function() {
		// TODO: confirm?
		$('#deleteSpinner').show('slow');
		
		var hash = $('#reviews').data('interHash');
		var username = $(this).parents('.reviewinfo:last').data('username');
		var cKey = $('#reviews').data('ckey');
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
							});
							
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
	return Number($('#review_info_rating span[property=v\\:average]').text());
}

function setAvg(value) {
	value = value.toFixed(2);
	$('#review_info_rating span[property=v\\:average]').text(value);
	var starWidth = getStarsWidth(value);
	$('#review_info_rating .stars-on').css('width', starWidth);
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

function getRating(starsWrapperId) {
	var stars = $(starsWrapperId).data("stars");
	return Number(stars.options.value);
}

function getStarsWidth(rating) {
	return STAR_WIDTH * rating;
}