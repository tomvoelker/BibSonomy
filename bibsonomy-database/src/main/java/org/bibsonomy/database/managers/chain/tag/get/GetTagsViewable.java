package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsViewable extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		final Integer groupId = this.groupDb.getGroupIdByGroupNameAndUserName(param.getRequestedGroupName(), param.getUserName(), session);
		if (groupId == GroupID.INVALID.getId()) {
			log.debug("groupId " + param.getRequestedGroupName() + " not found");
			return new ArrayList<Tag>(0);
		}
		param.setGroupId(groupId);
		if (present(param.getTagIndex()) == true) return this.db.getRelatedTagsViewable(param, session);
		return this.db.getTagsViewable(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (present(param.getUserName()) &&
				param.getGrouping() == GroupingEntity.VIEWABLE &&
				present(param.getRequestedGroupName()) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()) &&
				!present(param.getBibtexKey()) &&
				!present(param.getRegex()));
	}
}