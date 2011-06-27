package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.Comment;
import org.springframework.validation.Errors;

/**
 * @author dzo
 * @version $Id$
 */
public class CommentValidator extends DiscussionItemValidator<Comment> {

	@Override
	protected void validateDiscussionItem(final Comment comment, final Errors errors) {
		final String text = comment.getText();
		if (!present(text)) {
			errors.rejectValue(DISCUSSION_ITEM_PATH + "text", "error.field.valid.comment.text");
		} else {
			if (text.length() > Comment.MAX_TEXT_LENGTH) {
				errors.rejectValue(DISCUSSION_ITEM_PATH + "text", "error.field.valid.comment.text.length", new Object[] { Comment.MAX_TEXT_LENGTH }, "The text is too long. Only " + Comment.MAX_TEXT_LENGTH + " characters allowed.");
			}
		}
	}
}
