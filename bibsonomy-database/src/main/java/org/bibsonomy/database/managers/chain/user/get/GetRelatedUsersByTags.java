package org.bibsonomy.database.managers.chain.user.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.user.UserChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;

/**
 * Get related users for given tag(s)
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetRelatedUsersByTags extends UserChainElement {

	@Override
	protected List<User> handle(final UserParam param, final DBSession session) {
		if (UserRelation.FOLKRANK.equals(param.getUserRelation())) {
			return this.userDB.getRelatedUsersByFolkrankAndTags(param.getTagIndex(),
																param.getLimit(), 
																param.getOffset(), 
																session);
		}
		log.error("User Relation " + param.getUserRelation().name() + " not yet supported.");
		return new ArrayList<User>();
	}

	@Override
	protected boolean canHandle(final UserParam param) {
		return (GroupingEntity.ALL.equals(param.getGrouping()) && 
				present(param.getTagIndex()) && 
				present(param.getUserRelation()));
	}
}