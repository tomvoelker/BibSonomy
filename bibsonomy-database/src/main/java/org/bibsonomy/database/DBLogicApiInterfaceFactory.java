package org.bibsonomy.database;

import org.bibsonomy.database.util.DBSession;

/**
 * This class produces DBLogic instances with API authentication
 * 
 * @author Jens Illig
 */
public class DBLogicApiInterfaceFactory extends DBLogicUserInterfaceFactory {

	@Override
	protected boolean isValidLogin(String loginName, String password) {
		final DBSession session = openSession();
		try {
			return userDBManager.validateUserAccess(loginName, password, session);
		} finally {
			session.close();
		}
	}
}