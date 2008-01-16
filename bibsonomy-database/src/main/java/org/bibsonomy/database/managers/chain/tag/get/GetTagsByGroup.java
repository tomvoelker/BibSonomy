package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsByGroup extends TagChainElement {

	/**
	 * return a list of tags by a given group
	 */
	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		final Integer groupId = this.generalDb.getGroupIdByGroupName(param, session);
		if (groupId == GroupID.INVALID.getId()  || GroupID.isSpecialGroupId(groupId)) {
			log.debug("groupId " +  param.getRequestedGroupName() + " not found or special group" );
			return new ArrayList<Tag>(0);			
		}
		param.setGroupId(groupId);
		return this.db.getTagsByGroup(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (param.getGrouping() == GroupingEntity.GROUP) && present(param.getRequestedGroupName()) && !present(param.getTagIndex()) && !present(param.getRegex());
	}
}