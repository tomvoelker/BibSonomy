package org.bibsonomy.database;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.model.util.UserUtils;

/**
 * This class produces DBLogic instances with user authentication
 * 
 * @author Jens Illig
 */
public class DBLogicUserInterfaceFactory implements LogicInterfaceFactory {
	protected final UserDatabaseManager userDBManager = UserDatabaseManager.getInstance();
	protected final GeneralDatabaseManager generalDB = GeneralDatabaseManager.getInstance();
	
	private DBSessionFactory dbSessionFactory;
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterfaceFactory#getLogicAccess(java.lang.String, java.lang.String)
	 */
	public LogicInterface getLogicAccess(final String loginName, final String password) {
		if (loginName != null) {
			final User loggedInUser = getLoggedInUser(loginName, password); 
			if (loggedInUser.getName() != null) {
				return new DBLogic(loggedInUser, dbSessionFactory);
			}
			throw new ValidationException("Wrong Authentication.");
		}		
		return new DBLogic(new User(), dbSessionFactory);  // guest access
	}

	/** Returns a user object containing the details of the user, if he is logged in
	 * correctly. If not, the returned user object is empty and it's user name NULL. 
	 *  
	 * @param loginName
	 * @param password
	 * @return
	 */
	protected User getLoggedInUser(String loginName, String password) {
		final DBSession session = openSession();
		try {
			User loggedInUser = userDBManager.validateUserUserAccess(loginName, password, session);
			if (loggedInUser.getName() != null) {
				UserUtils.setGroupsByGroupIDs(loggedInUser, generalDB.getGroupIdsForUser(loggedInUser.getName(), session));
			}
			return loggedInUser;
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