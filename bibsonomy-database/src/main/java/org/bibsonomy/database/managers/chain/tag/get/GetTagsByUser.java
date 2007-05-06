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
public class GetTagsByUser extends TagChainElement {

	/*
	 * return a list of tags by a logged user.
	 * Following arguments have to be given:
	 * 
	 * grouping:user
	 * name:given
	 * regex: irrelevant  
	 */
	@Override
	protected List<Tag> handle(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end, final Transaction session) {
		final TagParam param = new TagParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);

		param.setGroups(generalDb.getGroupsForUser(param, session));
		List<Tag> tags = db.getTagsByUser(param, session);
		if (tags.size() != 0) {
			System.out.println("GetTagsByUser");
		}
		return tags;
	}

	/*
	 * prove arguments as mentioned above
	 */
	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		return authUser != null && grouping == GroupingEntity.USER && groupingName != null;
	}
}