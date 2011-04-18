var REVIEWS_URL = "/ajax/reviews";
var MARK_REVIEWS_URL = REVIEWS_URL + "/mark";

$(function() {
	// init all selectable stars
	$('.reviewrating').stars({
		split: 2
	});
	
	// create review form
	$('#createReviewForm').submit(function() {
		var reviewRating = getRating("#createReviewRating");
		if (reviewRating === false) {
			return false;
		}
		
		$('#createSpinner').toggle();
		
		var reviewText = $('#createReviewText').val();
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
			     			$('#newReview .reviewText').text(reviewText);
			     			$('#newReview .reviewinfo .stars-on').css('width', starWidth);
			     			$('#newReview').fadeIn(1000);
			     			
			     			// TODO: update overall counter and rating
						}
		});
		return false;
	});
	
	$('#updateReviewForm').submit(function() {
		var reviewRating = getRating("#updateReviewRating");
		if (reviewRating === false) {
			return false;
		}
		
		$('#updateSpinner').toggle();
		
		var reviewText = $('#updateReviewText').val();
		var starWidth = getStarsWidth(reviewRating);
		
		// call service
		var updRevUrl = REVIEWS_URL + "?" + $(this).serialize();
		alert(updRevUrl);
		$.ajax({
			url:		updRevUrl,
			type:		"PUT",
			success:	function(msg) {
							$('#editReviewForm').toggle('slow');
							$('#updateSpinner').toggle();
			     			
							// update values
			     			$('#ownReview .reviewText').text(reviewText);
			     			$('#ownReview .reviewinfo .stars-on').css('width', starWidth);
			     			
			     			// TODO: update overall counter and rating
						}
		});
		return false;
	});
	
	// delete link for own review
	$('a#reviewDelete').click(function() {
		// TODO: show spinner
		// TODO: confirm?
		var hash = $('#reviews').data('interHash');
		var username = $(this).parents('.reviewinfo:last').data('username');
		var cKey = $('#reviews').data('ckey');
		var review = $(this).parents('li:last');
		// TODO: encode user name?
		var revDelUrl = REVIEWS_URL + "?hash=" + hash + "&username=" + username + "&ckey=" + ckey;
		$.ajax({
			url:		revDelUrl,
			type:		"DELETE",
			success:	function(msg) {
							review.fadeOut(1000, function() {
			     				$(this).remove();
							});
						}
		});
		return false;
	});
	
	// show edit form for own review
	$('a#reviewEdit').click(function() {
		$('#editReviewForm').toggle('slow');
	});
	
	$('.helpful').submit(function() {
		var helpfulData = $(this).serialize();
		var container = $(this).parents('.helpfulContainer');
		var helpful = $(this).children("input[name=helpful]").val() === "true";
		alert(helpful);
		// call services
		$.ajax({
			url:		MARK_REVIEWS_URL,
			type:		"POST",
			data:		helpfulData,
			success:	function(msg) {
							// TODO: update feedback counter
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


function getRating(starsWrapperId) {
	var stars = $(starsWrapperId).data("stars");
	var reviewRating = stars.options.value;
	if (reviewRating == 0) {
		if (!confirm(getString("post.resource.review.rating0"))) {
			return false;
		}
	}
	
	return reviewRating;
}

function getStarsWidth(rating) {
	return 16 * rating;
}