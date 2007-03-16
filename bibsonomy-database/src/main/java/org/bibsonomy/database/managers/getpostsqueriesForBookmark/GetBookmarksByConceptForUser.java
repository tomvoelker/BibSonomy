package org.bibsonomy.database.managers.getpostsqueriesForBookmark;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBookmarksByConceptForUser extends RequestHandlerForGetPosts{

	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bookmarks by a tag-concept. All bookmarks will be return for a given "super-tag".
	 * Following arguments have to be given:
	 * 
	 * grouping:user
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
		
		param.setGroups(db.generalDatabaseManager.getGroupsForUser(param));
		
		for (String tag : tags){
			
			param.addTagName(tag);
			
			}
		
		List<Post<? extends Resource>> posts = db.bookmarkDatabaseManager.bookmarkList("getBookmarkByConceptForUser", param, true);
		return posts;
	}

	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && 
			grouping == GroupingEntity.USER && groupingName != null && 
			tags!=null && 
			hash==null &&
			popular == false && 
			added == false;
	}


}
