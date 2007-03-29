package org.bibsonomy.database.managers.chain;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author mgr
 */
public abstract class ChainElement implements ChainPerform {

	/** Singleton */
	protected final GeneralDatabaseManager generalDb;
	/** The next element of the chain */
	private ChainElement next;

	public ChainElement() {
		this.generalDb = GeneralDatabaseManager.getInstance();
	}

	public void setNext(final ChainElement nextElement) {
		this.next = nextElement;
	}

	public List<Post<? extends Resource>> perform(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		if (this.canHandle(authUser, grouping, groupingName, tags, hash, popular, added, start, end)) {
			return this.handleRequestForGetPosts(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
		} else {
			if (this.next != null) {
				return this.next.perform(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
			}
		}
		// FIXME nobody can handle this -> throw an exception
		return null;
	}

	protected abstract List<Post<? extends Resource>> handleRequestForGetPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end);

	protected abstract boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end);
}