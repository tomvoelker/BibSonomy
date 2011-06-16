package org.bibsonomy.database.params.discussion;

import org.bibsonomy.database.common.enums.DiscussionItemType;
import org.bibsonomy.model.Review;

/**
 * 
 * @author dzo
 * @version $Id$
 */
public class ReviewParam extends DiscussionItemParam<Review> {
	
	/**
	 * @return the discussion item as a review
	 */
	public Review getReview() {
		return this.getDiscussionItem();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.params.DiscussionItemParam#getDiscussionItemType()
	 */
	@Override
	public DiscussionItemType getDiscussionItemType() {
		return DiscussionItemType.REVIEW;
	}
	
}
