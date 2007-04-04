package org.bibsonomy.database.managers;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.ChainPerform;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChain;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChain;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * This class is the access point to all chains. They can be configured here and
 * can be used by calling the perform method.
 * 
 * @author Christian Schenk
 */
public class GenericChainHandler implements ChainPerform {

	/** Singleton */
	private final static GenericChainHandler singleton = new GenericChainHandler();
	/** List of all chains */
	private final List<FirstChainElement> chains;

	private GenericChainHandler() {
		this.chains = new ArrayList<FirstChainElement>();
		//this.chains.add(new BookmarkChain());
		this.chains.add(new BibTexChain());
	}

	public static GenericChainHandler getInstance() {
		return singleton;
	}

	public List<Post<? extends Resource>> perform(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		for (final FirstChainElement chain : this.chains) {
			final List<Post<? extends Resource>> list = chain.getFirstElement().perform(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
			// FIXME don't know whether this works
			if (list != null) return list;
		}
		// FIXME throw exception
		return null;
	}
}