package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBookmarksForHomePage extends BookmarkChainElement{

	
	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bookmark by a  logged user.
	 * Following arguments have to be given:
	 * 
	 * grouping:null
	 * name:irrelevant
	 * tags:irrelevant
	 * hash:irrelevant
	 * popular:false
	 * added:true
	 *   
	 */
	
	
	@Override
	protected List<Post<? extends Resource>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {

		final BookmarkParam param = new BookmarkParam();
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		/**
		 * retrieve bookmark list with appropriate iBatis statement
		 */
		List<Post<? extends Resource>> posts = db.getBookmarkForHomepage(param);
        System.out.println("post="+posts.size()+"in getBookmarkForHomepage");
		return posts;
		
		
	}
	
	/*
	 * prove arguments as mentioned above
	 */
	
	
	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end){
		
		return 
		grouping==null&&
		popular==false &&
		added==false;
		
	}

	
	

}