package org.bibsonomy.database.managers.chain;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Tag;

/**
 * A Chain Element for the Tag Chain
 * 
 * @author dbe
 */
public abstract class ChainElementForTag implements ChainPerformForTag {

	protected final GeneralDatabaseManager generalDb;
	/** The next element of the chain */
	private ChainElementForTag next;

	public ChainElementForTag() {
		this.generalDb = GeneralDatabaseManager.getInstance();
	}


	/**
	 * Sets the next element in the chain.
	 * 
	 * @param nextElement
	 */
	public final void setNext(final ChainElementForTag nextElement) {
		this.next = nextElement;
	}

	/**
	 * Walk through the chain until a Chain Element is found that can
	 * handle the request
	 * 
	 * @param authUser
	 * @param grouping
	 * @param groupingName
	 * @param regex
	 * @param subTags
	 * @param superTags
	 * @param subSuperTagsTransitive
	 * @param start
	 * @param end
	 * @param session
	 * @return
	 */
	public final List<Tag> perform(String authUser, GroupingEntity grouping, String groupingName, String regex, Boolean subTags, Boolean superTags, Boolean subSuperTagsTransitive, int start, int end, final Transaction session) {
		if (this.canHandle(authUser, grouping, groupingName, regex, subTags, superTags, subSuperTagsTransitive, start, end, session)) {
			return this.handle(authUser, grouping, groupingName, regex, subTags, superTags, subSuperTagsTransitive, start, end, session);
		} else {
			if (this.next != null) {
				return this.next.perform(authUser, grouping, groupingName, regex, subTags, superTags, subSuperTagsTransitive, start, end, session);
			}
		}
		// FIXME nobody can handle this -> throw an exception
		return null;
	}


	/**
	 * Handles the request.
	 * 
	 * @param authUser
	 * @param grouping
	 * @param groupingName
	 * @param regex
	 * @param subTags
	 * @param superTags
	 * @param subSuperTagsTransitive
	 * @param start
	 * @param end
	 * @param session
	 * @return List<Tag>
	 */
	protected abstract List<Tag> handle(String authUser, GroupingEntity grouping, String groupingName, String regex, Boolean subTags, Boolean superTags, Boolean subSuperTagsTransitive, int start, int end, final Transaction session);


	/**
	 * Returns true if the request can be handled by the current chain element, otherwise false.
	 * 
	 * @param authUser
	 * @param grouping
	 * @param groupingName
	 * @param regex
	 * @param subTags
	 * @param superTags
	 * @param subSuperTagsTransitive
	 * @param start
	 * @param end
	 * @param session
	 * @return Boolean
	 */
	protected abstract boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, String regex, Boolean subTags, Boolean superTags, Boolean subSuperTagsTransitive, int start, int end, final Transaction session);	
}