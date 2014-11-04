package org.bibsonomy.database.managers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.AbstractDatabaseManager;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class PersonDatabaseManager  extends AbstractDatabaseManager {
	private static final Log log = LogFactory.getLog(PersonDatabaseManager.class);

	private final static PersonDatabaseManager singleton = new PersonDatabaseManager();

	public static PersonDatabaseManager getInstance() {
		return singleton;
	}
	
}
