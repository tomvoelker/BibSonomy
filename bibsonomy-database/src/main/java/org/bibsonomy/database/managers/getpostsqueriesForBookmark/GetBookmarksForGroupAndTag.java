package org.bibsonomy.database.managers.getpostsqueriesForBookmark;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBookmarksForGroupAndTag extends RequestHandlerForGetBookmarkPosts{

	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bookmark by a given group and common tags of a group.
	 * Following arguments have to be given:
	 * 
	 * grouping:group
	 * name:given
	 * tags:given
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
		
			for (String tag : tags){
			
			param.addTagName(tag);
			
			}
		
	    param.setGroupId(gdb.getGroupIdByGroupName(param));
		param.setGroups(gdb.getGroupsForUser(param));
		
		List<Post<? extends Resource>> posts = db.getBookmarkForGroupByTag(param);
		return posts;
	}
    
	
	/*
	 * prove arguments as mentioned above
	 */
	
	
	
	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && 
			grouping == GroupingEntity.GROUP && groupingName != null && 
			tags!=null && 
			hash==null     &&
			popular == false && 
			added == false;
	}


}