package org.bibsonomy.database.managers.chain.bibtex;

import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * All elements for the chain of responsibility for publications are derived
 * from this class.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public abstract class BibTexChainElement extends ChainElement<Post<BibTex>, BibTexParam> {

	protected final BibTexDatabaseManager db;

	public BibTexChainElement() {
		this.db = BibTexDatabaseManager.getInstance();
	}
}