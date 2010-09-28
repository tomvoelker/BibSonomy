package org.bibsonomy.database.managers.chain.concept;

import org.bibsonomy.database.managers.TagRelationDatabaseManager;
import org.bibsonomy.database.managers.chain.ListChainElement;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.model.Tag;

/**
 * @author Stefan Stützer
 * @version $Id$
 */
public abstract class ConceptChainElement extends ListChainElement<Tag, TagRelationParam> {

	protected final TagRelationDatabaseManager db;
	
	/**
	 * Constructs a chain element
	 */
	public ConceptChainElement() {
		this.db = TagRelationDatabaseManager.getInstance();
	}
}