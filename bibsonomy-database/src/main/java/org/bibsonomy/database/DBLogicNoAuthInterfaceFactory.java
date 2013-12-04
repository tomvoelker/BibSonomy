package org.bibsonomy.database;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.model.util.BibTexReader;

/**
 * This is a temporary logic interface factory to enable logic interface access
 * from within BibSonomy 1 without having to re-do authentication
 * 
 * XXX: Please remove this class once this is not necessary anymore.
 * 
 * dbe, 20071203
 * tni: TEMPORARY???
 * 
 * @author Dominik Benz
 */
public class DBLogicNoAuthInterfaceFactory extends AbstractDBLogicInterfaceFactory {
	
	@Override
	public LogicInterface getLogicAccess(final String loginName, final String password) {
		if (loginName != null) {
			/*
			 * In this case we don't fill the user object completely, but set
			 * it's name such that the user is seen as logged in (users which
			 * are not logged in cause a user object with empty name).
			 */
			return new DBLogic(new User(loginName), this.getDbSessionFactory(), this.bibtexReader, this.getFileLogic());
		}
		// guest access
		return new DBLogic(new User(), this.getDbSessionFactory(), this.bibtexReader, this.getFileLogic());
	}

}