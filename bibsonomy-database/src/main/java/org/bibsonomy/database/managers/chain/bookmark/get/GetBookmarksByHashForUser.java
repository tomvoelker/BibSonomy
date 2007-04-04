package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBookmarksByHashForUser extends BookmarkChainElement{

	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bookmark by a given hash and a logged user.
	 * Following arguments have to be given:
	 * 
	 * grouping:user
	 * name:given
	 * tags:NULL
	 * hash:given
	 * popular:false
	 * added:false
	 *   
	 */

	@Override
	protected List<Post<? extends Resource>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		final BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		param.setHash(hash);
		
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		
		param.setGroups(generalDb.getGroupsForUser(param));
		
		/**
		 * retrieve bookmark list with appropriate iBatis statement
		 */
		List<Post<? extends Resource>> posts = db.getBookmarkByHashForUser(param);
		if(posts.size()!=0){
			System.out.println("GetBookmarksByHashForUser");
			
			
		}
		return posts;
	}
	
	/*
	 * prove arguments as mentioned above
	 */
	
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