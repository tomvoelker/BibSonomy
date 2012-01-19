package org.bibsonomy.webapp.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.bibsonomy.model.Comment;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.webapp.command.ajax.DiscussionItemAjaxCommand;
import org.bibsonomy.webapp.validation.util.ValidationTestUtils;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

/**
 * @author dzo
 * @version $Id$
 */
public class CommentValidatorTest {
	
	private static final CommentValidator COMMENT_VALIDATOR = new CommentValidator();

	/**
	 * tests {@link CommentValidator#validate(Object, Errors)}
	 */
	@Test
	public final void testValidate() {
		final DiscussionItemAjaxCommand<Comment> command = new DiscussionItemAjaxCommand<Comment>();
		final Comment comment = new Comment();
		command.setDiscussionItem(comment);
		Errors errors = ValidationTestUtils.validate(COMMENT_VALIDATOR, command);
		assertEquals(3, errors.getErrorCount()); // group, hash and comment text
		
		command.setAbstractGrouping(GroupUtils.getPublicGroup().getName());
		command.setHash("testhash");
		comment.setText("Great");
		
		errors = ValidationTestUtils.validate(COMMENT_VALIDATOR, command);
		assertEquals(0, errors.getErrorCount());
		
		
		/*
		 * only test text validation (hash and groups tested by other tests)
		 */
		comment.setText("");
		errors = ValidationTestUtils.validate(COMMENT_VALIDATOR, command);
		
		assertEquals(1, errors.getErrorCount());
		final String textFieldPath = DiscussionItemValidator.DISCUSSION_ITEM_PATH + "text";
		assertTrue(errors.hasFieldErrors(textFieldPath));
		FieldError fieldError = errors.getFieldError(textFieldPath);
		assertEquals("error.field.valid.comment.text", fieldError.getCode());
		
		final StringBuilder commentText = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			commentText.append("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore" +
					" magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
					"Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat" +
					"cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		}
		comment.setText(commentText.toString());
		
		errors = ValidationTestUtils.validate(COMMENT_VALIDATOR, command);
		
		assertTrue(errors.hasFieldErrors(textFieldPath));
		fieldError = errors.getFieldError(textFieldPath);
		
		assertEquals("error.field.valid.comment.text.length", fieldError.getCode());
	}

}
