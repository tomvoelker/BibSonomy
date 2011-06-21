package org.bibsonomy.database.params.discussion;

import org.bibsonomy.database.common.enums.DiscussionItemType;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.DiscussionItem;

/**
 * @param <D> 
 * @author dzo
 * @version $Id$
 */
public class DiscussionItemParam<D extends DiscussionItem> extends GenericParam {
	
	private String interHash;
	private D discussionItem;

	/**
	 * @return the interHash
	 */
	public String getInterHash() {
		return this.interHash;
	}

	/**
	 * @param interHash the interHash to set
	 */
	public void setInterHash(final String interHash) {
		this.interHash = interHash;
	}
	
	/**
	 * @return the discussion item
	 */
	public D getDiscussionItem() {
		return this.discussionItem;
	}

	/**
	 * @param discussionItem the discussion item to set
	 */
	public void setDiscussionItem(final D discussionItem) {
		this.discussionItem = discussionItem;
	}
	
	/**
	 * @return the type of the discussion item
	 */
	public DiscussionItemType getDiscussionItemType() {
		return DiscussionItemType.DISCUSSION_ITEM;
	}
}
