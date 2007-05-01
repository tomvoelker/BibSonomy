package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

public class GetBibtexByConceptForUser extends BibTexChainElement{

	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bibtex by a tag-concept. All bookmarks will be return for a given "super-tag".
	 * Following arguments have to be given:
	 * 
	 * grouping:user
	 * name:given
	 * tags:given
	 * hash:null
	 * popular:false
	 * added:true
	 *   
	 */
	@Override
	protected List<Post<BibTex>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
        final BibTexParam param =new BibTexParam();		
		param.setRequestedGroupName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		
		param.setGroups(generalDb.getGroupsForUser(param));
		
		for (String tag : tags){
			
			param.addTagName(tag);
			
			}
		
		List<Post<BibTex>> posts = db.getBibTexByConceptForUser(param);
		if(posts.size()!=0){
			System.out.println("GetBibtexByConceptForUser");
			
			
		}
		return posts;
	}

	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && 
			grouping == GroupingEntity.USER && groupingName != null && 
			tags!=null && 
			hash==null &&
			popular == false && 
			added == true;
	}


}
