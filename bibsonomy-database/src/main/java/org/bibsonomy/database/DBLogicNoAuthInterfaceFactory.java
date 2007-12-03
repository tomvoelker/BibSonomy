/*
 * Created on 30.08.2007
 */
package org.bibsonomy.database;

import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;

/**
 * This is a temporary logic interface factory to enable logic interface access 
 * from within BibSonomy 1 without having to re-do authentication
 * 
 * please remove this class once this is not necessary anymore
 * 
 * dbe, 20071203
 * 
 * @author Dominik Benz
 */
public class DBLogicNoAuthInterfaceFactory implements LogicInterfaceFactory {
	
	private DBSessionFactory dbSessionFactory;
	
	public LogicInterface getLogicAccess(final String loginName, final String password) {
		if (loginName != null) {
			return new DBLogic(loginName, dbSessionFactory);
		}
		return new DBLogic(null, dbSessionFactory);  // guest access
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
