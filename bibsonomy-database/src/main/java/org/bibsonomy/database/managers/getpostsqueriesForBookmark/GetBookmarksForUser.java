package org.bibsonomy.database.managers.getpostsqueriesForBookmark;

import java.util.List;

import org.bibsonomy.database.managers.getpostsqueries.RequestHandlerForGetPosts;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.enums.GroupingEntity;

public class GetBookmarksForUser extends RequestHandlerForGetPosts{
	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bookmark by a  logged user.
	 * Following arguments have to be given:
	 * 
	 * grouping:user
	 * name:given
	 * tags:NULL
	 * hash:NULL
	 * popular:false
	 * added:false
	 *   
	 */
	@Override
	protected List<Post<? extends Resource>> handleRequestForGetPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		final BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
	    
		param.setGroups(db.generalDatabaseManager.getGroupsForUser(param));
		List<Post<? extends Resource>> posts = db.bookmarkDatabaseManager.bookmarkList("getBookmarkForUser", param, true);
		return posts;
	}

	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && 
			grouping == GroupingEntity.USER && groupingName != null && 
			(tags==null || tags.size() == 0) && 
			hash ==null &&
			popular == false && 
			added == false;
	}

}
