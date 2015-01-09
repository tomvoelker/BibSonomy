package org.bibsonomy.webapp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelation;
import org.bibsonomy.model.logic.PersonLogicInterface;

/**
 * Some methods to help handling Persons.
 * 
 */
public class PersonLogic implements PersonLogicInterface {
	private static final Log log = LogFactory.getLog(PersonLogic.class);
	private PersonDatabaseManager personDatabaseManager;
	private DBSessionFactory dbSessionFactory;
	private User loginUser;
	
	public PersonLogic(final User loginUser, final DBSessionFactory dbSessionFactory) {
		this.loginUser = loginUser;
		this.dbSessionFactory = dbSessionFactory;
		this.personDatabaseManager = PersonDatabaseManager.getInstance();
	}
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonSuggestion(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonName> getPersonSuggestion(String name) {
		return this.personDatabaseManager.findPersonNames(name, this.dbSessionFactory.getDatabaseSession());
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersons(java.lang.String, java.lang.String, org.bibsonomy.model.PersonName, org.bibsonomy.model.enums.PersonResourceRelation)
	 */
	@Override
	public List<Person> getPersons(String longHash, String publicationOwner, PersonName personName, PersonResourceRelation rel) {
		// TODO Auto-generated method stub
		return new ArrayList<Person>();
	}


	@Override
	public void addResourceRelation(String longHash, String publicationOwner, int personNameId, PersonResourceRelation rel) {
		ResourcePersonRelation rpr = new ResourcePersonRelation();
		rpr.setSimhash1(longHash);
		rpr.setRelatorCode(rel.getRelatorCode());
		rpr.setPersonNameId(personNameId);
		
		this.personDatabaseManager.addResourceRelation(rpr, this.dbSessionFactory.getDatabaseSession());
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#removePersonRelation(java.lang.String, java.lang.String, org.bibsonomy.model.Person, org.bibsonomy.model.enums.PersonResourceRelation)
	 */
	@Override
	public void removePersonRelation(String longHash, String publicationOwner, int person_ID, PersonResourceRelation rel) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#createOrUpdatePerson(org.bibsonomy.model.Person)
	 */
	@Override
	public void createOrUpdatePerson(Person person) {
		if(person.getId() > 0) {
			this.personDatabaseManager.updatePerson(person, this.dbSessionFactory.getDatabaseSession());
			this.personDatabaseManager.updatePersonName(person.getMainName(), this.dbSessionFactory.getDatabaseSession());
		} else {
			this.personDatabaseManager.createPerson(person, this.dbSessionFactory.getDatabaseSession());
			if(person.getMainName().getId() == 0 )
				this.personDatabaseManager.createPersonName(person.getMainName(), this.dbSessionFactory.getDatabaseSession());
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonById(int)
	 */
	@Override
	public Person getPersonById(int id) {
		return this.personDatabaseManager.getPersonById(id, this.dbSessionFactory.getDatabaseSession());
	}
	
	@Override
	public Map<Person, BibTex> getQualifyingPublications(String personName) {
		return null;
	}
	/**
	 * @param id
	 * @return Set<PersonName>
	 */
	public List<?> getAlternateNames(int id) {
		return this.personDatabaseManager.getAlternateNames(id, this.dbSessionFactory.getDatabaseSession());
	}
	/**
	 * @param parseInt
	 * @return
	 */
	public PersonName getPersonNameById(int id) {
		return this.personDatabaseManager.getPersonNameById(id, this.dbSessionFactory.getDatabaseSession());
	}
}
