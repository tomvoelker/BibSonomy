package org.bibsonomy.database.managers.chain;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Tag;

public abstract class ChainElementForTag implements ChainPerformForTag {

	protected final GeneralDatabaseManager generalDb;
	/** The next element of the chain */
	private ChainElementForTag next;

	public ChainElementForTag() {
		this.generalDb = GeneralDatabaseManager.getInstance();
	}

	/**
	 * Sets the next element in the chain.
	 */
	public final void setNext(final ChainElementForTag nextElement) {
		this.next = nextElement;
	}

	public final List<Tag> perform(String authUser, GroupingEntity grouping, String groupingName,String regex, int start, int end, final Transaction transaction) {
		if (this.canHandle( authUser,  grouping,  groupingName,regex, start, end)) {
			return this.handle(  authUser,  grouping,  groupingName,regex, start, end, transaction);
		} else {
			if (this.next != null) {
				return this.next.perform(authUser, grouping, groupingName,regex,start, end, transaction);
			}
		}
		// FIXME nobody can handle this -> throw an exception
		return null;
	}

	/**
	 * Handles the request.
	 */	
	protected abstract List<Tag> handle(String authUser, GroupingEntity grouping, String groupingName,String regex , int start, int end, Transaction transaction);

	/**
	 * Returns true if the request can be handled, otherwise false.
	 */
	protected abstract boolean canHandle(String authUser, GroupingEntity grouping, String groupingName,String regex, int start, int end);	
}