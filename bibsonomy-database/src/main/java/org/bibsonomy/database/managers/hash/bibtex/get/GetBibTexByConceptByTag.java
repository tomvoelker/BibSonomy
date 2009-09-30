package org.bibsonomy.database.managers.hash.bibtex.get;

import java.util.List;

import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibTexByConceptByTag extends BibTexHashElement {

	/**
	 * TODO improve docs
	 */
	public GetBibTexByConceptByTag() {
		setTagIndex(true);
		setNumSimpleConceptsOverNull(true);

		addToOrders(Order.ADDED);
	}

	/**
	 * Returns a list of posts (bibtex) which is in relationship with the given
	 * concept name.
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		return this.db.getPostsByConceptByTag(param, session);
	}
}
