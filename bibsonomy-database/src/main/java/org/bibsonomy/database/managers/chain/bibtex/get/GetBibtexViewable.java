package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.Order;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexViewable extends BibTexChainElement {
	private static final Logger log = Logger.getLogger(GetBibtexViewable.class);

	/**
	 * return a list of bibtex by a given group (which is only viewable for
	 * groupmembers excluded public option regarding setting a post).
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final Transaction session) {
		final Integer groupId = this.generalDb.getGroupIdByGroupNameAndUserName(param, session);
		if (groupId == null) {
			log.debug("groupId not found");
			return new ArrayList<Post<BibTex>>(0);
		}
		log.debug("groupId=" + groupId);
		param.setGroupId(groupId);

		return this.db.getBibTexViewable(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.VIEWABLE) && present(param.getRequestedGroupName()) && !present(param.getTagIndex()) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED);
	}
}