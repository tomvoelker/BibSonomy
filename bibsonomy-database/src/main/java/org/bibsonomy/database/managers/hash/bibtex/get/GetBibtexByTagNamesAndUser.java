package org.bibsonomy.database.managers.hash.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of BibTex's for a given tag/tags and user.
 * 
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexByTagNamesAndUser extends BibTexHashElement {

	public GetBibtexByTagNamesAndUser() {
		setRequestedUserName(true);
		setNumSimpleTagsOverNull(true);
		setTagIndex(true);
		setGroupingEntity(GroupingEntity.USER);

		addToOrders(Order.ADDED);
	}

	/**
	 * return a list of bibtex entries by given tag/tags and User.
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		return this.db.getBibTexByTagNamesForUser(param, session);
	}

	protected boolean canHandle(final BibTexParam param) {
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.USER) 
		&& present(param.getTagIndex()) 
		&& (param.getNumSimpleConcepts() == 0) 
		&& (param.getNumSimpleTags() > 0) 
		&& (param.getNumTransitiveConcepts() == 0) 
		&& present(param.getRequestedUserName()) 
		&& !present(param.getHash()) 
		&& nullOrEqual(param.getOrder(), Order.ADDED) && !present(param.getSearch());
	}
}
