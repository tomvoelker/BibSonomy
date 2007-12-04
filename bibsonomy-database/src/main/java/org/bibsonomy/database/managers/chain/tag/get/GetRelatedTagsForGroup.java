package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

/**
 *
 * @version: $Id$
 * @author:  Stefan Stuetzer
 * $Author$
 *
 */
public class GetRelatedTagsForGroup extends TagChainElement {
	private static final Logger LOGGER = Logger.getLogger(GetRelatedTagsForGroup.class);

	@Override
	protected List<Tag> handle(TagParam param, DBSession session) {		
		return this.db.getRelatedTagsForGroup(param ,session);
	}
	
	@Override
	protected boolean canHandle(TagParam param) {
		return present(param.getRequestedGroupName()) && param.getGrouping() == GroupingEntity.GROUP && present(param.getTagIndex());
	}
}