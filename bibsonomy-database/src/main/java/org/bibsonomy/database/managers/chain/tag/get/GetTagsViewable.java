package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
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
		final String requestedGroupName = param.getRequestedGroupName();
		final String loginUserName = param.getUserName();
		/*
		 * For the special groups public, private, and friends, we must only 
		 * retrieve posts for this user name. Normally, this is the loginUserName!
		 */
		final String requestedUserName = param.getRequestedUserName();
		// retrieve ID of the requested group
		final Integer groupId = this.groupDb.getGroupIdByGroupNameAndUserName(requestedGroupName, loginUserName, session);
		if (groupId == GroupID.INVALID.getId()) {
			log.debug("groupId " + requestedGroupName + " not found");
			return Collections.emptyList();
		}
		if (present(param.getTagIndex())) {
			return this.db.getRelatedTagsViewable(param.getContentTypeConstant(), loginUserName, groupId, requestedUserName, param.getTagIndex(), param.getLimit(), param.getOffset(), session);
		}
		return this.db.getTagsViewable(param.getContentTypeConstant(), loginUserName, groupId, requestedUserName, param.getLimit(), param.getOffset(), session);

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