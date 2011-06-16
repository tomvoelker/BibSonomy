package org.bibsonomy.database.managers.discussion;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.discussion.CommentParam;
import org.bibsonomy.database.params.discussion.DiscussionItemParam;
import org.bibsonomy.model.Comment;

/**
 *  Used to create, read, update and delete comments from the database.
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
		if (!present(comment.getText())) {
			throw new ValidationException("comment text is empty");
		}
		
		this.checkLength(comment, session);
	}

	@Override
	protected boolean updateDiscussionItem(final String interHash, final Comment comment, final Comment oldComment, final DBSession session) {
		final DiscussionItemParam<Comment> param = new CommentParam();
		param.setDiscussionItem(comment);
		
		this.update("updateComment", param, session);
		
		return false;
	}
	
	@Override
	protected boolean createDiscussionItem(final String interHash, final Comment comment, final DBSession session, final int discussionId) {
		final String userName = comment.getUser().getName();
		
		/*
		 * check comment
		 */
		this.checkDiscussionItem(comment, session);
		
		/*
		 * build comment param and insert comment
		 */
		final CommentParam param = this.createCommentParam(interHash, userName);
		param.setDiscussionItem(comment);
		this.insert("insertComment", param, session);
		
		return true;
	}
	
	protected CommentParam createCommentParam(final String interHash, final String userName) {
		final CommentParam param = new CommentParam();
		this.fillDiscussionItemParam(param, interHash, userName);
		return param;
	}

	@Override
	protected List<Comment> getDiscussionItemsByHashForResource(final DiscussionItemParam<Comment> param, final DBSession session) {
		return this.queryForList("getCommentsByHashForResource", param, Comment.class, session);
	}
}
