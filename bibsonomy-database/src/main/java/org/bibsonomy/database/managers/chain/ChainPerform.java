package org.bibsonomy.database.managers.chain;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * This interface encapsulates the getter for a list of posts.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public interface ChainPerform<T extends Resource> {

	/**
	 * Returns a list of posts.
	 */
	public List<Post<T>> perform(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, Transaction session);
}