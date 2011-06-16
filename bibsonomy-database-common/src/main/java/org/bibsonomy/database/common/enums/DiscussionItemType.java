package org.bibsonomy.database.common.enums;

/**
 * @author dzo
 * @version $Id$
 */
public enum DiscussionItemType {
	
	/**
	 * represents a deleted or invisible discussion item
	 */
	DISCUSSION_ITEM(0),
	
	/**
	 * review (optional text with rating)
	 */
	REVIEW(1),
	
	/**
	 * comment (only text)
	 */
	COMMENT(2);
	
	private int id;
	
	private DiscussionItemType(final int id) {
		this.id = id;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}
}
