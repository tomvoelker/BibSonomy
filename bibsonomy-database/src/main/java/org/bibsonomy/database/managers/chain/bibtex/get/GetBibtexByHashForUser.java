package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

public class GetBibtexByHashForUser extends BibTexChainElement{

	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bibtex by a given hash and a logged user.
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
	protected List<Post<BibTex>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
        
		final BibTexParam param =new BibTexParam();
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
		List<Post<BibTex>> posts = db.getBibTexByHashForUser(param);
		if(posts.size()!=0){
			System.out.println("GetBibtexByHashForUser");
			
			
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