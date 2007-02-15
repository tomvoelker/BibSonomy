
package org.bibsonomy.database.managers;

import java.util.List;
import java.util.Set;

import org.bibsonomy.database.managers.DatabaseManager;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.enums.GroupingEntity;
import org.bibsonomy.rest.enums.ResourceType;

/*******
* 
* @author mgr
*
**/

public abstract class RequestHandlerForGetPosts {

	private RequestHandlerForGetPosts next;
	protected final DatabaseManager db = new DatabaseManager();

	public void setNext(RequestHandlerForGetPosts nextHandler) {
		this.next = nextHandler;
	}

	public List<Post<? extends Resource>> perform(String authUser, ResourceType resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		if (this.canHandle(authUser, resourceType, grouping, groupingName, tags, hash, popular, added, start, end)) {
			return this.handleRequestForGetPosts(authUser, resourceType, grouping, groupingName, tags, hash, popular, added, start, end);
		} else {
			if (next != null) {
				return next.perform(authUser, resourceType, grouping, groupingName, tags, hash, popular, added, start, end);
			}
		}
		return null;
	}

	protected abstract List<Post<? extends Resource>> handleRequestForGetPosts(String authUser, ResourceType resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end);

	protected abstract boolean canHandle(String authUser, ResourceType resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end);

}
