package org.bibsonomy.database.managers.getpostsqueriesForBibtex;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;


/*
 * TODO check
 */

public class GetBibtexOfFriendsByTags extends RequestHandlerForGetBibTexPosts{

	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bibtex by given friends of a user (this friends also posted this bookmarks to group friends, made bookmarks viewable for friends).
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
	 * at first all bibtex of user(which add me as friend) x are returned,  sencondly this list is restricted by those post which are posted to group friend, respectively are viewable for friends
	 * e.g.  mgr/friend/stumme  
	 * 
	 * 
	 * bibtex are listed which record me as friend and also posted this record to the group friend 
	 * 
	 * 
	
	 *   
	 */
	@Override
	protected List<Post<? extends Resource>> handleRequestForGetPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		final BibTexParam param =new BibTexParam();
		
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		param.setGroupId(ConstantID.GROUP_FRIENDS.getId());
		
		for (String tag : tags){
			
			param.addTagName(tag);
			
			}
			
		
		
		List<Post<? extends Resource>> posts = db.getBibTexForUser(param);
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
