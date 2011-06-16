package org.bibsonomy.database.params.discussion;

import org.bibsonomy.database.common.enums.DiscussionItemType;
import org.bibsonomy.model.Comment;

/**
 * @author dzo
 * @version $Id$
 */
public class CommentParam extends DiscussionItemParam<Comment> {
	
	/**
	 * @return the comment
	 */
	public Comment getComment() {
		return this.getDiscussionItem();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.params.DiscussionItemParam#getDiscussionItemType()
	 */
	@Override
	public DiscussionItemType getDiscussionItemType() {
		return DiscussionItemType.COMMENT;
	}
}
