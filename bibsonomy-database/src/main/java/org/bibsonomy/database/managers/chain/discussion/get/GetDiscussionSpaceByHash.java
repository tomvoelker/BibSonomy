package org.bibsonomy.database.managers.chain.discussion.get;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.discussion.DiscussionChainElement;
import org.bibsonomy.database.params.discussion.DiscussionItemParam;
import org.bibsonomy.model.DiscussionItem;

/**
 * @author dzo
 * @version $Id$
 */
public class GetDiscussionSpaceByHash extends DiscussionChainElement {

	@Override
	protected List<DiscussionItem> handle(final DiscussionItemParam<?> param, final DBSession session) {
		return this.discussionDatabaseManager.getDiscussionSpaceForResource(param.getInterHash(), param.getUserName(), param.getGroups(), session);
	}

	@Override
	protected boolean canHandle(final DiscussionItemParam<?> param) {
		return true; // currently only one param
	}

}
