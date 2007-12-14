package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsViewable extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {		
		final Integer groupId;
		try {
			final GroupID specialGroup = GroupID.getSpecialGroup(param.getRequestedGroupName());
			if (specialGroup != null) {
				groupId = specialGroup.getId();
			} else {
				groupId = this.generalDb.getGroupIdByGroupNameAndUserName(param, session);
			}
			if (groupId == null) {
				log.debug("groupId not found");
				return new ArrayList<Tag>(0);
			}
		}
		catch (IllegalArgumentException e) {
			log.debug("groupId not found");
			return new ArrayList<Tag>(0);
		}
		
		log.debug("groupId=" + groupId);
		param.setGroupId(groupId);
		
		// TODO: is this needed?  param.setGroups(this.generalDb.getGroupsForUser(param, session));
		// param.setGroupId(this.generalDb.getGroupIdByGroupNameAndUserName(param, session));
		return this.db.getTagsViewable(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return present(param.getUserName()) && param.getGrouping() == GroupingEntity.VIEWABLE && present(param.getRequestedGroupName())&& !present(param.getSearch()) && !present(param.getRegex());
	}
}