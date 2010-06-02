package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given search.
 * 
 * @author claus
 * @version $Id$
 */
public class GetBibtexSearchForGroup extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, DBSession session) {
		return this.db.getPostsSearchForGroup(param.getRequestedGroupName(), param.getGroupNames(), param.getRawSearch(), param.getUserName(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return false;
	}
}