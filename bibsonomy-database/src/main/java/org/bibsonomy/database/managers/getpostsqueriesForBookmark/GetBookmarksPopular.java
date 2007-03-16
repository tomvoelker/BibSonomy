package org.bibsonomy.database.managers.getpostsqueriesForBookmark;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBookmarksPopular extends RequestHandlerForGetPosts{

	
	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bookmark by a  logged user.
	 * Following arguments have to be given:
	 * 
	 * grouping:irrelevant
	 * name:irrelevant
	 * tags:irrelevant
	 * hash:irrelevant
	 * popular:true
	 * added:false
	 *   
	 */
	
	
	@Override
	protected List<Post<? extends Resource>> handleRequestForGetPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {

		final BookmarkParam param = new BookmarkParam();
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		/**
		 * retrieve bookmark list with appropriate iBatis statement
		 */
		List<Post<? extends Resource>> posts = db.bookmarkDatabaseManager.bookmarkList("getBookmarkPopular", param, true);
        System.out.println("post="+posts.size()+"in getBookmarkPopular");
		return posts;
		
		
	}
	
	/*
	 * prove arguments as mentioned above
	 */
	
	
	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end){
		
		return popular==true &&
		added==false;
		
	}

	
	

}