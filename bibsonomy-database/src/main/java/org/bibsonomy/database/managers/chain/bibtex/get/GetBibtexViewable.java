package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.Order;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexViewable extends BibTexChainElement {

	@SuppressWarnings("hiding")
	private static final Logger log = Logger.getLogger(GetBibtexViewable.class);

	/**
	 * return a list of bibtex by a given group (which is only viewable for
	 * groupmembers excluded public option regarding setting a post).
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		final Integer groupId = this.generalDb.getGroupIdByGroupNameAndUserName(param, session);
		if (groupId == GroupID.INVALID.getId()) {
			log.debug("groupId " +  param.getRequestedGroupName() + " not found" );
			return new ArrayList<Post<BibTex>>(0);			
		}
		param.setGroupId(groupId);
		if (present(param.getTagIndex()) == true) return this.db.getBibTexViewableByTag(param, session);
		return this.db.getBibTexViewable(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (present(param.getUserName()) && (param.getGrouping() == GroupingEntity.VIEWABLE) && present(param.getRequestedGroupName())&& present(param.getRequestedUserName()) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED) && !present(param.getSearch()));
	}
}