package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;
import static org.bibsonomy.util.ValidationUtils.presentValidGroupId;

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
 * Returns a list of BibTex's for a given user.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexForUser extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getPostsForUser(param.getUserName(), param.getRequestedUserName(), HashID.getSimHash(param.getSimHash()), param.getGroupId(), param.getGroups(), param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.USER &&
				!present(param.getBibtexKey()) &&
				present(param.getRequestedUserName()) &&
				!presentValidGroupId(param.getGroupId()) &&
				!present(param.getTagIndex()) &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()));
	}
}