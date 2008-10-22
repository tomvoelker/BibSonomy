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
public class GetBibtexForHomePageOrPopular extends BibTexHashElement {

	public GetBibtexForHomePageOrPopular() {
		addToOrders(Order.POPULAR);
		addToOrders(Order.ADDED);
	}

	/**
	 * depending on the order a list of bibtex entries given by homepage or all
	 * popular bibtex entries of bibSonomy are returned
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		if (param.getOrder() == Order.POPULAR) {
			return this.db.getBibTexPopular(param, session);
		} else if (param.getOrder() == Order.ADDED) {
			return this.db.getBibTexForHomePage(param, session);
		}
		return null;
	}
}