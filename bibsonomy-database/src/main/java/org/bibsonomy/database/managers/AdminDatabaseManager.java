package org.bibsonomy.database.managers;

import org.apache.log4j.Logger;
import org.bibsonomy.database.AbstractDatabaseManager;

/** Provides functionalities which are typically only available to admins.
 * This might include flagging a user as spammer, setting the status of an 
 * InetAddress (IP), and other things.
 * 
 * @author rja
 * @version $Id$
 */
public class AdminDatabaseManager extends AbstractDatabaseManager {

	
	private static final Logger LOGGER = Logger.getLogger(AdminDatabaseManager.class);
	private final static AdminDatabaseManager singleton = new AdminDatabaseManager();

	private AdminDatabaseManager() {
	}

	/**
	 * @return a singleton instance of this AdminDatabaseManager
	 */
	public static AdminDatabaseManager getInstance() {
		return singleton;
	}
	
}
