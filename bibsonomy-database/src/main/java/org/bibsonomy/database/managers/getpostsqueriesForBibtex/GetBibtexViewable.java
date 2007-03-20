package org.bibsonomy.database.managers.getpostsqueriesForBibtex;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBibtexViewable extends RequestHandlerForGetBibTexPosts{

	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bibtex by a given group (which is only viewable for groupmembers excluded public option regarding setting a post).
	 * Following arguments have to be given:
	 * 
	 * grouping:viewable
	 * name:given
	 * tags:NULL
	 * hash:NULL
	 * popular:falses
	 * added:false
	 *   
	 */
	@Override
	protected List<Post<? extends Resource>> handleRequestForGetPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		final BibTexParam param = new BibTexParam();
		
		param.setRequestedGroupName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		
		
		
		param.setGroupId(gdb.getGroupIdByGroupName(param));
		param.setGroups(gdb.getGroupsForUser(param));
		
		List<Post<? extends Resource>> posts = db.getBibTexViewable(param);
		return posts;
	}
    
	/*
	 * prove arguments as mentioned above
	 */
	
	
	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && 
			grouping == GroupingEntity.VIEWABLE && groupingName != null && 
			(tags==null || tags.size() == 0) && 
			hash==null     &&
			popular == false && 
			added == false;
	}


}