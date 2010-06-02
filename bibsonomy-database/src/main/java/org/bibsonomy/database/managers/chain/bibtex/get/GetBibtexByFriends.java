package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns all BibTex's of your friends.
 * 
 * TODO extension with user restriction rearding returned bibtex and appropriate
 * naming of URL in REST interface (e.g. /user/friend).
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @version $Id$
 */
public class GetBibtexByFriends extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getPostsByUserFriends(param.getUserName(), HashID.getSimHash(param.getSimHash()), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (present(param.getUserName()) &&
				!present(param.getBibtexKey()) &&
				param.getGrouping() == GroupingEntity.FRIEND &&
				!present(param.getRequestedGroupName()) &&
				!present(param.getRequestedUserName()) &&
				!present(param.getTagIndex()) &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()));
	}
}