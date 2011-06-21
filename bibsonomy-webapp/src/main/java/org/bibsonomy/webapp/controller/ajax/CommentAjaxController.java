package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.model.Comment;
import org.bibsonomy.webapp.command.ajax.DiscussionItemAjaxCommand;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.validation.CommentValidator;

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

	@Override
	public Validator<DiscussionItemAjaxCommand<Comment>> getValidator() {
		return new CommentValidator();
	}

}
