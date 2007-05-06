package org.bibsonomy.database.managers.chain.tag;

import org.bibsonomy.database.managers.TagDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElementForTag;

/**
 * All elements for the chain of responsibility for publications are derived
 * from this class.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public abstract class TagChainElement extends ChainElementForTag {

	protected final TagDatabaseManager db;

	public TagChainElement() {
		this.db = TagDatabaseManager.getInstance();
	}
}