package org.bibsonomy.database.managers.chain.tag.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.Tag;

public class GetTagsByExpression extends TagChainElement{
	/**
	 * 
	 * @author mgr
	 *
	 */

	/*
	 * return a list of tags by a logged user.
	 * Following arguments have to be given:
	 * 
	 * grouping:irrelevant
	 * name:irrelevant
	 * regex: given  
	 */
	@Override
	protected List<Tag> handle(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		final UserParam param = new UserParam();
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		List<Tag> tags = db.getTagsByExpression(param);
		if(tags.size()!=0){
			System.out.println("GetTagsByExpression");
		}
		return tags;
	}
	
	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		return regex != null; 
	
	}

	

}
