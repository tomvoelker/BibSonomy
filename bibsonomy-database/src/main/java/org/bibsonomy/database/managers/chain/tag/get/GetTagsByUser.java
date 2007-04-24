package org.bibsonomy.database.managers.chain.tag.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.Tag;

public class GetTagsByUser extends TagChainElement {
	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of tags by a logged user.
	 * Following arguments have to be given:
	 * 
	 * grouping:user
	 * name:given
	 * regex: irrelevant  
	 */
	
	@Override
	protected List<Tag> handle(String authUser, GroupingEntity grouping, String groupingName,String regex,int start, int end) {
		
		final UserParam param =new UserParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
	    
		param.setGroups(generalDb.getGroupsForUserTag(param));
		List<Tag> tags = db.getTagsByUser(param);
		if(tags.size()!=0){
			System.out.println("GetTagsByUser");
		}
		return tags;
	}
    
	/*
	 * prove arguments as mentioned above
	 */
	
	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping,String groupingName,String regex,int start,int end) {
		return authUser != null && 
			grouping == GroupingEntity.USER && groupingName != null;			
	}
}
