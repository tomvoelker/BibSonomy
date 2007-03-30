package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/*
 * TODO check
 */
public class GetBibtexByFriends extends BibTexChainElement{

	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * 
	 * TODO extension with user restriction rearding returned bibtex and appropriate namming of URL in REST interface
	 * 
	 * grouping:friend
	 * name:given
	 * tags:NULL
	 * hash:NULL
	 * popular:false
	 * added:false
	 * /user/friend
	 *   
	 */
	@Override
	protected List<Post<? extends Resource>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
        
		final BibTexParam param = new BibTexParam();		
		param.setRequestedGroupName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		
		
		
		
		List<Post<? extends Resource>> posts = db.getBibTexByUserFriends(param);
		return posts;
	}

	@Override
	
	/*
	 * prove arguments as mentioned above
	 */
	/*
	 * TODO username: semantik fehlt in API
	 */
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && 
			grouping == GroupingEntity.FRIEND && groupingName != null && 
			(tags==null || tags.size() == 0) && 
			hash==null     &&
			popular == false && 
			added == false;
	}


}
