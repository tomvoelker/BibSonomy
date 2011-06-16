package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.model.Comment;

/**
 * - ajax/comments
 * 
 * @author dzo
 * @version $Id$
 */
public class CommentAjaxController extends DiscussionItemAjaxController<Comment> {

	@Override
	protected Comment initDiscussionItem() {
		return new Comment();
	}

}
