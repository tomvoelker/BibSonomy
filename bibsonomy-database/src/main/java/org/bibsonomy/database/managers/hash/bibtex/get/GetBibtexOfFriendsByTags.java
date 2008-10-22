package org.bibsonomy.database.managers.hash.bibtex.get;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
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
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexOfFriendsByTags extends BibTexHashElement {

	public GetBibtexOfFriendsByTags() {
		setGroupingEntity(GroupingEntity.FRIEND);
		setLoginNeeded(true);
		setRequestedUserName(true);
		setTagIndex(true);
		setNumSimpleTagsOverNull(true);
		
		addToOrders(Order.ADDED);
	}

	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		/*
		 * if the requested user has the current user in his/her friend list, he
		 * may see the posts
		 */
		if (this.generalDb.isFriendOf(param.getUserName(), param.getRequestedUserName(), session)) {
			param.setGroupId(GroupID.FRIENDS.getId());
			return this.db.getBibTexByTagNamesForUser(param, session);
		}
		return new ArrayList<Post<BibTex>>();
	}
}