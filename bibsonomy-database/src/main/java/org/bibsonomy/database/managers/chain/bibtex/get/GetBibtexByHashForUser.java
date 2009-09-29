package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given hash and a user.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexByHashForUser extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getPostsByHashForUser(param.getUserName(), param.getHash(), param.getRequestedUserName(), param.getGroups(), session, HashID.getSimHash(param.getSimHash()));
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (present(param.getHash()) &&
				!present(param.getBibtexKey()) &&
				param.getGrouping() == GroupingEntity.USER &&
				present(param.getRequestedGroupName()) &&
				!present(param.getTagIndex()) &&
				!present(param.getOrder()) &&
				!present(param.getSearch()));
	}
}