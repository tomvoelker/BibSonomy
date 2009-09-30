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
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexByConceptForGroup extends BibTexHashElement {

	public GetBibtexByConceptForGroup() {
		setTagIndex(true);
		setNumSimpleConceptsOverNull(true);
		setGroupingEntity(GroupingEntity.GROUP);
		
		setRequestedGroupName(true);

		addToOrders(Order.ADDED);
	}

	@Override
	public List<Post<BibTex>> perform(BibTexParam param, DBSession session) {
		return this.db.getPostsByConceptForGroup(param, session);
	}
}