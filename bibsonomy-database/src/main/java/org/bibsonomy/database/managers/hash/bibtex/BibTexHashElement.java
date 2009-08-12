package org.bibsonomy.database.managers.hash.bibtex;

import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.hash.HashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public abstract class BibTexHashElement extends HashElement<Post<BibTex>, BibTexParam> {

	protected final BibTexDatabaseManager db;

	private boolean bibtexKey = false;
	
	/**
	 * Constructs a hash element
	 */
	public BibTexHashElement() {
		this.db = BibTexDatabaseManager.getInstance();
	}

	/**
	 * @return boolean
	 */
	public boolean isBibtexKey() {
		return this.bibtexKey;
	}

	/**
	 * @param bibtexKey
	 */
	public void setBibtexKey(final boolean bibtexKey) {
		this.bibtexKey = bibtexKey;
	}
}