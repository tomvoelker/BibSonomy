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
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of BibTex's for a given friend of a user (this friend also
 * posted the bibtex to group friends (made bibtex viewable for friends))
 * restricted by a given tag.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @version $Id$
 */
public class GetBibtexOfFriendsByTags extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		param.setGroupType(GroupID.FRIENDS);
		if (this.generalDb.isFriendOf(param.getRequestedUserName(), param.getUserName(), session) == true) {
			return this.db.getBibTexByTagNamesForUser(param, session);
		}
		return new ArrayList<Post<BibTex>>();
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return  present(param.getUserName()) && 
				!present(param.getBibtexKey()) && 
				(param.getGrouping() == GroupingEntity.FRIEND) && 
				present(param.getRequestedUserName()) && 
				present(param.getTagIndex()) && 
				(param.getNumSimpleConcepts() == 0) && 
				(param.getNumSimpleTags() > 0) && 
				(param.getNumTransitiveConcepts() == 0) && 
				!present(param.getHash()) && 
				nullOrEqual(param.getOrder(), Order.ADDED) && 
				!present(param.getSearch());
	}
}