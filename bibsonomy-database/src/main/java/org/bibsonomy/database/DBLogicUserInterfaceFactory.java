/*
 * Created on 30.08.2007
 */
package org.bibsonomy.database;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;

/**
 * This class produces DBLogic instances with user authentication
 * 
 * @author Jens Illig
 */
public class DBLogicUserInterfaceFactory implements LogicInterfaceFactory {
	protected final UserDatabaseManager userDBManager = UserDatabaseManager.getInstance();
	
	private DBSessionFactory dbSessionFactory;
	
	public LogicInterface getLogicAccess(final String loginName, final String password) {
		if (loginName != null) {
			if (isValidLogin(loginName, password) == true) {
				return new DBLogic(loginName, dbSessionFactory);
			}
			throw new ValidationException("Wrong Authentication.");
		}
		return new DBLogic(null, dbSessionFactory);  // guest access
	}

	protected boolean isValidLogin(String loginName, String password) {
		final DBSession session = openSession();
		try {
			return userDBManager.validateUserUserAccess(loginName, password, session);
		} finally {
			session.close();
		}
	}
	
	/**
	 * @param dbSessionFactory the {@link DBSessionFactory} to use
	 */
	public void setDbSessionFactory(DBSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}

	/**
	 * Returns a new database session.
	 */
	protected DBSession openSession() {
		return dbSessionFactory.getDatabaseSession();
	}
}
