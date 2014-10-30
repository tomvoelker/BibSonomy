package org.bibsonomy.webapp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.PersonResourceRelation;
import org.bibsonomy.model.logic.PersonLogicInterface;

/**
 * Some methods to help handling Persons.
 * 
 */
public class PersonLogic implements PersonLogicInterface {
	private static final Log log = LogFactory.getLog(PersonLogic.class);

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonSuggestion(java.lang.String)
	 */
	@Override
	public List<Person> getPersonSuggestion(String searchString) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersons(java.lang.String, java.lang.String, org.bibsonomy.model.PersonName, org.bibsonomy.model.enums.PersonResourceRelation)
	 */
	@Override
	public List<Person> getPersons(String longHash, String publicationOwner, PersonName personName, PersonResourceRelation rel) {
		// TODO Auto-generated method stub
		return new ArrayList<Person>();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#addPersonRelation(java.lang.String, java.lang.String, org.bibsonomy.model.Person, org.bibsonomy.model.enums.PersonResourceRelation)
	 */
	@Override
	public void addPersonRelation(String longHash, String publicationOwner, String personID, PersonResourceRelation rel) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#removePersonRelation(java.lang.String, java.lang.String, org.bibsonomy.model.Person, org.bibsonomy.model.enums.PersonResourceRelation)
	 */
	@Override
	public void removePersonRelation(String longHash, String publicationOwner, String person_ID, PersonResourceRelation rel) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#createOrUpdatePerson(org.bibsonomy.model.Person)
	 */
	@Override
	public void createOrUpdatePerson(Person person) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonById(int)
	 */
	@Override
	public Person getPersonById(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Map<Person, BibTex> getQualifyingPublications(String personName) {
		return null;
	}
}
