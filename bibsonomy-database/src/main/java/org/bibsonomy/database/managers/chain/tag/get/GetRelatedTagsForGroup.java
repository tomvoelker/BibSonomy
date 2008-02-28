package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

/**
 * @author Stefan Stuetzer
 * @version $Id$
 */
public class GetRelatedTagsForGroup extends TagChainElement {

	@Override
	protected List<Tag> handle(TagParam param, DBSession session) {		
		return this.db.getRelatedTagsForGroup(param ,session);
	}
	
	@Override
	protected boolean canHandle(TagParam param) {
		return present(param.getRequestedGroupName()) && 
		       param.getGrouping() == GroupingEntity.GROUP && 
		       present(param.getTagIndex());
	}
}