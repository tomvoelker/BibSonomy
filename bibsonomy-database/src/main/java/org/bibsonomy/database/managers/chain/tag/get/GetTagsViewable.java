package org.bibsonomy.database.managers.chain.tag.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;

/**
 * 
 * @author mgr
 *
 */
public class GetTagsViewable extends TagChainElement{

	/*
	 * return a list of tags by a logged user.
	 * Following arguments have to be given:
	 * 
	 * grouping:viewable
	 * name:given
	 * regex: irrelevant  
	 */	
	@Override
	protected List<Tag> handle(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		final TagParam param = new TagParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);

		param.setGroupId(generalDb.getGroupIdByGroupName(param));
		param.setGroups(generalDb.getGroupsForUser(param));

		List<Tag> tags = db.getTagsViewable(param);
		if (tags.size() != 0) {
			System.out.println("GetTagsViewable");
		}
		return tags;
	}

	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		return authUser != null && grouping == GroupingEntity.VIEWABLE && groupingName != null;
	}
}