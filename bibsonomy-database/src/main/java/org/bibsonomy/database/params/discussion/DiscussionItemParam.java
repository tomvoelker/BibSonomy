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

	private String loggedinUsername;

	private String interHash;
	
	private D discussionItem;
	
	private int discussionId;


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
	 * @param comment the discussion item to set
	 */
	public void setDiscussionItem(final D comment) {
		this.discussionItem = comment;
	}

	/**
	 * @return the loggedinUsername
	 */
	public String getLoggedinUsername() {
		return this.loggedinUsername;
	}

	/**
	 * @param loggedinUsername the loggedinUsername to set
	 */
	public void setLoggedinUsername(final String loggedinUsername) {
		this.loggedinUsername = loggedinUsername;
	}

	/**
	 * @param discussionId the commentId to set
	 */
	public void setDiscussionId(final int discussionId) {
		this.discussionId = discussionId;
	}

	/**
	 * @return the commentId
	 */
	public int getDiscusssionId() {
		return discussionId;
	}
	
	/**
	 * @return the type of the dicussion item
	 */
	public DiscussionItemType getDiscussionItemType() {
		return DiscussionItemType.DISCUSSION_ITEM;
	}
}
