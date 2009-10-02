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
 * Returns a list of BibTex's for a given user.
 * 
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexForUser extends BibTexHashElement {

	public GetBibtexForUser() {
		setGroupingEntity(GroupingEntity.USER);
		setRequestedUserName(true);
		
		addToOrders(Order.ADDED);
	}

	/**
	 * return a list of bibtex entries by a user
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		return this.db.getPostsForUser(param, session);
	}
}