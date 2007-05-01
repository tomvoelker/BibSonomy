package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;


/*
 * TODO check
 */

public class GetBibtexOfFriendsByTags extends BibTexChainElement{

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
	protected List<Post<BibTex>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
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
			
		
		
		List<Post<BibTex>> posts = db.getBibTexForUser(param);
		System.err.println("posts"+posts);
		if(posts.size()!=0){
			System.out.println("GetBibtexOfFriendsByTags");
		}
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
