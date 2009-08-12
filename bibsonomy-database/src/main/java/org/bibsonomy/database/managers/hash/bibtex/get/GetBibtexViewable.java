package org.bibsonomy.database.managers.hash.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of BibTex's for a given group (which is only viewable for
 * groupmembers excluded public option regarding setting a post).
 * 
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexViewable extends BibTexHashElement {

	@SuppressWarnings("hiding")
	private static final Log log = LogFactory.getLog(GetBibtexViewable.class);

	public GetBibtexViewable() {
		setLoginNeeded(true);
		setRequestedUserName(true);
		setGroupingEntity(GroupingEntity.VIEWABLE);

		addToOrders(Order.ADDED);
	}

	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		final Integer groupId = this.groupDb.getGroupIdByGroupNameAndUserName(param.getRequestedGroupName(), param.getUserName(), session);
		if (groupId == GroupID.INVALID.getId()) {
			log.debug("groupId " + param.getRequestedGroupName() + " not found");
			return new ArrayList<Post<BibTex>>(0);
		}
		param.setGroupId(groupId);
		if (present(param.getTagIndex()) == true) return this.db.getBibTexViewableByTag(param, session);
		return this.db.getBibTexViewable(param, session);
	}
}