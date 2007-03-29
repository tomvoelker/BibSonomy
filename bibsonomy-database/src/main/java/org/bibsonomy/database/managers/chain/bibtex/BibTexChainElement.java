package org.bibsonomy.database.managers.chain.bibtex;

import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;

/**
 * @author mgr
 */
public abstract class BibTexChainElement extends ChainElement {
	protected final BibTexDatabaseManager db = BibTexDatabaseManager.getInstance();
}