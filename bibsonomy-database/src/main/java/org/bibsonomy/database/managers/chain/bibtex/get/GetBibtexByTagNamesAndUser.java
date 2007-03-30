package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBibtexByTagNamesAndUser extends BibTexChainElement{
	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bibtex by given tag/tags and User.
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
	protected List<Post<? extends Resource>> handle(String authUser,  GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		
        final BibTexParam param= new BibTexParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		param.setGroups(generalDb.getGroupsForUser(param));
		
		for (String tag : tags){
			
			param.addTagName(tag);
			
			}
		
		
		List<Post<? extends Resource>> posts = db.getBibTexByTagNamesForUser(param);
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


