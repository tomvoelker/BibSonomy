package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags for a given user.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsByUser extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		if (param.getTagIndex() != null && param.getTagIndex().size() > 0) {
			// retrieve related tags
			return this.db.getRelatedTagsForUser(param.getUserName(),
							param.getRequestedUserName(), 
							param.getTagIndex(),
							param.getGroups(),
							param.getLimit(),
							param.getOffset(),
							session);
		}
		// retrieve all tags from user
		return this.db.getTagsByUser(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (param.getGrouping() == GroupingEntity.USER && 
				present(param.getRequestedUserName()) &&
				!present(param.getRegex()) &&
				!present(param.getBibtexKey()) &&
				!present(param.getHash()) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()) 
				);
	}
}