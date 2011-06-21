package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.model.Review;
import org.bibsonomy.webapp.command.ajax.DiscussionItemAjaxCommand;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.validation.ReviewValidator;

/**
 * - ajax/reviews
 * 
 * @author dzo
 * @version $Id$
 */
public class ReviewAjaxController extends DiscussionItemAjaxController<Review> {

	@Override
	protected Review initDiscussionItem() {
		return new Review();
	}

	@Override
	public Validator<DiscussionItemAjaxCommand<Review>> getValidator() {
		return new ReviewValidator();
	}
	
}
