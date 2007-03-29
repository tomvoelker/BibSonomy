package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBookmarksByTagNamesAndUser extends BookmarkChainElement{
	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bookmark by given tag/tags and User.
	 * Following arguments have to be given:
	 * 
	 * grouping:User
	 * name:given
	 * tags:given
	 * hash:null
	 * popular:false
	 * added:false
	 *   
	 */
	@Override
	protected List<Post<? extends Resource>> handleRequestForGetPosts(String authUser,  GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		
		final BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		param.setGroups(generalDb.getGroupsForUser(param));
		
		for (String tag : tags){
			
			param.addTagName(tag);
			
			}
		
		
		List<Post<? extends Resource>> posts = db.getBookmarkByTagNamesForUser(param);
		return posts;

	}
	
	/*
	 * prove arguments as mentioned above
	 */
	
	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		
		return
		authUser != null && 
		grouping==GroupingEntity.USER &&
		tags!=null && 
		hash==null &&
		popular==false &&
		added==false;
		
		
		
	}	


}


