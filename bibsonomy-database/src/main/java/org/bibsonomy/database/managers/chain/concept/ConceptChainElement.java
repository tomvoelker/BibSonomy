package org.bibsonomy.database.managers.chain.concept;

import java.util.List;

import org.bibsonomy.database.managers.TagRelationDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.model.Tag;

/**
 * @author Stefan Stützer
 * @version $Id$
 */
public abstract class ConceptChainElement extends ChainElement<List<Tag>, TagRelationParam> {

	protected final TagRelationDatabaseManager db;
	
	/**
	 * Constructs a chain element
	 */
	public ConceptChainElement() {
		this.db = TagRelationDatabaseManager.getInstance();
	}
}