package org.bibsonomy.database.managers;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 * @author Christian Pfeiffer / eisfair
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
			this.insert("insertPerson", person, session);
			person.setId((int) this.queryForObject("getMaxId", null, session));
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}


	/**
	 * @param user
	 * @param session 
	 * @return Person
	 */
	public Person getPersonByUser(String user, final DBSession session) {
		return (Person) this.queryForObject("getPersonByUser", user, session);
	}


	/**
	 * @param id
	 * @param session
	 * @return Person
	 */
	public Person getPersonById(int id, DBSession session) {
		return (Person) this.queryForObject("getPersonById", id, session);
	}


	/**
	 * @param mainName
	 * @param session
	 */
	public void createPersonName(PersonName mainName, DBSession session) {
		session.beginTransaction();
		try {
			this.insert("insertName", mainName, session);
			mainName.setId((int) this.queryForObject("getMaxNameId", null, session));
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}


	/**
	 * @param person
	 * @param session
	 */
	public void updatePerson(Person person, DBSession session) {
		session.beginTransaction();
		try {
			this.insert("updatePerson", person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}


	/**
	 * @param id
	 * @param session
	 * @return Set<PersonName>
	 */
	public List<?> getAlternateNames(int id, DBSession session) {
		return this.queryForList("getAlternateNames", id, session);
	}


	/**
	 * @param id
	 * @param session
	 * @return PersonName
	 */
	public PersonName getPersonNameById(int id, DBSession session) {
		return (PersonName) this.queryForObject("getPersonNameById", id, session);
	}


	/**
	 * @param mainName
	 * @param session
	 */
	public void updatePersonName(PersonName mainName, DBSession session) {
		session.beginTransaction();
		try {
			this.insert("updatePersonName", mainName, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
}
