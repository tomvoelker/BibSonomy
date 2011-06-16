package org.bibsonomy.database.managers.chain.discussion;

import org.bibsonomy.database.managers.chain.ListChainElement;
import org.bibsonomy.database.managers.discussion.DiscussionDatabaseManager;
import org.bibsonomy.database.params.discussion.DiscussionItemParam;
import org.bibsonomy.model.DiscussionItem;

/**
 * @author dzo
 * @version $Id$
 */
public abstract class DiscussionChainElement extends ListChainElement<DiscussionItem, DiscussionItemParam<?>> {
	
	protected final DiscussionDatabaseManager discussionDatabaseManager;
	
	/**
	 * constructs a new discussion chain element
	 */
	public DiscussionChainElement() {
		this.discussionDatabaseManager = DiscussionDatabaseManager.getInstance();
	}

}
