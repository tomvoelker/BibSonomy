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

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonSuggestion(java.lang.String, java.lang.String)
	 */
	@Override
	public List<PersonName> getPersonSuggestion(String lastName,
			String firstName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonSuggestion(org.bibsonomy.model.PersonName)
	 */
	@Override
	public List<PersonName> getPersonSuggestion(PersonName personName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#addResourceRelation(org.bibsonomy.model.ResourcePersonRelation)
	 */
	@Override
	public void addResourceRelation(ResourcePersonRelation resourcePersonRelation) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#removeResourceRelation(int)
	 */
	@Override
	public void removeResourceRelation(int resourceRelationId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#removePersonName(java.lang.Integer)
	 */
	@Override
	public void removePersonName(Integer personNameId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getResourceRelations(int)
	 */
	@Override
	public List<ResourcePersonRelation> getResourceRelations(int personNameId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getResourceRelations(org.bibsonomy.model.Person)
	 */
	@Override
	public List<ResourcePersonRelation> getResourceRelations(Person person) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getResourceRelations(org.bibsonomy.model.PersonName)
	 */
	@Override
	public List<ResourcePersonRelation> getResourceRelations(PersonName person) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getResourceRelations(org.bibsonomy.model.PersonName, java.lang.String, java.lang.String, java.lang.String, org.bibsonomy.model.enums.PersonResourceRelation)
	 */
	@Override
	public List<ResourcePersonRelation> getResourceRelations(PersonName pn,
			String interHash, String intraHash, String requestedUser,
			PersonResourceRelation relatorCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#createOrUpdatePersonName(org.bibsonomy.model.PersonName)
	 */
	@Override
	public void createOrUpdatePersonName(PersonName withPersonId) {
		// TODO Auto-generated method stub
		
	}
}
