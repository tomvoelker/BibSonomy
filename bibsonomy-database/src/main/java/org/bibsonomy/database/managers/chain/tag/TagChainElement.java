package org.bibsonomy.database.managers.chain.tag;

import org.bibsonomy.database.managers.TagDatabaseManager;
import org.bibsonomy.database.managers.chain.ListChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;

/**
 * All elements for the chain of responsibility for tags are derived from this
 * class.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public abstract class TagChainElement extends ListChainElement<Tag, TagParam> {

	protected final TagDatabaseManager db;

	/**
	 * Constructs a chain element
	 */
	public TagChainElement() {
		this.db = TagDatabaseManager.getInstance();
	}
}