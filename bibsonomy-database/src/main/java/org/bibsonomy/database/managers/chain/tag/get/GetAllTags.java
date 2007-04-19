package org.bibsonomy.database.managers.chain.tag.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.model.Tag;


public class GetAllTags extends TagChainElement{

	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected List<Tag> handle(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	

	


}
