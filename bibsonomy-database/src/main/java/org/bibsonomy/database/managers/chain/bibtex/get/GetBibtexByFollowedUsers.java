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
import org.bibsonomy.model.enums.Order;

/**
 * Returns all BibTex's of users you are following.
 * 
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class GetBibtexByFollowedUsers extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getBibTexByFollowedUsers(param.getUserName(), 
												param.getGroups(), 
												param.getLimit(), 
												param.getOffset(), 
												session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (present(param.getUserName()) &&
				present(param.getGroups()) &&
				param.getGrouping() == GroupingEntity.FOLLOWER);
	}
}