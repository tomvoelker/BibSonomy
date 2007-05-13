package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexForGroupAndTag extends BibTexChainElement {

	/**
	 * return a list of bibtex by a given group and common tags of a group.
	 * Following arguments have to be given:
	 * 
	 * grouping:group name:given tags:given hash:null popular:false added:false
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final Transaction session) {
		log.debug(this.getClass().getSimpleName());

		param.setGroupId(this.generalDb.getGroupIdByGroupName(param, session));
		param.setGroups(this.generalDb.getGroupsForUser(param, session));

		return this.db.getBibTexForGroupByTag(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return param.getUserName() != null && param.getGrouping() == GroupingEntity.GROUP && param.getRequestedGroupName() != null && param.getTagIndex() != null && param.getHash() == null && param.isPopular() == false && param.isAdded() == false;
	}
}