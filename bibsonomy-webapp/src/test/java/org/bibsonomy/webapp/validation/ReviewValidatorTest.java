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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.model.Review;
import org.bibsonomy.webapp.command.ajax.DiscussionItemAjaxCommand;
import org.bibsonomy.webapp.util.GroupingCommandUtils;
import org.bibsonomy.webapp.validation.util.ValidationTestUtils;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;


/**
 * @author dzo
 */
public class ReviewValidatorTest {
	private static final ReviewValidator VALIDATOR = new ReviewValidator();
	
	/**
	 * test validate method
	 */
	@Test
	public void testValidate() {
		final DiscussionItemAjaxCommand<Review> command = new DiscussionItemAjaxCommand<Review>();
		final Review review = new Review();
		review.setRating(-1.0);
		command.setDiscussionItem(review);
		
		Errors errors = ValidationTestUtils.validate(VALIDATOR, command);
		assertTrue(errors.hasErrors());
		assertTrue(errors.hasFieldErrors("hash"));
		assertTrue(errors.hasFieldErrors(DiscussionItemValidator.DISCUSSION_ITEM_PATH + "rating"));
		List<FieldError> fieldErrors = errors.getFieldErrors(DiscussionItemValidator.DISCUSSION_ITEM_PATH + "rating");
		assertEquals(1, fieldErrors.size());
		FieldError ratingFieldError = fieldErrors.get(0);
		assertEquals("error.field.valid.review.rating.range", ratingFieldError.getCode());
		
		review.setRating(5.1);
		errors = ValidationTestUtils.validate(VALIDATOR, command);
		assertTrue(errors.hasErrors());
		fieldErrors = errors.getFieldErrors(DiscussionItemValidator.DISCUSSION_ITEM_PATH + "rating");
		assertEquals(1, fieldErrors.size());
		ratingFieldError = fieldErrors.get(0);
		assertEquals("error.field.valid.review.rating.range", ratingFieldError.getCode());
		
		review.setRating(3.8);
		
		errors = ValidationTestUtils.validate(VALIDATOR, command);
		assertTrue(errors.hasErrors());
		fieldErrors = errors.getFieldErrors(DiscussionItemValidator.DISCUSSION_ITEM_PATH + "rating");
		assertEquals(1, fieldErrors.size());
		ratingFieldError = fieldErrors.get(0);
		assertEquals("error.field.valid.review.rating.decimal", ratingFieldError.getCode());
		
		command.setHash("a1d0c6e83f027327d8461063f4ac58a6");
		review.setText("This is Test");
		review.setRating(4.5);
		
		// only groups not valid
		errors = ValidationTestUtils.validate(VALIDATOR, command);
		assertEquals(1, errors.getFieldErrorCount("groups"));
		
		command.setAbstractGrouping(GroupingCommandUtils.OTHER_ABSTRACT_GROUPING);
		command.setGroups(Arrays.asList("test"));
		errors = ValidationTestUtils.validate(VALIDATOR, command);
		assertFalse(errors.hasErrors());
		
		final StringBuilder reviewText = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			reviewText.append("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore" +
					" magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
					"Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat" +
					"cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		}
		review.setText(reviewText.toString());
		errors = ValidationTestUtils.validate(VALIDATOR, command);
		assertTrue(errors.hasFieldErrors(DiscussionItemValidator.DISCUSSION_ITEM_PATH + "text"));
	}

}
