package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.Review;
import org.springframework.validation.Errors;


/**
 * validator for reviews
 * validates: 
 * 		- text length
 * 		- rating
 * 
 * @author dzo
 * @version $Id$
 */
public class ReviewValidator extends DiscussionItemValidator<Review> {
	
	@Override
	protected void validateDiscussionItem(final Review review, final Errors errors) {
		final double rating = review.getRating();
		if (Double.compare(rating, Review.MIN_REVIEW_RATING) < 0 || Double.compare(rating, Review.MAX_REVIEW_RATING) > 0) {
			errors.rejectValue(DISCUSSION_ITEM_PATH + "rating", "error.field.valid.review.rating.range");
		} else {
			final double decimal = Math.abs(rating - Math.rint(rating));
		
			/*
			 * only x.0 and x.5 ratings are allowed
			 */
			if (Double.compare(decimal, 0) != 0 && Double.compare(decimal - 0.5, 0) != 0) {
				errors.rejectValue(DISCUSSION_ITEM_PATH + "rating", "error.field.valid.review.rating.decimal");
			}
		}
		final String text = review.getText();
		if (present(text) && text.length() > Review.MAX_TEXT_LENGTH) {
			errors.rejectValue(DISCUSSION_ITEM_PATH + "text", "error.field.valid.comment.text.length");
		}
	}
}
