package org.bibsonomy.database.managers.chain.bibtex;

import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.model.BibTex;

/**
 * All elements for the chain of responsibility for publications are derived
 * from this class.
 * 
 * @author mgr
 */
public abstract class BibTexChainElement extends ChainElement<BibTex> {

	protected final BibTexDatabaseManager db;

	public BibTexChainElement() {
		this.db = BibTexDatabaseManager.getInstance();
	}
}