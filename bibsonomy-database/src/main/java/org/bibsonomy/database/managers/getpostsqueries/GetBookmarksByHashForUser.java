package org.bibsonomy.database.managers.getpostsqueries;

import java.util.List;

import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.enums.GroupingEntity;

public class GetBookmarksByHashForUser extends RequestHandlerForGetPosts{



	@Override
	protected List<Post<? extends Resource>> handleRequestForGetPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		final BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		param.setHash(hash);
		
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
	    
		param.setGroups(db.generalDatabaseManager.getGroupsForUser(param));
		List<Post<? extends Resource>> posts = db.bookmarkDatabaseManager.bookmarkList("getBookmarkByHashForUser", param, true);
       System.out.println("post="+posts.size());
		return posts;
	}

	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return hash != null && hash.length() > 0 &&
			authUser != null && 
			grouping == GroupingEntity.USER && groupingName != null && 
			(tags==null || tags.size() == 0) && 
			popular == false && 
			added == false;
	}

}
