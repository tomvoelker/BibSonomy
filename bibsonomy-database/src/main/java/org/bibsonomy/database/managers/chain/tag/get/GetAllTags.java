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
public class GetAllTags extends TagChainElement {

	@Override
	protected List<Tag> handle(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		final TagParam param = new TagParam();
		final List<Tag> tags = db.getAllTags(param);
		if (tags.size() != 0) {
			System.out.println("GetAllTags");
		}
		return tags;
	}

	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		return true;
	}
}