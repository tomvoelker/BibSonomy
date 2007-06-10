package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexForGroupAndTag extends BibTexChainElement {

	/**
	 * return a list of bibtex by a given group and common tags of a group.
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		param.setGroupId(this.generalDb.getGroupIdByGroupName(param, session));
		return this.db.getBibTexForGroupByTag(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.GROUP) && present(param.getRequestedGroupName()) && !present(param.getRequestedUserName()) && present(param.getTagIndex())&& (param.getNumSimpleConcepts() == 0) && (param.getNumSimpleTags() > 0) && (param.getNumTransitiveConcepts() == 0) && !present(param.getHash()) && !present(param.getOrder());
	}
}