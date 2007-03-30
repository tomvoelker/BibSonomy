package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetBibtexPopular extends BibTexChainElement{

	
	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of bibtex by a  logged user.
	 * Following arguments have to be given:
	 * 
	 * grouping:irrelevant
	 * name:irrelevant
	 * tags:irrelevant
	 * hash:irrelevant
	 * popular:true
	 * added:false
	 *   
	 */
	
	
	@Override
	protected List<Post<? extends Resource>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {

		final BibTexParam param = new BibTexParam();
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		
		/**
		 * retrieve bibtex list with appropriate iBatis statement
		 */
		List<Post<? extends Resource>> posts = db.getBibTexPopular(param);
        System.out.println("post="+posts.size()+"in getBibtexPopular");
		return posts;
		
		
	}
	
	/*
	 * prove arguments as mentioned above
	 */
	
	
	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end){
		
		return popular==true &&
		added==false;
		
	}

	
	

}