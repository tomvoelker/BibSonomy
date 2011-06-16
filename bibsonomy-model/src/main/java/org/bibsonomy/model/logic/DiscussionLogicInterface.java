package org.bibsonomy.model.logic;

import java.util.List;

import org.bibsonomy.model.DiscussionItem;

/**
 * @author dzo
 * @version $Id$
 */
public interface DiscussionLogicInterface {
	
	/**
	 * creates a discussion item for the specified resource (interHash) and user
	 * 
	 * @param interHash
	 * @param username
	 * @param discussionItem
	 */
	public void createDiscussionItem(String interHash, String username, DiscussionItem discussionItem);
	
	/**
	 * updates a discussion item for the specified resource (interHash) and user
	 * the item is identified by the hash (please don't recalculate the hash;
	 * done by the logic)
	 * 
	 * @param username
	 * @param interHash
	 * @param discussionItem
	 */
	public void updateDiscussionItem(String username, String interHash, DiscussionItem discussionItem);
	
	/**
	 * deletes the specified discussion item (hash) for the specified user and
	 * resource (interHash)
	 * 
	 * @param username
	 * @param interHash
	 * @param discussionItemHash
	 */
	public void deleteDiscussionItem(String username, String interHash, String discussionItemHash);
	
	/**
	 * get all 
	 * 
	 * @param interHash
	 * @return a list of discussion items (comment, reviews, …)
	 */
	public List<DiscussionItem> getDiscussionSpace(String interHash);	
}
