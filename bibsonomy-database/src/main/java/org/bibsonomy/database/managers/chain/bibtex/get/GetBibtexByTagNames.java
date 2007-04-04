package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBibtexByTagNames extends BibTexChainElement{
	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bibtex by given tag/tags.
	 * Following arguments have to be given:
	 * 
	 * grouping:all
	 * name:irrelevant
	 * tags:given
	 * hash:null
	 * popular:false
	 * added:false
	 *   
	 */
	
	@Override
	protected List<Post<? extends Resource>> handle(String authUser,  GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		
        final BibTexParam param =new BibTexParam();		
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		
		for (String tag : tags){
			
		param.addTagName(tag);
		
		}
		
		
/*		
 * 
 * 
 * 
 *      TODO    implement compartible method for concept structure
 */
		/*
		 * prove arguments as mentioned above
		 */
		
		List<Post<? extends Resource>> posts = db.getBibTexByTagNames(param);
		if(posts.size()!=0){
			System.out.println("GetBibtexByTagNames");
			
			
		}
		return posts;

	}
	

	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return
		grouping == GroupingEntity.ALL &&
		tags!=null  &&
		hash==null  &&
		popular == false && 
		added == false;
		
		
	}	


}

