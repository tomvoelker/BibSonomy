package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author mgr
 */
public abstract class RequestHandlerForGetPosts {

	protected final GeneralDatabaseManager gdb = GeneralDatabaseManager.getInstance();

	private RequestHandlerForGetPosts next;

	public void setNext(RequestHandlerForGetPosts nextHandler) {
		this.next = nextHandler;
	}

	public List<Post<? extends Resource>> perform(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		if (this.canHandle(authUser, grouping, groupingName, tags, hash, popular, added, start, end)) {
			return this.handleRequestForGetPosts(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
		} else {
			if (next != null) {
				return next.perform(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
			}
		}
		return null;
	}

	protected abstract List<Post<? extends Resource>> handleRequestForGetPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end);

	protected abstract boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end);
}