package org.bibsonomy.database.managers.hash.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of BibTex's for a given key.
 * 
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexByKey extends BibTexHashElement {

	public GetBibtexByKey() {
		setBibtexKey(true);

		addToOrders(Order.ADDED);
		addToOrders(Order.FOLKRANK);
	}

	/**
	 * return a list of bibtex by a given key.
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		return this.db.getPostsByKey(param, session);
	}

	protected boolean canHandle(final BibTexParam param) {
		return present(param.getBibtexKey()) && (param.getNumSimpleConcepts() == 0) && (param.getNumTransitiveConcepts() == 0) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED, Order.FOLKRANK) && !present(param.getSearch());
	}
}