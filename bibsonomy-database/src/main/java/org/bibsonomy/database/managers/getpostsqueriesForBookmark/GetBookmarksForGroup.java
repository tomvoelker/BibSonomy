package org.bibsonomy.database.managers.getpostsqueriesForBookmark;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBookmarksForGroup extends RequestHandlerForGetPosts{

	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bookmark by a given group.
	 * Following arguments have to be given:
	 * 
	 * grouping:group
	 * name:given
	 * tags:NULL
	 * hash:null
	 * popular:false
	 * added:false
	 *   
	 */
	@Override
	protected List<Post<? extends Resource>> handleRequestForGetPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		final BookmarkParam param = new BookmarkParam();
		
		param.setRequestedGroupName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		
	    param.setGroupId(db.generalDatabaseManager.getGroupIdByGroupName(param));
		param.setGroups(db.generalDatabaseManager.getGroupsForUser(param));
		
		List<Post<? extends Resource>> posts = db.bookmarkDatabaseManager.bookmarkList("getBookmarkForGroup", param, true);
		return posts;
	}

	/*
	 * prove arguments as mentioned above
	 */
	
	
	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && 
			grouping == GroupingEntity.GROUP && groupingName != null && 
			(tags==null || tags.size() == 0) && 
			hash==null     &&
			popular == false && 
			added == false;
	}


}