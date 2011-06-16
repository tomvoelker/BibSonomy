package org.bibsonomy.database.managers.chain.discussion;

import java.util.List;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstListChainElement;
import org.bibsonomy.database.managers.chain.ListChainElement;
import org.bibsonomy.database.managers.chain.discussion.get.GetDiscussionSpaceByHash;
import org.bibsonomy.database.params.discussion.DiscussionItemParam;
import org.bibsonomy.model.DiscussionItem;

/**
 * @author dzo
 * @version $Id$
 */
public class DiscussionChain implements FirstListChainElement<DiscussionItem, DiscussionItemParam<?>> {

	private final ListChainElement<DiscussionItem, DiscussionItemParam<?>> getCommentsByHash;
	
	/**
	 * constructs a new discussion chain
	 */
	public DiscussionChain() {
		this.getCommentsByHash = new GetDiscussionSpaceByHash();
	}
	
	@Override
	public ChainElement<List<DiscussionItem>, DiscussionItemParam<?>> getFirstElement() {
		return this.getCommentsByHash;
	}

}
