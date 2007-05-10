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
public class GetAllTags extends TagChainElement {

	@Override
	protected List<Tag> handle(String authUser, GroupingEntity grouping, String groupingName, String regex, Boolean subTags, Boolean superTags, Boolean subSuperTagsTransitive, int start, int end, final Transaction session) {
		final TagParam param = new TagParam();
		final List<Tag> tags = db.getAllTags(param, session);
		if (tags.size() != 0) {
			System.out.println("GetAllTags");
		}
		return tags;
	}

	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, String regex, Boolean subTags, Boolean superTags, Boolean subSuperTagsTransitive, int start, int end, final Transaction session) {
		return true;
	}
}