/*
 * Created on 30.08.2007
 */
package org.bibsonomy.database;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.managers.RestDatabaseManager;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;

public class DBLogicInterfaceFactory implements LogicInterfaceFactory {
	public LogicInterface getLogicAccess(final String loginName, final String password) {
		if (loginName != null) {
			if (isValidLogin(loginName, password) == true) {
				return new DBLogic(loginName, RestDatabaseManager.getInstance());
			}
			throw new ValidationException("Wrong Authentication.");
		}
		return new DBLogic(null, RestDatabaseManager.getInstance());  // guest access
	}

	protected boolean isValidLogin(String loginName, String password) {
		return RestDatabaseManager.getInstance().validateUserUserAccess(loginName, password);
	}
}
