package org.bibsonomy.database.managers.chain.bibtex;

import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;

/**
 * All elements for the chain of responsibility for publications are derived
 * from this class.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public abstract class BibTexChainElement extends ResourceChainElement<BibTex, BibTexParam> {

	protected final BibTexDatabaseManager db;

	/**
	 * Constructs a chain element
	 */
	public BibTexChainElement() {
		this.db = BibTexDatabaseManager.getInstance();
	}
}