package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * TODO check
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexOfFriendsByTags extends BibTexChainElement {

	/**
	 * return a list of bibtex by given friends of a user (this friends also
	 * posted this bookmarks to group friends, made bookmarks viewable for
	 * friends). Following arguments have to be given: * grouping:friend
	 * name:given tags:given hash:NULL popular:false added:false
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
		log.debug(this.getClass().getSimpleName());
		param.setGroupId(ConstantID.GROUP_FRIENDS.getId());
		return this.db.getBibTexForUser(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return param.getUserName() != null && param.getGrouping() == GroupingEntity.FRIEND && param.getRequestedGroupName() != null && param.getTagIndex() != null && param.getHash() == null && param.isPopular() == false && param.isAdded() == false;
	}
}