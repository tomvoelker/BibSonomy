package org.bibsonomy.database.managers.chain.user.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.user.UserChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;

/**
 * Get related users for a given user.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetRelatedUsersByUser extends UserChainElement {

	@Override
	protected List<User> handle(final UserParam param, final DBSession session) {
		/*
		 * handle folkrank separately
		 */
		if (UserRelation.FOLKRANK.equals(param.getUserRelation())) {
			return this.userDB.getRelatedUsersByFolkrankAndUser(param.getRequestedGroupName(),
																param.getUserName(),
																param.getLimit(), 
																param.getOffset(), 
																session);				
		}
		return this.userDB.getRelatedUsersBySimilarity(param.getRequestedUserName(), 
													   param.getUserName(),
													   param.getUserRelation(), 
													   param.getLimit(), 
													   param.getOffset(), 
													   session);
	}

	@Override
	protected boolean canHandle(final UserParam param) {
		return (GroupingEntity.USER.equals(param.getGrouping()) &&
				present(param.getRequestedUserName()) &&
				present(param.getUserRelation()));
	}
}