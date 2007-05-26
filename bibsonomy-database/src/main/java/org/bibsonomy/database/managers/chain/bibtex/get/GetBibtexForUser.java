package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

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
public class GetBibtexForUser extends BibTexChainElement {
	/**
	 * return a list of bibtex by a logged user.
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final Transaction session) {
		return this.db.getBibTexForUser(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.USER) && present(param.getRequestedUserName()) && !presentValidGroupId(param.getGroupId()) && !present(param.getTagIndex()) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED);
	}
}