package org.bibsonomy.database.managers.chain;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Tag;

/**
 * This interface encapsulates the getter for a list of tags.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public interface ChainPerformForTag {

	/**
	 * Returns a list of tags.
	 */
	public List<Tag> perform(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end, Transaction session);
}