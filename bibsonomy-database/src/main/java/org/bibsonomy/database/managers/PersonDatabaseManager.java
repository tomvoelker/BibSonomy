package org.bibsonomy.database.managers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;

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
	
	
	/**
	 * Inserts a {@link Person} into the database.
	 * 
	 * @param person 
	 * @param session 
	 */
	public void createPerson(final Person person, final DBSession session) {
		session.beginTransaction();
		try {
			// TODO: this.insertPerson(person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
}
