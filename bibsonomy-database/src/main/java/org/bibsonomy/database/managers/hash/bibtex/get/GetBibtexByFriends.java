package org.bibsonomy.database.managers.hash.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns all BibTex's of your friends.
 * 
 * TODO extension with user restriction rearding returned bibtex and appropriate
 * naming of URL in REST interface (e.g. /user/friend).
 * 
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexByFriends extends BibTexHashElement {

	public GetBibtexByFriends() {
		setLoginNeeded(true);
		setRequestedGroupName(false);
		setGroupingEntity(GroupingEntity.FRIEND);

		addToOrders(Order.ADDED);
	}

	/**
	 * return all bibtex entries of your friends TODO extension with user
	 * restriction rearding returned bibtex and appropriate naming of URL in
	 * REST interface
	 * 
	 * /user/friend
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		return this.db.getPostsByUserFriends(param.getUserName(), HashID.getSimHash(param.getSimHash()), param.getLimit(), param.getOffset(), session);
	}
}