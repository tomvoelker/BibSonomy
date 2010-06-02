package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsByExpression extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		return this.db.getTagsByExpression(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (present(param.getRegex()) &&
				present(param.getGrouping()) &&
				param.getGrouping() == GroupingEntity.USER &&
				!present(param.getBibtexKey()) &&
				present(param.getRequestedUserName()));
	}
}