package org.bibsonomy.database.managers.hash.bibtex.get;

import java.util.List;

import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given hash.
 * 
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexByHash extends BibTexHashElement {

	public GetBibtexByHash() {
		setHash(true);
	}

	/**
	 * return a list of bibtex by a given hash.
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		return this.db.getBibTexByHash(param, session);
	}
}