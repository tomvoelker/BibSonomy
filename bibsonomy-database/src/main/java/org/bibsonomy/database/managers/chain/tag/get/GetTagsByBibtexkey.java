package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;

/**
 * Retrieve tags by bibtexkey
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetTagsByBibtexkey extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		return this.db.getTagsByBibtexkey(param.getBibtexKey(), 
										  param.getGroups(), 
										  param.getRequestedUserName(),
										  param.getUserName(),
										  param.getLimit(), 
										  param.getOffset(),
										  session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return ( (GroupingEntity.ALL.equals(param.getGrouping()) || GroupingEntity.USER.equals(param.getGrouping()) ) &&
				 !present(param.getTagIndex()) && 
				 present(param.getBibtexKey()));
	}
}