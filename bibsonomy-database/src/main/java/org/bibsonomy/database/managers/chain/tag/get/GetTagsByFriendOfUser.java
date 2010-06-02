package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags for a given friend of a given user.
 * 
 * @author Steffen Kress
 * @version $Id$
 */
public class GetTagsByFriendOfUser extends TagChainElement {
	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		if (present(param.getTagIndex())) {
			// retrieve related tags
			return this.db.getRelatedTagsForUser(param.getUserName(), 
												 param.getRequestedUserName(), 
												 param.getTagIndex(), 
												 param.getGroups(),
												 param.getLimit(),
												 param.getOffset(),
												 session);
		}
		// retrieve all tags from friend that are visible to his friends
		return this.db.getTagsByFriendOfUser(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (param.getGrouping() == GroupingEntity.FRIEND && 
				present(param.getRequestedUserName()) && 
				!present(param.getRegex()) &&
				!present(param.getBibtexKey()) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()) &&
				!present(param.getHash()));
	}
}