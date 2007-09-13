package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.Order;

/**
 * @author Miranda Grahl
 * @author Jens Illig
 * @version $Id$
 */
public class GetBibtexOfFriendsByTags extends BibTexChainElement {

	/**
	 * return a list of bibtex entries by given friend of a user (this friends also
	 * posted this bookmarks to group friends (made bookmarks viewable for
	 * friends)).
	 * bibtex entries are restricted by a chosen tag 
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		param.setGroupType(GroupID.FRIENDS);
		if (this.generalDb.isFriendOf(param, session) == true) {
			return this.db.getBibTexByTagNamesForUser(param, session);
		}
		return new ArrayList<Post<BibTex>>();
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.FRIEND) && present(param.getRequestedUserName()) && present(param.getTagIndex()) && (param.getNumSimpleConcepts() == 0) && (param.getNumSimpleTags() > 0) && (param.getNumTransitiveConcepts() == 0) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED);
	}
}