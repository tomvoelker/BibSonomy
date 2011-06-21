package org.bibsonomy.database.managers.discussion;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.discussion.CommentParam;
import org.bibsonomy.database.params.discussion.DiscussionItemParam;
import org.bibsonomy.model.Comment;

/**
 * Used to create, read, update and delete comments from the database.
 * 
 * @author dzo
 * @version $Id$
 */
public class CommentDatabaseManager extends DiscussionItemDatabaseManager<Comment> {
	
	private static final CommentDatabaseManager INSTANCE = new CommentDatabaseManager();

	/**
	 * @return the @{link:CommentDatabaseManager} instance
	 */
	public static CommentDatabaseManager getInstance() {
		return INSTANCE;
	}

	private CommentDatabaseManager() {
		// just call super
	}
	
	@Override
	protected void checkDiscussionItem(final Comment comment, final DBSession session) {
		/*
		 * comments need a text
		 */
		final String text = comment.getText();
		if (!present(text)) {
			throw new ValidationException("comment text is empty");
		}
		
		/*
		 * max text length
		 */
		if (text.length() > Comment.MAX_TEXT_LENGTH) {
			throw new ValidationException("comment text is too long");
		}
	}

	@Override
	protected DiscussionItemParam<Comment> createDiscussionItemParam() {
		return new CommentParam();
	}
}
