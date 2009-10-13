package org.bibsonomy.database.managers.hash.bibtex.get;

import java.util.List;

import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given search.
 * 
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexSearch extends BibTexHashElement {

	public GetBibtexSearch() {
		setSearch(true);
	}

	/**
	 * return all popular bibtex entries of bibSonomy.
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		return this.db.getPostsSearch(param.getGroupId(), param.getSearch(), param.getRequestedUserName(), param.getLimit(), param.getOffset(), session);
	}
}