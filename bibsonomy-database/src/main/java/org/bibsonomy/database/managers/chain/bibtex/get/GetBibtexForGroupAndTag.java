package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBibtexForGroupAndTag extends BibTexChainElement{

	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bibtex by a given group and common tags of a group.
	 * Following arguments have to be given:
	 * 
	 * grouping:group
	 * name:given
	 * tags:given
	 * hash:null
	 * popular:false
	 * added:false
	 *   
	 */
	@Override
	protected List<Post<? extends Resource>> handleRequestForGetPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
        
		final BibTexParam param =new BibTexParam();		
		param.setRequestedGroupName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		
			for (String tag : tags){
			
			param.addTagName(tag);
			
			}
		
	    param.setGroupId(generalDb.getGroupIdByGroupName(param));
		param.setGroups(generalDb.getGroupsForUser(param));
		
		List<Post<? extends Resource>> posts = db.getBibTexForGroupByTag(param);
		return posts;
	}
    
	
	/*
	 * prove arguments as mentioned above
	 */
	
	
	
	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && 
			grouping == GroupingEntity.GROUP && groupingName != null && 
			tags!=null && 
			hash==null     &&
			popular == false && 
			added == false;
	}


}