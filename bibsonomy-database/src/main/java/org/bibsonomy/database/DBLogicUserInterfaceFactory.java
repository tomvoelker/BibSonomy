package org.bibsonomy.database;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.model.util.UserUtils;

/**
 * This class produces DBLogic instances with user authentication
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class DBLogicUserInterfaceFactory implements LogicInterfaceFactory {

	protected final UserDatabaseManager userDBManager = UserDatabaseManager.getInstance();
	protected final GroupDatabaseManager groupDb = GroupDatabaseManager.getInstance();

	protected DBSessionFactory dbSessionFactory;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterfaceFactory#getLogicAccess(java.lang.String,
	 *      java.lang.String)
	 */
	public LogicInterface getLogicAccess(final String loginName, final String password) {
		if (loginName != null) {
			final User loggedInUser = getLoggedInUser(loginName, password);
			if (loggedInUser.getName() != null) {
				return new DBLogic(loggedInUser, this.dbSessionFactory);
			}
			throw new AccessDeniedException("Wrong Authentication ('" + loginName + "'/'" + password + "')");
		}
		// guest access
		return new DBLogic(new User(), this.dbSessionFactory);
	}
	
	/**
	 * Returns a user object containing the details of the user, if he is logged
	 * in correctly. If not, the returned user object is empty and it's user
	 * name NULL.
	 * 
	 * @param loginName
	 * @param password
	 * @return user object with details of the logged in user
	 */
	protected User getLoggedInUser(final String loginName, final String password) {
		final DBSession session = openSession();
		try {
			final User loggedInUser = getLoggedInUserAccess(loginName, password, session);
			if (loggedInUser.getName() != null) {
				UserUtils.setGroupsByGroupIDs(loggedInUser, this.groupDb.getGroupIdsForUser(loggedInUser.getName(), session));
			}
			return loggedInUser;
		} finally {
			session.close();
		}
	}
	
	/**
	 * Calls the correct validation method on the {@link UserDatabaseManager}.
	 * @param loginName
	 * @param password
	 * @param session
	 * @return
	 */
	protected User getLoggedInUserAccess(final String loginName, final String password, final DBSession session) {
		return this.userDBManager.validateUserUserAccess(loginName, password, session);
	}
	
	/**
	 * @param dbSessionFactory
	 *            the {@link DBSessionFactory} to use
	 */
	public void setDbSessionFactory(DBSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}

	/**
	 * Returns a new database session.
	 */
	protected DBSession openSession() {
		return this.dbSessionFactory.getDatabaseSession();
	}
	
}