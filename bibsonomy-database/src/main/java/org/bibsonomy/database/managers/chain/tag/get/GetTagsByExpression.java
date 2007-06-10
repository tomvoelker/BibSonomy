package org.bibsonomy.database.managers.chain.tag.get;

import java.util.List;

import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsByExpression extends TagChainElement {

	/**
	 * return a list of tags by a logged user. Following arguments have to be
	 * given:
	 * 
	 * grouping:irrelevant name:irrelevant regex: given
	 */
	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		return this.db.getTagsByExpression(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return present(param.getRegex());
	}
}