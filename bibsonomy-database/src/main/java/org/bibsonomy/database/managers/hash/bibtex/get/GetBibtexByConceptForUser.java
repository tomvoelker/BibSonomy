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
 * Returns a list of BibTex's for a tag-concept.
 * 
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexByConceptForUser extends BibTexHashElement {

	public GetBibtexByConceptForUser() {
		setRequestedUserName(true);
		setTagIndex(true);
		setNumSimpleConceptsOverNull(true);
		setGroupingEntity(GroupingEntity.USER);

		addToOrders(Order.ADDED);
	}

	/**
	 * return a list of bibtex by a tag-concept. All bookmarks will be return
	 * for a given "super-tag".
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		return this.db.getBibTexByConceptForUser(param.getUserName(), param.getRequestedUserName(), param.getTagIndex(), param.getGroups(), false, param.getLimit(), param.getOffset(), session);
	}
}