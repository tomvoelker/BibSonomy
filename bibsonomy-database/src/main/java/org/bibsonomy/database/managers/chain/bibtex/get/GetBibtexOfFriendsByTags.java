package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.Order;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @author Jens Illig
 * @version $Id$
 */
public class GetBibtexOfFriendsByTags extends BibTexChainElement {
	
	private final GeneralDatabaseManager gdm = GeneralDatabaseManager.getInstance();

	/**
	 * return a list of bibtex by given friends of a user (this friends also
	 * posted this bookmarks to group friends, made bookmarks viewable for
	 * friends).
	 * 
	 * /user/friend at first all bibtex of user(which add me as friend) x are
	 * returned, sencondly this list is restricted by those post which are
	 * posted to group friend, respectively are viewable for friends e.g.
	 * mgr/friend/stumme
	 * 
	 * bibtex are listed which record me as friend and also posted this record
	 * to the group friend
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final Transaction session) {
		param.setGroupType(GroupID.GROUP_FRIENDS);
		if (this.gdm.isFriendOf(param, session) == true) {
			return this.db.getBibTexByTagNamesForUser(param, session);
		}
		return new ArrayList<Post<BibTex>>();
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.FRIEND) && present(param.getRequestedUserName()) && present(param.getTagIndex())&& (param.getNumSimpleConcepts() == 0) && (param.getNumSimpleTags() > 0) && (param.getNumTransitiveConcepts() == 0) && !present(param.getHash()) &&  nullOrEqual(param.getOrder(),Order.ADDED);
	}
}