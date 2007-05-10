package org.bibsonomy.database.managers.chain.tag.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.Transaction;
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
	protected List<Tag> handle(String authUser, GroupingEntity grouping, String groupingName, String regex, Boolean subTags, Boolean superTags, Boolean subSuperTagsTransitive, int start, int end, final Transaction session) {
		final TagParam param = new TagParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);

		param.setGroupId(generalDb.getGroupIdByGroupName(param, session));
		param.setGroups(generalDb.getGroupsForUser(param, session));

		List<Tag> tags = db.getTagsViewable(param, session);
		if (tags.size() != 0) {
			System.out.println("GetTagsViewable");
		}
		return tags;
	}

	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, String regex, Boolean subTags, Boolean superTags, Boolean subSuperTagsTransitive, int start, int end, final Transaction session) {
		return authUser != null && grouping == GroupingEntity.VIEWABLE && groupingName != null;
	}
}