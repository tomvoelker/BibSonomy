package org.bibsonomy.database.params.discussion;

import org.bibsonomy.database.common.enums.DiscussionItemType;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.DiscussionItem;

/**
 * @param <D> 
 * @author dzo
  */
public class DiscussionItemParam<D extends DiscussionItem> extends GenericParam {
	
	private String interHash;
	private String oldParentHash;
	private String newParentHash;
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
	 * @return the oldParentHash
	 */
	public String getOldParentHash() {
		return this.oldParentHash;
	}

	/**
	 * @param oldParentHash the oldParentHash to set
	 */
	public void setOldParentHash(final String oldParentHash) {
		this.oldParentHash = oldParentHash;
	}

	/**
	 * @return the newParentHash
	 */
	public String getNewParentHash() {
		return this.newParentHash;
	}

	/**
	 * @param newParentHash the newParentHash to set
	 */
	public void setNewParentHash(final String newParentHash) {
		this.newParentHash = newParentHash;
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
