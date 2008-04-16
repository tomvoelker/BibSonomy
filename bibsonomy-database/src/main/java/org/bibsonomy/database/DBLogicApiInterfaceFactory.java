package org.bibsonomy.database;

import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.User;

/**
 * This class produces DBLogic instances with API authentication
 * 
 * @author Jens Illig
 */
public class DBLogicApiInterfaceFactory extends DBLogicUserInterfaceFactory {

	@Override
	protected User getLoggedInUser(final String loginName, final String password) {
		final DBSession session = openSession();
		try {
			return this.userDBManager.validateUserAccess(loginName, password, session);
		} finally {
			session.close();
		}
	}
}