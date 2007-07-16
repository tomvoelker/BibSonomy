package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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
		final Integer groupId;
		final GroupID specialGroup = GroupID.getSpecialGroup( param.getRequestedGroupName() );
		if (specialGroup != null) {
			groupId = specialGroup.getId();
		} else {
			groupId = this.generalDb.getGroupIdByGroupNameAndUserName(param, session);
		}
		if (groupId == null) {
			log.debug("groupId not found");
			return new ArrayList<Post<BibTex>>(0);
		}
		log.debug("groupId=" + groupId);
		param.setGroupId(groupId);

		if (present(param.getTagIndex()) == true) return this.db.getBibTexViewableByTag(param, session);
		return this.db.getBibTexViewable(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.VIEWABLE) && present(param.getRequestedGroupName()) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED);
	}
}