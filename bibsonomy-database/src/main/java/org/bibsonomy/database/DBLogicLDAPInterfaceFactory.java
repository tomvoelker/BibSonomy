package org.bibsonomy.database;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;

/**
 * This class produces DBLogic instances with user authentication
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class DBLogicLDAPInterfaceFactory extends DBLogicUserInterfaceFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterfaceFactory#getLogicAccess(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public LogicInterface getLogicAccess(final String ldapId, final String password) {
		String loginName = null;
		if (ldapId != null) {
			
			// get bibsonomy user name with ldapId
			loginName = this.getUsernameByLdapId(ldapId);
			
			// now follow standard user authentication process
			final User loggedInUser = getLoggedInUser(loginName, password);
			
			if (loggedInUser.getName() != null) {
				return new DBLogic(loggedInUser, this.dbSessionFactory, bibTexSearch, bookmarkSearch);
			}
			throw new ValidationException("Wrong Authentication ('" + loginName + "'/'" + password + "')");
		}
		// guest access
		return new DBLogic(new User(), this.dbSessionFactory, bibTexSearch, bookmarkSearch);
	}
	
	/**
	 * @param ldapId
	 * @return bibsonomy username
	 */
	public String getUsernameByLdapId(final String ldapId) {
		// get bibsonomy user name with ldapId
		final DBSession session = openSession();
		try {
			return this.userDBManager.getUsernameByLdapUser(ldapId, session);
		} finally {
			session.close();
		}
		
	}
	
}