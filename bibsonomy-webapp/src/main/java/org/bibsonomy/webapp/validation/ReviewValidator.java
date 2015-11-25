/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 */
public class ReviewValidator extends DiscussionItemValidator<Review> {
	
	@Override
	protected void validateDiscussionItem(final Review review, final Errors errors) {
		final double rating = review.getRating();
		if (Double.compare(rating, Review.MIN_REVIEW_RATING) < 0 || Double.compare(rating, Review.MAX_REVIEW_RATING) > 0) {
			errors.rejectValue(DISCUSSION_ITEM_PATH + "rating", "error.field.valid.review.rating.range", new Object[] { Review.MIN_REVIEW_RATING, Review.MAX_REVIEW_RATING }, "Only ratings between " + Review.MIN_REVIEW_RATING  + " and " + Review.MAX_REVIEW_RATING + " are allowed.");
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
			errors.rejectValue(DISCUSSION_ITEM_PATH + "text", "error.field.valid.comment.text.length", new Object[] { Review.MAX_TEXT_LENGTH }, "The text is too long. Only " + Review.MAX_TEXT_LENGTH + " characters allowed.");
		}
	}
}
