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
		param.setGroupId(this.generalDb.getGroupIdByGroupName(param, session));
		// TODO: is this needed? param.setGroups(this.generalDb.getGroupsForUser(param, session));

		return this.db.getBibTexForGroupByTag(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.GROUP) && present(param.getRequestedGroupName()) && present(param.getTagIndex()) && !present(param.getHash()) && !present(param.getOrder());
	}
}