package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.model.Review;

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
	
}
