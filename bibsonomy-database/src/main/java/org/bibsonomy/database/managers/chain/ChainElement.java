package org.bibsonomy.database.managers.chain;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Represents one element in the chain of responsibility.
 * 
 * @author mgr
 */
public abstract class ChainElement implements ChainPerform {

	protected final GeneralDatabaseManager generalDb;
	
	/** The next element of the chain */
	private ChainElement next;

	public ChainElement() {
		this.generalDb = GeneralDatabaseManager.getInstance();
	}

	/**
	 * Sets the next element in the chain.
	 */
	public final void setNext(final ChainElement nextElement) {
		this.next = nextElement;
	}

	public final List<Post<? extends Resource>> perform(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		if (this.canHandle(authUser, grouping, groupingName, tags, hash, popular, added, start, end)) {
			return this.handle(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
		} else {
			if (this.next != null) {
				return this.next.perform(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
			}
		}
		// FIXME nobody can handle this -> throw an exception
		return null;
	}

	/**
	 * Handles the request.
	 */
	protected abstract List<Post<? extends Resource>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end);

	/**
	 * Returns true if the request can be handled, otherwise false.
	 */
	protected abstract boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end);
}