package org.bibsonomy.database.managers.getpostsqueriesForBookmark;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBookmarksByTagNames extends RequestHandlerForGetPosts{
	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bookmark by given tag/tags.
	 * Following arguments have to be given:
	 * 
	 * grouping:all
	 * name:irrelevant
	 * tags:given
	 * hash:null
	 * popular:false
	 * added:false
	 *   
	 */
	
	@Override
	protected List<Post<? extends Resource>> handleRequestForGetPosts(String authUser,  GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		
		final BookmarkParam param = new BookmarkParam();
		
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		
		for (String tag : tags){
			
		param.addTagName(tag);
		
		}
		
		
/*		
 * 
 * 
 * 
 *      TODO    implement compartible method for concept structure
 */
		/*
		 * prove arguments as mentioned above
		 */
		
		List<Post<? extends Resource>> posts = db.bookmarkDatabaseManager.bookmarkList("getBookmarkByTagNames", param, true);
		return posts;

	}
	

	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return
		grouping == GroupingEntity.ALL &&
		tags!=null  &&
		hash==null  &&
		popular == false && 
		added == false;
		
		
	}	


}

