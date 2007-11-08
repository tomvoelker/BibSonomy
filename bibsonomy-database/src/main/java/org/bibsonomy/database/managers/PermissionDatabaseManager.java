package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.ExceptionUtils;

/**
 * Database Manager for permissions
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class PermissionDatabaseManager extends AbstractDatabaseManager {

	private static final Logger log = Logger.getLogger(PermissionDatabaseManager.class);
	private final static PermissionDatabaseManager singleton = new PermissionDatabaseManager();

	private PermissionDatabaseManager() {
	}

	public static PermissionDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Checks whether the requested start- / end-values are OK
	 * 
	 * @param start
	 * @param end
	 * @param itemType
	 */
	public void checkStartEnd(Integer start, Integer end, String itemType) {				
		if ( end > 1000 ) {
			throw new ValidationException("You are not authorized to retrieve more than the last 1000 " + itemType + " items.");
		}
	}
}