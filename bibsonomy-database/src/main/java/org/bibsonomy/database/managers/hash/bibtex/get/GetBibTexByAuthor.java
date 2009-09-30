package org.bibsonomy.database.managers.hash.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of BibTex's for given author.
 *  
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibTexByAuthor extends BibTexHashElement {

	/**
	 * TODO: improve docs
	 */
	public GetBibTexByAuthor() {
		setSearch(true);
		setGroupingEntity(GroupingEntity.VIEWABLE);

		addToOrders(Order.ADDED);
	}

	/**
	 * return a list of bibtex entries by given author.
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		return this.db.getPostsByAuthor(param, session);
	}
}
