package org.bibsonomy.database;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.User;

/**
 * This class produces DBLogic instances with API authentication
 * 
 * @author Jens Illig
 */
public class DBLogicApiInterfaceFactory extends DBLogicUserInterfaceFactory {

	@Override
	protected User getLoggedInUserAccess(final String loginName, final String password, final DBSession session) {
		return this.userDBManager.validateUserAccess(loginName, password, session);
	}
}