package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.Order;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @author Jens Illig
 * @version $Id$
 */
public class GetBibtexOfFriendsByUser extends BibTexChainElement {

	private final GeneralDatabaseManager gdm = GeneralDatabaseManager.getInstance();
	
	/**
	 * return a list of bibtex by given friends of a user (this friends also
	 * posted this bibtex to group friends, made bibtex viewable for friends).
	 * 
	 * at first all bibtex of user x are returned, sencondly this list is
	 * restricted by those post which are posted to group friend, respectively
	 * are viewable for friends e.g. mgr/friend/stumme
	 * 
	 * bibtex are listed which record me as friend and also posted this record
	 * to the group friend
	 * 
	 * /user/friend
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		if (this.gdm.isFriendOf(param, session) == true) {
			param.setGroupId(GroupID.GROUP_FRIENDS.getId());
			return this.db.getBibTexForUser(param, session);
		}
		return new ArrayList<Post<BibTex>>();
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.FRIEND) && present(param.getRequestedUserName()) && !present(param.getTagIndex()) && !present(param.getHash()) && nullOrEqual(param.getOrder(),Order.ADDED);
	}
}