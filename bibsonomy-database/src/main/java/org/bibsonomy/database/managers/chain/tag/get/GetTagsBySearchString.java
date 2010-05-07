package org.bibsonomy.database.managers.chain.tag.get;

import java.util.List;

import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsBySearchString extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		return null; //this.db.getTagsBySearchString(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return false;
	}
}