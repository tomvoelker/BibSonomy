package org.bibsonomy.database.managers.chain.tag.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.Tag;


public class GetAllTags extends TagChainElement{

	
	@Override
	protected List<Tag> handle(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		UserParam param=new UserParam();
		List<Tag> tags = db.getAllTags(param);
		if(tags.size()!=0){
			System.out.println("GetAllTags");
		}
		return tags;
	}
	
	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		return true;
	}

}
