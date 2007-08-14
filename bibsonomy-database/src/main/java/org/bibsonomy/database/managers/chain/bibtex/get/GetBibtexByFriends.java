package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.Order;

/**
 * @author Miranda Grahl
 * @author Jens Illig
 * @version $Id$
 */
public class GetBibtexByFriends extends BibTexChainElement {

	/**
	 * TODO extension with user restriction rearding returned bibtex and
	 * appropriate namming of URL in REST interface
	 * 
	 * /user/friend
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getBibTexByUserFriends(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.FRIEND) && !present(param.getRequestedGroupName()) && !present(param.getRequestedUserName()) && !present(param.getTagIndex()) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED);
	}
}