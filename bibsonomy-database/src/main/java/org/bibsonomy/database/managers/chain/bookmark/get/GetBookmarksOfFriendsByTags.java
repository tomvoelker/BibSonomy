package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;


/*
 * TODO check
 */

public class GetBookmarksOfFriendsByTags extends BookmarkChainElement{

	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bookmark by given friends of a user (this friends also posted this bookmarks to group friends, made bookmarks viewable for friends).
	 * Following arguments have to be given:
	 * 
	 *  * grouping:friend
	 * name:given
	 * tags:given
	 * hash:NULL
	 * popular:false
	 * added:false
	 * 
	 * /user/friend
	 * at first all bookmarks of user(which add me as friend) x are returned,  sencondly this list is restricted by those post which are posted to group friend, respectively are viewable for friends
	 * e.g.  mgr/friend/stumme  
	 * 
	 * 
	 * bookmarks are listed which record me as friend and also posted this record to the group friend 
	 * 
	 * 
	
	 *   
	 */
	@Override
	protected List<Post<? extends Resource>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		final BookmarkParam param = new BookmarkParam();
		
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		param.setGroupId(ConstantID.GROUP_FRIENDS.getId());
		
		for (String tag : tags){
			
			param.addTagName(tag);
			
			}
			
		
		
		List<Post<? extends Resource>> posts = db.getBookmarkForUser(param);
		System.err.println("posts"+posts);
		return posts;
	}

	@Override
	
	/*
	 * prove arguments as mentioned above
	 */
	
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && 
			grouping == GroupingEntity.FRIEND && groupingName != null && 
			tags!=null && 
			hash==null &&
			popular == false && 
			added == false;
	}


}
