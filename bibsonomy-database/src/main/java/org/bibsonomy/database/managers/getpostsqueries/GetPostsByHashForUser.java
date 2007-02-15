package org.bibsonomy.database.managers.getpostsqueries;

import java.util.List;
import java.util.Set;

import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.enums.GroupingEntity;
import org.bibsonomy.rest.enums.ResourceType;

public class GetPostsByHashForUser extends RequestHandlerForGetPosts{

	

	@Override
	protected List<Post<Resource>> handleRequestForGetPosts(String authUser, ResourceType resourceType, GroupingEntity grouping, String groupingName, Set<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		final BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		param.setHash(hash);
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		
		List<Post<Resource>> posts = db.bookmarkDatabaseManager.bookmarkList("getPostsByHashForUser", param, true);
		return null;
	}

	@Override
	protected boolean canHandle(String authUser, ResourceType resourceType, GroupingEntity grouping, String groupingName, Set<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		
		if (hash != null && hash.length() > 0 && authUser!=null && grouping==GroupingEntity.USER && groupingName!=null) {
			return true;
		}
		return false;

}
}
